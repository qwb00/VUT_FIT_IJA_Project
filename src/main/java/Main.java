/**
 * Project: Jednoduchý 2D simulátor mobilních robotů
 */
package main.java;

/**
 * The Main class serves as the entry point for the application.
 * It initializes the environment presenter and starts the graphical user interface.
 */
public class Main {
    /**
     * The main method, which is the entry point of the application.
     * It creates an instance of the environment presenter and initializes the GUI.
     *
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String... args) {
        EnvPresenter presenter = new EnvPresenter(); // Create the main presenter for the environment
        presenter.open(); // Initialize and open the graphical user interface
    }
}