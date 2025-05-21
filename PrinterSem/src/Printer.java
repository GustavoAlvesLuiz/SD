import java.util.List;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Printer {
	
	private Semaphore streamSemaphore = new Semaphore(2);
	private ReentrantLock lockPrints = new ReentrantLock(true);
	private List<String> threadIn = new ArrayList<>();
	private PrintStream freeStream = System.out;

	public void printMessages(String[] numbers, PrinterThread thread) {
		try {
		streamSemaphore.acquire();
		
		lockPrints.lock();
		threadIn.add(Thread.currentThread().getName());
		
		thread.setStream(freeStream);
		
		if(freeStream == System.out)
			freeStream = System.err;
		else freeStream = System.out;
		
		lockPrints.unlock();
		
		}catch (InterruptedException e) {}
		
		finally {streamSemaphore.release();}
	}

}
