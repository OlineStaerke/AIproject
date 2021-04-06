import java.util.*;

public class State {

    public Map map;
    public HashMap<Integer, Agent> agents;
    public HashMap<Integer, Box> boxes;
    public ArrayList<Node> goals;
    public final State parent;
    public final Action[] jointAction;
    private final int g;
    public static Colour[] agentColours;
    public java.util.Map<String, Node> stringToNode;
    //Hashset of string for positions overtaken, each agent would add these positions he finds
    // himself in. This hashset would be public

    public HashMap<String, Agent> occupiedNodes;

    public State(java.util.Map<String, Node> stringToNode, HashMap<Integer, Agent> agents, Colour[] agentColours,
                 HashMap<Integer, Box> boxes, ArrayList<Node> goals, Map map)
    {
        this.stringToNode = stringToNode;
        this.agents = agents;
        this.agentColours = agentColours;
        this.boxes = boxes;
        this.goals = goals;
        this.parent = null;
        this.jointAction = null;
        this.g = 0;
        occupiedNodes = new HashMap<>();
        for(Agent agent : agents.values()) occupiedNodes.put(agent.initialState.NodeId, agent);
        this.map = map;
    }

    public ArrayList<Agent> AgentsInOrder(){
        ArrayList<Agent> sortedAgents = new ArrayList<>(agents.values());
        Collections.sort(sortedAgents);
        return sortedAgents;

    }

}
