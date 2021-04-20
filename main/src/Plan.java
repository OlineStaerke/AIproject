import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.*;

public class Plan {
    ArrayList<String> plan;
    HashMap<String, ArrayList<String>> precomputedDistance;

    public void createPlan(Map map, String Source,String Destination,Set<String> visited) {
        plan = breathFirstTraversal(map, Source, Destination,visited);
    }


    public void createAltPaths(State state, Node start, Map map, ArrayList<String> otherAgentPlan, String Destination) {
        System.err.println("Find new path");
        Set<String> visited = new LinkedHashSet<>();

        String problem_node = plan.get(0);

        Plan altPlans = new Plan(); // Initialize plan
        if (!state.stringToNode.get(problem_node).isTunnel) {
            System.err.println("problemnde:"+problem_node);
            System.err.println("startnode:"+start);

            visited.add(problem_node); //add problem node to visited, so that the algorithm does not enter this, if we are NOT in a tunnel.

        }

        altPlans.plan = altPlans.breathFirstTraversal_altpath(state, map, start.getNodeId(), visited,otherAgentPlan); //Run BFS, to cerate new alternative plan
        createPlan(map,altPlans.plan.get(altPlans.plan.size()-1),Destination,new LinkedHashSet<>()); //Find new main plan back to goal
        System.err.println("AltPlan"+altPlans.plan);
        altPlans.plan.addAll(plan); //Return new plan
        plan = altPlans.plan; //Overwrite old plan
        plan.remove(0); //Remove first index
        System.err.println(plan);// .

    }


    public ArrayList<String> getPlan() {
        return this.plan;
    }

    public ArrayList<String> breathFirstTraversal_altpath(State state, Map map, String root, Set<String> visited,ArrayList<String> otherAgentPlan) {
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
            Node node = state.stringToNode.get(vertex);

            //!otherAgentPlan.contains(node.NodeId) &&



            if (!visited.contains(vertex)) {
                if (!node.isTunnel) {
                    System.err.println(otherAgentPlan.get(route.size())+" "+node.getNodeId());
                    if (!otherAgentPlan.get(route.size()-1).equals(node.getNodeId())) {


                        //precomputedDistance.put(root+goal, route);

                        return route;
                    }
                }
                System.err.println("veretex"+vertex);
                System.err.println("visited"+visited);
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
