package src.RobotParts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import src.Workflow;

/*
 * OneSensor.java.
 * This Class is in charge of handling the entire workflow whenever the user chooses to use a single sensor
 * The class implements the Workflow interface and handles a Sensor, an Analysis and an Actuator.
 * The class also handles the user input for lambda and position which will be later used by other classes.
 * The class also handles the creation, joining and interrupting the threads for the sensor, the analysis and the actuator.
 * In this class, the size of the queue is also defined, and the queues are created. Finally, all the threads are interrupted whenever
 * all the lastTask values are equal, meaning that the robot has stopped moving and finished creating, analysing and actuating all tasks.
 */
public class OneSensor implements Workflow {
    // Variables to be changed by user input
    public double lambda = 0;
    public double position = -1.0;

    // Queue size definition
    public int queueSize = 1000;
    
    // Variables for user input
    BufferedReader dataBuffer = new BufferedReader(new InputStreamReader(System.in));
    String dataLine = "";

    // Sensor, Analysis and Actuator for the program with their respective threads
    Sensor sensor;
    Analysis analysis;
    Actuator actuator;

    Thread sensorThread;
    Thread analysisThread;
    Thread actuatorThread;

    // Queues for the program, taskQueue is for the sensor to produce tasks and resultsQueue is for the analysis to send the results to the actuator
    UpgradedQueue<Task> taskQueue = new UpgradedQueue<>(queueSize, "Task Queue");
    UpgradedQueue<Task> resultsQueue = new UpgradedQueue<>(queueSize, "Results Queue");
    
    // Class name, called by RobotController
    @Override
    public String name() {
        return "running with one sensor.";
    }

    // Class init, called by RobotController
    @Override
    public void init() {
        // Resetting the values of the sensor and the id of the Sensor
        enterValues();
        Sensor.resetId();
        
        // Creating the sensor, analysis and actuator with their respective threads
        sensor = new Sensor(lambda, taskQueue);
        analysis = new Analysis(taskQueue, resultsQueue);
        actuator = new Actuator(resultsQueue, position, 1);

        sensorThread = new Thread(sensor);
        analysisThread = new Thread(analysis); 
        actuatorThread = new Thread(actuator);
    }

    // Class go, called by RobotController
    @Override
    public void go(){
        // Starting the threads
        sensorThread.start();
        analysisThread.start();
        actuatorThread.start();

        // Let the sensor create tasks for 30 seconds
        try {Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Interrupting the Sensor
        sensorThread.interrupt();

        // Join the Sensor Thread
        try {
            sensorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Interrupting the threads when the robot has stopped moving (All tasks analysed and actuated)
        while(Sensor.lastTaskProduced != Actuator.lastTaskProcessed && Sensor.lastTaskProduced != Analysis.lastTaskSent){
            try {Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Interrupting the remaining threads
        analysisThread.interrupt();
        actuatorThread.interrupt();

        // Joining the remaining threads
        try {
            analysisThread.join();
            actuatorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Printing the final position of the robot
        System.out.println("\nRobot has stopped moving at: " + actuator.getPosition());
    }

    // Getter for lambda
    public double getLambda(){
        return lambda;
    }
    
    // enterValues method, called by init
    public void enterValues(){
        // While loop to handle the user input for lambda and position

        // lambda
        while(lambda <= 0){
            System.out.print("Please enter a value for lambda: ");
            try {
                dataLine = dataBuffer.readLine();
                System.out.println();
                lambda = Double.parseDouble(dataLine);

                if(lambda <= 0) {
                    throw new NumberFormatException();
                }

                System.out.println("Lambda is: " + lambda);

            } catch (NumberFormatException e) {
                System.out.println("'" + dataLine + "' is not recognized, please input a number." );
                continue;

            } catch (IOException e) {
                System.out.println("IOException, quitting...");
            }
        }

        // position
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
