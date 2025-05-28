
public class Producer extends Thread {

	private Cubby cubo;

	public Producer(Cubby cubo) {
		this.cubo = cubo;
	}


	@Override
	public void run() {
		while (true) {
			cubo.put((int)(Math.random() * 100));			
		}
	}
}
