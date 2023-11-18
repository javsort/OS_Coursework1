package src.RobotParts;

public class Actuator implements Runnable {
    private double prevPosition;
    private double maxPosition = 1.0;
    private double minPosition = 0.0;
    private double position;
    private double direction = 1;

    private int selectedOption;

    Task currentTask;
    UpgradedQueue<Task> resultsQueue;

    public Actuator(UpgradedQueue<Task> resultsQueue, double position, int selectedOption){
        this.resultsQueue = resultsQueue;
        this.position = position;
        this.selectedOption = selectedOption;
    }
    
    @Override
    public void run(){
        System.out.println("Actuator started");
        while(!Thread.currentThread().isInterrupted()){
            try {
                currentTask = resultsQueue.take();

                Move(currentTask.getYResult());

                switch (selectedOption) {
                    case 1:
                        System.out.println("Robot moving. Task id {" + currentTask.getId() + "}, result {" + currentTask.getYResult() + "}, old position: {" + prevPosition + "}, new position: {" + position + "}.");
                        break;

                    case 2:
                        System.out.println("Robot moving. Task id {" + currentTask.getId() + "}, from sensor {" + currentTask.getSensorId() + "}, result {" + currentTask.getYResult() + "}, old position: {" + prevPosition + "}, new position: {" + position + "}.");
                        break;
                    
                    default:
                        break;
                }
            
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Actuate error: no results to process. Last task processed {"+ currentTask.getId() +"}");
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
}
