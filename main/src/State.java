import java.util.*;

public class State {

    public Map map;
    public HashMap<Character, Agent> agents;
    public HashMap<Character, Box> boxes;
    public ArrayList<Node> goals;
    public final State parent;
    public final Action[] jointAction;
    private final int g;
    public Colour[] agentColours;
    public java.util.Map<String, Node> stringToNode;
    //Hashset of string for positions overtaken, each agent would add these positions he finds
    // himself in. This hashset would be public

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
        createTunnels();
        createObjectAssociations(NameToColor);
    }

    private void createTunnels(){
        for (String node: map.map.keySet()){
            if (map.map.get(node).size() <= 2){
                Node n = stringToNode.get(node);
                n.isTunnel = true;
                stringToNode.replace(node, n);
            }

        }
    }

    private void createObjectAssociations(HashMap<Character, String> NameToColor){
        for (Agent A: agents.values()){
            for (Box B: boxes.values()){
                if(NameToColor.get(A.ID).equals(NameToColor.get(B.ID))){
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
        ArrayList<Agent> sortedAgents = new ArrayList<>(agents.values());
        Collections.sort(sortedAgents);
        return sortedAgents;

    }


}
