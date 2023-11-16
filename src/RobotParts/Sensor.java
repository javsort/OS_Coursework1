package src.RobotParts;

import java.util.concurrent.*;
import java.util.Queue;
import java.util.Random;  
// Expected output: Task

public class Sensor implements Runnable {
    private double minComplexity = 0.1;
    private double maxComplexity = 0.5;
    private double lambda;
    private long sleepTime;
    

    Task currentTask;
    private final BlockingQueue<Task> taskQueue;
    private Queue<Task> remainingQueue;


    public Sensor(double lambda, BlockingQueue<Task> taskQueue){
        this.lambda = lambda;
        this.taskQueue = taskQueue;
    }

    @Override
    public void run(){
        System.out.println("Sensor started");
        while(!Thread.currentThread().isInterrupted()){
            try {

                /*while(remainingQueue.size() != 0){
                    taskQueue.add(remainingQueue.poll());
                }*/

                //System.out.println ("The amount of tasks is: " + taskAmount);
                for(int i = 0; i < getPoissonNum(lambda); i++){

                    double complexity = newComplexity();
                    currentTask = new Task(complexity);
                    taskQueue.add(currentTask);

                }
                
                Thread.sleep(1000);

            } catch (InterruptedException e){
                System.out.println("Sensor error: No tasks added. Last task added {"+ currentTask.getId() +"}");
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
}
