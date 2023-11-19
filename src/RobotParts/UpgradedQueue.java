package src.RobotParts;

import java.util.LinkedList;
import java.util.Queue;

/*
    * UpgradedQueue.java.
    * This class is where the tasks are stored, and it is a thread-safe queue, which means that it can be accessed by multiple threads
    * without any problems. It is also a FIFO queue, which means that the first task to be added is the first one to be processed.
    * The queue has a limit of tasks that can be added, and it is defined by the user. If the queue is full, the thread that is trying
    * to add a task to the queue will be put to sleep until there is space in the queue. The same happens if the queue is empty and a
    * thread is trying to take a task from the queue. 
    * TThere should be two instances of this class, one for the tasks that are waiting to be analysed, and another one for the tasks
    * that are waiting to be actuated.
 */
public class UpgradedQueue<T> {
    // UpgradedQueue properties
    private Queue<Task> queue = new LinkedList<>();
    private int limit;
    private String queueName;

    // UpgradedQueue constructor
    public UpgradedQueue(int limit, String name){
        this.limit = limit;
        this.queueName = name;
    }

    // UpgradedQueue put and take methods
    public synchronized void put(Task t, String sectorName, int lastTask) throws InterruptedException {
        // If the queue is full, the thread is put to sleep until there is space in the queue and a message is prompted with the sector that wanted to access to it
        while(queue.size() == limit){
            System.out.println(sectorName + " error: "+ queueName + " is full. Last task added {"+ lastTask +"}.");
            wait();
        }

        // If the queue is not full, the task is added to the queue and the thread is notified
        queue.add(t);
        notify();
    }

    public synchronized Task take(String sectorName, int lastTask) throws InterruptedException {
        // If the queue is empty, the thread is put to sleep until there is something in the queue and a message is prompted with the sector that wanted to access to it
        while(queue.isEmpty()){
            System.out.println(sectorName + " error: " + queueName + " is empty. Last task processed {"+ lastTask +"}.");
            wait();
        }

        // If the queue has a value to be taken, it pulls it out of the queue, and the thread is notified
        Task requestedTask = queue.poll();
        notify();
        return requestedTask;
    }

    // UpgradedQueue other methods
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