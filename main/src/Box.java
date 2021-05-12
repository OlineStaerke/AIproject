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
    int PriorityValue;





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


        currentowner.subgoals.UpdatedBlanked(this, false); //Now box is false in finished

        //Add this subgoal to the last one.
        if (currentowner.currentGoal.gType == SubGoals.GoalType.BoxBlanked) {
            currentowner.subgoals.goals.remove(currentowner.currentGoal);
            currentowner.subgoals.goals.add(currentowner.currentGoal);
        }

        //System.err.println("Owner current responsibility (OLD): " + currentowner.currentGoal);


        //TODO: Overlook these if statements again
        if (currentowner.mainPlan.plan.size()==0 || (currentowner.currentGoal.Obj.ID == ID && currentowner.attachedBox(state))) {
            currentowner.mainPlan.plan = new ArrayList<>();
            currentowner.planPi(state, new LinkedHashSet<>());
            //System.err.println("Owner current responsibility (NEW): " + currentowner.currentGoal);
        }







        // Kill the old mainplan (our #1 prio is now to help the other agent) - Iff we are not currently helping anybody else



    }


    public void findPriority(ArrayList<Box> otherBoxes){
        int newPrio = 0;

        for(Box B: otherBoxes){
            if (B.equals(this)) continue;

            for (String goal: B.Goal) {
                if (this.planToGoal.contains(goal)) {
                    newPrio += 1;
                }
            }
        }
        PriorityValue = newPrio;

    }


}
