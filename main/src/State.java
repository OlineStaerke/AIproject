import java.util.ArrayList;

public class State {

    public Map map;
    public Agent[] agents;
    public ArrayList<Box> boxes;
    public ArrayList<Node> goals;
    public final State parent;
    public final Action[] jointAction;
    private final int g;
    public static Colour[] agentColours;

    public State(Agent[] agents, Colour[] agentColours,
                 ArrayList<Box> boxes, ArrayList<Node> goals)
    {
        this.agentColours = agentColours;
        this.boxes = boxes;
        this.goals = goals;
        this.parent = null;
        this.jointAction = null;
        this.g = 0;
    }
}
