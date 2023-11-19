package src;

import java.io.*;

import src.RobotParts.OneSensor;
import src.RobotParts.MultipleSensors;

/*
 * RobotController.java.
 * RobotController is based on the code provided by Dr. James Stovold, and it is used to control the robot.
 * Citation: Stovold, J. (2022). Rig (Version 0.1)[Test Rig Code]. https://github.lancs.ac.uk/stovold/SCC211_AY23 
 * 
 * This class is used to control the robot, it will be the main class of the program.
 * It will be used to start the robot, and to let the choose the workflow to be used.
 * The user can choose between a workflow with a single sensor, multiple sensors, or to exit the program.
 */
public class RobotController {
    // RobotController properties
    Workflow workflow;

    // RobotController main method
    public static void main(String[] args) {
        // Variables for user input
        Integer selectedOption = 0;
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        String line = "";

        // Menu
        while(true) {
            System.out.println("\r\n==================================");
            System.out.println("       Robot Controller Menu\n  Menu inspired by James Stovold");
            System.out.println("==================================\r\n");
            System.out.println("1. Start with a single sensor");
            System.out.println("2. Start with multiple sensors");
            System.out.println("0. Exit");	

            System.out.print("Enter a number: ");

            // Read user input
            try {
                line = buffer.readLine();
                System.out.println();
                selectedOption = Integer.parseInt(line);

            } catch (NumberFormatException e) {
                System.out.println("'" + line + "' is not recognized, please input a number." );
                continue;

            } catch (IOException e) {
                System.out.println("IOException, quitting...");
            }

            // Check if the user wants to exit
            if(selectedOption == 0) {break;}

            // Create a RobotController instance and start it
            RobotController robot = new RobotController();
            System.out.println("Turning on robot...");

            // Check which workflow the user wants to use, 1 - One sensor, 2 - Multiple sensors
            switch (selectedOption) {
                case 1:
                    robot.workflow = new OneSensor();
                    break;
            
                case 2:
                    robot.workflow = new MultipleSensors();
                    break;
                    
                // For any number that isn't 0, 1 or 2, the program will exit
                default:
                    System.out.println("Not recognized. Shutting off now.");
                    return;
            }

            // Start the workflow
            System.out.println("Robot is: " + robot.workflow.name());
            robot.workflow.init();
            robot.workflow.go();
        }
    }
}
