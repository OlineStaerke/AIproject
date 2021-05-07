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


        UpdateOccupiedNodes();
        createTunnels();
        createObjectAssociations();


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

            /**if (box.currentowner != null && !(box.currentowner.currentGoal.Obj.ID == box.ID)) {
                for (Agent agent : agents.values()) {
                    if (NameToColor.get(agent.ID.charAt(0)) == NameToColor.get(box.ID.charAt(0)) && agent.mainPlan.plan.size() == 0) {
                        System.err.println();
                        box.currentowner = agent;
                    }
                }

            } else {
                for (Agent agent : agents.values()) {
                    if (NameToColor.get(agent.ID.charAt(0)) == NameToColor.get(box.ID.charAt(0))) {
                        box.currentowner = agent;
                        break;
                    }
                }

            }**/
            if (box.currentowner == null) {
                for (Agent agent : agents.values()) {
                    if (NameToColor.get(agent.ID.charAt(0)) == NameToColor.get(box.ID.charAt(0))) {
                        box.currentowner = agent;
                        break;
                    }
                }
            }
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
    private void createObjectAssociations(){
        for (Agent A: agents.values()){
            for (Box B: boxes.values()){
                System.err.println(NameToColor.get((A.ID).charAt(0))+NameToColor.get(B.ID.charAt(0)));
                if(NameToColor.get(A.ID.charAt(0)).equals(NameToColor.get(B.ID.charAt(0)))){
                    B.owners.add(agents.get(A.ID));
                    A.boxes.add(boxes.get(B.ID));
                }
            }
        }
    }




}
