
public class Consumer extends Thread {
	
	private Cubby cubo;
	
	public Consumer(Cubby cubo) {
		this.cubo = cubo;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			cubo.get();
		}
	}
}
