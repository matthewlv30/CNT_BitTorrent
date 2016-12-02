import java.io.FileReader;
import java.io.Reader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is for managing the choking and unchoking of peers. Its construction starts the timers for 
 * preferred neighbor and optimistically unchoked neighbor selection. It invokes the processes for these
 * selections
 *
 */
public class Unchoked {
	Properties cProp;
	Server myServer;
	
	public Unchoked(Server s) throws Exception {
		// Read Configuration File
		Reader cReader = new FileReader(CommonProperties.CONFIG_FILE);
        cProp = CommonProperties.read(cReader);
        
        myServer = s;
        
        int unchokingInterval = 1000 * Integer.parseInt(cProp.getProperty("UnchokingInterval"));
        int optimisticUnchokingInterval = 1000 * Integer.parseInt(cProp.getProperty("OptimisticUnchokingInterval"));
        
        // Start the timer for selecting preferred neighbors
        Timer pTimer = new Timer();
        pTimer.schedule(new PreferredNeighborTimer(myServer), 0, unchokingInterval);
        
        Timer oTimer = new Timer();
        oTimer.schedule(new OptimisticNeighborTimer(myServer), 0, optimisticUnchokingInterval);
	}
	
	/**
	 * Returns true if the peer associated with the Socket is optimistically unchoked or is a preferred neighbors
	 * @param peer:	the peer Socket to be checked
	 * @return 		true if unchoked, false if choked
	 */
	public boolean isUnchoked(Socket peer) {
		if (peer == myServer.getOptimisticallyUnchoked()) {
			return true;
		}
		
		ConcurrentHashMap<Socket,Boolean> preferredNeighbors = myServer.getPreferredNeighbors();
		
		for (Map.Entry<Socket, Boolean> entry : preferredNeighbors.entrySet()) {
			if ((entry.getKey() == peer) && (entry.getValue() == true)) {
				return true;
			}
		}
		
		return false;
 	}
	
	class OptimisticNeighborTimer extends TimerTask {

		Server myServer; // Instance of the server so that the optimistically unchoked neighbor can be set
		
		public OptimisticNeighborTimer(Server _myServer) {
			this.myServer = _myServer;
		}
		
		@Override
		public void run() {
			// Get the interested peers
			HashMap<Socket, Boolean> peers = myServer.getInterestedPeers();
			Socket optimisticallyUnchokedNeighbor = null;
			
			Object[] entries = peers.keySet().toArray();
			Map<Integer, Boolean> randMap = new HashMap<Integer, Boolean>();
			
			Random rand = new Random();
			int n = rand.nextInt(entries.length);
			while (randMap.containsKey(n) && (randMap.get(n) == true)) {
				n = rand.nextInt(entries.length);
			}
			
			optimisticallyUnchokedNeighbor = (Socket) entries[n];
			myServer.setOptimisticallyUnchoked(optimisticallyUnchokedNeighbor);
		}
		
	}
	
	class PreferredNeighborTimer extends TimerTask {

		Server myServer; // Instance of the server so that the preferred neighbors can be set
		
		Socket preferredNeighbors[] = new Socket[Integer.parseInt(cProp.getProperty("NumberOfPreferredNeighbors"))]; // The preferred neighbors array, currently empty
		LinkedList<Map.Entry<Socket, Double>> contenders = new LinkedList<Map.Entry<Socket, Double>>(); // The peers with duplicate downloading rates to choose from;
		Map.Entry<Socket, Double> previous = null; // The previous peer looked at. This is useful for determining duplicate chains.
		Map.Entry<Socket, Double> current = null;
		Map<Socket, Double> downloadingRates = new HashMap<Socket, Double>(); // Empty map of Socket -> downloading rates
		int count = 0; // indicates how many elements are currently in the preferredNeighbors array
		Iterator<Map.Entry<Socket, Double>> it;
		
		public PreferredNeighborTimer(Server _myServer) {
			this.myServer = _myServer;
		}
		
