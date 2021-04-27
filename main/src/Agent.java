import java.util.*;

public class Agent extends Object {

    ArrayList<Box> boxes = new ArrayList<>();
    int distance_to_goal = 100;
    Boolean blank = false;
    public SubGoals.SubGoal currentGoal;
    public SubGoals subgoals;
    Box attached_box;


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




    public void ExecuteMove(Agent agent,State state, Node wantedMove, Boolean NoOp) {

            position = wantedMove;
            finalPlan.add(wantedMove);
            finalPlanString.add(wantedMove.getNodeId());

            state.occupiedNodes.put(position.NodeId, this);

            if (mainPlan.plan.size()>0 && !NoOp) {
                mainPlan.plan.remove(0);
            }

            if (attached_box!=null) {
                Node wantedMoveBox = state.stringToNode.get(attached_box.mainPlan.plan.get(0));
                attached_box.finalPlan.add(wantedMoveBox);
                attached_box.finalPlanString.add(wantedMoveBox.NodeId);
                if (attached_box.mainPlan.plan.size()>0 && !NoOp) {
                    attached_box.mainPlan.plan.remove(0);
                }

                state.occupiedNodes.put(wantedMoveBox.NodeId, this);
                attached_box.position = wantedMoveBox;

            }

            for (Box b : boxes) {
                if (b!=attached_box) {
                    b.finalPlan.add(b.position);
                    b.finalPlanString.add(b.position.NodeId);
                }
            }




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
        return subgoals.ExtractNextGoal()==null;


    }

    boolean isInSubGoal() {
        if (currentGoal==null) {
            return (position.NodeId.equals(Goal.NodeId));
        }
        return currentGoal.Obj.position.NodeId.equals(currentGoal.Obj.Goal.NodeId);
    }

    public void planGoals(State state, LinkedHashSet visited){
        subgoals = new SubGoals(boxes, this);
        planPi(state, visited);
    }


    public void planPi(State state,LinkedHashSet visited) {
        var SG = subgoals.ExtractNextGoal();

        System.err.println("SG: " + SG);
        currentGoal = SG;

        if (currentGoal!=null) {
            switch (currentGoal.gType) {
                case BoxBlanked:
                    // code block
                    if ((state.map.getAdjacent(position.NodeId)).contains(SG.Obj.position.NodeId)) {

                        mainPlan.createPlanWithBox(state, position.NodeId, SG.Obj.position.NodeId, null, (Box) SG.Obj);
                        attached_box = (Box) SG.Obj;

                    } else {

                        attached_box = null;
                        mainPlan.createPlan(state.map, position.NodeId, state.map.getAdjacent(SG.Obj.position.NodeId), visited);

                    }
                    break;

                case BoxToGoal:
                    // code block

                    if ((state.map.getAdjacent(position.NodeId)).contains(SG.Obj.position.NodeId)) {

                        mainPlan.createPlanWithBox(state, position.NodeId, SG.Obj.position.NodeId, SG.Obj.Goal.NodeId, (Box) SG.Obj);
                        attached_box = (Box) SG.Obj;

                    } else {

                        attached_box = null;
                        mainPlan.createPlan(state.map, position.NodeId, state.map.getAdjacent(SG.Obj.position.NodeId), visited);

                    }
                    break;

                case AgentToGoal:
                    List<String> goalListAgent = new ArrayList<>();
                    goalListAgent.add(SG.Obj.Goal.NodeId);
                    mainPlan.createPlan(state.map, position.NodeId, goalListAgent, visited);

                    attached_box = null;
                    break;

                // code block


            }
            distance_to_goal = mainPlan.plan.size();


        }
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



