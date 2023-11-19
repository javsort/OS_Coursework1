package src.RobotParts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import src.Workflow;

public class MultipleSensors implements Workflow {
    public double lambda = 0;
    public double position = -1.0;
    public int sensorsAmount = 0;

    public int lastTask;
    
    BufferedReader dataBuffer = new BufferedReader(new InputStreamReader(System.in));
    String dataLine = "";

    Analysis analysis;
    Actuator actuator;

    Thread analysisThread;
    Thread actuatorThread;

    ArrayList<Sensor> sensors = new ArrayList<>();
    ArrayList<Thread> sensorsThreads = new ArrayList<>();

    UpgradedQueue<Task> taskQueue = new UpgradedQueue<>(100, "Task Queue");
    UpgradedQueue<Task> resultsQueue = new UpgradedQueue<>(100, "Results Queue");
    
    @Override
    public String name() {
        return "running with multiple sensors.";
    }

    @Override
    public void init() {
        enterValues();
        Sensor.resetId();

        for(int i = 0; i < sensorsAmount; i++){
            sensors.add(new Sensor(lambda, taskQueue));
            sensorsThreads.add(new Thread(sensors.get(i)));
        }

        analysis = new Analysis(taskQueue, resultsQueue);
        analysisThread = new Thread(analysis); 

        actuator = new Actuator(resultsQueue, position, 2);        
        actuatorThread = new Thread(actuator);
        
    }

    @Override
    public void go() {
        startSensors();
        analysisThread.start();
        actuatorThread.start();

        try {Thread.sleep(30000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        stopSensors();
        joinSensors();

        while(!taskQueue.isEmpty() || !resultsQueue.isEmpty()){
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

    public void enterValues(){

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

    public void startSensors(){
        for(int i = 0; i < sensorsAmount; i++){
            sensorsThreads.get(i).start();
        }
    }

    public void stopSensors(){
        for(int i = 0; i < sensorsAmount; i++){
            sensorsThreads.get(i).interrupt();
        }
    }

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
