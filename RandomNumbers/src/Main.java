
public class Main {

	public static void main(String[] args) {
		RandomA ra = new RandomA(1);
		RandomB rb = new RandomB(1);
		RandomA ra2 = new RandomA(2);
		RandomB rb2 = new RandomB(2);
		
		ra.start();
		ra2.start();
		
		rb.start();
		rb2.start();

	}

}
