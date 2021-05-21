import java.util.*;

public class State {

    // More or less Static structures containing info of the level-component
    public Map map;
    public HashMap<String, Agent> agents;
    public HashMap<String, Box> boxes;
    public HashMap<String, ArrayList<Box>> goals;
    public HashMap<String, Object> occupiedNodes;
    public HashMap<Character, String> NameToColor;
    public HashMap<String, Node> stringToNode;

    // Dynamic structures which are updated throughout the algorithm
    public LinkedHashSet<Agent> agentConflicts;
    public ArrayList<String> blankPlan;


    public ArrayList<State> allStates(){
        return recFindComponents(0);

    }

    public State(Map M, HashMap<Character, String> NTC, HashMap<String, Node> STN){

        agentConflicts = new LinkedHashSet<>();
        blankPlan = new ArrayList<>();
        occupiedNodes = new HashMap<>();
        stringToNode = new HashMap<>();
        agents = new HashMap<>();
        boxes = new HashMap<>();
        NameToColor = NTC;
        stringToNode = STN;
        map = M;
        goals = new HashMap<>();

    }


    public State(HashMap<String, Node> stringToNode, HashMap<String, Agent> agents, HashMap<Character,
            String> NameToColor, HashMap<String, Box> boxes, Map map)
    {
        this.stringToNode = stringToNode;
        this.agents = agents;
        this.boxes = boxes;
        this.map = map;
        this.NameToColor = NameToColor;

        agentConflicts = new LinkedHashSet<>();
        blankPlan = new ArrayList<>();
        occupiedNodes = new HashMap<>();


        //UpdateOccupiedNodes();
        //createObjectAssociations();
        //UpdateOccupiedNodes();
        //createTunnels();


    }

    public ArrayList<String> occupiedNodesString() {
        return new ArrayList<>(occupiedNodes.keySet());
    }


    public void UpdateOccupiedNodes() {
        occupiedNodes = new HashMap<>();
        for (Agent agent : agents.values()) {
            occupiedNodes.put(agent.position.NodeId, agent);

        }
        for (Box box : boxes.values()) {
            occupiedNodes.put(box.position.NodeId, box);

            if (box.currentowner == null) {
                for (Agent agent : agents.values()) {
                    if (NameToColor.get(agent.ID.charAt(0)) == NameToColor.get(box.ID.charAt(0))) {
                        box.currentowner = agent;
                        break;
                    }
                }
            }
        }

        for (String position : occupiedNodes.keySet()) {
            List<String> adjacent = map.getAdjacent(position);
            for (String adjPosition : adjacent) {

                Integer i = Integer.parseInt(adjPosition.split(" ")[0]);
                Integer j = Integer.parseInt(adjPosition.split(" ")[1]);

                boolean N = map.map.containsKey((i-1) + " " + j) && (!occupiedNodes.containsKey((i-1) + " " + j) || (occupiedNodes.get((i-1) + " " + j) instanceof Agent));
                boolean S = map.map.containsKey((i+1) + " " + j) && (!occupiedNodes.containsKey((i+1) + " " + j) || (occupiedNodes.get((i+1) + " " + j) instanceof Agent));
                boolean E = map.map.containsKey(i + " " + (1+j)) && (!occupiedNodes.containsKey(i+ " " + (j+1)) || (occupiedNodes.get(i + " " + (j+1)) instanceof Agent));
                boolean W = map.map.containsKey(i + " " + (j-1))&& (!occupiedNodes.containsKey(i + " " + (j-1)) || (occupiedNodes.get(i + " " + (j-1)) instanceof Agent));

                boolean NE = map.map.containsKey((i-1) + " " + (j+1))&& (!occupiedNodes.containsKey((i-1) + " " + (j+1)) || (occupiedNodes.get((i-1) + " " + (j+1)) instanceof Agent));
                boolean NW = map.map.containsKey((i-1) + " " + (j-1))&& (!occupiedNodes.containsKey((i-1) + " " + (j-1)) || (occupiedNodes.get((i-1) + " " + (j-1)) instanceof Agent));
                boolean SE = map.map.containsKey((i+1) + " " + (j+1))&& (!occupiedNodes.containsKey((i+1) + " " + (j+1)) || (occupiedNodes.get((i+1) + " " + (j+1)) instanceof Agent));
                boolean SW = map.map.containsKey((i+1) + " " + (j-1))&& (!occupiedNodes.containsKey((i+1) + " " + (j-1)) || (occupiedNodes.get((i+1) + " " + (j-1)) instanceof Agent));

                Node n = stringToNode.get(adjPosition);
                n.isTunnelDynamic =false;

                if ((S & !SE & !SW)) n.isTunnelDynamic = true;
                if ((N & !NE & !NW)) n.isTunnelDynamic = true;
                if ((E & !SE & !NE)) n.isTunnelDynamic = true;
                if ((W & !SW & !NW)) n.isTunnelDynamic = true;



                if ((E & W) & !(NE & N & NW || SW & SE & S)) n.isTunnelDynamic = true;
                if ((N & S) & !(E & NE & SE || W & NW & SW)) n.isTunnelDynamic = true;

                if ((N & W) & !(NW || SW & S & SE & E & NE)) n.isTunnelDynamic = true;
                if ((N & E) & !(NE || S & SW & SE & W & NW)) n.isTunnelDynamic = true;

                if ((S & W) & !(SW || NE & N & NW & E & SE)) n.isTunnelDynamic = true;
                if ((S & E) & !(SE || NE & N & NW & W & SW)) n.isTunnelDynamic = true;

                if (S & SE && NE & !N & !E & !W & !NW & !SW) n.isTunnelDynamic = true;



            }
        }



    }


