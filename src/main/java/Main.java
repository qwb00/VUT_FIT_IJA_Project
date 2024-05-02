package main.java;


import main.java.configuration.Configuration;
import main.java.common.Environment;
import main.java.simulation.SimulationManager;


public class

Main {
    public static void main(String... args) {
        EnvPresenter presenter = new EnvPresenter();
        presenter.open(); // Открываем приложение
    }

    /**
     * Uspani vlakna na zadany pocet ms.
     * @param ms Pocet ms pro uspani vlakna.
     */
    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
        }
    }
}