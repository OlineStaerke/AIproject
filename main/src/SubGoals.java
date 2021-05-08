import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SubGoals{
    public ArrayList<SubGoal> goals;
    enum GoalType{BoxBlanked, BoxToGoal, AgentToGoal} //Can put this enum above SubGoals class


    public SubGoals(ArrayList<Box> boxes, Agent A){


        goals = new ArrayList<>();

        // Put possible blanks as first goals
        // These are true, and changed to false when blanked.
        for(Box b : boxes){
            var sg = new SubGoal(b, GoalType.BoxBlanked, A);
            sg.Finished = true;
            goals.add(sg);
        }

        // Boxes to their goal locations are added
        for(Box b : boxes){
            if (b.Goal.size()!=0) {

                goals.add(new SubGoal(b, GoalType.BoxToGoal, A));
            }
        }

        // Agent to its goal is added.
        if (A.Goal.size()>0){
            goals.add(new SubGoal(A, GoalType.AgentToGoal, A));
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

    public Boolean InGoal(){
        for(SubGoal sg: goals){
            if (!sg.Obj.isInSubGoal()) {

                return false;}

        }
        return true;


    }

    public Boolean ExistsBlankGoal() {
        for (SubGoal sg : goals) {
            if (sg.gType == GoalType.BoxBlanked && !sg.Finished) return true;
        }
        return false;
    }

    public SubGoal ExtractNextGoal(SubGoal currentGoal){

        if (currentGoal!=null) {
            System.err.println("WHAT?");

            for (SubGoal sg : goals) {
                System.err.println(sg + " "+ sg.Obj.Taken);

                if (!sg.Finished && sg.gType == GoalType.BoxBlanked && !((sg.Obj).Taken)) {
                    (sg.Obj).Taken = true;
                    return sg;
                }
            }

            //Return the same goal, if an agent has moved to a box and the box is not yet in its goal.
            if ((currentGoal.gType == GoalType.BoxToGoal)&& !currentGoal.Obj.isInSubGoal()) {
                (currentGoal.Obj).Taken=true;
                return currentGoal;
            }
            //Try not to return the same box, if that box has just been blanked.
            for (SubGoal sg : goals) {

                if (!sg.Finished && !(sg.Obj).Taken && !sg.Obj.position.NodeId.equals(currentGoal.Obj.position.NodeId)) {
                    (sg.Obj).Taken = true;
                    return sg;
                }
            }
        }
        // Otherwise, select the next goal
        for(SubGoal sg2: goals){

            if (!sg2.Finished && !(sg2.Obj).Taken){
                (sg2.Obj).Taken=true;
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
        public static Agent agent;


        public SubGoal(Object o, GoalType g, Agent a){
            this.Obj = o;
            gType = g;
            Finished = false;
            this.agent = a;
        }

        @Override
        public String toString(){
            return "gType: " + gType.toString() + ", Object: " + Obj.toString() + " finished: " + Finished;

        }

        public static class CustomComparator implements Comparator<SubGoal> {
            @Override
            public int compare(SubGoal s1, SubGoal s2) {
                String[] s1_value = s1.Obj.position.NodeId.split(" ");
                String[] s2_value = s2.Obj.position.NodeId.split(" ");
                String[] agent_value = agent.position.NodeId.split(" ");
                Integer s1_distanceToAgent = ((Integer.parseInt(s1_value[0]))-Integer.parseInt(agent_value[0]))^2+((Integer.parseInt(s1_value[1]))-Integer.parseInt(agent_value[1]))^2;
                Integer s2_distanceToAgent = ((Integer.parseInt(s2_value[0]))-Integer.parseInt(agent_value[0]))^2+((Integer.parseInt(s2_value[1]))-Integer.parseInt(agent_value[1]))^2;

                //Integer comparevalue = s2.Obj.mainPlan.plan.size() + s2_value;
                return (s2_distanceToAgent).compareTo(s1_distanceToAgent);
            }
        }



    }

}


