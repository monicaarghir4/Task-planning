/* Implement this class. */

import java.util.List;

public class MyDispatcher extends Dispatcher {
    // counter to keep track of the hosts indexes for RR algorithm
    private int idHost = 0;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    synchronized public void addTask(Task task) {
        switch(algorithm) {
            // adding the tasks based on the id of the hosts
            case ROUND_ROBIN:
                hosts.get(idHost).addTask(task);
                idHost = (idHost + 1) % hosts.size();
                break;

            // adding the tasks based on its type
            case SIZE_INTERVAL_TASK_ASSIGNMENT:
                switch (task.getType()) {
                    case SHORT -> hosts.get(0).addTask(task);
                    case MEDIUM -> hosts.get(1).addTask(task);
                    case LONG -> hosts.get(2).addTask(task);
                }
                break;

            // adding the tasks based on the queue of every host
            case SHORTEST_QUEUE:
                // assuming the first host is the one with the minimum queue size
                int minSize = hosts.get(0).getQueueSize();
                int minIndexSQ = 0;

                // going through the other hosts to update the index of
                // the host with the shortest queue
                for (int i = 1; i < hosts.size(); i++) {
                    int queueSize = hosts.get(i).getQueueSize();

                    if (minSize > queueSize) {
                        minSize = queueSize;
                        minIndexSQ = i;

                    // if the hosts have the same size of the queue, we make a decision
                    // based on the id of the hosts
                    } else if (minSize == queueSize) {
                        if (hosts.get(minIndexSQ).getId() > hosts.get(i).getId()) {
                            minIndexSQ = i;
                        }
                    }
                }

                // adding the task
                hosts.get(minIndexSQ).addTask(task);
                break;

            // similar mechanism as the previous one
            case LEAST_WORK_LEFT:
                long lwl = hosts.get(0).getWorkLeft();
                int minIndexLWL = 0;

                for (int i = 1; i < hosts.size(); i++) {

                    long wl = hosts.get(i).getWorkLeft();

                    if (lwl > wl) {
                        lwl = wl;
                        minIndexLWL = i;

                    } else if (lwl == wl) {
                        if (hosts.get(minIndexLWL).getId() > hosts.get(i).getId()) {
                            minIndexLWL = i;
                        }
                    }
                }

                hosts.get(minIndexLWL).addTask(task);
                break;
        }
    }
}
