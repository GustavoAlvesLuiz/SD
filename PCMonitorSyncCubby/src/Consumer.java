
public class Consumer extends Thread {
	
	private Cubby cubo;
	
	public Consumer(Cubby cubo) {
		this.cubo = cubo;
	}
	
	@Override
	public void run() {
		while (true) {
			cubo.get();
		}
	}
}
