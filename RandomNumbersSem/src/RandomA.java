import java.util.Random;

public class RandomA extends Thread {
	
	private String id;
	
	public RandomA(int id) {
		this.id = "a-".concat(String.valueOf(id));
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				// Operação P(s) - decremento
				Lock.lock.acquire();
				
				int number = new Random().nextInt(1000);
				System.out.printf("randon-%s\n", id);
				System.out.printf("%s-> %d\n", id, number);
				
				Thread.sleep(number);
			} catch (InterruptedException ie) {}
			finally {Lock.lock.release();}
		}
	}
}