    // Definition of a tunnel: A node, X, is a tunnel, iff removing X disconnects the graph.
    private void createTunnels(){
        Plan P = new Plan();
        for (String node: map.map.keySet()){
            Node n = stringToNode.get(node);
            if (n == null) continue;

            var o = node.split(" ");
            int i = Integer.parseInt(o[0]);
            int j = Integer.parseInt(o[1]);

            boolean N = map.map.containsKey((i - 1) + " " + j);
            boolean S = map.map.containsKey((i + 1) + " " + j);
            boolean E = map.map.containsKey(i + " " + (1 + j));
            boolean W = map.map.containsKey(i + " " + (j - 1));

            boolean NE = map.map.containsKey((i - 1) + " " + (j + 1));
            boolean NW = map.map.containsKey((i - 1) + " " + (j - 1));
            boolean SE = map.map.containsKey((i + 1) + " " + (j + 1));
            boolean SW = map.map.containsKey((i + 1) + " " + (j - 1));

            if ((S & !SE & !SW)) n.isTunnel = true;
            if ((N & !NE & !NW)) n.isTunnel = true;
            if ((E & !SE & !NE)) n.isTunnel = true;
            if ((W & !SW & !NW)) n.isTunnel = true;


            if ((E & W) & !(NE & N & NW || SW & SE & S)) n.isTunnel = true;
            if ((N & S) & !(E & NE & SE || W & NW & SW)) n.isTunnel = true;

            if ((N & W) & !(NW || SW & S & SE & E & NE)) n.isTunnel = true;
            if ((N & E) & !(NE || S & SW & SE & W & NW)) n.isTunnel = true;

            if ((S & W) & !(SW || NE & N & NW & E & SE)) n.isTunnel = true;
            if ((S & E) & !(SE || NE & N & NW & W & SW)) n.isTunnel = true;

            if (P.DFSForTunnels(map, node, map.map.get(node))){
                n.isTunnelOneWay = true;
                n.isTunnel = true;
            }
            stringToNode.replace(node, n);


        }

    }
    // This simply checks if the agent/box is in the graph
    private void naiveGraphCheck(){
        var M = map;
        var newAgents = new HashMap<String, Agent>();
        // Add obtainable goals from agent
        for(String AS : agents.keySet()){
            if (M.map.containsKey(agents.get(AS).position.NodeId)){
                ArrayList<String> newGoals = new ArrayList<>();
                for (String g : agents.get(AS).Goal){
                    if (M.map.containsKey(g)) newGoals.add(g);
                }
                agents.get(AS).Goal = newGoals;
                newAgents.put(AS, agents.get(AS));
            }
        }
        var newBoxes = new HashMap<String, Box>();
        // Add obtainable goals for each box
        for(String BS : boxes.keySet()){
            if (M.map.containsKey(boxes.get(BS).position.NodeId)){
                ArrayList<String> newGoals = new ArrayList<>();
                for (String g : boxes.get(BS).Goal){
                    if (M.map.containsKey(g)) newGoals.add(g);
                }
                boxes.get(BS).Goal = newGoals;
                newBoxes.put(BS, boxes.get(BS));
            }
        }
        boxes = newBoxes;
        agents = newAgents;
        deepGraphCheck();
    }


