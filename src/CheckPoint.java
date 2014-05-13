import java.io.BufferedWriter;
import java.io.FileWriter;

//import com.sun.org.apache.xpath.internal.FoundIndex;

public class CheckPoint {
	static int[] clock; // Array storing all processes logical clock
	static int[] checkPoint;
	static boolean[] sent_to_i;
	static double min_to_i = Double.POSITIVE_INFINITY;
	static int[] min_to;
	static int[] ckpt; // Array storing checkpoint clock
	static boolean[] taken;
	static int noOfProcess;
	static int process_id;
	static int lambdaMTT = 50;
	static int lambdaICT = 1;
	public static int forcedCheckpointNumber = 0;
	public static String name;
	public static String forced;
	public static int basic = 0;

	public static int[] getMeanChkptTime(int number) {
		int[] cpt = null;
		cpt = new int[number];
		for (int i = 0; i < number; i++) {
			cpt[i] = StdRandom.uniform(1000, 1500);
		}
		return cpt;
	}

	public static int getMeanTXTime() {
		// int[] mtt = null;
		// mtt= new int[number];
		// for(int i=0;i<number;i++){
		// mtt[i]=(int)(StdRandom.exp(0.4)*100);
		// }
		int mtt;
		mtt = (int) (StdRandom.exp(0.4) * 100);
		return mtt;
	}

	public CheckPoint(int id, int numOfProcess) {
		noOfProcess = numOfProcess;
		// System.out.println("Number of process in CheckPoint " + noOfProcess);
		process_id = id;
		clock = new int[noOfProcess + 1];
		taken = new boolean[noOfProcess + 1];
		ckpt = new int[noOfProcess + 1];
		min_to = new int[noOfProcess + 1];
		sent_to_i = new boolean[noOfProcess + 1];
		name = "output_" + process_id + ".txt";
		forced = "forced" + process_id + ".txt";
		writeToFile(name, "----------------Process " + process_id, false);
		writeToFile(forced, "----------------Process forced " + process_id,
				false);

	}

	public void initialization() {
		for (int i = 1; i <= noOfProcess; i++) {
			clock[i] = 0;
			ckpt[i] = 0;
			taken[i] = false;
		}
		taken[process_id] = false;
		takeCheckPoint("Initial");
	}

	public static void takeCheckPoint(String type) {
		for (int i = 1; i <= noOfProcess; i++) {
			sent_to_i[i] = false;
			min_to[i] = Integer.MAX_VALUE;
			taken[i] = true;
		}
		taken[process_id] = false;
		clock[process_id]++;
		ckpt[process_id]++;
		String write = "";
		// String write = type + " " + " clock value : " + clock[process_id] +
		// " CheckPoint Number : " + ckpt[process_id];
		if (type.equals("Forced")) {
			write = type + " " + " forced number : " + forcedCheckpointNumber
					+ " CheckPoint Number : " + ckpt[process_id];
			writeToFile(forced, write, true);
		} else {
			basic++;
			write = type + " " + " clock value : " + basic
					+ " CheckPoint Number : " + ckpt[process_id];
			writeToFile(name, write, true);
		}

		// writeToFile(name, write, true);

		// System.out.println("CheckPoint : " + type + " With timeStamp " +
		// clock[process_id] + " Check Point Number  : " + ckpt[process_id]);

	}

	public static String sendMessage(int toProcess) {
		// while(MainClass.mutex){}
		// MainClass.mutex=true;
		sent_to_i[toProcess] = true;
		min_to[toProcess] = Math.min(min_to[toProcess], clock[process_id]);

		String message = createMessageToSend();
		// System.out.println("Message : " + message);
		/*
		 * here we have message with all the controls which we can send
		 */
		// MainClass.mutex=false;
		return message;

	} // Send message ends here

	/*
	 * Creating message to send which includes all the control part which is
	 * specified in algorithm
	 * 
	 * Message structure
	 * 
	 * Application Message#clock#ckpt#taken
	 * 
	 * Clock, ckpt, taken, values are comma separated
	 */

	public static String createMessageToSend() {
		String message = "#";
		String clock_i = "";
		String ckpt_i = "";
		String taken_i = "";
		for (int i = 1; i <= noOfProcess; i++) {
			if (i == noOfProcess) {
				clock_i += clock[i];
				ckpt_i += ckpt[i];
				taken_i += taken[i];
			} else {
				clock_i += clock[i] + ",";
				ckpt_i += ckpt[i] + ",";
				taken_i += taken[i] + ",";
			}
		}
		message += clock_i + "#";
		message += ckpt_i + "#";
		message += taken_i + "#";
		message += process_id + "E";

		return message;
	}

	public static double MMT() {
		double time = 0;
		double random = Math.random();
		double X = (-Math.log(1 - random)) / lambdaMTT;
		// System.out.println("X Men = " + X);
		lambdaMTT += 50;
		time = X;
		return time;
	}

