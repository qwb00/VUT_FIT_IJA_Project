Project: Jednoduchý 2D simulátor mobilních robotů

Description

This project is a robot simulation in a 2D environment with functionality to control robots, their movements, and to reverse the state of the simulation.

Installation and Running

Requirements:

1. Make sure you have the following components installed on your computer:
JDK 17
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

Robot Control Buttons
Go Forward: Commands the robot to move forward in the direction it is currently facing.
Turn Left: Rotates the robot counterclockwise.
Turn Right: Rotates the robot clockwise.

Simulation Control Buttons
Start/Resume Simulation: Initially, the simulation is paused. Press this button to start or resume the simulation.
Pause Simulation: Temporarily stops the simulation, allowing you to resume it later from the same point.
Stop Simulation: Completely stops the simulation and resets the environment to its initial state.
Reverse Simulation: Rolls back the simulation to a previously saved state.

Configuration Management
Load Configuration: Loads the simulation settings from data/config.txt.
Save Configuration: Saves the current simulation settings to data/config.txt.
Window Management Buttons
Minimize Application: Minimizes the application window.
Full Screen Mode: Toggles the application window to full-screen mode.
Close Application: Closes the application.

Project Structure
src/: The main code of the project.
Main.java: The main package with the entry point to the project.
design/: Window design and custom elements.
environment/: Description of the environment and its elements.
robot/: Different types of robots.
simulation/: Simulation logic, state, and management.
view/: Graphical interface and object views.
data/: Project resources like configuration files.
lib/: External libraries and images used in the project.

Project Team
xposte00 - Aleksander Postelga - Robot movement simulation and all related functionalities.
xpetri23 - Aleksei Petrishko - Visual component of the program, design, and presentation.

