import java.lang.reflect.Array;
import java.util.*;

public class State {

    public Map map;
    public HashMap<String, Agent> agents;
    public HashMap<String, Box> boxes;
    public HashMap<String, Object> occupiedNodes;
    public HashMap<Character, String> NameToColor;
    public HashMap<String, Node> stringToNode;

    public LinkedHashSet<Agent> agentConflicts;
    public ArrayList<String> blankPlan;

    public ArrayList<State> allStates(){
        ArrayList<State> states = new ArrayList<>();
        var newMaps = findConnectedComponents();

        for(Map M : newMaps){
            var newState = new State(M, NameToColor, stringToNode);
            for(String AS : agents.keySet()){
                if (M.map.containsKey(agents.get(AS).position.NodeId)){
                    newState.agents.put(AS, agents.get(AS));
                }
            }
            for(String BS : boxes.keySet()){
                if (M.map.containsKey(boxes.get(BS).position.NodeId)){
                    newState.boxes.put(BS, boxes.get(BS));
                }
            }

            for(Box B: newState.boxes.values()){
                System.err.println("BOX: " + B + " ,goals: " + B.Goal);

            }
            newState.UpdateOccupiedNodes();
            newState.createObjectAssociations();
            newState.UpdateOccupiedNodes();
            newState.createTunnels();

            System.err.println("HERE!: " + newState.agents.values() + ", " + newState.boxes.values());
            System.err.println(M);
            for(Box B: newState.boxes.values()){
                System.err.println("BOX AFTER: " + B + " ,goals: " + B.Goal);

            }

            states.add(newState);

        }
        // System.exit(0);

        return states;

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
        }
    }





    // Definition of a tunnel: A node, X, is a tunnel, iff removing X disconnects the graph.
    private void createTunnels(){
        for (String node: map.map.keySet()){
            Node n = stringToNode.get(node);
            //if (map.map.get(n.NodeId).size() == 1) {
              //  n.isTunnel = true;
               // stringToNode.replace(node, n);
                //continue;
            //}
            var o = node.split(" ");
            int i = Integer.parseInt(o[0]);
            int j = Integer.parseInt(o[1]);

            boolean E = map.map.containsKey((i+1) + " " + j);
            boolean W = map.map.containsKey((i-1) + " " + j);
            boolean N = map.map.containsKey(i + " " + (1+j));
            boolean S = map.map.containsKey(i + " " + (j-1));

            boolean NE = map.map.containsKey((i+1) + " " + (j+1));
            boolean NW = map.map.containsKey((i-1) + " " + (j+1));
            boolean SE = map.map.containsKey((i+1) + " " + (j-1));
            boolean SW = map.map.containsKey((i-1) + " " + (j-1));

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

            stringToNode.replace(node, n);
        }
    }

    // Links boxes to agents and vice-versa
    private void createObjectAssociations() {
        Plan P = new Plan();

        for (Agent A : agents.values()) {
            for (Box B : boxes.values()) {

                if (NameToColor.get(A.ID.charAt(0)).equals(NameToColor.get(B.ID.charAt(0)))) {
                    var foo = new ArrayList<String>();
                    foo.add(B.position.NodeId);
                    P.createPlan(this, A.position.NodeId, foo, new HashSet<>());

                    // Can agent reach box?
                    if (P.plan != null) {
                        B.owners.add(agents.get(A.ID));
                        A.boxes.add(boxes.get(B.ID));
                    }

                }
            }
        }
        var BoxesCopy = new HashMap<>(boxes);

        for (var box : boxes.entrySet()) {
            if (box.getValue().currentowner == null) {
                // Any agent can reach box?
                if (box.getValue().owners.size() != 0){
                    box.getValue().currentowner = box.getValue().owners.get(0);
                }
                // Otherwise, "remove" the box. Box can now be seen as a wall.
                else{
                    // Remove from neighbors
                    for (String neighbors: map.map.get(box.getValue().position.NodeId)){
                        map.map.get(neighbors).remove(box.getValue().position.NodeId);
                    }

                    map.map.remove(box.getValue().position.NodeId);
                    BoxesCopy.remove(box.getKey());
                }

            }

        }

        boxes = BoxesCopy;
        // Each box has a specific goal for now.
        ArrayList<String> doneGoals = new ArrayList<>();

        for (Box B: boxes.values()){
            if (B.Goal.size() == 0) continue;
            var goals = new ArrayList<String>(B.Goal);
            goals.removeAll(doneGoals);

            P.createPlan(this, B.position.NodeId, goals, new HashSet<>());
            var newGoal = new ArrayList<String>();
            newGoal.add(P.plan.get(P.plan.size()-1));
            B.Goal = newGoal;
            doneGoals.add(newGoal.get(0));

        }



        for (Box B: boxes.values()){
            System.err.println("HASDHAHSDHASHD: " + B.ID + ", " + B.Goal);

        }

    }

    private ArrayList<Map> findConnectedComponents(){
        Plan P = new Plan();
        var DoneComponents = new HashSet<String>();
        var components = new ArrayList<Map>();



        for(String pos: map.map.keySet()){
            if (DoneComponents.contains(pos)) continue;

            var componentNodes = P.MathiasBFS(map, pos);
            var newComponent = new Map();

            for(String cPos : componentNodes){
                newComponent.map.put(cPos, map.map.get(cPos));
            }
            components.add(newComponent);
            DoneComponents.addAll(componentNodes);


        }
        return components;
    }

}
