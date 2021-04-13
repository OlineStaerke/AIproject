import java.lang.reflect.Array;
import java.util.*;

public class Plan {
    ArrayList<String> plan;
    HashMap<String, ArrayList<String>> precomputedDistance;

    public void createPlan(Map map, String Source,String Destination,Set<String> visited) {
        plan = breathFirstTraversal(map, Source, Destination,visited);
    }


    public void createAltPaths(State state, Node start, Map map) {
        String problem_node = plan.remove(0); //Get the string node where conflict arises.
        Plan altPlans = new Plan(); // Initialize plan
        Set<String> visited = new LinkedHashSet<>();
        if (!state.stringToNode.get(problem_node).isTunnel) {
            visited.add(problem_node); //add problem node to visited, so that the algorithm does not enter this, if we are NOT in a tunnel.

        }
        altPlans.plan = altPlans.breathFirstTraversal_altpath(state, map, start.getNodeId(), visited); //Run BFS
        altPlans.plan.remove(0); //remove current position

        ArrayList<String> reverseplan = altPlans.plan;
        Collections.reverse(reverseplan); //Reverse the new plan away from teh conflict, to add to get back to the current position.
        reverseplan.remove(0); //Remove the first element to ensure no replicates.

        altPlans.plan.addAll(reverseplan); //Add the reverseplan to the new plan.

        altPlans.plan.addAll(plan); //Return new plan
        plan = altPlans.plan;
        //Merge plans
    }


    public ArrayList<String> getPlan() {
        return this.plan;
    }

    public ArrayList<String> breathFirstTraversal_altpath(State state, Map map, String root, Set<String> visited) {
        ArrayList<String> route = new ArrayList<>();


        //if (precomputedDistance.containsKey(root.getNodeId()+goal.getNodeId())) return precomputedDistance.get(root.getNodeId()+goal.getNodeId());
        Deque<ArrayList<String>> routes = new ArrayDeque<>();


        Deque<String> queue = new ArrayDeque<>();
        Deque<Integer> queue_integer = new ArrayDeque<>();
        queue.push(root);
        queue_integer.push(0); //Ensure that the agent moves at least one step away from the tunnel.

        ArrayList<String> root_route = new ArrayList<>();
        root_route.add(root);
        routes.add(root_route);

        while (!queue.isEmpty()) {
            String vertex = queue.pollFirst();
            Integer i = queue_integer.pollFirst();
            route = routes.pollFirst();
            Node node = state.stringToNode.get(vertex);


            if (!node.isTunnel && i==1) {
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

                    //Ensure that the agent moves at least 1 step away from the tunnel.
                    if (!node.isTunnel) {
                        queue_integer.addLast(1);}
                    else {
                        queue_integer.addLast(0);}

                    //precomputedDistance.put(root.getNodeId()+v.getNodeId(), route);



                }

            }
            i+=1;
        }
        return null;
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
