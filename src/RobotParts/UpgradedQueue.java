package src.RobotParts;

import java.util.LinkedList;
import java.util.Queue;

public class UpgradedQueue<T> {

    private Queue<Task> queue = new LinkedList<>();
    private int limit;

    public UpgradedQueue(int limit){
        this.limit = limit;
    }

    public synchronized void put(Task t) throws InterruptedException {
        while(queue.size() == limit){
            System.out.println("Queue is full!!!");
            wait();
        }

        queue.add(t);
        notify();
    }

    public synchronized Task take() throws InterruptedException {
        while(queue.isEmpty()){
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
