package src.RobotParts;

import java.lang.Math;
// Expected behaviour - receive Task as input and then produce analysis of Y
// Expected output: result
public class Analysis implements Runnable {
    private double analysisY;
    private String id;
    private int complexity;

    public Analysis(String ID, int compl){
        id = ID;
        complexity = compl;
        analysisY = 0;

    }

    public void run(){
        analysisY();
        sendResult();
    }
    

    private void analysisY(){
        analysisY = Math.sqrt((1 / complexity));
    }

    public void sendResult(){
        Actuator result = new Actuator(id,  complexity, analysisY);
    }

    protected class Result {
        private String id;
        private int complexity;
        private double analysisY;

        public Result(String ID, int compl, double aY){
            id = ID;
            complexity = compl;
            analysisY = aY;
        }
    }
}
