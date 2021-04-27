import java.util.*;

public class Agent extends Object {
    ArrayList<Node> finalPlan;
    ArrayList<String> finalPlanString;
    ArrayList<Box> boxes = new ArrayList<>();
    Agent conflicts;
    int distance_to_goal = 100;
    Boolean blank = false;

    public Agent(Node node, char ID) {
        // The finalPlan (output plan) of an agent must always contain the initial node)
        finalPlan = new ArrayList<>();
        finalPlan.add(node);
        finalPlanString = new ArrayList<>();
        finalPlanString.add(node.getNodeId());
        position = node;
        this.ID = ID;
        setPriority();

    }

    public Agent() {

    }

    public static class CustomComparator implements Comparator<Agent> {
        @Override
        public int compare(Agent o1, Agent o2) {
            return ((Integer) o2.mainPlan.plan.size()).compareTo(o1.mainPlan.plan.size());
        }
    }


    public ArrayList<Node> getFinalPlan(){
        return this.finalPlan;
    }
    public void setFree(State state) {
        //System.err.println("Ive been set free:" +getID());
        if (!isInGoal()) {
            blank = false;
            mainPlan.createPlan(state.map, position.NodeId, Goal.NodeId, new LinkedHashSet<>());}
    }



    public void planAltPaths() {}


    public void ExecuteMove(Agent agent,State state, Node wantedMove) {

            position = wantedMove;
            finalPlan.add(wantedMove);
            finalPlanString.add(wantedMove.getNodeId());
            state.occupiedNodes.put(position.NodeId, this);
            mainPlan.plan.remove(0);


            if (blank) {
                if (state.blankPlan.size()>0) {
                    state.blankPlan.remove(0);
                }
            }
            if (isInGoal() && mainPlan.plan.size()==0) {
                blank = false;

            }

            if (mainPlan.plan.size()==0 && blank && conflicts!=null) {
                blank = false;
                conflicts.blank = true;
                conflicts.bringBlank(state,conflicts);
                if (conflicts.conflicts == agent) {
                    agent.conflicts = null;
                }

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


    @Override
    public void planPi(Map map, LinkedHashSet visited) {
        if (Goal == null) return;
        mainPlan.createPlan(map, position.NodeId, Goal.NodeId,visited);
        distance_to_goal = mainPlan.plan.size();
    }

    // Must update the new position of blanked agent
    @Override
    public void bringBlank(State state, Agent otherAgent) {
        //!state.occupiedNodes.containsKey(mainPlan.plan.get(0))
        if (mainPlan.plan.size()!=0 && !state.occupiedNodes.containsKey(mainPlan.plan.get(0))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }

        mainPlan.createAltPaths(state, position,otherAgent, Goal.NodeId);
    }




}



