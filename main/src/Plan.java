import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.*;

public class Plan {
    ArrayList<String> plan;
    HashMap<String, ArrayList<String>> precomputedDistance;

    public void createPlan(Map map, String Source,String Destination,Set<String> visited) {
        if (Destination == null) return;
        plan = breathFirstTraversal(map, Source, Destination,visited);
    }


    public void createAltPaths(State state, Node start, Map map, ArrayList<String> otherAgentPlan, String Destination, String problem_node) {
        System.err.println("Alternative plan");
        Set<String> visited = new LinkedHashSet<>();

        Plan altPlans = new Plan(); // Initialize plan
        if (!state.stringToNode.get(problem_node).isTunnel) {
            visited.add(problem_node); //add problem node to visited, so that the algorithm does not enter this, if we are NOT in a tunnel.

        }

        altPlans.plan = altPlans.breathFirstTraversal_altpath(state, map, start.getNodeId(), visited,otherAgentPlan); //Run BFS, to cerate new alternative plan
        createPlan(map,altPlans.plan.get(altPlans.plan.size()-1),Destination,new LinkedHashSet<>()); //Find new main plan back to goal
        altPlans.plan.addAll(plan); //Return new plan
        plan = altPlans.plan; //Overwrite old plan
        plan.remove(0); //Remove first index

    }


    public ArrayList<String> getPlan() {
        return this.plan;
    }

    public ArrayList<String> breathFirstTraversal_altpath(State state, Map map, String root, Set<String> visited,ArrayList<String> otherAgentPlan) {
        ArrayList<String> route = new ArrayList<>();
        Deque<ArrayList<String>> routes = new ArrayDeque<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.push(root);

        //Adding root to the list of routes to start with
        ArrayList<String> root_route = new ArrayList<>();
        root_route.add(root);
        routes.add(root_route);


        //Start runnning BFS
        while (!queue.isEmpty()) {
            String vertex = queue.pollFirst();
            route = routes.pollFirst();
            Node node = state.stringToNode.get(vertex);

            if (!visited.contains(vertex)) {

                //When we are out of a tunnel, and away from the conflicting agents route, return the alternative path
                if (!node.isTunnel) {

                    if (!otherAgentPlan.contains(node.getNodeId())) {

                        return route;
                    }

                }
                visited.add(vertex);

                for (String v : map.getAdjacent(vertex)) {

                    ArrayList<String> newroute = new ArrayList<>(route) ;
                    queue.addLast(v);
                    newroute.add(v);
                    routes.addLast(newroute);
                }
            }
        }
        return null;
    }


    public ArrayList<String> breathFirstTraversal(Map map, String root, String goal, Set<String> visited) {
        if (goal == null) return new ArrayList<>();

        ArrayList<String> route = new ArrayList<>();
        Deque<ArrayList<String>> routes = new ArrayDeque<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.push(root);

        //Adding root to the list of routes to start with
        ArrayList<String> root_route = new ArrayList<>();
        root_route.add(root);
        routes.add(root_route);


        //Start runnning BFS
        while (!queue.isEmpty()) {
            String vertex = queue.pollFirst();
            route = routes.pollFirst();

            //If we are in goal, stop BFS
            if (vertex.equals(goal)) {
                return route;
            }

            //If not in goal, check neighbours not in visited.
            if (!visited.contains(vertex)) {
                visited.add(vertex);


                for (String v : map.getAdjacent(vertex)) {
                    ArrayList<String> newroute = new ArrayList<>(route) ;
                    queue.addLast(v);
                    newroute.add(v);
                    routes.addLast(newroute);

                }
            }
        }
        return route;
    }


}
