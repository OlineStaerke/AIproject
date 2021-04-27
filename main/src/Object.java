import java.util.ArrayList;
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



    public void setPriority(){
        this.priority = ID - '0';
        this.originalPriority = ID - '0';
    }


    public abstract void planPi(Map map, LinkedHashSet visited);

    abstract void bringBlank(State state, Map map, Agent otherAgent);




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
