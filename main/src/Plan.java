import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.*;

public class Plan {
    ArrayList<String> plan;
    HashMap<String, ArrayList<String>> precomputedDistance;

    public void createPlan(State state, String Source,List<String> Destination,Set<String> visited) {
        Map map = state.map;
        if (Destination == null) return;
        System.err.println("dest"+Destination);

        visited.addAll(state.occupiedNodes.keySet());
        visited.remove(Source);

        plan = breathFirstTraversal(map, Source, Destination,visited);


        System.err.println("PLAN:"+plan);
        if (!Destination.contains(plan.get(plan.size()-1))) {
            plan = breathFirstTraversal(map, Source, Destination,new LinkedHashSet<>());
        }

    }

    public void createPlanWithBox(State state, String rootAgent, String rootBox, String goal,Box box) {
        if (goal == null) return;

        ArrayList<Tuple> tuple_plan = breathFirstTraversal_box(state,rootAgent,rootBox,new LinkedHashSet<>(),new ArrayList<String>(),goal,false);
        ArrayList plan_agent = new ArrayList<>();
        ArrayList plan_box = new ArrayList<>();
        for (Tuple T : tuple_plan) {
            plan_agent.add(T.agentpos);
            plan_box.add(T.boxpos);
        }

        plan = plan_agent;
        box.mainPlan.plan = plan_box;


        System.err.println("PLAN AGENT:"+plan);
        System.err.println("PLAN BOX:"+plan_box);
        //if (!goal.equals(plan.get(plan.size()-1))) {
         //   plan = new ArrayList<>();
       // }




    }


    public void createAltPaths(State state, Agent agent) {
        System.err.println("### NOW COMPUTING Alternative plan ###");
        Map map = state.map;


        Set<String> visited = new LinkedHashSet<>(state.occupiedNodes.keySet());
        visited.remove(agent.position.getNodeId());

        Plan altPlans = new Plan(); // Initialize plan

        ArrayList<String> allPlans = new ArrayList<>();

        allPlans.addAll(agent.conflicts.mainPlan.plan);
        allPlans.addAll(state.occupiedNodesString());
        altPlans.plan = altPlans.breathFirstTraversal_altpath(state, agent.position.getNodeId(), visited,allPlans, false); //Run BFS, to create new alternative plan
/**
        if (agent.conflicts!=null && !(agent.conflicts.mainPlan.plan.get((agent.conflicts.mainPlan.plan.size()))).equals(agent.conflicts.Goal.NodeId)) {
            System.err.println("2");
            ArrayList<String> altplan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), new LinkedHashSet<>(),new ArrayList<>(), true); //Run BFS, to create new alternative plan
            altPlans.plan = altplan;
        }
 **/
        if (altPlans.plan==null) {
            System.err.println("2");
            ArrayList<String> altplan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), new LinkedHashSet<>(),allPlans, false); //Run BFS, to create new alternative plan

            altPlans.plan = altplan;
        }

        if (altPlans.plan==null) {
            System.err.println("3");
            ArrayList<String> altplan = altPlans.breathFirstTraversal_altpath(state,agent.position.getNodeId(), new LinkedHashSet<>(),allPlans, true); //Run BFS, to create new alternative plan

            altPlans.plan = altplan;
        }

        plan = altPlans.plan;
        System.err.println("PLan to goal:"+plan);
        ArrayList<String> planblank = new ArrayList<>(plan);
        state.blankPlan = planblank;

    }


    public ArrayList<String> getPlan() {
        return this.plan;
    }

    public ArrayList<String> breathFirstTraversal_altpath(State state, String root, Set<String> visited,ArrayList<String> otherAgentPlan, Boolean second) {
        Map map = state.map;
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
                    if (!otherAgentPlan.contains(node.getNodeId()) && (!node.isTunnel || second)) {
                        route.add(node.getNodeId());

                        return route;
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

    public ArrayList<Tuple> breathFirstTraversal_box(State state, String rootagent, String rootbox, Set<Tuple> visited,ArrayList<String> otherAgentPlan, String goal, Boolean second) {
        Map map = state.map;
        ArrayList<Tuple> route_agent = new ArrayList<>();

        Deque<ArrayList<Tuple>> routes_agent = new ArrayDeque<>();
        Deque<Tuple> queue_agent = new ArrayDeque<>();

        Tuple root = new Tuple(rootagent,rootbox);
        queue_agent.push(root);


        //Adding root to the list of routes to start with
        ArrayList<Tuple> root_route = new ArrayList<>();
        root_route.add(root);
        routes_agent.add(root_route);


        //Start runnning BFS
        while (!queue_agent.isEmpty()) {
            Tuple vertex = queue_agent.pollFirst();
            String vertex_agent = vertex.agentpos;
            String vertex_box = vertex.boxpos;

            //Change String to Node
            Node node_agent = state.stringToNode.get(vertex_agent);
            Node node_box = state.stringToNode.get(vertex_box);

            route_agent = routes_agent.pollFirst();

            //TODO: CHange visited to be array of vertices, where the combination of agent and box can shift places.
            if (!visited.contains(vertex)){

                //When we are out of a tunnel, and away from the conflicting agents route, return the alternative path
                if (goal==null && !otherAgentPlan.contains(node_box.getNodeId()) && (!node_box.isTunnel || second)) {
                    //System.err.println("found alternative route:"+route);
                    Tuple last_position = new Tuple(node_agent.NodeId,node_box.NodeId);
                    route_agent.add(last_position);
                    return route_agent;
                }

                else if (vertex_box.equals(goal)) {
                    return route_agent;
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
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<String> breathFirstTraversal(Map map, String root, List<String> goal, Set<String> visited) {
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
            if (goal.contains(vertex)) {
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
