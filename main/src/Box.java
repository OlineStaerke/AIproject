import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Objects;

public class Box extends Object {

    public Agent owner;


    public Box(Node node, char id){
        this.position = node;
        this.ID = id;
        this.finalPlan = new ArrayList<>();
        this.finalPlanString = new ArrayList<>();
        this.mainPlan = new Plan();
    }

    public void bringBlank(State state, Map map, Agent otheragent){


    }

    @Override
    boolean isInSubGoal() {
        if (Objects.isNull(Goal)) return true;
        return Goal.NodeId.equals(position.NodeId);
    }

    @Override
    boolean isInGoal() {
        return false;
    }


    @Override
    void bringBlank(State state, Agent agent) throws InterruptedException {
        System.err.println("BRING BLANK inside");
        //System.err.println("ASHDAHSDH: " + owner.currentGoal.gType.equals(SubGoals.GoalType.BoxBlanked));

        if ((mainPlan.plan.size()>0) && (!state.occupiedNodes.containsKey(mainPlan.plan.get(0) )) && (owner.attached_box!=null && owner.attached_box.ID==ID && owner.attachedBox(state))){
            System.err.println("HI");
            state.blankPlan = new ArrayList<>(mainPlan.plan);
            return;
        }
        // Placeholder currentGoal is created if null (no current task)
        if (owner.currentGoal == null) {
            owner.currentGoal = new SubGoals.SubGoal(this, SubGoals.GoalType.AgentToGoal);
            System.err.println("HELLO");
        }

        owner.subgoals.UpdatedBlanked(this, false);
        System.err.println("Owner current responsibility (NEW): " + owner.currentGoal);
        if (owner.mainPlan.plan.size()==0 || (owner.currentGoal.Obj.ID == ID && owner.attachedBox(state))) {
            owner.mainPlan.plan = new ArrayList<>();
            System.err.println("SG State: " + owner.subgoals.goals);
            owner.planPi(state, new LinkedHashSet<>());
            System.err.println("Owner current responsibility (NEW): " + owner.currentGoal);
        }







        // Kill the old mainplan (our #1 prio is now to help the other agent) - Iff we are not currently helping anybody else



    }


}
