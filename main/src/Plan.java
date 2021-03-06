import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.*;

public class Plan {
    ArrayList<String> plan = new ArrayList<String>();
    HashMap<String, ArrayList<String>> precomputedDistance;

    public void createPlan(State state, String Source,List<String> Destination,Set<String> visited) {
        Map map = state.map;
        if (Destination == null) return;
        state.UpdateOccupiedNodes();
        //System.err.println("OCC"+state.occupiedNodes.keySet());
        visited.addAll(state.occupiedNodes.keySet());
        visited.remove(Source);
        plan = breathFirstTraversal(map, Source, Destination,visited);

        if (plan == null) {
            LinkedHashSet visitedNoTunnel = new LinkedHashSet<String>();
            for (String v: state.occupiedNodes.keySet()) {
                Node n = state.stringToNode.get(v);
                if (state.occupiedNodes.get(v) instanceof Box) {
                    visitedNoTunnel.add(n.NodeId);
                }
            }

            visitedNoTunnel.remove(Source);
            plan = breathFirstTraversal(map, Source, Destination,visitedNoTunnel);

        }

        if (plan == null) {
            LinkedHashSet visitedNoTunnel = new LinkedHashSet<String>();
            for (String v: state.occupiedNodes.keySet()) {
                Node n = state.stringToNode.get(v);
                if (!n.isTunnel&& !n.isTunnelDynamic) {
                    visitedNoTunnel.add(n.NodeId);
                }
            }

            visitedNoTunnel.remove(Source);
            plan = breathFirstTraversal(map, Source, Destination,visitedNoTunnel);

        }
        if (plan==null){
            plan = breathFirstTraversal(map, Source, Destination,new LinkedHashSet<>());

        }



    }

    public void createPlanWithBox(State state,Agent agent, ArrayList<String> goal,Box box) throws InterruptedException {
        Plan altPlans = new Plan();
        ArrayList<Tuple> tuple_plan;
        SocialRulesBox SR = new SocialRulesBox(state,agent,goal,box);
        tuple_plan = SR.runBFS(altPlans);
        if (tuple_plan!=null) {
            ArrayList plan_agent = new ArrayList<>();
            ArrayList plan_box = new ArrayList<>();
            for (Tuple T : tuple_plan) {
                plan_agent.add(T.agentpos);
                plan_box.add(T.boxpos);
            }

            plan = plan_agent;
            box.mainPlan.plan = plan_box;

        }
        box.conflictRoute = new ArrayList<>();



    }

    public void createAltPaths(State state, Agent agent) {

        Plan altPlans = new Plan(); // Initialize plan
        SocialRules SR = new SocialRules(state,agent);
        altPlans.plan = SR.runBFS(altPlans);
        plan = altPlans.plan;
        ArrayList<String> planblank = new ArrayList<>(plan);
        state.blankPlan = planblank;


    }

