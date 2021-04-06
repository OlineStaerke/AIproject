import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class Agent extends Object {
    public Plan mainPlan = new Plan();
    ArrayList<Node> finalPlan;
    Node[] privateZone;
    Node initialState;
    ArrayList<Box> boxes = new ArrayList<>();
    boolean blanked;



    //Hashset of string for positions overtaken, each agent would add these positions he finds
    // himself in. This hashset would be public
    public Agent(Node node) {
        this.initialState = node;
        // The finalPlan (output plan) of an agent must always contain the initial node)
        finalPlan = new ArrayList<>();
        finalPlan.add(node);
        position = node;
    }

    public ArrayList<Node> getFinalPlan(){
        return this.finalPlan;
    }

    public void planAltPaths() {}

    public void planPi(Map map) {

        mainPlan.createPlan(map, position.NodeId, Goal.NodeId,new LinkedHashSet<>());
    }

    public void ExecuteMove(State state, Node wantedMove) {
        state.occupiedNodes.remove(position.NodeId, this);
        position = wantedMove;
        finalPlan.add(wantedMove);
        state.occupiedNodes.put(position.NodeId, this);
        mainPlan.plan.remove(0);
    }

    // Must update the new position of blanked agent
    public void bringBlank(Map map) {
        mainPlan.createAltPaths(position,map);
        blanked = true;
    }


}



