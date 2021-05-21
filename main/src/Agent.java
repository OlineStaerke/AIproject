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

    public ArrayList<Node> getFinalPlan(){
        return this.finalPlan;
    }

    public void executeMove(State state, Boolean NoOp) throws InterruptedException {

            Node wantedMove;
            if (NoOp) {
                wantedMove = this.position;
            }
            else {
                wantedMove = state.stringToNode.get(this.mainPlan.plan.get(0));
            }


            position = wantedMove;
            finalPlan.add(wantedMove);
            finalPlanString.add(wantedMove.getNodeId());

            state.occupiedNodes.put(position.NodeId, this);

            if (mainPlan.plan.size()>0 && !NoOp) {
                mainPlan.plan.remove(0);
            }




            if (attached_box!=null) {
                executeMoveWithoutBox(state, NoOp);
            }

            if (currentGoal!=null && attached_box==null && attachedBox(state) && mainPlan.plan.size()==0) {
                planPi(state, new LinkedHashSet(), true);
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
                        if (conflicts.conflicts!=null && conflicts.currentGoal != null && conflicts.currentGoal.gType!= SubGoals.GoalType.BoxBlanked && conflicts.mainPlan.plan.size() ==0) {

                            conflicts.bringBlank(state, conflicts);

                        }

                    }
                    if (this.blankInCircle()) {
                        conflicts.conflicts = null;
                        conflicts = null;
                    }

                }

            }


        }


    public void executeMoveWithoutBox(State state, Boolean NoOp){
        Node wantedMoveBox;
        if (attached_box.mainPlan.plan.size()==0 || NoOp) wantedMoveBox = attached_box.position;

        else wantedMoveBox = state.stringToNode.get(attached_box.mainPlan.plan.get(0));

        attached_box.finalPlan.add(wantedMoveBox);
        attached_box.finalPlanString.add(wantedMoveBox.NodeId);

        if (attached_box.mainPlan.plan.size() > 0 && !NoOp) {
            attached_box.mainPlan.plan.remove(0);
        }
        state.occupiedNodes.put(wantedMoveBox.NodeId, attached_box);
        attached_box.position = wantedMoveBox;

        if (attached_box.mainPlan.plan.size()==0 && this.currentGoal.gType.equals(SubGoals.GoalType.BoxBlanked)) {
            this.subgoals.UpdatedBlanked((Box) this.currentGoal.Obj, true);

        }
    }

    boolean attachedBox(State state) {
        if (attached_box!=null) {return true;}

        return (state.map.getAdjacent(position.NodeId).contains(currentGoal.Obj.position.NodeId));
    }

    boolean isInSubGoal() {
        if (Goal.size()==0) {
            return true;
        }
        else {
            return (Goal.contains(position.NodeId));
        }
    }

    public void planGoals(State state){
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
                    // If this is 1:1 with BoxToGoal case, remove the code (duplicate code)
                    if ((state.map.getAdjacent(position.NodeId)).contains(SG.Obj.position.NodeId)) {

                        attached_box = (Box) SG.Obj;
                        ((Box) SG.Obj).currentowner = this;

                        mainPlan.createPlanWithBox(state, this, null, (Box) SG.Obj);


                    } else {

                        attached_box = null;
                        mainPlan.createPlan(state, position.NodeId, state.map.getAdjacent(SG.Obj.position.NodeId), visited, this);
                        ((Box) SG.Obj).currentowner = this;
                    }
                    break;

                case BoxToGoal:
                    // code block

                    if ((state.map.getAdjacent(position.NodeId)).contains(SG.Obj.position.NodeId)) {

                        attached_box = (Box) SG.Obj;
                        mainPlan.createPlanWithBox(state, this, SG.Obj.Goal, (Box) SG.Obj);
                    } else {

                        attached_box = null;
                        mainPlan.createPlan(state, position.NodeId, state.map.getAdjacent(SG.Obj.position.NodeId), visited,this);
                    }
                    ((Box) SG.Obj).currentowner = this;
                    break;

                case AgentToGoal:
                    List<String> goalListAgent = new ArrayList<>(SG.Obj.Goal);
                    mainPlan.createPlan(state, position.NodeId, goalListAgent, visited,this);
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


        blank = true;
        if (mainPlan.plan.size()!=0 && ((!state.occupiedNodes.containsKey(nextMove())))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }

        if ( attached_box!= null && mainPlan.plan.size()!=0 ){

            //If the first move is its own position, look one further
            String wantedMoveAgent = mainPlan.plan.get(0);
            String wantedMoveBox = attached_box.mainPlan.plan.get(0);

            Boolean check_wantedMoveAgent = (!state.occupiedNodes.containsKey(wantedMoveAgent)) || wantedMoveAgent.equals(attached_box.position.NodeId);
            Boolean check_wantedMoveBox = (!state.occupiedNodes.containsKey(wantedMoveBox)) || wantedMoveBox.equals(position.NodeId);
            if(check_wantedMoveAgent && check_wantedMoveBox) {

                state.blankPlan = new ArrayList<>(mainPlan.plan);
                return;
            }
        }
        if (attached_box!=null && ((conflicts.mainPlan.plan.contains(attached_box.position.NodeId))||(conflicts.attached_box!=null&&conflicts.attached_box.mainPlan.plan.contains(attached_box.position.NodeId)))) {
              subgoals.UpdatedBlanked(attached_box,false);
            planPi(state,new LinkedHashSet(), false);
        }
        else {
             mainPlan.createAltPaths(state, agent);
            attached_box = null;
        }

    }

    public String nextMove() {
        for (String s: mainPlan.plan) {
            if (!s.equals(position.NodeId)) {
                String wantedMove = s;

                // if the wanted move is a box position
                if (attached_box!=null) {
                    if (wantedMove.equals(attached_box.position.NodeId) && attached_box.mainPlan.plan.size() > 0) {
                        wantedMove = attached_box.mainPlan.plan.get(mainPlan.plan.indexOf(s));
                    }

                }
                return wantedMove;
            }
        }
        return null;
    }


    public String GetWantedMove() {
        if (mainPlan.plan == null || mainPlan.plan.size() == 0) return position.NodeId;
        String wantedMove = mainPlan.plan.get(0);


        // if the wanted move is a box position
        if (attached_box!=null) {
            if (wantedMove.equals(attached_box.position.NodeId) && attached_box.mainPlan.plan.size() > 0) {
                wantedMove = attached_box.mainPlan.plan.get(0);
            }
            // wantedMove = position: Stay.
            else {
                wantedMove = mainPlan.plan.get(0);
            }
        }
        return wantedMove;
    }

    public Boolean blankInCircle() {
        Agent nextAgent = conflicts;
        HashSet<Agent> visited = new HashSet<>();
        while (nextAgent!=null) {
            visited.add(nextAgent);
            if (nextAgent.equals(this)) return true;
            nextAgent = nextAgent.conflicts;
            if (visited.contains(nextAgent)) return false;
        }
        return false;
    }


    public void findPriority(State state){
        var otherBoxes = state.boxes.values();
        int newPrio = 0;

        for(Box B: otherBoxes){


            for (String goal: B.Goal) {

                if (this.planToGoal!=null && this.planToGoal.contains(goal)) {
                    newPrio += 1;
                }
            }
        }
        for(Agent A: state.agents.values()){
            if (A.equals(this)) continue;
            for (String goal: A.Goal) {

                if (this.planToGoal!=null && this.planToGoal.contains(goal)) {
                    newPrio += 1;
                }
            }
        }

        this.PriorityValue = newPrio;
    }

    //Compare to find the agent who is furthest away from goal, by looking at the plan size.
    public static class CustomComparator implements Comparator<Agent> {
        @Override
        public int compare(Agent o1, Agent o2) {
            int o2_value = 0;
            int o1_value = 0;
            //Computes of another agents goal is on the agent path. If it is, their value should be smaller.
            if(o1.nextGoal!=null) {
                o1_value = o1.nextGoal.Obj.PriorityValue;
            }
            if(o2.nextGoal!=null) {
                o2_value = o2.nextGoal.Obj.PriorityValue;
            }

            if (o1.subgoals.ExistsBlankGoal()) {

                o1_value+= 2000;
            }
            if (o2.subgoals.ExistsBlankGoal()) {

                o2_value+= 2000;
            }

            return o2_value-o1_value;
        }
    }

    @Override
    boolean isInGoal() {
        return subgoals.InGoal();

    }
}



