import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;

public class SCTPClient extends Thread {

	public static boolean runningClient = true;
	public int sendingExitMessage = 7;

	// private SctpChannel[] ClientSock=new SctpChannel[10];

	public static void sendMessage(SctpChannel clientSock, String Message)
			throws IOException {
		// System.out.println("Hey Im inside Send Messgae");
		// prepare byte buffer to send massage
		ByteBuffer sendBuffer = ByteBuffer.allocate(512);
		sendBuffer.clear();
		// Reset a pointer to point to the start of buffer
		sendBuffer.put(Message.getBytes());
		sendBuffer.flip();
		if (BasicCheckPoint.sentEnd == false
				|| MainClass.rcvdEnd < MainClass.connectedTo.size()) {
			// System.out.println("Sending to... " +
			// clientSock.getRemoteAddresses()
			// + " " + Message);
			try {
				// Send a message in the channel
				MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
				clientSock.send(sendBuffer, messageInfo);
				sendBuffer.clear();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void sendMessage2(SctpChannel clientSock, String Message)
			throws IOException {
		// System.out.println("Hey Im inside Send Messgae");
		// prepare byte buffer to send massage
		ByteBuffer sendBuffer = ByteBuffer.allocate(512);
		sendBuffer.clear();
		// Reset a pointer to point to the start of buffer
		sendBuffer.put(Message.getBytes());
		sendBuffer.flip();
		System.out.println("Sending2 to... " + clientSock.getRemoteAddresses()
				+ " " + Message);
		try {
			// Send a message in the channel
			MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
			clientSock.send(sendBuffer, messageInfo);
			sendBuffer.clear();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void receiveMessage(SctpChannel clientSock) {
		ByteBuffer byteBuffer;
		byteBuffer = ByteBuffer.allocate(512);
		try {
			MessageInfo messageInfo = clientSock
					.receive(byteBuffer, null, null);
			String message = byteToString(byteBuffer);
			// System.out.println("Received Message from Server:");
			// System.out.println(message);
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

	public void run() {
		try {
			for (int i = 0; i < MainClass.connectedTo.size(); i++) {
				System.out.println("In Client");
				InetSocketAddress serverAddr = new InetSocketAddress(
						MainClass.connectedTo.get(i).ipAddress,
						MainClass.connectedTo.get(i).portNo);
				MainClass.ClientSocket.put(
						MainClass.connectedTo.get(i).hostName,
						SctpChannel.open());
				MainClass.ClientSocket.get(
						MainClass.connectedTo.get(i).hostName).connect(
						serverAddr, 0, 0);
			}
			while (BasicCheckPoint.takingCheckpoint) {
				Random r = new Random();
				int i = r.nextInt(MainClass.nodeList.size() - 1) + 0;
				while (i == MainClass.myNodeNumber) {
					i = r.nextInt(MainClass.nodeList.size() - 1) + 0;
				}
				// for (int i = 0; i < MainClass.connectedTo.size(); i++) {
				int sleepTime = (int) CheckPoint.MMT();
				sleepTime = 50;
				// System.out.println("Sleeping for time : " + sleepTime +
				// " I love sleeping");

				// while(MainClass.mutex)
				// {
				//
				// }

				Thread.sleep(sleepTime);

				// System.out.println(MainClass.connectedTo.get(i).hostName);
				String message1 = CheckPoint.sendMessage(MainClass.connectedTo
						.get(i).hostName);
				message1 = "Hello From Client" + message1;
				// System.out.println("Message Send in clint sahil: " +
				// message1);
				/*
				 * String message = "Hello From Client with node number:" +
				 * MainClass.connectedTo.get(i).hostName;
				 */
				sendMessage(MainClass.ClientSocket.get(MainClass.connectedTo
						.get(i).hostName), message1);
				// MainClass.mutex = false;
			}
			// }

			// while (true) {
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}