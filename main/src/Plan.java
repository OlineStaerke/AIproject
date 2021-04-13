import java.lang.reflect.Array;
import java.util.*;

public class Plan {
    ArrayList<String> plan;
    HashMap<String, ArrayList<String>> precomputedDistance;

    public void createPlan(Map map, String Source,String Destination,Set<String> visited) {
        plan = breathFirstTraversal(map, Source, Destination,visited);
    }


    public void createAltPaths(Node start, Map map) {
        String problem_node = plan.remove(0); //Get the string node where conflict arises.
        String goal_node = plan.get(0); //Choose a new goal node, to go to.
        Plan altPlans = new Plan(); // Initialize plan
        Set<String> visited = new LinkedHashSet<>();
        visited.add(problem_node); //add problem node to visited, so that the algorithm does not enter this.
        altPlans.createPlan(map, start.getNodeId(), goal_node, visited); //Run BFS
        altPlans.plan.remove(0);
        altPlans.plan.addAll(plan); //Return new plan
        plan = altPlans.plan;
        System.err.println(plan);
        //Merge plans
    }


    public ArrayList<String> getPlan() {
        return this.plan;
    }


    public ArrayList<String> breathFirstTraversal(Map map, String root, String goal, Set<String> visited) {
        ArrayList<String> route = new ArrayList<>();


        //if (precomputedDistance.containsKey(root.getNodeId()+goal.getNodeId())) return precomputedDistance.get(root.getNodeId()+goal.getNodeId());
        Deque<ArrayList<String>> routes = new ArrayDeque<>();


        Deque<String> queue = new ArrayDeque<>();


        queue.push(root);

        ArrayList<String> root_route = new ArrayList<>();
        root_route.add(root);
        routes.add(root_route);


        while (!queue.isEmpty()) {
            String vertex = queue.pollFirst();
            route = routes.pollFirst();

            if (vertex.equals(goal)) {
                //precomputedDistance.put(root+goal, route);

                return route;
            }
            if (!visited.contains(vertex)) {
                visited.add(vertex);


                for (String v : map.getAdjacent(vertex)) {

                    ArrayList<String> newroute = new ArrayList<>(route) ;
                    queue.addLast(v);
                    newroute.add(v);

                    routes.addLast(newroute);
                    //precomputedDistance.put(root.getNodeId()+v.getNodeId(), route);



                }

            }
        }
        return route;
    }


}
