
public class Consumer extends Thread {
	
	private Cubby cubo;
	
	public Consumer(Cubby cubo) {
		this.cubo = cubo;
	}
	
	@Override
	public void run() {
		while (true) {
			
			System.out.println("Consumidor entrando na SC");
			
			synchronized (cubo) {
				if(!cubo.getSomeToConsume())
					try {
						cubo.wait();
					} catch (InterruptedException ie) {}
				
				cubo.get();
				cubo.notify();
			}
		}
	}
}
