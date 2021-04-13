import java.util.LinkedHashSet;
import java.util.Set;

public class Box extends Object {

    Agent owner;


    public Box(Node node, char id){
        this.position = node;
        this.ID = id;
    }

    public void bringBlank(State state, Map map, Set<String> visited){
        // Things that Box.bringBlank should do:
        // Create a new path (like agent.bringBlank)

        // Signal to the owner agent, that this box should be moved (extend their mainplan)

    }

    @Override
    void planPi(Map map) {
        mainPlan.createPlan(map, position.NodeId, Goal.NodeId,new LinkedHashSet<>());


    }
}
