import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import java.util.Objects;

public abstract class Object{

    ArrayList<Node> finalPlan;
    ArrayList<String> finalPlanString;
    String ID;
    Node position;
    ArrayList<String> Goal = new ArrayList<>();
    Plan mainPlan = new Plan();
    Agent conflicts;
    Boolean Taken;


    public void setGoal(Node goal) {
        Goal.add(goal.NodeId);

    }

    abstract boolean isInGoal();
    abstract boolean isInSubGoal();


    abstract void bringBlank(State state, Agent agent) throws InterruptedException;


    @Override
    public String toString(){
        return "ID: " + ID + ", position: " + position.toString();

    }


}
