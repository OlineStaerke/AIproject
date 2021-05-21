import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class SocialRules {
    public State state;
    public Agent agent;
    LinkedHashSet visitedBoxes;
    LinkedHashSet visited;
    ArrayList allPlans;

    public SocialRules(State state, Agent agent) {
        this.state = state;
        this.agent = agent;
        visited = new LinkedHashSet<>(state.occupiedNodes.keySet());
        visited.remove(agent.position.getNodeId());
        InitAllPlans();
        InitBoxVisited(); // Initialize array of box positions
    }

    private void InitAllPlans() {
        allPlans = new ArrayList<>();
        allPlans.addAll(agent.conflicts.mainPlan.plan);
        allPlans.addAll(state.occupiedNodesString());
        if(agent.conflicts.currentGoal!=null
                && agent.conflicts.currentGoal.Obj instanceof Box
                && !agent.conflicts.currentGoal.Obj.isInSubGoal()) {
            allPlans.addAll(agent.conflicts.currentGoal.Obj.planToGoal);
        }
    }


    private void InitBoxVisited() {
        visitedBoxes = new LinkedHashSet<String>();
        for (String v : state.occupiedNodes.keySet()) {
            Node n = state.stringToNode.get(v);
            if (state.occupiedNodes.get(v) instanceof Box) {
                visitedBoxes.add(n.NodeId);
            }
        }
        visitedBoxes.remove(agent.position.NodeId);
    }

    public ArrayList<String> runBFS(Plan altPlans) {

        // First case: No tunnel, not through any other elements, and not ending on the conflict plab
        ArrayList<String> plan = altPlans.breathFirstTraversal_altpath(state, agent.position.getNodeId(), visited,allPlans, false); //Run BFS, to create new alternative plan
        // Second case: Can be a tunnel, not through any other elements, and not ending on the conflict plan
        if (plan==null) {
            ArrayList<String> altplan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), visited,allPlans, true);
            plan = altplan;
        }
        else return plan;

        // Third case Not Through any boxes, and its okay to end at a tunnel, but dont go through other elements and end at conflict agent plan
        if (plan == null) {
            ArrayList<String> altplan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), visitedBoxes,allPlans, true);
            plan = altplan;
        }
        else return plan;

        // Fourth case Its okay to go through other elements (all elements), but try not to end at a tunnel or otheragenbtplan
        if (plan==null) {
            ArrayList<String> altplan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), new LinkedHashSet<>(),allPlans, false); //Run BFS, to create new alternative plan
            plan = altplan;
        }
        else return plan;

        // Fifth case  Its okay to go through other elements (all elements), and its okay to end at tunnel but not otheragenbtplan
        if (plan==null) {
            ArrayList<String> altplan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), new LinkedHashSet<>(),allPlans, true); //Run BFS, to create new alternative plan
            plan = altplan;
        }
        else return plan;

        // Seventh Return empty list
        if (plan==null) return new ArrayList<>();

        else return plan;
    }


}

