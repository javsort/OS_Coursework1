package src.RobotParts;

import java.util.concurrent.atomic.AtomicInteger;

/*
    * Task.java.
    * This class is the object the entire system will be creating, analysing and actuating through the robot.
    * A feature of this class, is that every Task instance created is unique, all thanks to the fact that the ID
    * is provided by the AtomicInteger class, which provides a thread-safe way to generate unique IDs.
    * The complexity of the task is also provided by the constructor, and it is a random number between 0.1 and 0.5 given
    * by the Sensor class.
    * Tasks are eventually turned into results, which is the Y value of the task, and it is also provided by the Analysis class
    * after the task has been analysed and then is sent to the actuator to perform the respective movement according to Y.
 */
public class Task {
    // Task properties
    private int id;
    private double complexity;
    private double YResult;
    private int sensorId;

    private static AtomicInteger newId = new AtomicInteger();

    // Constructor
    public Task(double complexity){
        this.id = newId.getAndIncrement();
        this.complexity = complexity;
        this.YResult = 0;               // To be changed after passing through analysis
        this.sensorId = 1;
    }

    // Getters and setters
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

    public void setSensorId(int id){
        sensorId = id;
    }

    public int getSensorId(){
        return sensorId;
    }
}
