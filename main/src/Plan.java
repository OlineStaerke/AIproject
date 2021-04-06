import java.lang.reflect.Array;
import java.util.*;

public class Plan {
    ArrayList<Node> plan;
    HashMap<String, ArrayList<Node>> precomputedDistance;

    public void createPlan(Node Source,Node Destination) {

    }

    public ArrayList<Node> getPlan() {
        return this.plan;
    }


    public ArrayList<Node> breathFirstTraversal(Map map, Node root, Node goal) {
        ArrayList<Node> route = new ArrayList<>();

        if (precomputedDistance.containsKey(root.getNodeId()+goal.getNodeId())) return precomputedDistance.get(root.getNodeId()+goal.getNodeId());
        Deque<ArrayList<Node>> routes = new ArrayDeque<>();

        Set<Node> visited = new LinkedHashSet<>();
        Deque<Node> queue = new ArrayDeque<>();


        queue.push(root);

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
                    ArrayList<Node> newroute = route;
                    queue.addLast(v);
                    newroute.add(v);
                    routes.addLast(newroute);
                    precomputedDistance.put(root.getNodeId()+v.getNodeId(), route);



                }

            }
        }

        return route;
    }


}
