package main.java;


import main.java.configuration.Configuration;
import main.java.common.Environment;


public class

Main {
    public static void main(String... args) {
        String homePath = System.getProperty("user.home");

        String configFilePath = "src/main/resources/config.txt";
        Environment room = Configuration.loadConfiguration(configFilePath);

        EnvPresenter presenter = new EnvPresenter(room);

        presenter.open();

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