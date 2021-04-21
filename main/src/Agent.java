import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

public class Agent extends Object {
    ArrayList<Node> finalPlan;
    ArrayList<String> finalPlanString;
    ArrayList<Box> boxes = new ArrayList<>();
    ArrayList<Agent> conflicts = new ArrayList<>();
    String problemnode = null;

    public Agent(Node node, char ID) {
        // The finalPlan (output plan) of an agent must always contain the initial node)
        finalPlan = new ArrayList<>();
        finalPlan.add(node);
        finalPlanString = new ArrayList<>();
        finalPlanString.add(node.getNodeId());
        position = node;
        this.ID = ID;
        //Goal = null;
        setPriority();
        conflicts = new ArrayList<>();



    }

    public ArrayList<Node> getFinalPlan(){
        return this.finalPlan;
    }
    public void setFree(State state) {
        //System.err.println("Ive been set free:" +getID());
        if (!isInGoal()) {
        mainPlan.createPlan(state.map, position.NodeId, Goal.NodeId, new LinkedHashSet<>());}
    }
    public void setAgentsFree(State state) {
        //System.err.println("I am agent:"+getID()+"I will check :"+conflicts);

        Boolean setfree = true;
        for (Agent a: conflicts) {
            if (!a.passedProblem()) {setfree = false;}


        }
        if (setfree) {setFree(state);conflicts = new ArrayList<>();}

    }



    public void planAltPaths() {}


    public void ExecuteMove(State state, Node wantedMove) {
        position = wantedMove;
        finalPlan.add(wantedMove);
        finalPlanString.add(wantedMove.getNodeId());
        state.occupiedNodes.put(position.NodeId, this);
        mainPlan.plan.remove(0);
        //if (!wantedMove.isTunnel) priority = originalPriority;
        if (wantedMove.getNodeId().equals(problemnode)) {
            problemnode = null;
        }
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

    boolean passedProblem() {
        if(problemnode == null) {
            return true;
        }
        else return false;
    }

    @Override
    public void planPi(Map map) {
        if (Goal == null) return;
        mainPlan.createPlan(map, position.NodeId, Goal.NodeId,new LinkedHashSet<>());
    }

    // Must update the new position of blanked agent
    @Override
    public void bringBlank(State state, Map map, Agent otherAgent, String problem_node) {
        if (isInGoal()) {
            setPriority();
        }
        //!state.occupiedNodes.containsKey(mainPlan.plan.get(0))
        if (mainPlan.plan.size()!=0 && !state.occupiedNodes.containsKey(mainPlan.plan.get(0))){
            return;
        }

        mainPlan.createAltPaths(state, position,map,otherAgent, Goal.NodeId, problem_node);
    }


}



