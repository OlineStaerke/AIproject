import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

public class Box extends Object {

    Agent owner;


    public Box(Node node, char id){
        this.position = node;
        this.ID = id;
        this.finalPlan = new ArrayList<>();
        this.finalPlanString = new ArrayList<>();
        this.mainPlan = new Plan();
    }

    public void bringBlank(State state, Map map, Agent otheragent){
        owner.subgoals.AddBringBlankBoxAsGoal(this);


    }

    @Override
    boolean isInSubGoal() {
        if (Objects.isNull(Goal)) return true;
        return Goal.NodeId.equals(position.NodeId);
    }

    @Override
    boolean isInGoal() {
        return false;
    }


    @Override
    void bringBlank(State state, Agent agent) {

    }


}
