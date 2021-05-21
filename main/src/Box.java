import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

public class Box extends Object {

    public Agent currentowner;
    public ArrayList<Agent> owners = new ArrayList<>();
    public ArrayList<String> conflictRoute = new ArrayList<>();
    Boolean blankByOwn;
    Box conflict_box;





    public Box(Node node, String id){
        this.position = node;
        this.ID = id;
        this.finalPlan = new ArrayList<>();
        this.finalPlanString = new ArrayList<>();
        this.mainPlan = new Plan();
        this.Taken = false; //True if it is has been taken by an agent
        this.planToGoal = new ArrayList<>();
        this.blankByOwn = false; //True if it has been brough blank by its owner.

    }

    public void bringBlank(State state, Map map, Agent otheragent){


    }

    @Override
    boolean isInSubGoal() {
        if (Goal.size()==0) return true;
        return Goal.contains(position.NodeId);
    }

    @Override
    boolean isInGoal() {
        return false;
    }

    public void setGoal(String goal, State state) {
        for (String g : Goal) {
            if (!g.equals(goal)) {
                ArrayList<Box> boxes = state.goals.get(g);
                boxes.remove(this);
                state.goals.put(g,boxes);
            }
        }
        if (Goal.contains(goal)) {
            Goal = new ArrayList<>();
            Goal.add(goal);

            for (Box box : state.boxes.values()) {
                if (box != this) {
                    box.Goal.remove(goal);
                }

            }
        }
    }

    public void findPlanToGoal(State state) {
        Plan plan = new Plan();
        plan.createPlan(state,position.NodeId,Goal,new LinkedHashSet<>(),null);
        planToGoal = plan.plan;
    }


    @Override
    void bringBlank(State state, Agent agent) throws InterruptedException {

        //Dont compute a new path if we are already moving away
        if ((mainPlan.plan.size()>0) && (!state.occupiedNodes.containsKey(mainPlan.plan.get(0))) && (currentowner.attached_box!=null && currentowner.attached_box.ID==ID && currentowner.attachedBox(state))){
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }
        // Placeholder currentGoal is created if null (no current task)

        if (currentowner.currentGoal == null) {
            currentowner.currentGoal = new SubGoals.SubGoal(this, SubGoals.GoalType.AgentToGoal, agent);

        }


        //Add this subgoal to the last one.
        if (currentowner.currentGoal.gType == SubGoals.GoalType.BoxBlanked) {
            currentowner.subgoals.goals.remove(currentowner.currentGoal);
            currentowner.subgoals.goals.add(currentowner.currentGoal);
        }

        //System.err.println("Owner current responsibility (OLD): " + currentowner.currentGoal);

            currentowner.subgoals.UpdatedBlanked(this, false); //Now box is false in finished


            //TODO: Overlook these if statements again
        //(currentowner.currentGoal.Obj.equals(this) && currentowner.attachedBox(state))
            if (currentowner.mainPlan.plan.size()== 0||state.occupiedNodes.containsKey(currentowner.nextMove())) {
                currentowner.mainPlan.plan = new ArrayList<>();
                currentowner.planPi(state, new LinkedHashSet<>(), false);

                //System.err.println("Owner current responsibility (NEW): " + currentowner.currentGoal);
            }



    }


    public void findPriority(State state) {


            var otherBoxes = state.boxes.values();
            int newPrio = 0;

            for (Box B : otherBoxes) {
                if (B.equals(this)) continue;

                for (String goal : B.Goal) {


                    if (this.planToGoal != null && this.planToGoal.contains(goal)) {
                        newPrio += 1;
                    }
                }
            }

            for (Agent A : state.agents.values()) {
                for (String goal : A.Goal) {
                    if (this.planToGoal != null && this.planToGoal.contains(goal)) {
                        newPrio += 1;
                    }
                }
            }
            Plan P = new Plan();
            this.PriorityValue = newPrio;//+ P.PriobreathFirstTraversal(state, position.NodeId);


    }

        public void findOwner (State state){
            for (Agent agent : owners) {
                if ((!(agent.currentGoal.Obj instanceof Box) || agent.mainPlan.plan.size() == 0)) {
                    currentowner = agent;
                    return;
                }
            }
            currentowner = owners.get(0);
        }



}
