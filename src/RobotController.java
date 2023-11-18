package src;

import java.io.*;

import src.RobotParts.OneSensor;
import src.RobotParts.MultipleSensors;

public class RobotController {
    Workflow workflow;

    public static void main(String[] args) {
        Integer selectedOption = 0;
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        String line = "";

        while(true) {
            System.out.println("\r\n==================================");
            System.out.println("       Robot Controller Menu\n  Menu inspired by James Stovold");
            System.out.println("==================================\r\n");
            System.out.println("1. Start with a single sensor");
            System.out.println("2. Start with multiple sensors");
            System.out.println("0. Exit");	

            System.out.print("Enter a number: ");

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

            if(selectedOption == 0) {break;}

            RobotController robot = new RobotController();
            System.out.println("Turning on robot...");

            switch (selectedOption) {
                case 1:
                    robot.workflow = new OneSensor();
                    break;
            
                case 2:
                    robot.workflow = new MultipleSensors();
                    break;
                    
                default:
                    System.out.println("Not recognized. Shutting off now.");
                    return;
            }

            System.out.println("Robot is: " + robot.workflow.name());
            robot.workflow.init();
            
            robot.workflow.go();
        }
    }
}
