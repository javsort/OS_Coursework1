package src.RobotParts;

import java.util.concurrent.atomic.AtomicInteger;
import src.RobotParts.OneSensor.UpgradedQueue;
import java.util.Random;  

// Expected output: Task
public class Sensor implements Runnable {
    private double minComplexity = 0.1;
    private double maxComplexity = 0.5;
    private double lambda;
    private int sensorId;
    private static AtomicInteger sensorID = new AtomicInteger();
    

    Task currentTask;
    private UpgradedQueue<Task> taskQueue;


    public Sensor(double lambda, UpgradedQueue<Task> taskQueue){
        sensorId = sensorID.getAndIncrement();
        this.lambda = lambda;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run(){
        System.out.println("Sensor started");
        Task.resetId();

        while(!Thread.currentThread().isInterrupted()){
            try {

                for(int i = 0; i < getPoissonNum(lambda); i++){

                    if(taskQueue.isFull()){
                        System.out.println("Sensor error: Task queue is full. Last task added {"+ currentTask.getId() +"}");
                        continue;
                    }

                    double complexity = newComplexity();
                    currentTask = new Task(complexity);
                    taskQueue.put(currentTask);

                }
                
                Thread.sleep(1000);

            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.println("Sensor error: No more tasks to be added. Last task added {"+ currentTask.getId() +"}");
            }
        }
        currentTask = new Task(newComplexity());
    }

    // Poisson method based on Donald E. Knuth's Algorithm, full reference in report
    public int getPoissonNum(double lambda){
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
            k++;
            p *= Math.random();
        } while (p > L);
        
        return k - 1;
    }

    public double newComplexity(){
        Random random = new Random();

        // Find new complexity between 0.1 and 0.5
        double c = minComplexity + (random.nextDouble() * (maxComplexity - minComplexity));

        return c;
    }

    public int getSensorId(){
        return sensorId;
    }
    
    public void resetSensorId(){
        sensorID.set(0);
    }

    public int getLastTaskSent(){
        return currentTask.getId();
    }
}
