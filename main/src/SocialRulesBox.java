import java.util.ArrayList;
import java.util.LinkedHashSet;

public class SocialRulesBox {
    public State state;
    public Agent agent;
    public Box box;
    LinkedHashSet visitedBoxes;
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
        allPlans = new ArrayList<>();
        if (goal==null) {
            if (box.conflicts!=null) {
                allPlans.addAll(box.conflicts.mainPlan.plan);
            }
            if (agent.conflicts!=null) {
                allPlans.addAll(agent.conflicts.mainPlan.plan);
            }
            if (box.conflict_box!=null) {

                allPlans.addAll(box.conflict_box.planToGoal);
                allPlans.add(box.conflict_box.position);
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
        //System.err.println(visitedNoTunnel);
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
        //System.err.println(visitedNoTunnel);
        visitedNoTunnel.remove(agent.position.NodeId);
        visitedNoTunnel.remove(agent.attached_box.position.NodeId);
    }

    public ArrayList<Tuple> runBFS(Plan altPlans) throws InterruptedException {
        ArrayList<Tuple> tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,allPlans,goal,false, false);
        //System.err.println("ASD"+tuple_plan);


        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,allPlans,goal,true, false);
        }
        else return tuple_plan;
        //System.err.println("Second"+tuple_plan);

        if (tuple_plan == null){
            InitNoTunnelVisited();
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visitedNoTunnel,allPlans,goal,false, false);
        }
        else return tuple_plan;

        /**
        if (tuple_plan == null){
            InitInGoalVisited();
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visitedInGoal,allPlans,goal,false, false);
        }
        else return tuple_plan;
         **/

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,allPlans,goal,true, true);

        }
        else return tuple_plan;

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),new ArrayList<>(),goal,false, false);

        }
        else return tuple_plan;

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),allPlans,goal,true, false);

        }
        else return tuple_plan;

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),allPlans,goal,true, true);

        }
        else return tuple_plan;


        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),allPlans,goal,true, true);

        }
        else return tuple_plan;

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,new ArrayList<String>(),goal,false,true);

        }
        else return tuple_plan;
        //System.err.println("SECOND"+tuple_plan);
        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),visited,new ArrayList<String>(),goal,true,true);

        }
        else return tuple_plan;
        //System.err.println("THIRD"+tuple_plan);

        if (tuple_plan==null) {
            tuple_plan = altPlans.breathFirstTraversal_box(state,agent,box,new LinkedHashSet<>(),new LinkedHashSet<>(),new ArrayList<String>(),goal,true,true);

        }
        else return tuple_plan;
        //System.err.println("FOUR"+tuple_plan);


        if (tuple_plan!=null) return tuple_plan;
        //System.err.println("SIXTH"+tuple_plan);

        return null;
    }


}

