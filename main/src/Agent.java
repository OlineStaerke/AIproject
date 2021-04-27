import java.util.*;

public class Agent extends Object {
    ArrayList<Node> finalPlan;
    ArrayList<String> finalPlanString;
    ArrayList<Box> boxes = new ArrayList<>();
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

 //Compare to find the agent who is furthest away from goal, by looking at the plan size.
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
                //The conflicts has been solved
                if (mainPlan.plan.size()==0 && conflicts!=null ) {
                    blank = false;
                    conflicts.blank = true;
                    if( !conflicts.isInGoal()) conflicts.bringBlank(state,agent);

                    if (conflicts.conflicts == agent) {
                        conflicts.conflicts = null;
                        conflicts = null;
                    }

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

        switch(currentGoal.gType) {
            case BoxBlanked:
                // code block

            case BoxToGoal:
                // code block
                if (map.getAdjacent(position.getNodeId()).contains(SG.Obj.Goal.NodeId)) {
                    mainPlan.createPlan(map, position.NodeId, map.getAdjacent(SG.Obj.position.NodeId), visited);


                }
                else {
                    mainPlan.createPlan(map, position.NodeId, map.getAdjacent(SG.Obj.position.NodeId), visited);

                }

            case AgentToGoal:
                // code block


        }

        distance_to_goal = mainPlan.plan.size();
    }

    // Must update the new position of blanked agent
    public void bringBlank(State state, Agent agent) {
        //!state.occupiedNodes.containsKey(mainPlan.plan.get(0))
        if (mainPlan.plan.size()!=0 && !state.occupiedNodes.containsKey(mainPlan.plan.get(0))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }
        blank = true;
        mainPlan.createAltPaths(state, agent);
    }




}



