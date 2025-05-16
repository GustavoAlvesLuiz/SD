
public class Main {

	public static void main(String[] args) {
		
		//Thread main
		String threadName = Thread.currentThread().getName();
		
		for (int i = 0; i <= 1000; i++) {
			System.out.printf("%s - %d\n", threadName, i);
		}

	}

}
