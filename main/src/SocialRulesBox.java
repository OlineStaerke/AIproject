import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SocialRulesBox {
    public State state;
    public Agent agent;
    public Box box;
    LinkedHashSet visited;
    ArrayList allPlans;
    ArrayList<String> goal;
    LinkedHashSet visitedInGoal;
    LinkedHashSet visitedNoTunnel;


    public SocialRulesBox(State state, Agent agent, ArrayList<String> goal, Box box) {


        this.state = state;
        this.agent = agent;
        this.box = box;
        visited = new LinkedHashSet<>(state.occupiedNodes.keySet());
        visited.remove(agent.position.NodeId);
        visited.remove(agent.attached_box.position.NodeId);
        allPlans = new ArrayList<>();
        this.goal = goal;
        InitAllPlans();

    }

    private void InitAllPlans() {
        allPlans = new ArrayList<String>();
        if (goal==null) {
            if (box.conflicts!=null) {
                allPlans.addAll(box.conflicts.mainPlan.plan);
            }
            if (agent.conflicts!=null) {
                allPlans.addAll(agent.conflicts.mainPlan.plan);
                if (box.conflict_box != null){
                    allPlans.addAll(box.conflict_box.planToGoal);
                }
            }
        }
        if(agent.conflicts!=null && agent.conflicts.ID.equals(agent.ID)) {
            allPlans.addAll(box.conflictRoute);
        }
        allPlans.addAll(visited);

    }


    private void InitInGoalVisited() {
        visitedInGoal = new LinkedHashSet<String>();
        for (String v: state.occupiedNodes.keySet()) {
            Node n = state.stringToNode.get(v);
            if (state.occupiedNodes.get(v).isInSubGoal()) {
                visitedInGoal.add(n.NodeId);
            }
        }
        visitedInGoal.remove(agent.position.NodeId);
        visitedInGoal.remove(agent.attached_box.position.NodeId);
    }

    public void InitNoTunnelVisited() {
        visitedNoTunnel = new LinkedHashSet<String>();
        for (String v: state.occupiedNodes.keySet()) {
            Node n = state.stringToNode.get(v);
            if (!n.isTunnel&& !n.isTunnelDynamic) {
                visitedNoTunnel.add(n.NodeId);
            }
        }
        visitedNoTunnel.remove(agent.position.NodeId);
        visitedNoTunnel.remove(agent.attached_box.position.NodeId);
    }

    // Tries to find the best route under different rules
    public ArrayList<Tuple> runBFS(Plan altPlans, Boolean secondtry) throws InterruptedException {
        ArrayList<Tuple> tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,allPlans,goal,false, false);


        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,allPlans,goal,true, false);
        }

        if (tuple_plan == null){
            InitNoTunnelVisited();
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visitedNoTunnel,allPlans,goal,false, false);
        }

        if (tuple_plan == null){
            InitInGoalVisited();
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visitedInGoal,allPlans,goal,false, false);
        }

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,allPlans,goal,true, true);

        }

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),new ArrayList<>(),goal,false, false);

        }

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),allPlans,goal,true, false);

        }

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),allPlans,goal,true, true);

        }



        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),allPlans,goal,true, true);

        }


        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,new ArrayList<String>(),goal,false,true);

        }

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,new ArrayList<String>(),goal,true,true);

        }


        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),new ArrayList<String>(),goal,true,true);

        }


        return tuple_plan;
    }


}

