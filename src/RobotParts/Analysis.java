package src.RobotParts;

import java.lang.Math;
// Expected behaviour - receive Task as input and then produce analysis of Y
// Expected output: result
public class Analysis implements Runnable {
    Task currentTask;

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
                currentTask = taskQueue.take();
                
                currentTask.setYResult(analysisY(currentTask.getComplexity()));

                int sleepTime = (int) (currentTask.getComplexity() * 1000);
                Thread.sleep(sleepTime);

                resultsQueue.put(currentTask);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Analysis error: no tasks to analyse. Last task analysed {"+ currentTask.getId() +"} ");
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
}
