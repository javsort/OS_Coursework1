package src.RobotParts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import src.Workflow;

public class OneSensor implements Workflow {
    public double lambda = 0;
    public double position = -1.0;

    public int lastTask;
    
    BufferedReader dataBuffer = new BufferedReader(new InputStreamReader(System.in));
    String dataLine = "";

    Sensor sensor;
    Analysis analysis;
    Actuator actuator;

    Thread sensorThread;
    Thread analysisThread;
    Thread actuatorThread;

    UpgradedQueue<Task> taskQueue = new UpgradedQueue<>(100, "Task Queue");
    UpgradedQueue<Task> resultsQueue = new UpgradedQueue<>(100, "Results Queue");
    
    @Override
    public String name() {
        return "running with one sensor.";
    }

    @Override
    public void init() {
        enterValues();
        Sensor.resetId();
        
        sensor = new Sensor(lambda, taskQueue);
        analysis = new Analysis(taskQueue, resultsQueue);
        actuator = new Actuator(resultsQueue, position, 1);

        sensorThread = new Thread(sensor);
        analysisThread = new Thread(analysis); 
        actuatorThread = new Thread(actuator);
    }

    @Override
    public void go(){
        sensorThread.start();
        analysisThread.start();
        actuatorThread.start();

        try {Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        sensorThread.interrupt();

        try {
            sensorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        while(Sensor.lastTaskId != Actuator.lastTaskId){
            try {Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        analysisThread.interrupt();
        actuatorThread.interrupt();

        try {
            analysisThread.join();
            actuatorThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nRobot has stopped moving at: " + actuator.getPosition());

    }

    public double getLambda(){
        return lambda;
    }
    
    public void enterValues(){

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
