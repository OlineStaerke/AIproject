import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import java.util.Objects;

public abstract class Object{

    ArrayList<Node> finalPlan;
    ArrayList<String> finalPlanString;
    char ID;
    Node position;
    Node Goal;
    Plan mainPlan = new Plan();
    Agent conflicts;


    public void setGoal(Node goal) {
        this.Goal = goal;
    }

    abstract boolean isInGoal();
    abstract boolean isInSubGoal();


    abstract void bringBlank(State state, Agent agent) throws InterruptedException;


    @Override
    public String toString(){
        return "ID: " + ID + ", position: " + position.toString();

    }


}
