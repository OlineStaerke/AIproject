import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class AlternativePlan {
    ArrayList<String> altPath;


    public ArrayList<String> createAltPaths(Node start, Plan plan, Map map) {
        String problem_node = plan.plan.remove(0); //Get the string node where conflict arises.
        String goal_node = plan.plan.get(0); //Choose a new goal node, to go to.
        Plan altPlans = new Plan(); // Initialize plan
        Set<String> visited = new LinkedHashSet<>();
        visited.add(problem_node); //add problem node to visited, so that the algorithm does not enter this.
        altPlans.createPlan(map, start.getNodeId(),goal_node,visited); //Run BFS
        altPath = altPlans.plan; //Return new plan
        altPath.addAll(plan.plan);
        //Merge plans
        return altPath;




    }
}
