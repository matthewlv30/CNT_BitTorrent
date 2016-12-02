import java.util.HashMap;

import ActualMessages.BitfieldHandler;
import ActualMessages.HaveHandler;
import ActualMessages.InterestedHandler;
import ActualMessages.MessageHandler;
import ActualMessages.UninterestedHandler;
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
		//handlers.put(0, new ChokeHandler(unchokedPeers));
		//handlers.put(1, new UnchokeHandler(unchokedPeers));
		handlers.put(2, new InterestedHandler());
		handlers.put(3, new UninterestedHandler());
		handlers.put(4, new HaveHandler());
		handlers.put(5, new BitfieldHandler());
		//handlers.put(6, new RequestHandler(myBitfield, clientList));
		//handlers.put(7, new PieceHandler());*/
	}
}
