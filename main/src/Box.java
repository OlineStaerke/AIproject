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
        // Things that Box.bringBlank should do:
        // Create a new path (like agent.bringBlank)

        // Signal to the owner agent, that this box should be moved (extend their mainplan)

    }

    @Override
    boolean isInGoal() {
        if (Objects.isNull(Goal)) return true;
        return Goal.NodeId.equals(position.NodeId);
    }

    @Override
    public void planPi(Map map, LinkedHashSet visited) {
        mainPlan.createPlan(map, position.NodeId, Goal.NodeId,visited);


    }



}
