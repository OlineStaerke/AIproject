import java.util.LinkedHashSet;
import java.util.Set;

import java.util.Objects;

public abstract class Object implements Comparable<Object> {
    int priority;
    int originalPriority;
    char ID;
    Node position;
    Node Goal;
    Plan mainPlan = new Plan();


    public char getID(){
        return ID;
    }
    public void setID(char ID) {
        this.ID = ID;
    }
    public void setGoal(Node goal) {
        this.Goal = goal;
    }

    abstract boolean isInGoal();

    public void setPriority(int priority){
        this.priority = priority;
    }


    abstract void planPi(Map map);

    abstract void bringBlank(State state, Map map, Set<String> visited);





    // Allows comparison and sorting w.r.t priority
    @Override
    public int compareTo(Object o) {
        return Integer.compare(priority, o.priority);
    }

    @Override
    public String toString(){
        return "ID: " + ID + ", priority: " + priority + ", position: " + position.toString();

    }


}
