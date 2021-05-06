import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SubGoals{
    public ArrayList<SubGoal> goals;
    enum GoalType{BoxBlanked, BoxToGoal, AgentToGoal} //Can put this enum above SubGoals class


    public SubGoals(ArrayList<Box> boxes, Agent A){
        System.err.println("HELLO");
        System.err.println(A.Goal);
        goals = new ArrayList<>();

        // Put possible blanks as first goals
        // These are true, and changed to false when blanked.
        for(Box b : boxes){
            var sg = new SubGoal(b, GoalType.BoxBlanked);
            sg.Finished = true;
            goals.add(sg);
        }

        // Boxes to their goal locations are added
        for(Box b : boxes){
            goals.add(new SubGoal(b, GoalType.BoxToGoal));
        }

        // Agent to its goal is added.
        if (A.Goal.size()>0){
            goals.add(new SubGoal(A, GoalType.AgentToGoal));
        }


        UpdateGoals();


    }

    // Update what goals are now fulfilled
    public void UpdateGoals(){
        for(SubGoal sg: goals){
            if (sg.gType.equals(GoalType.BoxBlanked)) continue;
            sg.Finished = sg.Obj.isInSubGoal();

        }
        Collections.sort(goals,new SubGoal.CustomComparator());

    }

    public SubGoal ExtractNextGoal(SubGoal currentGoal){

        if (currentGoal!=null) {

            for (SubGoal sg : goals) {

                if (!sg.Finished && sg.gType == GoalType.BoxBlanked) {
                    return sg;
                }
            }

            //Return the same goal, if an agent has moved to a box and the box is not yet in its goal.
            if (currentGoal.gType == GoalType.BoxToGoal && !currentGoal.Obj.isInSubGoal()) {
                return currentGoal;
            }
            //Try not to return the same box, if that box has just been blanked.
            for (SubGoal sg : goals) {

                if (!sg.Finished && !sg.Obj.position.NodeId.equals(currentGoal.Obj.position.NodeId)) {
                    return sg;
                }
            }
        }
        // Otherwise, select the next goal
        for(SubGoal sg2: goals){

            if (!sg2.Finished){
                return sg2;
            }
        }
        return null;
    }


    // Update BringBlanked subgoal manually.
    public void UpdatedBlanked(Box o, boolean newValue){
        for(SubGoal SG : goals){
            if(SG.gType.equals(GoalType.BoxBlanked) && SG.Obj.ID == (o.ID)){
                SG.Finished = newValue;
            }
        }
    }




    public static class SubGoal {
        public Object Obj;
        public GoalType gType;
        public boolean Finished;

        public SubGoal(Object o, GoalType g){
            this.Obj = o;
            gType = g;
            Finished = false;
        }

        @Override
        public String toString(){
            return "gType: " + gType.toString() + ", Object: " + Obj.toString() + " finished: " + Finished;

        }

        public static class CustomComparator implements Comparator<SubGoal> {
            @Override
            public int compare(SubGoal s1, SubGoal s2) {
                Integer s1_value = 0;
                Integer s2_value =1;

                Integer comparevalue = s2.Obj.mainPlan.plan.size() + s2_value;
                return (comparevalue).compareTo(((Integer) s1.Obj.mainPlan.plan.size())+s1_value);
            }
        }



    }

}


