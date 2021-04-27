import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

public class Box extends Object {

    Agent owner;


    public Box(Node node, char id){
        this.position = node;
        this.ID = id;
    }

    public void bringBlank(State state, Map map, Agent otheragent){
        owner.subgoals.AddBringBlankBoxAsGoal(this);


    }

    @Override
    boolean isInGoal() {
        if (Objects.isNull(Goal)) return true;
        return Goal.NodeId.equals(position.NodeId);
    }

    @Override
    void bringBlank(State state, Agent agent) {

    }


}
