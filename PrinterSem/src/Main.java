
public class Main {

	public static void main(String[] args) {
		Printer printer =  new Printer();
		
		PrinterThread job1 = new PrinterThread(printer);
		
		job1.start();
	}

}
