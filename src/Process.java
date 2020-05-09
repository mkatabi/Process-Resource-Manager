import javafx.util.Pair;

import java.util.Arrays;
import java.util.LinkedList;

public class Process {
    private int state;
    private int parent;
    private int priority;
    private LinkedList<Integer> children;
    private LinkedList<Pair<Integer, Integer>> resources;

    public Process(int newPriority) {
        state = 1;
        priority = newPriority;
        children = new LinkedList<>();
        resources = new LinkedList<>();
    }

    public Process(int newParent, int newPriority) {
        state = 1;
        parent = newParent;
        priority = newPriority;
        children = new LinkedList<>();
        resources = new LinkedList<>();
    }

    public int getState() {
        return state;
    }

    public int getParent() {
        return parent;
    }

    public int getPriority() {
        return priority;
    }

    public LinkedList<Integer> getChildren() {
        return children;
    }

    public LinkedList<Pair<Integer, Integer>> getResources() {
        return resources;
    }

    public void setState(int newState) {
        state = newState;
    }

    public void setParent(int newParent) {
        parent = newParent;
    }

    public void setPriority(int newPriority) {
        priority = newPriority;
    }

    public void setChildren(LinkedList<Integer> newChildren) {
        children = newChildren;
    }

    public void setResources(LinkedList<Pair<Integer, Integer>> newResources) {
        resources = newResources;
    }

    public void addChild(int child) {
        children.add(child);
    }

    public void removeChild(int child) {
        children.removeAll(Arrays.asList(child));
    }

    public void insertResource(int resourceIndex, int numUnitsRequested) {
        resources.add(new Pair<>(resourceIndex, numUnitsRequested));
    }

    public void removeResource(int resourceIndex, int numUnitsReleased) {
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getKey() == resourceIndex) {
                int units = resources.get(i).getValue();
                if (units - numUnitsReleased == 0)
                    resources.remove(i);
                else
                    resources.set(i, new Pair<>(resourceIndex, units - numUnitsReleased));
            }
        }
    }

    public boolean containsResource(int resourceIndex) {
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getKey() == resourceIndex)
                return true;
        }

        return false;
    }

    public int numUnitsHeld(int resourceIndex) {
        int totalUnitsHeld = 0;

        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getKey() == resourceIndex)
                totalUnitsHeld += resources.get(i).getValue();
        }

        return totalUnitsHeld;
    }
}
