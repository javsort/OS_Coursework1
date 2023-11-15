package src.RobotParts;

import java.util.Random;  
// Expected output: Task

public class Sensor implements Runnable {
    public int id;
    private double lambda = 2;
    private double complexity; 

    private double minComplexity = 0.1;
    private double maxComplexity = 0.5;

    public Sensor(double lam){
        id = ID;
        lambda = lam;
        complexity = 0;


    }

    public void run(){

    }

    public double getPoissonNum(double lambda){
        // Random object to find k number of tasks
        Random r = new Random();
        int k = r.nextInt();
    
        // Set numerator and denominator
        double numerator = Math.pow(lambda, k) * Math.exp(-lambda);
        double denominator = factorial(k);

        // Make final division
        double poissonValue = numerator / denominator;
        
        return poissonValue;
    }

    private double factorial(double x){
        if (x == 0){
            return 1;
        } else {
            return x * factorial(x - 1);
        }
    }

    public double newComplexity(){
        Random random = new Random();

        // Find new complexity between 0.1 and 0.5
        double c = minComplexity + (random.nextDouble() * (maxComplexity - minComplexity));

        return c;
    }
}
