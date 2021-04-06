import java.lang.reflect.Array;
import java.util.*;

public class Plan {
    ArrayList<Node> plan;
    HashMap<String, ArrayList<Node>> precomputedDistance;

    public void createPlan(Map map, Node Source,Node Destination) {
        plan = breathFirstTraversal(map, Source, Destination);
    }

    public ArrayList<Node> getPlan() {
        return this.plan;
    }


    public ArrayList<Node> breathFirstTraversal(Map map, Node root, Node goal) {
        ArrayList<Node> route = new ArrayList<>();

        System.out.println("GOAL"+goal);

        //if (precomputedDistance.containsKey(root.getNodeId()+goal.getNodeId())) return precomputedDistance.get(root.getNodeId()+goal.getNodeId());
        Deque<ArrayList<Node>> routes = new ArrayDeque<>();

        Set<Node> visited = new LinkedHashSet<>();
        Deque<Node> queue = new ArrayDeque<>();


        queue.push(root);
        System.out.println(root);

        ArrayList<Node> root_route = new ArrayList<>();
        root_route.add(root);
        routes.add(root_route);


        while (!queue.isEmpty()) {
            Node vertex = queue.pollFirst();
            route = routes.pollFirst();

            if (vertex.equals(goal)) {
                precomputedDistance.put(root.getNodeId()+goal.getNodeId(), route);

                return route;
            }
            if (!visited.contains(vertex)) {
                visited.add(vertex);

                for (Node v : map.getAdjacent(vertex)) {
                    ArrayList<Node> newroute = new ArrayList<>(route) ;
                    queue.addLast(v);
                    newroute.add(v);
                    System.out.println(newroute);
                    routes.addLast(newroute);
                    //precomputedDistance.put(root.getNodeId()+v.getNodeId(), route);



                }

            }
        }

        return route;
    }


}
