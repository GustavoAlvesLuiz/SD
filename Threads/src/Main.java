
public class Main {

	public static final int MAX = 1000;
	
	public static void main(String[] args) {
		
		JobThread job1 = new JobThread();
		job1.setName("Job 1");
		// Coloca a thread em estado Runnable
		job1.start();
		
		JobRunnable job2 = new JobRunnable();
		Thread threadJob2 = new Thread(job2);
		threadJob2.setName("Job 2");
		threadJob2.start();
		
		
		// Thread main
		String threadName = Thread.currentThread().getName();
		for (int i = 0; i <= MAX; i++) {
			System.out.printf("%s - %d\n", threadName, i);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
	}
}
