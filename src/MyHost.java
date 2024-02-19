/* Implement this class. */

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class MyHost extends Host {
    // blocking priority queue that stores tasks based on their start time and priority
    BlockingQueue<Task> blockingQueue = new PriorityBlockingQueue<>(1, new Comparator<Task>() {
        @Override
        public int compare(Task o1, Task o2) {
            if (o1.getPriority() < o2.getPriority() || o1.getPriority() > o2.getPriority()) {
                return Integer.compare(o2.getPriority(), o1.getPriority());
            } else {
                return Integer.compare(o1.getStart(), o2.getStart());
            }
        }
    });

    // the task that's running at a certain point of time
    private Task currTask = null;

    // boolean variable to know if a host has shutdown yet
    private boolean go = true;

    // variable to store the point where the current task has begun
    private long beforeSleep = 0;

    // variable to store the point where the task stopped
    private long afterSleep = 0;

    @Override
    public void run() {
        // while the host hasn't shut down
        while (go) {
            // if the queue has elements
            if (!blockingQueue.isEmpty()) {
                // the first element of the queue is the task we have to run
                try {
                    currTask = blockingQueue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                // trying to stop the program for the amount of time the task has to last
                try {
                    beforeSleep = System.currentTimeMillis();
                    this.sleep(currTask.getLeft());

                    // if the program reaches this point
                    // the task finished sleeping without problems
                    // setting it to null to know there is no task running
                    currTask.finish();
                    currTask = null;

                // if we interrupt the thread sleeping
                } catch (InterruptedException e) {
                    // calculating the amount of time the task has until it will be finished
                    afterSleep = System.currentTimeMillis();
                    currTask.setLeft(currTask.getLeft() - afterSleep + beforeSleep);

                    // if it has 0 or less, it means it finished
                    if (currTask.getLeft() <= 0) {
                        currTask.finish();
                        currTask = null;
                    // if not, we add the task back to the queue
                    } else {
                        blockingQueue.add(currTask);
                    }
                }
            }
        }
    }

    @Override
    public void addTask(Task task) {
        // adding the task to the queue
        blockingQueue.add(task);

        // if the task we just added has a higher priority and the current task that
        // is running is preemptive, we interrupt the execution of the current task
        if (currTask != null && currTask.isPreemptible()
                && task.getPriority() > currTask.getPriority()) {
            this.interrupt();
        }
    }

    @Override
    public int getQueueSize() {
        // if there is no task running, we have the size of the queue
        if (currTask == null) {
            return blockingQueue.size();
        // if there is a task running we have to increase the size by 1
        // to consider the running task that has been taken out of the queue
        } else {
            return blockingQueue.size() + 1;
        }
    }

    @Override
    public long getWorkLeft() {
        // calculating the amount of time for the host
        long lwl = 0;

        // going through the queue to add the time of each task
        for (Task task : blockingQueue) {
            lwl += task.getLeft();
        }

        // if we have a task running we have to also add the time it has left
        if (currTask != null) {
            long timeSlept = currTask.getLeft() - System.currentTimeMillis() + beforeSleep;
            lwl += timeSlept;
        }

        return Math.round((float) lwl / 1000);
    }

    @Override
    public void shutdown() {
        // if the host is shutdown we alert the program with this variable
        go = false;
    }
}
