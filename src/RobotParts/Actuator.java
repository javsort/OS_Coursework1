package src.RobotParts;
/*
    * Actuator.java. 
    * This class is in charge of handling the Actuator, the final part of the robot controller. This class implements the 
    * Runnable interface, so it can be executed as a thread. It has a reference to the UpgradedQueue object resultsQueue
    * that will be used to get the results from the Analysis. It is also in charge of changing the robot's position, which
    * is done by the Move() method. This method is called every time a new result is obtained from the Analysis and doesn't
    * stop until the robot has been set between minPosition (0.0) and maxPosition (1.0). When the movement is made, it prompts
    * the user with a response in terminal about the movement made along with the original movement value (Y) from the Analysis.
 */
public class Actuator implements Runnable {
    // Min and max position of the robot
    private double maxPosition = 1.0;
    private double minPosition = 0.0;

    // Save current and past position
    private double position;
    private double prevPosition;

    // Direction of the movement
    private double direction = 1;

    // Option selected by the user, determines the prompt message to be printed to terminal
    private int selectedOption;

    // Task being analyzed
    Task currentTask;

    // Last task id and last task processed
    static int lastTaskId;
    static int lastTaskProcessed;               // Used for terminating the threads

    // Queue to get the results from the Analysis
    UpgradedQueue<Task> resultsQueue;

    // Constructor
    public Actuator(UpgradedQueue<Task> resultsQueue, double position, int selectedOption){
        this.resultsQueue = resultsQueue;
        this.position = position;
        this.selectedOption = selectedOption;
    }
    
    // Run method, called when the thread is started
    @Override
    public void run(){
        // Reset lastTaskId
        lastTaskId = 0;

        // Reset lastTaskProcessed
        lastTaskProcessed = 0;

        System.out.println("Actuator started");

        // Loop to be followed while the thread is active
        while(!Thread.currentThread().isInterrupted()){
            try {
                // Get the next task to be processed
                currentTask = resultsQueue.take(getSectorName(), lastTaskId);

                // Move the robot
                Move(currentTask.getYResult());

                // Print the message to terminal depending on if the system has one sensor of multiple sensors
                switch (selectedOption) {
                    case 1:
                        System.out.println("Robot moving. Task id {" + currentTask.getId() + "}, result {" + currentTask.getYResult() + "}, old position: {" + prevPosition + "}, new position: {" + position + "}.");
                        lastTaskId = currentTask.getId();

                        // Assign last task processed everytime a higher task id is processed
                        if(currentTask.getId() > lastTaskProcessed){
                            lastTaskProcessed = currentTask.getId();
                        }
                        
                        break;

                    case 2:
                        System.out.println("Robot moving. Task id {" + currentTask.getId() + "}, from sensor {" + currentTask.getSensorId() + "}, result {" + currentTask.getYResult() + "}, old position: {" + prevPosition + "}, new position: {" + position + "}.");
                        lastTaskId = currentTask.getId();

                        // Assign last task processed everytime a higher task id is processed
                        if(currentTask.getId() > lastTaskProcessed){
                            lastTaskProcessed = currentTask.getId();
                        }
                        
                        break;
                    
                    default:
                        break;
                }
            
            } catch (InterruptedException e) {
                // The thread has been interrupted, print the last task processed and terminate the thread
                Thread.currentThread().interrupt();
                System.out.println("Actuator error: no more results to process. Last task processed {"+ lastTaskId +"}.");
            }
        }
    }

    // Move method, called everytime a new result is obtained from the Analysis
    public void Move(double Yresult){
        // Save the previous position
        prevPosition = position;

        // Move the robot
        position += Yresult * direction;

        // Move the robot as many times as necessary until the position is between minPosition and maxPosition
        while (position > maxPosition || position < minPosition) {

            // If bigger than 1.0, set the position to -1.0 and change the direction, then reassign position by multiplying the remaining distance by the direction
            if (position > maxPosition) {
                double remaining = (position - maxPosition) * direction;
                position = maxPosition;
                direction = -1.0;
                position += remaining * direction;

            // If smaller than 0.0, set the position to 1.0 and change the direction, then reassign position by multiplying the remaining distance by the direction
            } else if (position < minPosition) {
                double remaining = (-position) * direction;
                position = minPosition;
                direction = 1.0;
                position += remaining * direction;
            }
        }
    }

    // Getters
    public double getPosition(){
        return position;
    }

    public int getLastTaskSent(){
        return currentTask.getId();
    }

    public String getSectorName(){
        return "Actuator";
    }

}
