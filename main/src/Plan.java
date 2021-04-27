import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.*;

public class Plan {
    ArrayList<String> plan;
    HashMap<String, ArrayList<String>> precomputedDistance;

    public void createPlan(Map map, String Source,String Destination,Set<String> visited) {
        if (Destination == null) return;
        System.err.println("dest"+Destination);

        plan = breathFirstTraversal(map, Source, Destination,visited);
        System.err.println("PLAN:"+plan);
        if (!plan.get(plan.size()-1).equals(Destination)) {
            plan = new ArrayList<>();
        }



        //plan.remove(0);
    }


    public void createAltPaths(State state, Node start, Map map, Agent otherAgent, String Destination) {
        System.err.println("### NOW COMPUTING Alternative plan ###");
        //System.err.println("Problem node:"+problem_node);



        Set<String> visited = new LinkedHashSet<>(state.occupiedNodes.keySet());
        visited.remove(start.getNodeId());

        Plan altPlans = new Plan(); // Initialize plan
        //if (!state.stringToNode.get(problem_node).isTunnel) {
        //visited.add(problem_node); //add problem node to visited, so that the algorithm does not enter this, if we are NOT in a tunnel.

        //}


        ArrayList<String> allPlans = new ArrayList<>();
        allPlans.addAll(otherAgent.mainPlan.plan);
        //allPlans.addAll(otherAgent.finalPlanString);
        //allPlans.add(otherAgent.position.getNodeId());
        allPlans.addAll(state.occupiedNodesString());
        //System.err.println("!!OtherPlan:"+allPlans);
        altPlans.plan = altPlans.breathFirstTraversal_altpath(state, map, start.getNodeId(), visited,allPlans, false); //Run BFS, to create new alternative plan

        if (altPlans.plan==null) {
            ArrayList<String> altplan = altPlans.breathFirstTraversal_altpath(state, map,start.getNodeId(), new LinkedHashSet<>(),allPlans, true); //Run BFS, to create new alternative plan

            /**
            ArrayList<Tuple> twoPlans = altPlans.breathFirstTraversal_box(state, map, otherAgent.position.getNodeId(),start.getNodeId(), new LinkedHashSet<>(),allPlans, true); //Run BFS, to create new alternative plan
            ArrayList<String> agentPlan = new ArrayList<>();
            ArrayList<String> boxPlan = new ArrayList<>();

            for (Tuple t : twoPlans) {
                agentPlan.add(t.string1);
                boxPlan.add(t.string2);
            }

            otherAgent.mainPlan.plan = agentPlan;
             **/
            altPlans.plan = altplan;
        }




        //altPlans.plan.remove(0);
        /***
        if (agent.priority>=otherAgent.priority)  {
            createPlan(map,altPlans.plan.get(altPlans.plan.size()-1),Destination,new LinkedHashSet<>()); //Find new main plan back to goal
            altPlans.plan.addAll(plan);
            plan = altPlans.plan;

        }
        else {
         ***/
            //altPlans.plan.addAll(plan); //Return new plan
        plan = altPlans.plan; //Overwrite old plan
        //}
        System.err.println("PLan to goal:"+plan);
        //plan.remove(0); //Remove first index
        ArrayList<String> planblank = new ArrayList<>(plan);
        state.blankPlan = planblank;

    }


    public ArrayList<String> getPlan() {
        return this.plan;
    }

    public ArrayList<String> breathFirstTraversal_altpath(State state, Map map, String root, Set<String> visited,ArrayList<String> otherAgentPlan, Boolean second) {

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
                        //System.err.println("found alternative route:"+route);
                        route.add(node.getNodeId());
                        //route.add(node.getNodeId());

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

    public ArrayList<Tuple> breathFirstTraversal_box(State state, Map map, String rootagent, String rootbox, Set<String> visited,ArrayList<String> otherAgentPlan, Boolean second) {

        ArrayList<Tuple> route_agent = new ArrayList<>();
        Deque<ArrayList<Tuple>> routes_agent = new ArrayDeque<>();
        Deque<Tuple> queue_agent = new ArrayDeque<>();

        Tuple root = new Tuple();
        root.Tuple(rootagent,rootbox);
        queue_agent.push(root);


        //Adding root to the list of routes to start with
        ArrayList<Tuple> root_route = new ArrayList<>();
        root_route.add(root);
        routes_agent.add(root_route);




        //Start runnning BFS
        while (!queue_agent.isEmpty()) {
            Tuple vertex = queue_agent.pollFirst();
            String vertex_agent = vertex.string1;
            String vertex_box = vertex.string2;

            route_agent = routes_agent.pollFirst();
            Node node_agent = state.stringToNode.get(vertex_agent);
            Node node_box = state.stringToNode.get(vertex_box);


            //TODO: CHange visited to be array of vertices, where the combination of agent and box can shift places.
            if (!visited.contains(vertex_box)){

                //When we are out of a tunnel, and away from the conflicting agents route, return the alternative path

                if (!otherAgentPlan.contains(node_box.getNodeId()) && (!node_box.isTunnel || second)) {
                    //System.err.println("found alternative route:"+route);

                    root.Tuple(node_agent.NodeId,node_box.NodeId);
                    route_agent.add(root);

                    return route_agent;
                }


                visited.add(vertex_box);

                for (String v : map.getAdjacent(vertex_agent)) {

                    ArrayList<Tuple> newroute = new ArrayList<>(route_agent);
                    root.Tuple(v,vertex_agent);
                    queue_agent.addLast(root);
                    newroute.add(root);
                    routes_agent.addLast(newroute);
                }
                for (String v : map.getAdjacent(vertex_box)) {

                    ArrayList<Tuple> newroute = new ArrayList<>(route_agent);
                    root.Tuple(vertex_box,v);
                    queue_agent.addLast(root);
                    newroute.add(root);
                    routes_agent.addLast(newroute);
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
