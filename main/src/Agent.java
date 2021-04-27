import java.util.*;

public class Agent extends Object {
    ArrayList<Node> finalPlan;
    ArrayList<String> finalPlanString;
    ArrayList<Box> boxes = new ArrayList<>();
    Agent conflicts;
    int distance_to_goal = 100;
    Boolean blank = false;
    public SubGoals.SubGoal currentGoal;


    public SubGoals subgoals;


    public Agent(Node node, char ID) {
        finalPlan = new ArrayList<>();
        finalPlan.add(node);
        finalPlanString = new ArrayList<>();
        finalPlanString.add(node.getNodeId());
        position = node;
        this.ID = ID;
        currentGoal = null;

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
        return currentGoal.Obj.position.NodeId.equals(currentGoal.Obj.Goal.NodeId);
    }

    public void planGoals(Map map, LinkedHashSet visited){
        subgoals = new SubGoals(boxes, this);
        planPi(map, visited);
    }


    public void planPi(Map map, LinkedHashSet visited) {
        var SG = subgoals.ExtractNextGoal();
        System.err.println("SG: " + SG);
        currentGoal = SG;


        mainPlan.createPlan(map, position.NodeId, SG.Obj.Goal.NodeId, visited);
        distance_to_goal = mainPlan.plan.size();
    }

    // Must update the new position of blanked agent
    public void bringBlank(State state, Agent otherAgent) {
        //!state.occupiedNodes.containsKey(mainPlan.plan.get(0))
        if (mainPlan.plan.size()!=0 && !state.occupiedNodes.containsKey(mainPlan.plan.get(0))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }
        blank = true;
        conflicts = otherAgent;

        mainPlan.createAltPaths(state, position,otherAgent, Goal.NodeId);
    }




}



