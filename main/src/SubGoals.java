import java.util.ArrayList;

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
    }

    public SubGoal ExtractNextGoal(){
        System.err.println("Allgoals:"+goals);
        for(SubGoal sg: goals){


            if (!sg.Finished){
                return sg;
            }
        }
        return null;
    }


    public void UpdatedBlanked(Box o, boolean newValue){
        for(SubGoal SG : goals){
            System.err.println("here: " +SG);
            if(SG.gType.equals(GoalType.BoxBlanked) && SG.Obj.ID == o.ID){
                SG.Finished = newValue;
                System.err.println("Updated!; " + SG);
                return;
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



    }

}


