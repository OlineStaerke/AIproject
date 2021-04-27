import java.util.ArrayList;

public class SubGoals{
    enum GoalType{BoxBlanked, BoxToGoal, AgentToGoal}
    public ArrayList<SubGoal> goals;


    public SubGoals(ArrayList<Box> boxes, Agent A){
        goals = new ArrayList<>();

        for(Box b : boxes){
            goals.add(new SubGoal(b, GoalType.BoxToGoal));
        }

        if (A.Goal != null){
            goals.add(new SubGoal(A, GoalType.AgentToGoal));
        }



    }

    public void UpdateGoals(){
        //System.err.println("GOALS"+ goals);
        for(SubGoal sg: goals){

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


    public void AddBringBlankBoxAsGoal(Object o){
        goals.add(0, new SubGoal(o, GoalType.BoxBlanked));
    }



    public class SubGoal {
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


