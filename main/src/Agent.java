import java.util.*;

public class Agent extends Object {
    ArrayList<Node> finalPlan;
    ArrayList<String> finalPlanString;
    ArrayList<Box> boxes = new ArrayList<>();
    Agent conflicts;
    String problemnode = null;
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
        //Goal = null;
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
    public void setAgentsFree(State state) {
        //System.err.println("I am agent:"+getID()+"I will check :"+conflicts);

        Boolean setfree = true;

        if (!conflicts.isInGoal()) {setfree = false;}



        if (setfree) {setFree(state);conflicts = null;}

    }



    public void planAltPaths() {}


    public void ExecuteMove(Agent agent,State state, Node wantedMove) {

            position = wantedMove;
            finalPlan.add(wantedMove);
            finalPlanString.add(wantedMove.getNodeId());
            state.occupiedNodes.put(position.NodeId, this);
            mainPlan.plan.remove(0);
            //if (!wantedMove.isTunnel) priority = originalPriority;
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
                System.err.println("SIIZE"+conflicts.mainPlan.plan.size());
                conflicts.bringBlank(state,state.map,conflicts);
                if (conflicts.conflicts == agent) {
                    agent.conflicts = null;
                }
                /**
                if (conflicts.mainPlan.plan.size()==0) {
                    if (conflicts.isInGoal()) System.err.println("NO PLAN");
                    else {
                        System.err.println("TRUE PLAN");
                        conflicts.planPi(state.map, new LinkedHashSet());
                    }
                    //new LinkedHashSet(state.occupiedNodes.keySet()
                }
                 **/

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
    public void planPi(Map map, LinkedHashSet visited) {
        if (Goal == null) return;
        mainPlan.createPlan(map, position.NodeId, Goal.NodeId,visited);
        distance_to_goal = mainPlan.plan.size();
    }

    // Must update the new position of blanked agent
    @Override
    public void bringBlank(State state, Map map, Agent otherAgent) {
        if (isInGoal()) {
            setPriority();
        }
        //!state.occupiedNodes.containsKey(mainPlan.plan.get(0))
        if (mainPlan.plan.size()!=0 && !state.occupiedNodes.containsKey(mainPlan.plan.get(0))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }

        mainPlan.createAltPaths(state, position,map,otherAgent, Goal.NodeId);
    }




}



