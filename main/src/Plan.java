import java.lang.reflect.Array;
import java.util.*;

public class Plan {
    ArrayList<String> plan;
    HashMap<String, ArrayList<String>> precomputedDistance;

    public void createPlan(Map map, String Source,String Destination) {
        plan = breathFirstTraversal(map, Source, Destination);
    }

    public ArrayList<String> getPlan() {
        return this.plan;
    }


    public ArrayList<String> breathFirstTraversal(Map map, String root, String goal) {
        ArrayList<String> route = new ArrayList<>();


        //if (precomputedDistance.containsKey(root.getNodeId()+goal.getNodeId())) return precomputedDistance.get(root.getNodeId()+goal.getNodeId());
        Deque<ArrayList<String>> routes = new ArrayDeque<>();

        Set<String> visited = new LinkedHashSet<>();
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
