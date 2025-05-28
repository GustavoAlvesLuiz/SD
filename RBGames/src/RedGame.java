
public class RedGame extends Thread {
	
	private MegaSena mega;
	
	public RedGame(MegaSena m) {
		this.mega = m;
	}
	
	@Override
	public void run() {
		for (int i = 0; i < mega.getGames(); i++) {
			// Lock
			while (Lock.lock.compareAndSet(0, 1) == false) System.err.print("");
						
			System.err.println("Red in.");
			mega.play(System.err);
			System.err.println("Red out.");
			System.err.flush();
			
			Lock.lock.set(0);
		}
	}
}
