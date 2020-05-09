import java.util.Arrays;
import java.util.LinkedList;

public class ReadyList {
    private LinkedList<Integer> levelOnePriority;
    private LinkedList<Integer> levelTwoPriority;
    private LinkedList<Integer> levelThreePriority;

    public ReadyList() {
        levelOnePriority = new LinkedList<>();
        levelTwoPriority = new LinkedList<>();
        levelThreePriority = new LinkedList<>();
    }

    public LinkedList<Integer> getLevelOnePriority() {
        return levelOnePriority;
    }

    public LinkedList<Integer> getLevelTwoPriority() {
        return levelTwoPriority;
    }

    public LinkedList<Integer> getLevelThreePriority() {
        return levelThreePriority;
    }

    public void insertProcess(int priority, int process) {
        if (priority == 0)
            levelOnePriority.add(process);
        else if (priority == 1)
            levelTwoPriority.add(process);
        else if (priority == 2)
            levelThreePriority.add(process);
    }

    public void removeProcess(int priority, int process) {
        if (priority == 0)
            levelOnePriority.removeAll(Arrays.asList(process));
        else if (priority == 1)
            levelTwoPriority.removeAll(Arrays.asList(process));
        else if (priority == 2)
            levelThreePriority.removeAll(Arrays.asList(process));
    }
}
