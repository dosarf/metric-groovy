package io.github.dosarf;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.print("Hello ");
		Dude defaultDude = Dude.defaultDude();
		System.out.println(defaultDude.getName());
	}
}
