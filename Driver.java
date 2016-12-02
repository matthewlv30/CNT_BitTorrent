
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

public class Driver {

	public static void main(String[] args) throws Exception {
//        //Read Peer Info File and Get All info
//		LinkedList<RemotePeerInfo> peersToConnect = new LinkedList<RemotePeerInfo>();
//		Reader pReader = null;
//		PeerInfo peerInfo = new PeerInfo();
//		pReader = new FileReader(PeerInfo.CONFIG_FILE);
//		peerInfo.read(pReader);
//		peersToConnect = peerInfo.getPeerInfo();
//
//        System.out.println(peersToConnect.get(0).getPeerId());
		
//		Unchoked a = new Unchoked();
		
		
		// Test map is a map from the socket to downloading rate.
		Map<String, Double> testMap = new HashMap<String, Double>();
		testMap.put("allison", 5.0);
		testMap.put("ben", 5.0);
		testMap.put("matt", 3.0);
		testMap.put("alexandria", 1.0);
		testMap.put("sam", 1.0);
		testMap.put("joe", 1.0);
		// NOTE: there is an error when there is a duplicate chain at the end!!! Middle is fine!
		
		testMap = MapUtil.sortByValue( testMap );
		 
        for (Map.Entry<String, Double> entry : testMap.entrySet()) {
        	System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println();
        
        String k[] = new String[6]; // These are all the peers that need to be filled!
        int count = 0; // also indicates how many elements are in k...
        
		Iterator<Map.Entry<String, Double>> it = testMap.entrySet().iterator(); // This iterates through all the possible peers
		LinkedList<Map.Entry<String, Double>> contenders = new LinkedList<Map.Entry<String, Double>>(); // The peers with duplicate downloading rates to randomly choose from
		Map.Entry<String, Double> previous = null; // The previous peer looked at. This is useful for determining duplicate chains!
		
		boolean com = false;
		
		if (com == false) {
		// Fill up all k spots
		while (count != k.length) {
			// If there are still peers to look at
			if (it.hasNext()) {
				// Retrieve the peer/rate pair
				Map.Entry<String, Double> current = (Map.Entry<String, Double>)it.next();
				
				System.out.println("Key: " + current.getKey());
				
				// If there is a previous to look at, we can perform a bunch of crazy duplicate analyzing!
				if (previous != null) {
					// Need to store the values in a double or else they won't compare properly
					double dp = (Double)current.getValue();
					double dc = (Double)previous.getValue();
					
					// If there is a duplicate value, add it to the contenders list
					if (dp == dc) {
						System.out.println("Dup chain begins!");
						contenders.add(previous);
						contenders.add(current);
						
						// Keep looking for other chaining duplicates and add those to the contenders list too
						boolean con = true;
						while (con == true) {
							Map.Entry<String, Double> nextPair;
							
							if (it.hasNext()) {
								nextPair = (Map.Entry<String, Double>)it.next();
//								i++;
								
								double dn = (Double)nextPair.getValue();
								if (dn == dc) {
									System.out.println("Another dup: " + nextPair.getKey());
									contenders.add(nextPair);
									// If we run out of peers to look for, break out of the searching loop
									if (!it.hasNext()) {
										con = false;
									}
								} else {
									System.out.println("Not a dup: " + nextPair.getKey());
									current = nextPair;
									con = false;
								}
							} else {
								con = false;
							}
						}
						
						// Now we need to determine who should go and who should stay!
						int peersNeeded = k.length - count;
						System.out.println("We need " + peersNeeded + " more peers");
						System.out.println("Number of contenders: " + contenders.size()); // Note, error because we have added the "previous duplicate"
						
						if (contenders.size() == peersNeeded) {
							for (int j = 0; j < peersNeeded; j++) {
								k[count] = (String)contenders.get(j).getKey();
								count++;
							}
						} else {
							System.out.println("Gotta do the random thing");
							Random rand = new Random();
							Map<Integer, Boolean> randMap = new HashMap<Integer, Boolean>();
							
							while (contenders.size() > 0) {
								if (count == k.length) {
									break;
								}
								int n = rand.nextInt(contenders.size());
								while (randMap.containsKey(n) && (randMap.get(n) == true)) {
									n = rand.nextInt(contenders.size());
								}
								System.out.println(count);
								k[count] = (String)contenders.get(n).getKey();
								count++;
								contenders.remove(n);
							}
							System.out.println(count);
							
							contenders.clear();
						}
					} else {
						// If the previous and current are not duplicates, then you are free to add the previous to the peers
						k[count] = (String)previous.getKey();
						count++;
					}
				}
				previous = current;
			} else {
				k[count] = (String)previous.getKey();
				count++;
			}
		}
		} else {
			Object[] entries = testMap.keySet().toArray();
			Map<Integer, Boolean> randMap = new HashMap<Integer, Boolean>();
			
			Random rand = new Random();
			for (int i = 0; i < k.length; i++) {
				int n = rand.nextInt(entries.length);
				while (randMap.containsKey(n) && (randMap.get(n) == true)) {
					n = rand.nextInt(entries.length);
				}
				
				k[i] = (String) entries[n];
				randMap.put(n, true);
			}
		}
		
		System.out.println();
		
		for (int i = 0; i < k.length; i++) {
			System.out.println(k[i]);
		}
	}

}
