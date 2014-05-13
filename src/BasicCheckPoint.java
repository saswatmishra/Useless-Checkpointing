import java.io.IOException;

public class BasicCheckPoint extends Thread {
	public static boolean takingCheckpoint = true;
	public static boolean sentEnd = false;
	int i = 1;
	int number = 50;
	int meanChkptArr[] = new int[number];

	@Override
	public void run() {
		meanChkptArr = CheckPoint.getMeanChkptTime(number);
		while (i < number) {
			try {

				int sleepTime = 6000;
				// int sleepTime=meanChkptArr[i];
				// while(MainClass.mutex)
				// {
				//
				// }
				// MainClass.mutex=true;
				Thread.sleep(sleepTime);
				MainClass.check.takeCheckPoint("Basic");
				// MainClass.mutex = false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		takingCheckpoint = false;
		for (int i = 0; i < MainClass.connectedTo.size(); i++) {
			try {
				System.out
						.println("Socket open: "
								+ MainClass.ClientSocket.get(
										MainClass.connectedTo.get(i).hostName)
										.isOpen());
				if (MainClass.ClientSocket
						.get(MainClass.connectedTo.get(i).hostName) != null
						&& MainClass.ClientSocket.get(
								MainClass.connectedTo.get(i).hostName).isOpen()) {
					SCTPClient.sendMessage(MainClass.ClientSocket
							.get(MainClass.connectedTo.get(i).hostName), "End");
					System.out.println("Sent End...");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sentEnd = true;

	}

}
