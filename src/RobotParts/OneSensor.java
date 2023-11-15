package src.RobotParts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import src.Workflow;

public class OneSensor implements Workflow {
    public double lambda = 0;
    public double position = -1.0;
    BufferedReader dataBuffer = new BufferedReader(new InputStreamReader(System.in));
    String dataLine = "";
    
    public String name() {
        return ("running with one sensor.");
    }

    public void init() {

    }

    public void go(){
        enterValues();
    }
    
    public void enterValues(){
        boolean lambdaset = false;

        while(!lambdaset){
            System.out.print("Please enter a value for lambda: ");
            try {
                dataLine = dataBuffer.readLine();
                System.out.println();
                lambda = Double.parseDouble(dataLine);
                System.out.println("Lambda is: " + lambda);
                lambdaset = true;

            } catch (NumberFormatException e) {
                System.out.println("'" + dataLine + "' is not recognized, please input a number." );
                continue;

            } catch (IOException e) {
                System.out.println("IOException, quitting...");
            }
        }

        while(position < 0.0 || position > 1.0) {
            System.out.print("Please enter a value for position: ");
            try {
                dataLine = dataBuffer.readLine();
                System.out.println();
                position = Double.parseDouble(dataLine);

                if(position < 0.0 || position > 1.0) {
                    throw new NumberFormatException();
                }

                System.out.println("Position is: " + position);

            } catch (NumberFormatException e) {
                System.out.println("'" + dataLine + "' is not recognized, please input a number between 0.0 and 1.0." );
                continue;

            } catch (IOException e) {
                System.out.println("IOException, quitting...");
            }
        }
    }
}
