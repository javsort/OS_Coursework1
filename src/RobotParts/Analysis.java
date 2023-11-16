package src.RobotParts;

import java.lang.Math;
import java.util.concurrent.BlockingQueue;
// Expected behaviour - receive Task as input and then produce analysis of Y
// Expected output: result
public class Analysis implements Runnable {


    Task currentTask;
    private final BlockingQueue<Task> taskQueue;
    private final BlockingQueue<Task> resultsQueue;

    public Analysis(BlockingQueue<Task> taskQueue, BlockingQueue<Task> resultsQueue){
        this.taskQueue = taskQueue;
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run(){
        System.out.println("Analysis started");
        while(!Thread.currentThread().isInterrupted()){
            try {
                currentTask = taskQueue.take();
                
                currentTask.setYResult(analysisY(currentTask.getComplexity()));

                int sleepTime = (int) (currentTask.getComplexity() * 1000);
                Thread.sleep(sleepTime);

                resultsQueue.add(currentTask);

            } catch (InterruptedException e) {
                System.out.println("Analysis error: no tasks to analyse. Last task analysed {"+ currentTask.getId() +"} ");
            }
        }

        
    }
    
    public double analysisY(double c){
        double analysisY = Math.sqrt((1 / c));

        return analysisY;
    }
}
