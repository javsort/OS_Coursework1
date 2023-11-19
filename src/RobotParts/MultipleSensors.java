package src.RobotParts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import src.Workflow;

/*
 * MultipleSensors.java.
 * This Class is in charge of handling the entire workflow whenever the user chooses to use multiple sensors
 * The class implements the Workflow interface and handles however many Sensors the user desires, an Analysis and an Actuator.
 * The class also handles the user input for the amount of sensors, lambda and position which will be later used by other classes.
 * The class also handles the creation, joining and interrupting the threads for the sensors, the analysis and the actuator.
 * In this class, the size of the queue is also defined, and the queues are created. Finally, all the threads are interrupted whenever
 * all the lastTask values are equal, meaning that the robot has stopped moving and finished creating, analysing and actuating all tasks.
 */
public class MultipleSensors implements Workflow {
    // Variables to be changed by user input
    public double lambda = 0;
    public double position = -1.0;
    public int sensorsAmount = 0;

    // Queue size definition
    public int queueSize = 1000;
    
    // Variables for user input
    BufferedReader dataBuffer = new BufferedReader(new InputStreamReader(System.in));
    String dataLine = "";

    // Analysis and Actuator for the program
    Analysis analysis;
    Actuator actuator;

    // Threads for the program
    Thread analysisThread;
    Thread actuatorThread;

    // ArrayLists for the sensors and their threads
    ArrayList<Sensor> sensors = new ArrayList<>();
    ArrayList<Thread> sensorsThreads = new ArrayList<>();

    // Queues for the program, taskQueue is for the sensors to produce tasks and resultsQueue is for the analysis to send the results to the actuator
    UpgradedQueue<Task> taskQueue = new UpgradedQueue<>(queueSize, "Task Queue");
    UpgradedQueue<Task> resultsQueue = new UpgradedQueue<>(queueSize, "Results Queue");
    
    // Class name, called by RobotController
    @Override
    public String name() {
        return "running with multiple sensors.";
    }

    // Class init, called by RobotController
    @Override
    public void init() {
        // Resetting the values of the sensors and the id of the Sensors
        enterValues();
        Sensor.resetId();

        // Creating the sensors and their threads
        for(int i = 0; i < sensorsAmount; i++){
            sensors.add(new Sensor(lambda, taskQueue));
            sensorsThreads.add(new Thread(sensors.get(i)));
        }

        // Creating the analysis and its thread
        analysis = new Analysis(taskQueue, resultsQueue);
        analysisThread = new Thread(analysis); 

        // Creating the actuator and its thread
        actuator = new Actuator(resultsQueue, position, 2);        
        actuatorThread = new Thread(actuator);
    }

    // Class go, called by RobotController
    @Override
    public void go() {
        // Starting the threads
        startSensors();
        analysisThread.start();
        actuatorThread.start();

        // Generate tasks for the defined amount of seconds
        try {Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Stopping the Sensors and joining them
        stopSensors();
        joinSensors();

        // Interrupting the threads when the robot has stopped moving (All tasks analysed and actuated)
        while(Sensor.lastTaskProduced != Actuator.lastTaskProcessed && Sensor.lastTaskProduced != Analysis.lastTaskSent){
            try {Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Interrupting the threads
        analysisThread.interrupt();
        actuatorThread.interrupt();

        // Joining the threads
        try {
            analysisThread.join();
            actuatorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Printing the final result
        System.out.println("\nRobot has stopped moving at: " + actuator.getPosition());        
    }

    // enterValues method, called by init
    public void enterValues(){
        // While loops to handle the user input for:

        // Sensors amount
        while(sensorsAmount <= 0){
            System.out.println("Please enter the desired amount of sensors: ");
            try {
                dataLine = dataBuffer.readLine();
                System.out.println();
                sensorsAmount = Integer.parseInt(dataLine);

                if(sensorsAmount <= 0) {
                    throw new NumberFormatException();
                }

                System.out.println("Sensors amount is: " + sensorsAmount);

            } catch (NumberFormatException e) {
                System.out.println("'" + dataLine + "' is not recognized, please input at least 1 sensor." );
                continue;

            } catch (IOException e) {
                System.out.println("IOException, quitting...");
            }
        }

        // Lambda
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

        // Position
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

    // startSensors method, called by go
    public void startSensors(){
        for(int i = 0; i < sensorsAmount; i++){
            sensorsThreads.get(i).start();
        }
    }

    // stopSensors method, called by go
    public void stopSensors(){
        for(int i = 0; i < sensorsAmount; i++){
            sensorsThreads.get(i).interrupt();
        }
    }

    // joinSensors method, called by go
    public void joinSensors(){
        for(int i = 0; i < sensorsAmount; i++){
            try {
                sensorsThreads.get(i).join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
