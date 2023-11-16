package src.RobotParts;

import java.util.concurrent.BlockingQueue;

public class Actuator implements Runnable {
    private double prevPosition;
    private double maxPosition = 1.0;
    private double minPosition = 0.0;
    private double position;

    Task currentTask;
    private final BlockingQueue<Task> resultsQueue;

    public Actuator(BlockingQueue<Task> resultsQueue, double position){
        this.resultsQueue = resultsQueue;
        this.position = position;
    }
    
    @Override
    public void run(){
        System.out.println("Actuator started");
        while(!Thread.currentThread().isInterrupted()){
            try {
                currentTask = resultsQueue.take();

                Move(currentTask.getYResult());

                System.out.println("Robot moving. Task id {" + currentTask.getId() + "}, result {" + currentTask.getYResult() + "}, old position: {" + prevPosition + "}, new position: {" + position + "}.");
            } catch (InterruptedException e) {
                System.out.println("Actuate error: no results to process. Last task processed {"+ currentTask.getId() +"}");
            }
        }
    }

    public void Move(double Yresult){
        prevPosition = position;

        position += Yresult;

        while(position > maxPosition || position < minPosition){
    
            if (position > maxPosition){
                position = position - maxPosition;

            } else if (position < minPosition){
                position = position + maxPosition;
            }

        }
    }

    public double getPosition(){
        return position;
    }
}
