import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import java.util.Objects;

public abstract class Object{

    char ID;
    Node position;
    Node Goal;
    Plan mainPlan = new Plan();


    public void setGoal(Node goal) {
        this.Goal = goal;
    }

    abstract boolean isInGoal();


    abstract void bringBlank(State state, Map map, Agent otherAgent);


    @Override
    public String toString(){
        return "ID: " + ID + ", position: " + position.toString();

    }


}