		@Override
		public void run() {
			// Retrieve the number of bytes each neighbor has provided us
			Map<Socket, Double> neighborByteCount = myServer.getNeighborByteCount();
			
			// To calculate the downloading speed for each neighbor, divide bytes by unchokingInterval
			for (Map.Entry<Socket, Double> entry : neighborByteCount.entrySet()) {
				double byteCount = entry.getValue();
				double downloadingSpeed = byteCount / Integer.parseInt(cProp.getProperty("UnchokingInterval"));
				
				// Store calculations in the map of downloading rates, if they are interested
				if (myServer.isPeerInterested(entry.getKey())) {
					downloadingRates.put(entry.getKey(), downloadingSpeed);
				}
			}
			
			// Sort the map of downloading rates
			downloadingRates = MapUtil.sortByValue(downloadingRates);
			
			it = downloadingRates.entrySet().iterator(); // Iterator for iterating through all the possible peers
			
			if (myServer.hasCompleteFile()) {
				randomlySelectNeighbors();
			} else{
				findPreferredNeighbors();
			}
		}
		
		private void randomlySelectNeighbors() {
			Object[] entries = downloadingRates.keySet().toArray();
			Map<Integer, Boolean> randMap = new HashMap<Integer, Boolean>();
			
			Random rand = new Random();
			for (int i = 0; i < preferredNeighbors.length; i++) {
				int n = rand.nextInt(entries.length);
				while (randMap.containsKey(n) && (randMap.get(n) == true)) {
					n = rand.nextInt(entries.length);
				}
				preferredNeighbors[i] = (Socket) entries[n];
			}
		}
		
		// Start the process of finding preferred neighbors
		private void findPreferredNeighbors() {
			// Fill up all k spots
			while (count != preferredNeighbors.length) {
				// If there are still peers to look at
				if (it.hasNext()) {
					// Retrieve the peerID/rate pair
					current = (Map.Entry<Socket, Double>)it.next();

					// If there is a previous peer to look at, we can look for duplicates
					if (previous != null) {
						findDuplicates();
					} 
					
					previous = current;
				} else {
					preferredNeighbors[count] = (Socket)previous.getKey();
					count++;
				}
			}
			
			// Set the preferred neighbors for the server object
			myServer.setPreferredNeighbors(preferredNeighbors);
			myServer.resetByteCount();
			
			// Reset the unchoked info for the next selection interval
			previous = null;
			current = null;
			downloadingRates.clear();
			count = 0;
		}
		
		// Search through the list of peers. If a duplicate chain is found, go through the duplicate value process.
		private void findDuplicates() {
			// Need to store the values in a double or else they won't compare properly
			double dp = (Double)current.getValue();
			double dc = (Double)previous.getValue();
			
			// If there is a duplicate value, add it to the contenders list
			if (dp == dc) {
				contenders.add(previous);
				contenders.add(current);
				
				// Keep looking for other chaining duplicates and add those to the contenders list too
				boolean c = true;
				while (c == true) {
					Map.Entry<Socket, Double> next;
					
					if (it.hasNext()) {
						next = (Map.Entry<Socket, Double>)it.next();
						
						double dn = (Double)next.getValue();
						if (dn == dc) {
							contenders.add(next);
							
							// If we run out of peers to look for, break out of the searching loop
							if (!it.hasNext()) {
								c = false;
							}
						} else {
							current = next;
							c = false;
						}
					} else {
						c = false;
					}
				}
				addDuplicates();
			} else {
				// If the previous and current are not duplicates, then you are free to add the previous to the peers
				preferredNeighbors[count] = (Socket)previous.getKey();
				count++;
			}
		}
		
		// Determine which duplicate peers will be added to the list
		private void addDuplicates() {
			int peersNeeded = preferredNeighbors.length - count;
			
			if (contenders.size() == peersNeeded) {
				for (int j = 0; j < peersNeeded; j++) {
					preferredNeighbors[count] = (Socket)contenders.get(j).getKey();
					count++;
;				}
			} else {
				// Problem with selecting the same neighbor already selected!
				Random rand = new Random();
				Map<Integer, Boolean> randMap = new HashMap<Integer, Boolean>();
				
				while (contenders.size() > 0) {
					if (count == preferredNeighbors.length) {
						break;
					}
					
					int n = rand.nextInt(contenders.size());
					while (randMap.containsKey(n) && (randMap.get(n) == true)) {
						n = rand.nextInt(contenders.size());
					}
					preferredNeighbors[count] = (Socket)contenders.get(n).getKey();
					count++;
					contenders.remove(n);
				}
				contenders.clear();
			}
		}
	}
}
