package src.RobotParts;

public class Actuator implements Runnable {
    private double position;
    private double maxPosition = 1.0;
    private double minPosition = 0.0;

    public Actuator(){

    }

    public void run(){

    }

    public void Move(double Yresult){
        position += Yresult;

        while(position > maxPosition || position < minPosition){
    
            if (position > maxPosition){
                position = position - maxPosition;

            } else if (position < minPosition){
                position = position + maxPosition;
            }

        }
    }

    
}
