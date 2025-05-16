
public class JobThread extends Thread {
	
	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		
		for (int i = 0; i <= 1000; i++) {
			System.err.printf("%s - %d", threadName, i);
		}
	}
}
