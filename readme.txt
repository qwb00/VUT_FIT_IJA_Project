Project: Jednoduchý 2D simulátor mobilních robotů

Description

This project is a robot simulation in a 2D environment with functionality to control robots, their movements, and to reverse the state of the simulation.

Installation and Running

Requirements:

1. Make sure you have the following components installed on your computer:
JDK 11+
Maven

2. Compile and run the project:
The project is compiled using Maven.

Compile the project:
mvn compile

Run the project:
mvn exec:java

Features
Simulation Control: Start and pause the simulation of robots in a 2D environment.
Reverse Simulation: Roll back the environment state to a previous saved state.
Add elements: Ability to add new obstacles and robots of different types.
Window Management: Custom window design with control buttons.

Project Structure
src/: The main code of the project.
Main.java: The main package with the entry point to the project.
design/: Window design and custom elements.
environment/: Description of the environment and its elements.
robot/: Different types of robots.
simulation/: Simulation logic, state, and management.
view/: Graphical interface and object views.
data/: Project resources like images, configuration files, etc.

Project Team
xposte00 - Aleksander Postelga - Robot movement simulation and all related functionalities.
xpetri23 - Aleksei Petrishko - Visual component of the program, design, and presentation.

