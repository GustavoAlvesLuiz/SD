
public class JobThread extends Thread {
	
	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		
		for (int i = 0; i <= Main.MAX; i++) {			
			System.err.printf("%s - %d\n", threadName, i);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
	}
}
