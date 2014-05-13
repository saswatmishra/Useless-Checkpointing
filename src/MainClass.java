import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.sun.nio.sctp.SctpChannel;

public class MainClass {
	public static String myHostAddress;
	public static int myPortNumber;
	public static int myNodeNumber;
	public static Map<Integer, SctpChannel> ClientSocket = new HashMap<Integer, SctpChannel>();
	public static ArrayList<String> hostNames = new ArrayList<String>();
	public static ArrayList<Integer> portNumbers = new ArrayList<Integer>();
	public static ArrayList<Integer> nodeNumbers = new ArrayList<Integer>();
	public static ArrayList<TripletData> connectedTo = new ArrayList<TripletData>();
	public static ArrayList<TripletData> nodeList = new ArrayList<TripletData>();
	public static CheckPoint check;
	public static HashMap<String, Integer> processes = new HashMap<String, Integer>();
	// public static boolean mutex = false;
	public static int rcvdEnd = 0;

	public static void readConnectedTo(String filePath) throws Exception {
		BufferedReader br = null;
		String sCurrentLine;
		String[] output;
		ArrayList<TripletData> result = new ArrayList<TripletData>();
		br = new BufferedReader(new FileReader(filePath));

		while (!(sCurrentLine = br.readLine()).equals("END")) {
		}
		int counter = 1;
		while ((sCurrentLine = br.readLine()) != null) {
			System.out.println("Hello");
			output = sCurrentLine.split("\\s+");
			if (counter == myNodeNumber) {
				for (int i = 0; i < output.length; i++) {
					if (output[i].equals("1")) {
						System.out.println("Hey");
						for (int j = 0; j < nodeList.size(); j++) {
							if (nodeList.get(j).hostName == i + 1)
								connectedTo.add(nodeList.get(j));
						}
					}
				}

			}
			counter++;
		}
		br.close();
	}

	public static void readNode(String filePath) throws Exception {
		BufferedReader br = null;
		String sCurrentLine;
		String[] output;
		ArrayList<TripletData> result = new ArrayList<TripletData>();
		br = new BufferedReader(new FileReader(filePath));
		System.out.println("here");
		while (!(sCurrentLine = br.readLine()).equals("END")) {
			TripletData newEntry = new TripletData();
			if (sCurrentLine.equals("")) {
			} else {
				output = sCurrentLine.split("\\s+");
				if (Integer.parseInt(output[0]) == myNodeNumber) {

					myHostAddress = output[1];
					myPortNumber = Integer.parseInt(output[2]);
					System.out.println("Hostname:" + myHostAddress + ";"
							+ "myPortNumber:" + myPortNumber);
					newEntry.hostName = Integer.parseInt(output[0]);
					newEntry.ipAddress = output[1];
					newEntry.portNo = Integer.parseInt(output[2]);
					nodeList.add(newEntry);
					processes.put(myHostAddress, Integer.parseInt(output[0]));
					// lud.put(myNodeNumber,-1);
				} else {
					newEntry.hostName = Integer.parseInt(output[0]);
					newEntry.ipAddress = output[1];
					newEntry.portNo = Integer.parseInt(output[2]);
					nodeList.add(newEntry);
					processes.put(output[1], Integer.parseInt(output[0]));
					// lud.put(newEntry.hostName,-1);

				}
			}
		}

		/*
		 * CheckPoint declaration
		 */

		check = new CheckPoint(myNodeNumber, processes.size());
		InitializeCheckPoint();
		br.close();

	}

	public static void InitializeCheckPoint() {
		check.initialization();
		System.out.println("Initial check Point taken for Process # "
				+ myNodeNumber);
		/*
		 * Thread server = new SCTPServer(); server.setName("Server Thread");
		 * server.start();
		 * 
		 * try { Thread.sleep(10000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * Thread client = new SCTPClient(); client.setName("Client Thread");
		 * client.start();
		 */
	}

	public static void main(String args[]) throws Exception {
		System.out.println("Enter Node Number");
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(
				System.in));
		myNodeNumber = Integer.parseInt(args[0]);
		System.out.println("myNodeNumber:" + myNodeNumber);

		readNode("topo2.txt");
		readConnectedTo("topo2.txt");
		for (int i = 0; i < MainClass.connectedTo.size(); i++) {
			System.out.println("Connected To : "
					+ MainClass.connectedTo.get(i).hostName + ","
					+ MainClass.connectedTo.get(i).ipAddress + ","
					+ MainClass.connectedTo.get(i).portNo);
		}
		for (int i = 0; i < MainClass.nodeList.size(); i++) {
			System.out.println("Node List : "
					+ MainClass.nodeList.get(i).hostName + ","
					+ MainClass.nodeList.get(i).ipAddress + ","
					+ MainClass.nodeList.get(i).portNo);
		}
		// InitializeCheckPoint();

		Thread server = new SCTPServer();
		server.setName("Server Thread");
		server.start();

		Thread.sleep(10000);

		Thread client = new SCTPClient();
		client.setName("Client Thread");
		client.start();

		Thread.sleep(100);
		Thread basicCheckPoint = new BasicCheckPoint();
		basicCheckPoint.setName("Basic CheckPoint");
		basicCheckPoint.start();

		basicCheckPoint.join();
		System.out.println("basicCheckPoint Joined");
		while (BasicCheckPoint.sentEnd == false
				|| MainClass.rcvdEnd < (MainClass.connectedTo.size())) {

		}
		client.join();
		System.out.println("client joined");
		server.join();
		// server.join();
		System.out.println("Server Joined");
		// client.join();

		System.out.println("Main ending");
		System.exit(0);

	}

}
