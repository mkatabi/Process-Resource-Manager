import javafx.util.Pair;
import java.util.LinkedList;

public class Resource {
    private int state;
    private int inventory;
    private LinkedList<Pair<Integer, Integer>> waitlist;

    public Resource(int newInventory) {
        state = newInventory;
        inventory = newInventory;
        waitlist = new LinkedList<>();
    }

    public int getState() {
        return state;
    }

    public int getInventory() {
        return inventory;
    }

    public LinkedList<Pair<Integer, Integer>> getWaitlist() {
        return waitlist;
    }

    public void setState(int newState) {
        state = newState;
    }

    public void setInventory(int newInventory) {
        inventory = newInventory;
    }

    public void setWaitlist(LinkedList<Pair<Integer, Integer>> newWaitlist) {
        waitlist = newWaitlist;
    }

    public void removeProcess(int process) {
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).getKey() == process) {
                waitlist.remove(i);
                break;
            }
        }
    }

    public void releaseResource(int resourceUnits) {
        state += resourceUnits;
    }

    public void insertIntoWaitlist(int processIndex, int numUnitsRequested) {
        waitlist.add(new Pair<>(processIndex, numUnitsRequested));
    }
}
