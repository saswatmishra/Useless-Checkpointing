import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

public class RecieveMsg implements Runnable {
	public static boolean running = true;
	SctpChannel clientSocket;

	RecieveMsg(SctpChannel clientSocket) {
		this.clientSocket = clientSocket;
	}

	public static void receiveMessage(SctpChannel clientSock)
			throws InterruptedException {
		String[] output;
		String[] outputVector;
		ByteBuffer byteBuffer;
		byteBuffer = ByteBuffer.allocate(512);
		String messageType = "";
		try {
			if (running = true) {
				MessageInfo messageInfo = clientSock.receive(byteBuffer, null,
						null);
				String message = byteToString(byteBuffer);
				String message1 = "";
				// System.out.println("Message: "+message);
				// System.out.println("Received Message from Server Sasu Chu: "+
				// clientSock.getRemoteAddresses()+ " " + message);
				message1 = message.substring(0, 3);
				// System.out.println(message1);
				if (message1.equals("End")) {
					MainClass.rcvdEnd++;
					System.out.println("rcvdEnd count is ........: "
							+ MainClass.rcvdEnd);
				} else {
					try {
						MainClass.check.messageReceive(message);
						messageType = message.substring(0, 2);
						message = message.substring(2, message.length());
					} catch (Exception e) {

					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String byteToString(ByteBuffer byteBuffer) {
		byteBuffer.position(0);
		byteBuffer.limit(512);
		byte[] bufArr = new byte[byteBuffer.remaining()];
		byteBuffer.get(bufArr);
		return new String(bufArr);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (BasicCheckPoint.sentEnd == false
				|| MainClass.rcvdEnd < (MainClass.connectedTo.size())) {
			try {
				if (clientSocket.isOpen()) {
					receiveMessage(clientSocket);
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}
		}
		System.out.println("Ended RcvMessage");
		Thread.yield();
	}

}
