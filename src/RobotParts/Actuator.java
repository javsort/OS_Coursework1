package src.RobotParts;

public class Actuator implements Runnable {
    private double prevPosition;
    private double maxPosition = 1.0;
    private double minPosition = 0.0;
    private double position;
    private double direction = 1;

    private int selectedOption;

    Task currentTask;
    static int lastTaskId;
    static int lastTaskProcessed;

    UpgradedQueue<Task> resultsQueue;

    public Actuator(UpgradedQueue<Task> resultsQueue, double position, int selectedOption){
        this.resultsQueue = resultsQueue;
        this.position = position;
        this.selectedOption = selectedOption;
    }
    
    @Override
    public void run(){
        lastTaskId = 0;
        System.out.println("Actuator started");
        while(!Thread.currentThread().isInterrupted()){
            try {
                currentTask = resultsQueue.take(getSectorName(), lastTaskId);

                Move(currentTask.getYResult());

                switch (selectedOption) {
                    case 1:
                        System.out.println("Robot moving. Task id {" + currentTask.getId() + "}, result {" + currentTask.getYResult() + "}, old position: {" + prevPosition + "}, new position: {" + position + "}.");
                        lastTaskId = currentTask.getId();
                        if(currentTask.getId() > lastTaskProcessed){
                            lastTaskProcessed = currentTask.getId();
                        }
                        
                        break;

                    case 2:
                        System.out.println("Robot moving. Task id {" + currentTask.getId() + "}, from sensor {" + currentTask.getSensorId() + "}, result {" + currentTask.getYResult() + "}, old position: {" + prevPosition + "}, new position: {" + position + "}.");
                        lastTaskId = currentTask.getId();
                        if(currentTask.getId() > lastTaskProcessed){
                            lastTaskProcessed = currentTask.getId();
                        }
                        
                        break;
                    
                    default:
                        break;
                }
            
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Actuator error: no more results to process. Last task processed {"+ lastTaskId +"}.");
            }
        }
    }

    public void Move(double Yresult){
        prevPosition = position;

        position += Yresult * direction;

        // Check if the position is out of bounds
        while (position > maxPosition || position < minPosition) {
            if (position > maxPosition) {
                double remaining = (position - maxPosition) * direction;
                position = maxPosition;
                direction = -1.0;
                position += remaining * direction;

            } else if (position < minPosition) {
                double remaining = (-position) * direction;
                position = minPosition;
                direction = 1.0;
                position += remaining * direction;
            }
        }
    }

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
