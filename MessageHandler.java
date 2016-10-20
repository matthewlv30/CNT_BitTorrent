import java.net.Socket;

/**
 * This interface declares the behavior of peers after receiving a message.
 * 
 */
public interface MessageHandler {
	
	public void handleMessage(ActualMessage m, Socket n);
}