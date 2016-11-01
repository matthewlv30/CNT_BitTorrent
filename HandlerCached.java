import java.util.HashMap;

import ActualMessages.BitfieldHandler;
import ActualMessages.MessageHandler;
import fileHandlers.RemotePeerInfo;

/**
 * Note on handlers: handlers contains mappings from types to handler objects,
 * e.g. handlers.put(1, new ChokeHandler(this)); handlers HashMap usage example:
 * handlers.get(messageType).handleMessage(message, peer);
 */
public class HandlerCached {
	// Integer: message type, MessageHandler: the message-handling
	// implementation
	private static HashMap<Integer, MessageHandler> handlers = new HashMap<Integer, MessageHandler>();

	public static MessageHandler getHandler(int id, RemotePeerInfo p) {
		MessageHandler cachedHandler = handlers.get(id);
		//Set PeerInfo of the class trying to access info 
		cachedHandler.setPeerInfo(p);
		return (MessageHandler) cachedHandler.clone();
	}

	// for each Handler run database query and create handler
	public static void loadCache() {
		//Fix This////////////////////////////////////////////////////////////////////////
		//handlers.put(1, new ChokeHandler(unchokedPeers));
		//handlers.put(2, new UnchokeHandler(unchokedPeers));
		//handlers.put(3, new InterestedHandler(interestedPeers));
		//handlers.put(4, new UninterestedHandler(interestedPeers));
		//handlers.put(5, new HaveHandler(myBitfield));
		handlers.put(6, new BitfieldHandler());
		//handlers.put(7, new RequestHandler(myBitfield, clientList));
		//handlers.put(8, new PieceHandler());*/
	}
}
