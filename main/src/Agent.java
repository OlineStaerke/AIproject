import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Agent extends Object {
    ArrayList<Node> finalPlan;
    ArrayList<Box> boxes = new ArrayList<>();



    //Hashset of string for positions overtaken, each agent would add these positions he finds
    // himself in. This hashset would be public
    public Agent(Node node, char ID) {
        // The finalPlan (output plan) of an agent must always contain the initial node)
        finalPlan = new ArrayList<>();
        finalPlan.add(node);
        position = node;
        this.ID = ID;
        Goal = null;
    }

    public ArrayList<Node> getFinalPlan(){
        return this.finalPlan;
    }

    public void planAltPaths() {}


    public void ExecuteMove(State state, Node wantedMove) {
        state.occupiedNodes.remove(position.NodeId, this);
        position = wantedMove;
        finalPlan.add(wantedMove);
        state.occupiedNodes.put(position.NodeId, this);
        mainPlan.plan.remove(0);
    }

    @Override
    public void planPi(Map map) {
        mainPlan.createPlan(map, position.NodeId, Goal.NodeId,new LinkedHashSet<>());
    }

    // Must update the new position of blanked agent
    @Override
    public void bringBlank(Map map) {
        mainPlan.createAltPaths(position,map);
    }


}



