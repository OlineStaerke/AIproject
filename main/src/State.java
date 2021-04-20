import java.util.*;

public class State {

    public Map map;
    public HashMap<Character, Agent> agents;
    public HashMap<Character, Box> boxes;
    public ArrayList<Node> goals;
    public final State parent;
    public final Action[] jointAction;
    private final int g;
    public java.util.Map<String, Node> stringToNode;
    //Hashset of string for positions overtaken, each agent would add these positions he finds
    // himself in. This hashset would be public

    public HashMap<Character, String> NameToColor;

    public HashMap<String, Object> occupiedNodes;

    public State(java.util.Map<String, Node> stringToNode, HashMap<Character, Agent> agents, HashMap<Character,
            String> NameToColor, HashMap<Character, Box> boxes, ArrayList<Node> goals, Map map)
    {
        this.stringToNode = stringToNode;
        this.agents = agents;
        this.boxes = boxes;
        this.goals = goals;
        this.parent = null;
        this.jointAction = null;
        this.g = 0;
        occupiedNodes = new HashMap<>();
        for(Agent agent : agents.values()) occupiedNodes.put(agent.position.NodeId, agent);
        for(Box box : boxes.values()) occupiedNodes.put(box.position.NodeId, box);

        this.map = map;
        this.NameToColor = NameToColor;
        createTunnels();
        createObjectAssociations();
    }

    private void createTunnels(){
        for (String node: map.map.keySet()){
            Node n = stringToNode.get(node);
            if (map.map.get(n.NodeId).size() == 1) {
                n.isTunnel = true;
                stringToNode.replace(node, n);
                continue;
            }
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

            if ((E & W) & !(NE & N & NW || SW & SE & S)) n.isTunnel = true;
            if ((N & S) & !(E & NE & SE || W & NW & SW)) n.isTunnel = true;

            if ((N & W) & !(NW || SW & S & SE & E & NE)) n.isTunnel = true;
            if ((N & E) & !(NE || S & SW & SE & W & NW)) n.isTunnel = true;

            if ((S & W) & !(SW || NE & N & NW & E & SE)) n.isTunnel = true;
            if ((S & E) & !(SE || NE & N & NW & W & SW)) n.isTunnel = true;

            stringToNode.replace(node, n);






        }
    }

    private void createObjectAssociations(){
        for (Agent A: agents.values()){
            for (Box B: boxes.values()){
                if(NameToColor.get(A.ID).equals(NameToColor.get(B.ID))){
                    System.err.println(agents);
                    B.owner = agents.get(A.ID);
                    A.boxes.add(boxes.get(B.ID));
                }
            }
        }
        for (Box B: boxes.values()){
            B.priority = B.owner.priority;
        }
    }

    public ArrayList<Agent> AgentsInOrder(){
        for (Agent A: agents.values()) if (!A.position.isTunnel) A.priority = A.originalPriority;

        ArrayList<Agent> sortedAgents = new ArrayList<>(agents.values());
        Collections.sort(sortedAgents);
        return sortedAgents;
    }


}
