import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class Printer {

	private Semaphore streamSemaphore = new Semaphore(2);
	private ReentrantLock lockPrints = new ReentrantLock(true);
	
	private List<String> threadIn = new ArrayList<>();
	private PrintStream freeStream = System.out;
	
	public void printMessages(String[] numbers, 
			PrinterThread thread) {
		try {
			// Seção crítica 2 threads
			streamSemaphore.acquire();
			
			// Seção crítica mutex das 2 threads dentro
			lockPrints.lock();
			
			String console = freeStream == System.out ? "-out" : "err";
			
			threadIn.add(Thread.currentThread().getName() + console);
			
			thread.setStream(freeStream);
			
			if (freeStream == System.out)
				freeStream = System.err;
			else freeStream = System.out;
	
			thread.getStream().printf("Dentro [%s]: %s\n", Thread.currentThread().getName(), threadIn);
			thread.getStream().flush();
			// Fim seção crítica mutex das 2 threads dentro
			lockPrints.unlock();
			
			// Impressão dos dados
			for (String number : numbers) {
				thread.getStream().print(number);
				thread.getStream().flush();
			}
			
			lockPrints.lock();
			threadIn.remove(thread.getName());
			freeStream = thread.getStream();
			thread.getStream().flush();
			lockPrints.unlock();
			
			
		} catch (InterruptedException e) {} 
		finally {
			// Fim seção crítica 2 threads
			streamSemaphore.release();
			
//			try {
//				Thread.sleep(new Random().nextInt(500));
//			} catch (InterruptedException e) {}
		}
	}
}
