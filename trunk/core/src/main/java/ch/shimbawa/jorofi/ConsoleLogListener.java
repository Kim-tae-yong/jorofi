package ch.shimbawa.jorofi;

public class ConsoleLogListener implements LogListener {
	public void message(String message) {
		System.out.println(message);
	}
}