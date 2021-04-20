import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

public class Agent extends Object {
    ArrayList<Node> finalPlan;
    ArrayList<Box> boxes = new ArrayList<>();
    boolean hasMoved = false;

    public Agent(Node node, char ID) {
        // The finalPlan (output plan) of an agent must always contain the initial node)
        finalPlan = new ArrayList<>();
        finalPlan.add(node);
        position = node;
        this.ID = ID;
        Goal = null;
        setPriority();

    }

    public ArrayList<Node> getFinalPlan(){
        return this.finalPlan;
    }

    public void planAltPaths() {}


    public void ExecuteMove(State state, Node wantedMove) {
        position = wantedMove;
        finalPlan.add(wantedMove);
        state.occupiedNodes.put(position.NodeId, this);
        mainPlan.plan.remove(0);
        if (!wantedMove.isTunnel) priority = originalPriority;
    }

    @Override
    boolean isInGoal() {
        // Any box not in Goal?
        for (Box B: boxes){
            if (!B.isInGoal()) return false;
        }
        // Goal not existing?
        if (Objects.isNull(Goal)) return true;

        // Otherwise, check if agent is in goal
        return Goal.NodeId.equals(position.NodeId);
    }

    @Override
    public void planPi(Map map) {
        if (Goal == null) return;
        mainPlan.createPlan(map, position.NodeId, Goal.NodeId,new LinkedHashSet<>());
    }

    // Must update the new position of blanked agent
    @Override
    public void bringBlank(State state, Map map, ArrayList<String> otherAgentPlan) {

        if (!state.occupiedNodes.containsKey(mainPlan.plan.get(0))){
            ExecuteMove(state, state.stringToNode.get(mainPlan.plan.get(0)));
            hasMoved = true;
            return;
        }

        mainPlan.createAltPaths(state, position,map,otherAgentPlan, Goal.NodeId);
    }


}



