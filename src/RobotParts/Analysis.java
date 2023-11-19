package src.RobotParts;

import java.lang.Math;
// Expected behaviour - receive Task as input and then produce analysis of Y
// Expected output: result
public class Analysis implements Runnable {
    Task currentTask;
    int lastTaskId = 0;

    private UpgradedQueue<Task> taskQueue;
    private UpgradedQueue<Task> resultsQueue;

    public Analysis(UpgradedQueue<Task> taskQueue, UpgradedQueue<Task> resultsQueue){
        this.taskQueue = taskQueue;
        this.resultsQueue = resultsQueue;
    }

    @Override
    public void run(){
        System.out.println("Analysis started");
        while(!Thread.currentThread().isInterrupted()){
            try {
                currentTask = taskQueue.take(getSectorName(), lastTaskId);
                
                currentTask.setYResult(analysisY(currentTask.getComplexity()));

                int sleepTime = (int) (currentTask.getComplexity() * 1000);
                Thread.sleep(sleepTime);

                lastTaskId = currentTask.getId();

                resultsQueue.put(currentTask, getSectorName(), lastTaskId);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Analysis error: no more tasks to analyse. Last task analysed {"+ lastTaskId +"}.");
            }
        }

        
    }
    
    public double analysisY(double c){
        double analysisY = Math.sqrt((1 / c));

        return analysisY;
    }

    public int getLastTaskSent(){
        return currentTask.getId();
    }

    public String getSectorName(){
        return "Analysis";
    }
}
