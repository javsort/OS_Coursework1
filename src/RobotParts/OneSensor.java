package src.RobotParts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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

    UpgradedQueue<Task> taskQueue = new UpgradedQueue<>(1000);
    UpgradedQueue<Task> resultsQueue = new UpgradedQueue<>(1000);
    
    public String name() {
        return ("running with one sensor.");
    }

    public void init() {
        enterValues();
        sensor = new Sensor(lambda, taskQueue);
        analysis = new Analysis(taskQueue, resultsQueue);
        actuator = new Actuator(resultsQueue, position);

        sensorThread = new Thread(sensor);
        analysisThread = new Thread(analysis); 
        actuatorThread = new Thread(actuator);
    }

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

        /*while(true){
            try {Thread.sleep(1000);} catch (InterruptedException e) {}
            if(lastTask == actuator.getLastTaskSent()){
                analysisThread.interrupt();
                actuatorThread.interrupt();
                break;
            }
        }*/

        System.out.println("\nRobot has stopped moving at: " + actuator.getPosition());

    }

    public double getLambda(){
        return lambda;
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

    public class UpgradedQueue<Task> {
        private Queue<Task> queue = new LinkedList<>();
        private int limit;

        public UpgradedQueue(int limit){
            this.limit = limit;
        }

        public synchronized void put(Task t) throws InterruptedException {
            while(queue.size() == limit){
                System.out.println("Queue is full!!!");
                wait();
            }

            queue.add(t);
            notify();
        }

        public synchronized Task take() throws InterruptedException {
            while(queue.isEmpty()){
                wait();
            }
            Task requestedTask = queue.poll();
            notify();
            return requestedTask;
        }

        public synchronized void clear(){
            queue.clear();
        }

        public synchronized int size(){
            return queue.size();
        }

        public boolean isEmpty(){
            return queue.isEmpty();
        }

        public boolean isFull(){
            return queue.size() == limit;
        }

    }
}