    public ArrayList<String> breathFirstTraversal_altpath(State state, String root, Set<String> visited,ArrayList<String> otherAgentPlan, Boolean second) {
        visited = new HashSet<>(visited);
        Map map = state.map;
        ArrayList<String> route;
        Deque<ArrayList<String>> routes = new ArrayDeque<>();
        Deque<String> queue = new ArrayDeque<>();
        ArrayList<String> routesFinal =null;


        //Adding root to the list of routes to start with
        ArrayList<String> root_route = new ArrayList<>();
        queue.push(root);
        root_route.add(root);
        routes.add(root_route);


        //Start runnning BFS
        while (!queue.isEmpty()) {
            String vertex = queue.pollFirst();
            route = routes.pollFirst();
            Node node = state.stringToNode.get(vertex);

            if (!visited.contains(vertex)) {
                //When we are out of a tunnel, and away from the conflicting agents route, return the alternative path
                if (!otherAgentPlan.contains(node.getNodeId()) && (!node.isTunnel || second)&& (!node.isTunnelDynamic || second)) {
                    if (node.isTunnel) {
                        routesFinal = route;
                    }
                    else return route;
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
        return routesFinal;
    }

    public ArrayList<Tuple> breathFirstTraversal_box(State state, Agent agent, Box box, Set<Tuple> visited,Set<String> occupied,
                                                     ArrayList<String> otherAgentPlan, ArrayList<String> goal, Boolean second,
                                                     Boolean third, Boolean fourth) throws InterruptedException {
        visited = new HashSet<Tuple>(visited);
        occupied= new HashSet<String>(occupied);
        String rootagent = agent.position.NodeId;
        String rootbox = box.position.NodeId;
        Map map = state.map;
        ArrayList<Tuple> route_agent = new ArrayList<Tuple>();
        Deque<ActionType> actionList = new ArrayDeque<>();


        Deque<ArrayList<Tuple>> routes_agent = new ArrayDeque<>();
        Deque<Tuple> queue_agent = new ArrayDeque<>();

        Tuple root = new Tuple(rootagent,rootbox);
        queue_agent.push(root);


        //Adding root to the list of routes to start with
        ArrayList<Tuple> root_route = new ArrayList<>();
        root_route.add(root);
        routes_agent.add(root_route);
        actionList.add(ActionType.NoOp);
        ArrayList<Tuple> routesFinal = null;




        //Start runnning BFS
        while (!queue_agent.isEmpty()) {

            Tuple vertex = queue_agent.pollFirst();
            ActionType action = actionList.pollFirst();

            String vertex_agent = vertex.agentpos;
            String vertex_box = vertex.boxpos;


            //Change String to Node
            Node node_agent = state.stringToNode.get(vertex_agent);
            Node node_box = state.stringToNode.get(vertex_box);

            route_agent = routes_agent.pollFirst();


            if (!visited.contains(vertex) && !occupied.contains(vertex_box) && !occupied.contains(vertex_agent)){
                //When we are out of a tunnel, and away from the conflicting agents route, return the alternative path
                if (goal == null && (fourth && node_box.isCorridor && node_box.NodeId!=rootbox && node_agent.NodeId!=rootagent)){
                    //System.err.println("HERE: " + route_agent);
                    return route_agent;
                }
                else if (!fourth && goal==null && !otherAgentPlan.contains(node_box.getNodeId()) && !otherAgentPlan.contains(node_agent.getNodeId()) && (!node_box.isTunnel || second)&& (!node_box.isTunnelDynamic || second)) {
                    if (node_box.NodeId!=rootbox && node_agent.NodeId!=rootagent && (!action.equals(ActionType.Pull)|| (!node_agent.isTunnel &&!node_agent.isTunnelDynamic) || third)) {

                        Tuple last_position = new Tuple(node_agent.NodeId, node_box.NodeId);

                        if (node_box.isTunnel) {
                            routesFinal = route_agent;
                        }
                        else {
                            return route_agent;}
                    }
                }

                //TODO: Dont push other boxes out of their goal unless necessary

                else if (goal!=null && goal.contains(vertex_box) && ((!(state.occupiedNodes.keySet().contains(node_box.getNodeId())))||second)) {
                    if ((!action.equals(ActionType.Pull)|| (!node_agent.isTunnel &&!node_agent.isTunnelDynamic)|| third)) {

                        // If box goal and agent goal is adjacent, box position and agent position should be in goal when exit
                        if (GoalAdjacent(agent,box,state)) {
                            if (agent.Goal.contains(vertex_agent)) return route_agent;
                        }
                        else return route_agent;
                    }
                }


                visited.add(vertex);

                //PULL box gets agents position
                for (String v : map.getAdjacent(vertex_agent)) {

                    if (!v.equals(vertex_box)) {

                        ArrayList<Tuple> newroute = new ArrayList<>(route_agent);
                        Tuple new_position = new Tuple(v, vertex_agent);
                        queue_agent.addLast(new_position);
                        newroute.add(new_position);
                        routes_agent.addLast(newroute);
                        actionList.addLast(ActionType.Pull);
                    }
                }

                //PUSH agent gets box position
                for (String v : map.getAdjacent(vertex_box)) {

                    if (!v.equals(vertex_agent)) {

                        ArrayList<Tuple> newroute = new ArrayList<>(route_agent);
                        Tuple new_position = new Tuple(vertex_box, v);

                        queue_agent.addLast(new_position);
                        newroute.add(new_position);
                        routes_agent.addLast(newroute);
                        actionList.addLast(ActionType.Push);
                    }
                }
            }
        }
        return routesFinal;
    }

    public ArrayList<String> breathFirstTraversal(Map map, String root, List<String> goal, Set<String> visited) {
        if (goal == null) return new ArrayList<>();
        visited = new HashSet<>(visited);

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
            if (goal.contains(vertex) && !visited.contains(vertex)) {
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
        return null;
    }


    public boolean DFSForTunnels(Map map, String nodePosition, List<String> neighbors) {

        for(String N: neighbors){
            Deque<String> queue = new ArrayDeque<>();
            queue.push(N);


            HashSet<String> visited = new HashSet<>();
            visited.add(nodePosition);
            //Start runnning DFS

            while (!queue.isEmpty()) {
                String vertex = queue.pollFirst();
                visited.add(vertex);

                //If we are in goal, stop BFS
                if (neighbors.contains(vertex) && !N.equals(vertex)) {
                    return false;
                }

                //If not in goal, check neighbours not in visited.

                for (String v : map.getAdjacent(vertex)) {
                    if (!visited.contains(v)) queue.push(v);



                }
            }
        }

        return true;
    }

    // dont touch.
    public HashSet<String> MathiasBFS(Map map, String root) {

        Deque<String> queue = new ArrayDeque<>();
        queue.push(root);
        var visited = new HashSet<String>();


        //Start runnning BFS
        while (!queue.isEmpty()) {
            String vertex = queue.pollFirst();

            //If not in goal, check neighbours not in visited.
            if (!visited.contains(vertex)) {
                visited.add(vertex);


                for (String v : map.getAdjacent(vertex)) {
                    queue.addLast(v);
                }
            }
        }
        return visited;
    }


    public Boolean GoalAdjacent(Agent agent, Box box, State state) {
        if (agent.Goal.size()==0) {
            return false;
        }
        Node agentGoalNode = state.stringToNode.get(agent.Goal.get(0));
        List<String> agentneighbours = state.map.getAdjacent(agent.Goal.get(0));

        return agentneighbours.contains(box.Goal.get(0)) && agentGoalNode.isTunnel;

    }




}
