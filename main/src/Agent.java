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
            Integer o2_value = 0;
            Integer o1_value =1;
            //Computes of another agents goal is on the agent path. If it is, their value should be smaller.

            if(o1.currentGoal!=null && o2.currentGoal!=null) {
                if (o2.mainPlan.plan.contains(o1.currentGoal.Obj.Goal.NodeId)) {
                    o2_value = 1000;
                }
                if (o1.mainPlan.plan.contains(o2.currentGoal.Obj.Goal.NodeId)) {
                    o1_value = 1000;
                }
            }
            Integer comparevalue = o2.mainPlan.plan.size() + o2_value;
            return (comparevalue).compareTo(((Integer) o1.mainPlan.plan.size())+o1_value);
        }
    }


    public ArrayList<Node> getFinalPlan(){
        return this.finalPlan;
    }




    public void ExecuteMove(Agent agent,State state, Boolean NoOp) throws InterruptedException {

            Node wantedMove;
            if (NoOp) {
                wantedMove = agent.position;
            }
            else {
                wantedMove = state.stringToNode.get(agent.mainPlan.plan.get(0));
            }
            position = wantedMove;
            finalPlan.add(wantedMove);
            finalPlanString.add(wantedMove.getNodeId());

            state.occupiedNodes.put(position.NodeId, this);

            if (mainPlan.plan.size()>0 && !NoOp) {
                mainPlan.plan.remove(0);
            }

            if (attached_box!=null) {
                Node wantedMoveBox;
                if (attached_box.mainPlan.plan.size()==0 || NoOp) {
                    wantedMoveBox = attached_box.position;


                }
                else {
                    wantedMoveBox = state.stringToNode.get(attached_box.mainPlan.plan.get(0));
                    }

                attached_box.finalPlan.add(wantedMoveBox);
                attached_box.finalPlanString.add(wantedMoveBox.NodeId);

                if (attached_box.mainPlan.plan.size() > 0 && !NoOp) {
                    attached_box.mainPlan.plan.remove(0);
                }

                if (attached_box.mainPlan.plan.size()==0 && agent.currentGoal.gType.equals(SubGoals.GoalType.BoxBlanked)) {
                    System.err.println("Set to true");
                    agent.subgoals.UpdatedBlanked((Box) agent.currentGoal.Obj, true);
                }

                System.err.println("ADDED to occupied nodes"+ wantedMoveBox.NodeId);
                state.occupiedNodes.put(wantedMoveBox.NodeId, attached_box);
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

                    // THIS LINE OF CODE RUINS STUFF WITH BOXES LETS TRY TO FIX IT!!
                    if(!conflicts.isInGoal()) {
                        if (conflicts.conflicts!=null) {
                            System.err.println("CONFLICT");
                            conflicts.bringBlank(state, conflicts);
                        }
                        else {
                            System.err.println("PLANPI");
                            conflicts.planPi(state,new LinkedHashSet());
                        }
                    }

                    if (conflicts.conflicts == agent) {
                        conflicts.conflicts = null;
                        conflicts = null;
                    }

                }

            }


        }



    @Override
    boolean isInGoal() {
        return subgoals.ExtractNextGoal(currentGoal)==null;


    }

    boolean attachedBox(State state) {
        if (attached_box==null) {return false;}
        return (state.map.getAdjacent(position.NodeId).contains(attached_box.position.NodeId));
    }

    boolean isInSubGoal() {
        if (Goal==null) {
            return true;
        }
        else {
            return (position.NodeId.equals(Goal.NodeId));
        }
    }

    public void planGoals(State state, LinkedHashSet visited) throws InterruptedException {
        subgoals = new SubGoals(boxes, this);
        planPi(state, visited);
    }


    public void planPi(State state,LinkedHashSet visited) throws InterruptedException {
        var SG =subgoals.ExtractNextGoal(currentGoal);


        System.err.println("SG: " + SG);
        currentGoal = SG;

        if (currentGoal!=null) {
            switch (currentGoal.gType) {
                case BoxBlanked:
                    // If this is 1:1 with BoxToGoal case, remove the code (dupliacte code)
                    if ((state.map.getAdjacent(position.NodeId)).contains(SG.Obj.position.NodeId)) {

                        System.err.println("CREATING PLAN AWAY WITH BOX!");
                        var neighs = state.map.map.get(SG.Obj.position.NodeId);
                        attached_box = (Box) SG.Obj;
                        mainPlan.createPlanWithBox(state, this, null, (Box) SG.Obj);




                    } else {

                        attached_box = null;
                        mainPlan.createPlan(state, position.NodeId, state.map.getAdjacent(SG.Obj.position.NodeId), visited);
                    }
                    subgoals.UpdatedBlanked((Box) SG.Obj,true);
                    break;

                case BoxToGoal:
                    // code block

                    if ((state.map.getAdjacent(position.NodeId)).contains(SG.Obj.position.NodeId)) {
                        System.err.println(SG.Obj.Goal.NodeId);
                        attached_box = (Box) SG.Obj;
                        mainPlan.createPlanWithBox(state, this, SG.Obj.Goal.NodeId, (Box) SG.Obj);



                    } else {

                        attached_box = null;
                        mainPlan.createPlan(state, position.NodeId, state.map.getAdjacent(SG.Obj.position.NodeId), visited);

                    }
                    break;

                case AgentToGoal:
                    List<String> goalListAgent = new ArrayList<>();
                    goalListAgent.add(SG.Obj.Goal.NodeId);
                    mainPlan.createPlan(state, position.NodeId, goalListAgent, visited);

                    attached_box = null;
                    break;


            }
            System.err.println("SG: " + SG);

            distance_to_goal = mainPlan.plan.size();


        }
    }

    // Must update the new position of blanked agent
    public void bringBlank(State state, Agent agent) {
        //!state.occupiedNodes.containsKey(mainPlan.plan.get(0))

        if ((currentGoal.gType.equals(SubGoals.GoalType.BoxBlanked) && mainPlan.plan.size()>0) || mainPlan.plan.size()!=0 && (!state.occupiedNodes.containsKey(mainPlan.plan.get(0) ))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }
        blank = true;
        mainPlan.createAltPaths(state, agent);
        attached_box = null;
    }




}



