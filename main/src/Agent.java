import java.util.*;

public class Agent extends Object {

    ArrayList<Box> boxes = new ArrayList<>();
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
            // If a NoOP has been requested, save the agent's wanted move to be his own position
            if (NoOp) {
                wantedMove = this.position;
            }
            // Else query his next expected move
            else {
                wantedMove = state.stringToNode.get(this.mainPlan.plan.remove(0));
            }

            // Update agent's position to be his new move
            position = wantedMove;
            // Add it to his final plan
            finalPlan.add(wantedMove);
            finalPlanString.add(wantedMove.getNodeId());

            // Add his new position to occupiedNodes
            state.occupiedNodes.put(position.NodeId, this);

            // If he is attached to a box execute a move with it
            if (attached_box!=null) {
                executeMoveWithBox(state, NoOp);
            }

            // If he is close to a box, plan movement with it and attach the agent to it i.e. (SecondTry == True)
            if (currentGoal!=null && attached_box==null && attachedBox(state) && mainPlan.plan.size()==0) {
                planPi(state, new LinkedHashSet(), true);
            }

            // The agent is requested to blank (move aside)
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


    public void executeMoveWithBox(State state, Boolean NoOp){
        Node wantedMoveBox;

        // If the box has no plan or a NoOP request for the agent has been requested
        if (attached_box.mainPlan.plan.size()==0 || NoOp) wantedMoveBox = attached_box.position;
        // Else, fetch the boxes next action
        else wantedMoveBox = state.stringToNode.get(attached_box.mainPlan.plan.remove(0));

        // Add it to its final plan
        attached_box.finalPlan.add(wantedMoveBox);
        attached_box.finalPlanString.add(wantedMoveBox.NodeId);

        // Add the box' position to the occupied nodes
        state.occupiedNodes.put(wantedMoveBox.NodeId, attached_box);
        attached_box.position = wantedMoveBox;

        // If the box has no plan and the agent has been requested to blank this box, update it
        if (attached_box.mainPlan.plan.size()==0 && this.currentGoal.gType.equals(SubGoals.GoalType.BoxBlanked)) {
            this.subgoals.UpdatedBlanked((Box) this.currentGoal.Obj, true);

        }
    }

    boolean attachedBox(State state) {
        if (attached_box!=null) {return true;}

        return (isBeside(state));
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
        if (!secondTry) SG = subgoals.ExtractNextGoal(currentGoal, state);

        currentGoal = SG;


        if (currentGoal != null) {
            currentGoal.Obj.Taken = true;
            switch (currentGoal.gType) {
                case BoxBlanked:
                    // The agent has been requested to blank one of its boxes
                    if (isBeside(state)) {

                        // Attach the box to the agent
                        attached_box = (Box) SG.Obj;

                        // Create a plan to move this box
                        mainPlan.createPlanWithBox(state, this, null, (Box) SG.Obj);


                    } else {

                        // Find a plan for the agent to reach this box that has been requested to be blanked
                        attached_box = null;
                        mainPlan.createPlan(state, position.NodeId, state.map.getAdjacent(currentGoal.Obj.position.NodeId), this);
                        // To make sure nobody else takes this box he is going towards to
                    }
                    ((Box) SG.Obj).currentowner = this;
                    break;

                case BoxToGoal:

                    // The agent has reached the box and now creates a plan to bring the box to its goal
                    if (isBeside(state)) {

                        attached_box = (Box) SG.Obj;
                        mainPlan.createPlanWithBox(state, this, SG.Obj.Goal, (Box) SG.Obj);
                    }
                    // The agent is not by the box and first needs to go there
                    else {

                        attached_box = null;
                        mainPlan.createPlan(state, position.NodeId, state.map.getAdjacent(currentGoal.Obj.position.NodeId),this);
                    }
                    ((Box) SG.Obj).currentowner = this;
                    break;

                case AgentToGoal:

                    // Query the goal he needs to go to
                    List<String> goalListAgent = new ArrayList<>(SG.Obj.Goal);
                    // Create a plan to go to its goal
                    mainPlan.createPlan(state, position.NodeId, goalListAgent,this);
                    attached_box = null;
                    planToGoal = new ArrayList<>(mainPlan.plan);
                    break;
                case AgentBlanked:
                    bringBlank(state,conflicts);

            }
        }
        else {
            attached_box = null;
        }
    }


    public boolean isBeside(State state){
        // Is the agent close to its box
        return (state.map.getAdjacent(position.NodeId)).contains(currentGoal.Obj.position.NodeId);
    }

    public void bringBlank(State state, Agent agent) throws InterruptedException {

        // The agent is set in a state as "blanked"
        blank = true;

        // If the agent has a plan and his next moving action is not occupied
        // Then proceed to create a blanking plan
        if (mainPlan.plan.size()!=0 && (!state.occupiedNodes.containsKey(nextMove()))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }

        // If the agent has an attached box AND
        // The agent's box' position is in the conflicting agent's plan OR
        // The conflicting agent has a box and his box' plan contains our agent's box's position
        // Then update the blank plan and create a new plan to let him through
        if (attached_box!=null && ((conflicts.mainPlan.plan.contains(attached_box.position.NodeId))||(conflicts.attached_box!=null&&conflicts.attached_box.mainPlan.plan.contains(attached_box.position.NodeId)))) {
            subgoals.UpdatedBlanked(attached_box,false);
            planPi(state,new LinkedHashSet(), false);
        }
        // The agent has no box, he will just find a new plan to let the other agent through
        else {
            mainPlan.createAltPaths(state, agent);
            attached_box = null;
        }
    }

    // Look-up the next agent's move (ignoring NoOP's)
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

    // Look-up
    public String getWantedMoveWithBox() {
        if (mainPlan.plan == null || mainPlan.plan.size() == 0) return position.NodeId;
        String wantedMove = mainPlan.plan.get(0);


        if (attached_box!=null) {
            // if the wanted move is a box position the agent will move into the box' position
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