	public static int ICT() {
		int time = 0;
		double random = Math.random();
		double lambdaInMilli = lambdaICT * 1000;
		double X = (-Math.log(1 - random)) / lambdaInMilli;
		lambdaICT += 1;
		time = (int) X;
		// System.out.println("Sleeping time ICT " + time);
		return time;
	}

	/*
	 * Writing the server info to file
	 */

	public static void writeToFile(String path, String message, boolean flag) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path,
					flag));

			if (!flag) {
				writer.write(message);
				writer.newLine();
				writer.close();
			} else {
				writer.append(message);
				writer.newLine();
				writer.close();
			}

		} catch (Exception e) {
			System.out.println("Error in Writing File. ");
			e.printStackTrace();
		}
	}

	/*
	 * Method to be called When message arrives
	 */

	public static void messageReceive(String message) {
		String[] messagetokens = message.split("#");
		// System.out.println("Message Received in Receieve: " + message);

		// Clock values in message
		String[] tempClock = messagetokens[1].split(",");
		int[] messageClock = new int[noOfProcess + 1];

		// ckpt values in message
		String[] tempCkpt = messagetokens[2].split(",");
		int[] messageCkpt = new int[noOfProcess + 1];

		// taken values in message
		String[] temptaken = messagetokens[3].split(",");
		boolean[] messageTaken = new boolean[noOfProcess + 1];
		// System.out.println("Size of sender " + messagetokens[4].length());
		int x = 0;
		String send = "";
		while (!(messagetokens[4].charAt(x) == 'E')) {
			send += messagetokens[4].charAt(x);
			x++;
		}
		int sender = Integer.parseInt(send);
		// System.out.println("Message " + messagetokens[0] +
		// " Received from process " + sender);

		for (int j = 0; j < tempClock.length; j++) {
			messageClock[j + 1] = Integer.parseInt(tempClock[j]);
			messageCkpt[j + 1] = Integer.parseInt(tempCkpt[j]);
			messageTaken[j + 1] = Boolean.parseBoolean(temptaken[j]);
		}

		for (int k = 1; k <= noOfProcess; k++) {
			if (sent_to_i[k]) {
				if (messageClock[sender] > min_to[k]) {
					if ((messageClock[sender] > Math.max(clock[k],
							messageClock[k]))
							|| ((messageCkpt[process_id] == ckpt[process_id]) && (messageTaken[process_id]))) {
						// Forced checkpoint
						// System.out.println("Taking forced CheckPoint");
						forcedCheckpointNumber++;
						takeCheckPoint("Forced");
						// System.out.println("Number of forced checkpoint: " +
						// ":" +forcedCheckpointNumber);
					}

				}
			}
		}

		// while(MainClass.mutex)
		// {
		//
		// }
		// MainClass.mutex=true;
		//
		int newClock = Math.max(clock[process_id], messageClock[sender]);
		clock[process_id] = newClock;

		for (int k = 1; k <= noOfProcess; k++) {
			if (k == process_id)
				continue;
			else {
				int clockValue = Math.max(clock[k], messageClock[k]);
				clock[k] = clockValue;

				if (messageCkpt[k] > ckpt[k]) {
					ckpt[k] = messageCkpt[k];
					taken[k] = messageTaken[k];
				} else if (messageCkpt[k] == ckpt[k]) {
					taken[k] = (taken[k] || messageTaken[k]);
				} else {
					// Do nothing
				}
			}
		}
		// MainClass.mutex = false;
		// System.out.println("Message " + messagetokens[0] + " from process " +
		// sender + " Delivered");
	}

	public static void show() {
		String clockS = "";
		String ckptS = "";
		String min_toS = "";
		String takenS = "";
		String sent_toS = "";
		for (int i = 1; i <= noOfProcess; i++) {
			clockS += clock[i] + " ";
			ckptS += ckpt[i] + " ";
			min_toS += min_to[i] + " ";
			takenS += taken[i] + " ";
			sent_toS += sent_to_i[i] + " ";
		}
		// System.out.println("Clock : " + clockS);
		// System.out.println("ckptS : " + ckptS);
		// System.out.println("min_toS : " + min_toS);
		// System.out.println("takenS : " + takenS);
		// System.out.println("sent_toS : " + sent_toS);

	}

	/*
	 * public static void main(String[] args) { CheckPoint c = new CheckPoint(1,
	 * 3); c.initialization(); //c.show();
	 * c.messageReceive("Application Message#0,1,0#0,1,0#true,false,true#2");
	 * c.sendMessage(3);
	 * //c.messageReceive("Application Message#1,0,0#1,0,0#false,true,true#1");
	 * //c.takeCheckPoint("Basic"); c.show();
	 * 
	 * c.show();
	 * 
	 * //c.messageReceive("Application Message#1,0,0#1,0,0#false,true,true#1");
	 * //c.takeCheckPoint("Basic"); //c.sendMessage(3); //c.show();
	 * 
	 * //c.takeCheckPoint("Forced"); //c.sendMessage(3); //c.messageReceive(
	 * "Application Message#1,3,0,0,0#1,3,0,0,0#true,false,true,true,true#2"); }
	 */

}
