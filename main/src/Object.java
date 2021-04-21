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

    abstract boolean passedProblem();


    public void setPriority(){
        this.priority = ID - '0';
        this.originalPriority = ID - '0';
    }


    abstract void planPi(Map map);

    abstract void bringBlank(State state, Map map, Agent otherAgent, String problem_node);





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
