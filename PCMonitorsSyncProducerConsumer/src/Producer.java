
public class Producer extends Thread {

	private Cubby cubo;

	public Producer(Cubby cubo) {
		this.cubo = cubo;
	}


	@Override
	public void run() {
		while (true) {
			
			System.out.println("Produtor entrando na SC");
			
			synchronized (cubo) {
				if(cubo.getSomeToConsume()) {
					try {
						cubo.wait();
					} catch (InterruptedException ie) {}
				}
				cubo.put((int) (Math.random() * 100));
				cubo.notify();
			}
		}
	}
}
