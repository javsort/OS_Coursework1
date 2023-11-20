package src.RobotParts;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;  

/*
    * Sensor.java.
    * This class is in charge of handling the Sensor, the first part of the robot controller. This class implements the
    * Runnable interface, so it can be executed as a thread. It has a reference to the UpgradedQueue object taskQueue
    * that will be used to send the created tasks to the Analysis. It is also in charge of creating the tasks, which is defined by
    * the getPoissonNum() method. This method is called every second and doesn't stop until the thread is interrupted. When the#
    * thread is interrupted, it prints a message to terminal that all tasks have been generated and stops the thread.
 */
public class Sensor implements Runnable {
    // Min and max complexity of the tasks
    private double minComplexity = 0.1;
    private double maxComplexity = 0.5;

    // Lambda value for the Poisson method
    private double lambda;

    // Sensor id for whenever the system is for multiple sensors
    private int sensorId;

    //
    private static AtomicInteger sensorID = new AtomicInteger();
    

    // Task being created
    Task currentTask;

    // Last task id and last task produced
    static int lastTaskId;
    static int lastTaskProduced;           // Used for terminating the thread

    // Queue to send the tasks to the Analysis
    private UpgradedQueue<Task> taskQueue;

    // Constructor
    public Sensor(double lambda, UpgradedQueue<Task> taskQueue){
        sensorId = sensorID.getAndIncrement();
        this.lambda = lambda;
        this.taskQueue = taskQueue;
    }

    // Run method, called when the thread is started
    @Override
    public void run(){
        // Print the sensor that has started
        System.out.println("Sensor "+ sensorId +" started");

        // Reset the id for tasks, lastTaskProduced and lastTaskId
        Task.resetId();
        lastTaskId = 0;
        lastTaskProduced = 0;

        // Loop to be followed while the thread is active
        while(!Thread.currentThread().isInterrupted()){
            try {
                // Create as many tasks as the Poisson method returns
                for(int i = 0; i < getPoissonNum(lambda); i++){
                    // Create a new task, its complexity, set the sensor id it belongs to, and send it to the Analysis
                    double complexity = newComplexity();
                    currentTask = new Task(complexity);
                    currentTask.setSensorId(sensorId);

                    taskQueue.put(currentTask, getSectorName(), lastTaskId);

                    // Whenever a larger task has been created, update lastTaskProduced
                    if(currentTask.getId() > lastTaskProduced){
                        lastTaskProduced = currentTask.getId();
                    }

                    // set the last task id
                    lastTaskId = currentTask.getId();
                }
                // Generate x number of tasks every second
                Thread.sleep(1000);

            } catch (InterruptedException e){
                // The thread has been interrupted, print the last task created and terminate the thread
                Thread.currentThread().interrupt();
                System.out.println("Sensor "+ sensorId + " error: No more tasks to be added. Last task added {"+ lastTaskProduced +"}.");
            }
        }
    }

    // Poisson method based on Donald E. Knuth's Algorithm, full reference and explanation in report
    public int getPoissonNum(double lambda){
        double end = Math.exp(-lambda);
        double modifier = 1.0;
        int events = 0;

        do {
            events++;
            modifier *= Math.random();
        } while (modifier > end);
        
        return events - 1;
    }

    // Method to generate a new complexity for the task
    public double newComplexity(){
        Random random = new Random();

        // Find new complexity between 0.1 and 0.5
        double c = minComplexity + (random.nextDouble() * (maxComplexity - minComplexity));

        return c;
    }

    // Get the sensorId
    public int getSensorId(){
        return sensorId;
    }
    
    // Reset the sensorIds
    public static void resetId(){
        sensorID.set(0);
    }

    // Get the last task sent
    public int getLastTaskSent(){
        return lastTaskId;
    }
    
    // Get the sector name
    public String getSectorName(){
        return "Sensor "+ sensorId;
    }
}
