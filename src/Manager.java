import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Stack;

public class Manager {
    private Process PCB[];
    private Resource RCB[];
    private ReadyList RL;
    private int currProcessIndex;

    public Manager() {
        init();
    }

    public int create(int priority) {
        if (priority < 0 || priority > 3 || getTotalProcesses() == PCB.length)
            return -1;

        // Finds current running process
        Process curProcess = PCB[currProcessIndex];
        // Finds first available block in PCB
        int indNewProcess = getFirstAvailableIndex();
        // Allocates new process with given priority and parent
        Process newProcess = new Process(currProcessIndex, priority);
        // Inserts newProcess into children of curProcess
        curProcess.addChild(indNewProcess);
        // Inserts newProcess into PCB
        PCB[indNewProcess] = newProcess;
        // Inserts newProcess into RL
        RL.insertProcess(priority, indNewProcess);
        // Displays that newProcess has been created
        // System.out.println("Process " + indNewProcess + " created");
        // Context switches to highest-level ready process
        scheduler();

        return 0;
    }

    public int destroy(int processIndex) {
        // Saves total number of processes in PCB prior to destroy call
        // int preNumProcess = getTotalProcesses();

        if (!PCB[currProcessIndex].getChildren().contains(Integer.valueOf(processIndex)))
            return -1;

        // Retrieves process' descendants
        Stack<Integer> children = getDescendants(processIndex, new Stack<>());

        // Destroys children of process
        for (Integer child : children)
            destroyProcess(child);

        // Destroys current running process
        destroyProcess(processIndex);

        // Context switches to highest-level ready process
        scheduler();

        return 0;
    }

    public int request(int resourceIndex, int numUnitsRequested) {
        if (currProcessIndex == 0 || resourceIndex < 0 || resourceIndex > 3 ||
                (PCB[currProcessIndex].numUnitsHeld(resourceIndex) + numUnitsRequested) > RCB[resourceIndex].getInventory())
            return -1;

        Resource resource = RCB[resourceIndex];
        Process process = PCB[currProcessIndex];

        if (resource.getState() >= numUnitsRequested && resource.getWaitlist().isEmpty()) {
            resource.setState(resource.getState() - numUnitsRequested);
            process.insertResource(resourceIndex, numUnitsRequested);
        }
        else {
            process.setState(0);
            RL.removeProcess(process.getPriority(), currProcessIndex);
            resource.insertIntoWaitlist(currProcessIndex, numUnitsRequested);
            scheduler();
        }

        return 0;
    }

    public int release(int resourceIndex, int numUnitsReleased) {
        if (!PCB[currProcessIndex].containsResource(resourceIndex) || PCB[currProcessIndex].numUnitsHeld(resourceIndex) < numUnitsReleased)
            return -1;

        PCB[currProcessIndex].removeResource(resourceIndex, numUnitsReleased);
        releaseResource(resourceIndex, numUnitsReleased);
        scheduler();

        return 0;
    }

    public void timeout(){
        Process curProcess = PCB[currProcessIndex];
        RL.removeProcess(curProcess.getPriority(), currProcessIndex);
        RL.insertProcess(curProcess.getPriority(), currProcessIndex);

        scheduler();
    }

    public void init() {
        PCB = new Process[16];
        for (int i = 0; i < PCB.length; i++) {
            if (i == 0)
                PCB[0] = new Process(0);
            else
                PCB[i] = null;
        }

        RCB = new Resource[4];
        RCB[0] = new Resource(1);
        RCB[1] = new Resource(1);
        RCB[2] = new Resource(2);
        RCB[3] = new Resource(3);

        RL = new ReadyList();
        RL.insertProcess(PCB[0].getPriority(), 0);

        currProcessIndex = 0;
    }

    public void scheduler() {
        LinkedList<Integer> levelThree = RL.getLevelThreePriority();
        for (int i = 0; i < levelThree.size(); i++) {
            int processIndex = levelThree.get(i);
            if (PCB[processIndex].getState() == 1) {
                currProcessIndex = processIndex;
                // System.out.println("Process " + currProcessIndex + " is currently running");
                return;
            }
        }

        LinkedList<Integer> levelTwo = RL.getLevelTwoPriority();
        for (int i = 0; i < levelTwo.size(); i++) {
            int processIndex = levelTwo.get(i);
            if (PCB[processIndex].getState() == 1) {
                currProcessIndex = processIndex;
                // System.out.println("Process " + currProcessIndex + " is currently running");
                return;
            }
        }

        LinkedList<Integer> levelOne = RL.getLevelOnePriority();
        for (int i = 0; i < levelOne.size(); i++) {
            int processIndex = levelOne.get(i);
            if (PCB[processIndex].getState() == 1) {
                currProcessIndex = processIndex;
                // System.out.println("Process " + currProcessIndex + " is currently running");
                return;
            }
        }
    }

    public int getCurrProcessIndex() {
        return currProcessIndex;
    }

    public int getTotalProcesses() {
        int total = 0;
        for (int i = 0; i < PCB.length; i++) {
            if (PCB[i] != null)
                total++;
        }
        return total;
    }

    private Stack<Integer> getDescendants(int index, Stack<Integer> descendants) {
        LinkedList<Integer> children = PCB[index].getChildren();

        for (int i = 0; i < children.size(); i++) {
            int childIndex = children.get(i);
            if (PCB[childIndex] != null)
                getDescendants(childIndex, descendants);

            descendants.push(Integer.valueOf(childIndex));
        }

        return descendants;
    }

    private void destroyProcess(int processIndex) {
        // Retrieves to-be deleted process
        Process process = PCB[processIndex];

        // Removes process from parent's list of children
        Process parent = PCB[process.getParent()];
        parent.removeChild(processIndex);

        // Gets state of process
        int processState = process.getState();
        // Removes process from RL
        if (processState == 1)
            RL.removeProcess(process.getPriority(), processIndex);
        else {
            // Removes process from waitlist of all resources requested
            for (int i = 0; i < RCB.length; i++) {
                Resource resource = RCB[i];
                resource.removeProcess(processIndex);
            }
        }

        // Releases all resources of process
        LinkedList<Pair<Integer, Integer>> resources = process.getResources();
        for (int i = 0; i < resources.size(); i++)
            releaseResource(resources.get(i).getKey(), resources.get(i).getValue());

        // Frees PCB of process
        PCB[processIndex] = null;
    }

    private void releaseResource(int resourceIndex, int numUnitsReleased) {
        Resource resource = RCB[resourceIndex];
        resource.setState(resource.getState() + numUnitsReleased);

        while (!resource.getWaitlist().isEmpty() && resource.getState() > 0) {
            Pair<Integer, Integer> waitingProcess = resource.getWaitlist().pop();
            if (resource.getState() >= waitingProcess.getValue()) {
                resource.setState(resource.getState() - waitingProcess.getValue());
                int waitingProcessIndex = waitingProcess.getKey();
                PCB[waitingProcessIndex].insertResource(resourceIndex, waitingProcess.getValue());
                PCB[waitingProcessIndex].setState(1);
                resource.removeProcess(waitingProcessIndex);
                RL.insertProcess(PCB[waitingProcessIndex].getPriority(), waitingProcessIndex);
            }
            else
                break;
        }
    }

    private int getFirstAvailableIndex() {
        for (int i = 0; i < PCB.length; i++) {
            if (PCB[i] == null)
                return i;
        }
        return 0;
    }
}
