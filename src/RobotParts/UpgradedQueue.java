package src.RobotParts;

import java.util.LinkedList;
import java.util.Queue;

public class UpgradedQueue<T> {

    private Queue<Task> queue = new LinkedList<>();
    private int limit;
    private String queueName;

    public UpgradedQueue(int limit, String name){
        this.limit = limit;
        this.queueName = name;
    }

    public synchronized void put(Task t, String sectorName, int lastTask) throws InterruptedException {
        while(queue.size() == limit){
            System.out.println(sectorName + " error: "+ queueName + " is full. Last task added {"+ lastTask +"}");
            wait();
        }

        queue.add(t);
        notify();
    }

    public synchronized Task take(String sectorName, int lastTask) throws InterruptedException {
        while(queue.isEmpty()){
            System.out.println(sectorName + " error: " + queueName + " is empty. Last task processed {"+ lastTask +"}");
            wait();
        }
        Task requestedTask = queue.poll();
        notify();
        return requestedTask;
    }

    public synchronized void clear(){
        queue.clear();
    }

    public synchronized int size(){
        return queue.size();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

    public boolean isFull(){
        return queue.size() == limit;
    }
}