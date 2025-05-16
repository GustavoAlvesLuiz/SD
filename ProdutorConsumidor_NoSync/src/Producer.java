
public class Producer extends Thread {

	private Cubby cubo;

	public Producer(Cubby cubo) {
		this.cubo = cubo;
	}


	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			cubo.put((int)(Math.random() * 100));			
		}
	}
}
