package main.java;


import main.java.Configuration.Configuration;
import main.java.common.Environment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class

Main {
    public static void main(String... args) {
        Environment room = Configuration.loadConfiguration("/Users/aleksander/Documents/SchoolProjects/IJA/IJA-Project/config.txt");

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