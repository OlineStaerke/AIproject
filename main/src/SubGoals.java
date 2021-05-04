import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SubGoals{
    enum GoalType{BoxBlanked, BoxToGoal, AgentToGoal, NULL}
    public ArrayList<SubGoal> goals;


    public SubGoals(ArrayList<Box> boxes, Agent A){
        goals = new ArrayList<>();

        // Put possible blanks as first goals
        // These are true, and changed to false when blanked.
        for(Box b : boxes){
            goals.add(new SubGoal(b, GoalType.BoxBlanked));
            goals.get(0).Finished = true;
        }

        for(Box b : boxes){
            goals.add(new SubGoal(b, GoalType.BoxToGoal));

        }

        if (A.Goal != null){
            goals.add(new SubGoal(A, GoalType.AgentToGoal));
        }

        UpdateGoals();


    }

    public void UpdateGoals(){
        //System.err.println("GOALS"+ goals);
        for(SubGoal sg: goals){
            if (sg.gType.equals(GoalType.BoxBlanked)) continue;
            sg.Finished = sg.Obj.isInSubGoal();

        }
        Collections.sort(goals,new SubGoal.CustomComparator());

    }

    public SubGoal ExtractNextGoal(SubGoal currentGoal){
        //System.err.println("Allgoals:"+goals);

        if (currentGoal!=null) {

            for (SubGoal sg : goals) {

                if (!sg.Finished && sg.gType == GoalType.BoxBlanked) {
                    currentGoal = sg;
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
                    currentGoal = sg;
                    return sg;
                }
            }
        }
        for(SubGoal sg2: goals){

            if (!sg2.Finished){
                currentGoal =sg2;
                return sg2;
            }
        }
        return null;
    }


    public void UpdatedBlanked(Box o, boolean newValue){
        for(SubGoal SG : goals){
            System.err.println("here: " +SG);
            if(SG.gType.equals(GoalType.BoxBlanked) && SG.Obj.ID == (o.ID)){
                SG.Finished = newValue;
                System.err.println("Updated!; " + SG);
                return;
            }
            else if(!SG.gType.equals(GoalType.BoxBlanked) && SG.Obj.ID == o.ID) {
                 goals.remove(SG);
                 goals.add(goals.size()-1, SG);
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
                /**
                if (s1.gType.equals(GoalType.AgentToGoal)) {
                    s1_value =1000;
                }
                if (s1.gType.equals(GoalType.AgentToGoal)) {
                    s2_value =1000;
                }
                **/

                //Computes of another agents goal is on the agent path. If it is, their value should be smaller.

                /**
                if (s1.Obj.mainPlan.plan.contains(s2.Obj.Goal.NodeId)) {
                    s2_value = 1000;
                }
                if (s1.Obj.mainPlan.plan.contains(s2.Obj.Goal.NodeId)) {
                    s1_value = 1000;
                }
                 **/

                Integer comparevalue = s2.Obj.mainPlan.plan.size() + s2_value;
                return (comparevalue).compareTo(((Integer) s1.Obj.mainPlan.plan.size())+s1_value);
            }
        }



    }

}