    // Links boxes to agents and vice-versa, if box/agent is actually reachable (using BFS)
    // Also replaces boxes with no owner, with a wall
    private void deepGraphCheck() {
        Plan P = new Plan();
        for (Agent A: agents.values()){
            A.boxes = new ArrayList<>();
        }
        for (Box B: boxes.values()){
            B.owners = new ArrayList<>();
        }

        for (Agent A : agents.values()) {
            var g = new ArrayList<String>();
            g.add(A.position.NodeId);

            for (Box B : boxes.values()) {

                // Can we actual reach the box from the owner-agent?
                if (NameToColor.get(A.ID.charAt(0)).equals(NameToColor.get(B.ID.charAt(0)))) {

                    if (P.breathFirstTraversal(map, B.position.NodeId, g, new LinkedHashSet<>()) != null){
                        B.owners.add(A);
                        A.boxes.add(B);
                    }

                }
                // What goals can the box reach?
                var newGoals = new ArrayList<String>();
                for (String BG : B.Goal){
                    var bg = new ArrayList<String>();
                    bg.add(BG);
                    if (P.breathFirstTraversal(map, B.position.NodeId, bg, new LinkedHashSet<>()) != null){
                        newGoals.add(BG);
                    }
                    B.Goal = newGoals;
                }

            }
            // What goals can the agent reach?
            var newGoals = new ArrayList<String>();
            for (String AG : A.Goal){
                var ag = new ArrayList<String>();
                ag.add(AG);
                if (P.breathFirstTraversal(map, A.position.NodeId, ag, new LinkedHashSet<>()) != null){
                    newGoals.add(AG);
                }
                A.Goal = newGoals;
            }
        }
        var BoxesCopy = new HashMap<>(boxes);

        // No owners of a box? Transform box into a wall.
        for (var box : boxes.entrySet()) {
            if (box.getValue().owners.size() == 0) {
                for (String neighbors: map.map.get(box.getValue().position.NodeId)){
                    map.map.get(neighbors).remove(box.getValue().position.NodeId);
                }

                map.map.remove(box.getValue().position.NodeId);
                BoxesCopy.remove(box.getKey());
            }

        }

        boxes = BoxesCopy;

    }
    // Find connected-components of the current map (graph)
    private ArrayList<Map> findConnectedComponents(){
        Plan P = new Plan();
        var DoneComponents = new HashSet<String>();
        var components = new ArrayList<Map>();


        for(String pos: map.map.keySet()){
            if (DoneComponents.contains(pos)) continue;

            var componentNodes = P.connectedComponentsBFS(map, pos);
            var newComponent = new Map();

            for(String cPos : componentNodes){
                newComponent.map.put(cPos, map.map.get(cPos));
            }
            components.add(newComponent);
            DoneComponents.addAll(componentNodes);
        }
        return components;
    }

    // Recursively finds connected components in the graph
    private ArrayList<State> recFindComponents(int sizeBefore){

        ArrayList<Map> maps = findConnectedComponents();
        var newStates = new ArrayList<State>();

        for (Map M: maps){
            // For each connected component in the graph, create a new state object
            var newState = new State(M, NameToColor, stringToNode);
            newState.boxes = boxes;
            newState.agents = agents;
            newState.naiveGraphCheck();
            newState.UpdateOccupiedNodes();
            newState.createTunnels();
            newStates.add(newState);

        }
        if (newStates.size() == 1 && sizeBefore == 1) return newStates; //No more recursion can be done
        var recCombined = new ArrayList<State>();

        for(State S: newStates) recCombined.addAll(S.recFindComponents(newStates.size())); //Can continue to recurse

        return recCombined;
    }

}
