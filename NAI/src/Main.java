
public class Main {
	public static void main(String[] args) {
		//run with --add-opens=java.base/java.lang=ALL-UNNAMED
		//Because of Wekka's problems
		new Thread(()->{
			new GUI();
		}).start();
	}
}
