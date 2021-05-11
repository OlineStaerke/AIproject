import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SubGoals{
    public ArrayList<SubGoal> goals;
    enum GoalType{BoxBlanked, BoxToGoal, AgentToGoal, AgentBlanked} //Can put this enum above SubGoals class


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

        /**
        //If agent has been blanked
        var sg = new SubGoal(A, GoalType.AgentBlanked,A);
        sg.Finished = true;
        goals.add(sg);
         **/


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
            if (!sg.Obj.isInSubGoal() || (sg.gType == GoalType.BoxBlanked && !sg.Finished)) {

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

            currentGoal.Obj.Taken=false;
            SubGoal savesg = null;

            for (SubGoal sg : goals) {
                if (!sg.Finished && sg.gType == GoalType.BoxBlanked && !((sg.Obj).Taken)) {
                    return sg;
                }
                //add current Obj as the last element in goals, to make sure other goals are treated first, if it has been brought blank by it self.
                if (sg.Obj.ID == currentGoal.Obj.ID && sg.gType == GoalType.BoxToGoal && currentGoal.Obj instanceof Box && ((Box) currentGoal.Obj).blankByOwn) {
                    savesg = sg;

                }
            }
            //see above comment
            if (savesg!=null) {
                goals.remove(savesg);
                goals.add(savesg);
            }

            //Return the same goal, if an agent has moved to a box and the box is not yet in its goal.
            if ((currentGoal.gType == GoalType.BoxToGoal)&& !currentGoal.Obj.isInSubGoal()) {
                return currentGoal;
            }
        }
        // Otherwise, select the next goal
        for(SubGoal sg2: goals){
            if (!sg2.Finished && !(sg2.Obj).Taken){
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
                Integer s1_value = 0;
                Integer s2_value = 0;
                /**
                String[] s1_value = s1.Obj.position.NodeId.split(" ");
                String[] s2_value = s2.Obj.position.NodeId.split(" ");
                String[] agent_value = agent.position.NodeId.split(" ");
                Integer s2_distanceToAgent = ((Integer.parseInt(s1_value[0]))-Integer.parseInt(agent_value[0]))^2+((Integer.parseInt(s1_value[1]))-Integer.parseInt(agent_value[1]))^2;
                Integer s1_distanceToAgent = ((Integer.parseInt(s2_value[0]))-Integer.parseInt(agent_value[0]))^2+((Integer.parseInt(s2_value[1]))-Integer.parseInt(agent_value[1]))^2;

                //Integer comparevalue = s2.Obj.mainPlan.plan.size() + s2_value;
                return (s2_distanceToAgent).compareTo(s1_distanceToAgent);
                 **/

                if (s1.Obj instanceof Box) {

                    for (String goal: s2.Obj.Goal) {
                        if (((Box) s1.Obj).planToGoal.contains(goal) && s1.Obj.ID != s2.Obj.ID) {
                            s2_value += 100;
                        }
                    }
                }
                if (s2.Obj instanceof Box) {

                    for (String goal: s1.Obj.Goal) {
                        if (((Box) s2.Obj).planToGoal.contains(goal) && s1.Obj != s2.Obj) {
                            s1_value += 100;
                        }
                    }
                }

                return (s1_value).compareTo((Integer) s2_value);
            }
        }



    }

}


