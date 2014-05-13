// Removed static word as every process will have different clock value
// in i clock array

public class LogicalClock {
	public int clockValue;

	public LogicalClock() {
		clockValue = 0;
	}

	public synchronized int setClockValue(int clock) {
		return clockValue += clock;
	}

	public synchronized int getClockValue() {
		return clockValue;
	}

	public void tick() {
		clockValue += 1;
	}

	public void sendAction() {
		clockValue = clockValue + 1;
	}

	public void rcvAction(int src, int sentValue) {
		clockValue = Math.max(clockValue, sentValue);
	}

}
