package src.RobotParts;

import java.lang.Math;
// Expected behaviour - receive Task as input and then produce analysis of Y
// Expected output: result
/*
 * Analysis.java. 
 * This class is in charge of handling the Analysis, the second part of the robot controller. This class implements the
 * Runnable interface, so it can be executed as a thread. It has a reference to the UpgradedQueue object taskQueue and resultsQueue
 * that will be used to get the tasks from the Sensors and send the results to the Actuator. It is also in charge of calculating
 * the result of Y, which is given by analysisY(). This method is called every time a new task is obtained from the Sensors and
 * doesn't stop until the task has been processed. When the analysis is made, it sleeps for the Task's complexity value and then
 * continues analysing the next task.
 */
public class Analysis implements Runnable {
    // Task being analysed
    Task currentTask;

    // Last task id and last task processed
    int lastTaskId;
    static int lastTaskSent;            // Used for terminating the threads

    // Queue to get the tasks from the Sensors and another one to send the results to the Actuator
    private UpgradedQueue<Task> taskQueue;
    private UpgradedQueue<Task> resultsQueue;

    // Constructor
    public Analysis(UpgradedQueue<Task> taskQueue, UpgradedQueue<Task> resultsQueue){
        this.taskQueue = taskQueue;
        this.resultsQueue = resultsQueue;
    }

    // Run method, called when the thread is started
    @Override
    public void run(){
        // Reset both lastTaskId and lastTaskSent
        lastTaskId = 0;
        lastTaskSent = 0;

        System.out.println("Analysis started");

        // Loop to be followed while the thread is active
        while(!Thread.currentThread().isInterrupted()){
            try {
                // Get the next task to be processed
                currentTask = taskQueue.take(getSectorName(), lastTaskId);
                
                // Perform the method to calculate the result of Y
                currentTask.setYResult(analysisY(currentTask.getComplexity()));

                // Sleep for the time given by the complexity of the task
                int sleepTime = (int) (currentTask.getComplexity() * 1000);
                Thread.sleep(sleepTime);

                // Set the new lastTaskId
                lastTaskId = currentTask.getId();

                // Find the biggest task id value
                if(currentTask.getId() > lastTaskSent){
                    lastTaskSent = currentTask.getId();
                }

                // Send the result to the Actuator
                resultsQueue.put(currentTask, getSectorName(), lastTaskId);

            } catch (InterruptedException e) {
                // The thread has been interrupted, print the last task processed and terminate the thread
                Thread.currentThread().interrupt();
                System.out.println("Analysis error: no more tasks to analyse. Last task analysed {"+ lastTaskId +"}.");
            }
        }

        
    }
    
    // Method to calculate the result of Y
    public double analysisY(double c){
        double analysisY = Math.sqrt((1 / c));

        return analysisY;
    }

    // Getters
    public int getLastTaskSent(){
        return currentTask.getId();
    }

    public String getSectorName(){
        return "Analysis";
    }
}
