import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Message {

	// private ObjectInputStream in; // stream read from the socket
	private ObjectOutputStream out; // stream write to the socket

	
	Message(ObjectInputStream in, ObjectOutputStream out) {
		// this.in = in;
		this.out = out;
	}

	// send a message to the output stream
	public void sendMessage(Object msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
}
