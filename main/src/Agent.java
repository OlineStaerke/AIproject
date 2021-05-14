import java.lang.reflect.Array;
import java.util.*;

public class Agent extends Object {

    ArrayList<Box> boxes = new ArrayList<>();
    int distance_to_goal = 100;
    Boolean blank = false;
    public SubGoals.SubGoal currentGoal;
    public SubGoals.SubGoal nextGoal;
    public SubGoals subgoals;
    Box attached_box;
    public int stuck;




    public Agent(Node node, String ID) {
        finalPlan = new ArrayList<>();
        finalPlan.add(node);
        finalPlanString = new ArrayList<>();
        finalPlanString.add(node.getNodeId());
        position = node;
        this.ID = ID;
        currentGoal = null;
        this.Taken = false;
        this.planToGoal = new ArrayList<>();
        stuck = 0;



    }

 //Compare to find the agent who is furthest away from goal, by looking at the plan size.
    public static class CustomComparator implements Comparator<Agent> {
        @Override
        public int compare(Agent o1, Agent o2) {
            Integer o2_value = 0;
            Integer o1_value = 0;
            //Computes of another agents goal is on the agent path. If it is, their value should be smaller.

            if(o1.nextGoal!=null && o2.nextGoal!=null) {

                for (String goal : o1.nextGoal.Obj.Goal) {
                    if (o2.nextGoal.Obj.planToGoal != null && o2.nextGoal.Obj.planToGoal.contains(goal)) {
                        o2_value+= 100;
                    }
                }

                for (String goal : o2.nextGoal.Obj.Goal) {
                    if (o1.nextGoal.Obj.planToGoal != null && o1.nextGoal.Obj.planToGoal.contains(goal)) {
                        o1_value+= 100;
                    }
                }

            }

            if (o1.subgoals.ExistsBlankGoal()) {

                o1_value+= 2000;
            }
            if (o2.subgoals.ExistsBlankGoal()) {

                o2_value+= 2000;
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


            //Add elements to attached box final plan

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
                state.occupiedNodes.put(wantedMoveBox.NodeId, attached_box);
                attached_box.position = wantedMoveBox;

                if (attached_box.mainPlan.plan.size()==0 && agent.currentGoal.gType.equals(SubGoals.GoalType.BoxBlanked)) {
                    agent.subgoals.UpdatedBlanked((Box) agent.currentGoal.Obj, true);

                }




            }


            // if blank call conflicts
            if (blank) {
               // System.err.println("CONFLICTS");
                //System.err.println(ID+" "+conflicts);

                if (state.blankPlan.size()>0) {
                    state.blankPlan.remove(0);
                }

                //The conflicts has been solved
                if (mainPlan.plan.size()==0 && conflicts!=null ) {

                    blank = false;
                    conflicts.blank = true;

                    // THIS LINE OF CODE RUINS STUFF WITH BOXES LETS TRY TO FIX IT!!

                    if(!conflicts.isInGoal()) {
                        if (conflicts.conflicts!=null && conflicts.currentGoal != null && conflicts.mainPlan.plan.size() ==0) {

                            conflicts.bringBlank(state, conflicts);
                            //conflicts.planPi(state,new LinkedHashSet());

                        }
                        else {
                            if (conflicts.mainPlan.plan.size()==0){
                            //conflicts.planPi(state,new LinkedHashSet(), false);
                            }
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
        return subgoals.InGoal();

    }

    boolean attachedBox(State state) {
        if (attached_box!=null) {return true;}

        return (state.map.getAdjacent(position.NodeId).contains(currentGoal.Obj.position.NodeId));
    }

    boolean thisAttachedBox(State state, Box attachedB) {
        if (attached_box!=null && attached_box.equals(attachedB)) {return true;}

        return (state.map.getAdjacent(position.NodeId).contains(attachedB.position.NodeId));
    }

    boolean isInSubGoal() {
        if (Goal.size()==0) {
            return true;
        }
        else {
            return (Goal.contains(position.NodeId));
        }
    }

    public void planGoals(State state) throws InterruptedException {
        subgoals = new SubGoals(boxes, this, state);
    }


    public void planPi(State state,LinkedHashSet visited, Boolean secondTry) throws InterruptedException {

        mainPlan.plan = new ArrayList<>();
        if (currentGoal != null) (currentGoal.Obj).Taken = false;
        var SG = currentGoal;
        if (!secondTry) {
         SG = subgoals.ExtractNextGoal(currentGoal, state);}


        currentGoal = SG;


        if (currentGoal != null) {
            currentGoal.Obj.Taken = true;
            switch (currentGoal.gType) {
                case BoxBlanked:
                    // If this is 1:1 with BoxToGoal case, remove the code (dupliacte code)
                    if ((state.map.getAdjacent(position.NodeId)).contains(SG.Obj.position.NodeId)) {

                        attached_box = (Box) SG.Obj;
                        ((Box) SG.Obj).currentowner = this;
                        mainPlan.createPlanWithBox(state, this, null, (Box) SG.Obj);


                    } else {

                        attached_box = null;
                        mainPlan.createPlan(state, position.NodeId, state.map.getAdjacent(SG.Obj.position.NodeId), visited);
                        ((Box) SG.Obj).currentowner = this;
                    }
                    break;

                case BoxToGoal:
                    // code block

                    if ((state.map.getAdjacent(position.NodeId)).contains(SG.Obj.position.NodeId)) {

                        attached_box = (Box) SG.Obj;
                        attached_box.currentowner = this;
                        mainPlan.createPlanWithBox(state, this, SG.Obj.Goal, (Box) SG.Obj);
                        if (!secondTry) (SG.Obj).planToGoal = new ArrayList<>(SG.Obj.mainPlan.plan);




                    } else {

                        attached_box = null;
                        mainPlan.createPlan(state, position.NodeId, state.map.getAdjacent(SG.Obj.position.NodeId), visited);
                        ((Box) SG.Obj).currentowner = this;
                    }
                    break;

                case AgentToGoal:
                    List<String> goalListAgent = new ArrayList<>();
                    goalListAgent.addAll(SG.Obj.Goal);
                    mainPlan.createPlan(state, position.NodeId, goalListAgent, visited);
                    attached_box = null;
                    planToGoal = new ArrayList<>(mainPlan.plan);
                    break;
                case AgentBlanked:
                    bringBlank(state,conflicts);

            }
            if (mainPlan.plan == null) mainPlan.plan = new ArrayList<>();
            distance_to_goal = mainPlan.plan.size();


        }
        else {
            attached_box = null;

        }
    }


    // Must update the new position of blanked agent
    public void bringBlank(State state, Agent agent) throws InterruptedException {

        //!state.occupiedNodes.containsKey(mainPlan.plan.get(0))
        blank = true;
        if ( mainPlan.plan.size()!=0 && (!state.occupiedNodes.containsKey(mainPlan.plan.get(0)))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }

        if ( attached_box!= null && mainPlan.plan.size()!=0 ){

            //If the first move is its own position, look one further
            String wantedMoveAgent = mainPlan.plan.get(0);
            String wantedMoveBox = attached_box.mainPlan.plan.get(0);

            Boolean check_wantedMoveAgent = (!state.occupiedNodes.containsKey(wantedMoveAgent)) || wantedMoveAgent ==attached_box.position.NodeId;
            Boolean check_wantedMoveBox = (!state.occupiedNodes.containsKey(wantedMoveBox)) || wantedMoveBox ==position.NodeId ;
            if(check_wantedMoveAgent && check_wantedMoveBox) {

                state.blankPlan = new ArrayList<>(mainPlan.plan);
                return;
            }
        }
        //System.err.println(conflicts.mainPlan.plan);
        //System.err.println(attached_box.position.NodeId);
        if (subgoals.ExtractNextGoal(currentGoal, state) != null && attached_box!=null && ((conflicts.mainPlan.plan.contains(attached_box.position.NodeId))||(conflicts.attached_box!=null&&conflicts.attached_box.mainPlan.plan.contains(attached_box.position.NodeId)))) {
            //System.err.println("PLANPI");
            subgoals.UpdatedBlanked(attached_box,false);
            planPi(state,new LinkedHashSet(), false);
            //mainPlan.createPlanWithBox(state, this, null, attached_box);
        }
        else {
            if (currentGoal!=null) (currentGoal.Obj).Taken = false;
            //System.err.println("PLANPI2");
            mainPlan.createAltPaths(state, agent);
            attached_box = null;
        }

    }




}



