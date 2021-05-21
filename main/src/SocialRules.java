import java.util.*;

public class SocialRules {
    public State state;
    public Agent agent;
    LinkedHashSet<String> visitedBoxes;
    LinkedHashSet<String> visited;
    ArrayList<String> allPlans;

    public SocialRules(State state, Agent agent) {
        this.state = state;
        this.agent = agent;
        visited = new LinkedHashSet<>(state.occupiedNodes.keySet());
        if (agent != null)visited.remove(agent.position.getNodeId());

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
        visitedBoxes = new LinkedHashSet<>();
        for (String v : state.occupiedNodes.keySet()) {
            Node n = state.stringToNode.get(v);
            if (state.occupiedNodes.get(v) instanceof Box) {
                visitedBoxes.add(n.NodeId);
            }
        }
        visitedBoxes.remove(agent.position.NodeId);
    }

    public ArrayList<String> runBFS(Plan altPlans) {
        InitAllPlans();
        InitBoxVisited(); // Initialize array of box positions

        // First case: No tunnel, not through any other elements, and not ending on the conflict plab
        ArrayList<String> plan = altPlans.breathFirstTraversal_altpath(state, agent.position.getNodeId(), visited,allPlans, false); //Run BFS, to create new alternative plan
        // Second case: Can be a tunnel, not through any other elements, and not ending on the conflict plan
        if (plan==null) {
            plan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), visited,allPlans, true);
        }
        else return plan;

        // Third case Not Through any boxes, and its okay to end at a tunnel, but dont go through other elements and end at conflict agent plan
        if (plan == null) {
            plan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), visitedBoxes,allPlans, true);
        }
        else return plan;

        // Fourth case Its okay to go through other elements (all elements), but try not to end at a tunnel or otheragenbtplan
        if (plan==null) {
            plan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), new LinkedHashSet<>(),allPlans, false);
        }
        else return plan;

        // Fifth case  Its okay to go through other elements (all elements), and its okay to end at tunnel but not otheragenbtplan
        if (plan==null) {
            plan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), new LinkedHashSet<>(),allPlans, true);
        }
        else return plan;

        // Seventh Return empty list
        if (plan==null) return new ArrayList<>();

        else return plan;
    }

    // A planning method, which finds a path between source and destination(s).
    public ArrayList<String> findMainPlan(Plan planner, String Source, List<String> Destination){

        // Check if a plan exists, while avoiding all occupied nodes
        ArrayList<String> mainPlan = planner.breathFirstTraversal(state.map, Source, Destination,visited);

        if (mainPlan == null) {
            // Check if we can avoid boxes
            LinkedHashSet<String> visitedBoxes = new LinkedHashSet<>();
            for (String v: state.occupiedNodes.keySet()) {
                Node n = state.stringToNode.get(v);
                if (state.occupiedNodes.get(v) instanceof Box) {
                    visitedBoxes.add(n.NodeId);
                }
            }
            visitedBoxes.remove(Source);
            mainPlan = planner.breathFirstTraversal(state.map, Source, Destination,visitedBoxes);
        }


        if (mainPlan == null) {
            // Check if we can avoid objects in tunnels
            LinkedHashSet<String> visitedNoTunnel = new LinkedHashSet<>();
            for (String v: state.occupiedNodes.keySet()) {
                Node n = state.stringToNode.get(v);
                if (!n.isTunnel&& !n.isTunnelDynamic) {
                    visitedNoTunnel.add(n.NodeId);
                }
            }
            visitedNoTunnel.remove(Source);
            mainPlan = planner.breathFirstTraversal(state.map, Source, Destination,visitedNoTunnel);
        }


        if (mainPlan == null && agent!=null) {
            //Only go though your own boxes
            LinkedHashSet<String> visitedBoxes = new LinkedHashSet<>();
            for (String v: state.occupiedNodes.keySet()) {
                Node n = state.stringToNode.get(v);
                if (state.occupiedNodes.get(v) instanceof Box && !agent.boxes.contains(state.occupiedNodes.get(v))) {
                    visitedBoxes.add(n.NodeId);
                }
            }
            visitedBoxes.remove(Source);
            mainPlan = planner.breathFirstTraversal(state.map, Source, Destination,visitedBoxes);
        }


        // Finally, just find a plan, which can go through everything.
        if (mainPlan==null){
            mainPlan = planner.breathFirstTraversal(state.map, Source, Destination,new LinkedHashSet<>());
        }
        return mainPlan;
    }


}

