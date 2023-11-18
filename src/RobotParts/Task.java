package src.RobotParts;

import java.util.concurrent.atomic.AtomicInteger;

public class Task {
    private int id;
    private double complexity;
    private double YResult;

    private static AtomicInteger newId = new AtomicInteger();

    public Task(double complexity){
        this.id = newId.getAndIncrement();
        this.complexity = complexity;
        this.YResult = 0;               // To be changed after passing through analysis
    }

    public int getId(){
        return id;
    }

    public double getComplexity(){
        return complexity;
    }

    public void setComplexity(double c){
        complexity = c;
    }

    public void setYResult(double Y){
        YResult = Y;
    }

    public double getYResult(){
        return YResult;
    }

    public static void resetId(){
        newId.set(0);
    }
}
