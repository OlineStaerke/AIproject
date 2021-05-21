import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SearchClient {

    // Prases Level by following the protocol of how levels are defined.
    public static State parseLevel(BufferedReader serverMessages)
            throws IOException {
        HashMap<String, Node> stringToNode = new HashMap<>();
        HashMap<Character, String> NameToColor = new HashMap<>();
        ArrayList<String> levelLines = new ArrayList<>(64);
        Map map = new Map();
        HashMap<String, Agent> agents = new HashMap<>();
        HashMap<Character, ArrayList<Agent>> agents_lookup = new HashMap<>();

        HashMap<String, Box> boxes = new HashMap<>();
        HashMap<Character, ArrayList<Box>> boxes_lookup = new HashMap<>();

        // We can assume that the level file is conforming to specification, since the server verifies this.
        // Read domain
        serverMessages.readLine(); // #domain
        serverMessages.readLine(); // hospital

        // Read Level name
        serverMessages.readLine(); // #levelname
        serverMessages.readLine(); // <name>

        // Read colours
        serverMessages.readLine(); // #colours

        String line = serverMessages.readLine();
        while (!line.startsWith("#")) {
            String[] split = line.split(":");

            String colour = split[0].strip();

            String[] entities = split[1].split(",");

            for (String entity : entities) {
                char c = entity.strip().charAt(0);
                if ('0' <= c && c <= '9') {
                    NameToColor.put(c, colour);

                } else if ('A' <= c && c <= 'Z') {
                    NameToColor.put(c, colour);

                }
            }
            line = serverMessages.readLine();
        }

        // Read initial state
        // line is currently "#initial"
        int numRows = 0;
        int numCols = 0;
        line = serverMessages.readLine();
        while (!line.startsWith("#")) {
            levelLines.add(line);
            numCols = Math.max(numCols, line.length());
            ++numRows;
            line = serverMessages.readLine();

        }

        for (int row = 0; row < numRows - 1; ++row) {
            line = levelLines.get(row);
            for (int col = 0; col < line.length() - 1; ++col) {
                char c = line.charAt(col);
                // Not a wall
                if (c != '+') {

                    //First add node
                    Node node = new Node(row + " " + col);
                    String node_string = node.NodeId;
                    stringToNode.put(node_string,node);
                    map.addNode(node_string);

                    //Link nodes between each other
                    if (line.charAt(col + 1) != '+') {
                        // Temporary token to increment the id value for column
                        int tempCol = col + 1;
                        map.addEdge(node_string, (row + " " + tempCol));
                    }
                    String nextLine = levelLines.get(row + 1);
                    try {
                        if (nextLine.charAt(col) != '+') {
                            // Temporary token to increment the id value for row
                            int tempRow = row + 1;
                            map.addEdge(node_string, (tempRow + " " + col));
                        }
                    } catch (Exception e){
                        System.err.println("Reading the file gave an OutOfBoundsException");
                    }

                    //Check if it's an agent
                    if ('0' <= c && c <= '9') {
                        //System.err.println(agents_lookup);

                        // Add agent of certain char to the agent map
                        if (agents_lookup.containsKey(c)) {
                            Integer newi = agents_lookup.get(c).size()+1;
                            Agent agent = new Agent(node, c + newi.toString());
                            agents.put(c + newi.toString(), agent);
                            ArrayList<Agent> addAgent = new ArrayList<>(agents_lookup.get(c));
                            agents_lookup.replace(c,addAgent);
                        }
                        // Create a new entry in the map
                        else {
                            Agent agent = new Agent(node, c+"");
                            agents.put(c+"", agent);
                            ArrayList<Agent> addAgent = new ArrayList<>();
                            addAgent.add(agent);
                            agents_lookup.put(c,addAgent);
                        }


                    }
                    //Else check if it's a box, same procedure as agent
                    else if ('A' <= c && c <= 'Z') {

                        if (boxes_lookup.containsKey(c)) {
                            Integer newi = boxes_lookup.get(c).size()+1;
                            Box box = new Box(node, c+newi.toString());

                            boxes.put(c + newi.toString(), box);
                            ArrayList<Box> addBox = new ArrayList<>(boxes_lookup.get(c));
                            addBox.add(box);
                            boxes_lookup.replace(c,addBox);
                        }
                        else {
                            Box box = new Box(node, c+"0");
                            boxes.put(c+"0", box);
                            ArrayList<Box> addBox = new ArrayList<>();
                            addBox.add(box);
                            boxes_lookup.put(c,addBox);
                        }
                    }

                }
            }
        }

        // Read goal state
        // line is currently "#goal"

        line = serverMessages.readLine();
        int row = 0;

        while (!line.startsWith("#")) {
            for (int col = 0; col < line.length(); ++col) {
                char c = line.charAt(col);
                // Add goal to agent goals
                Node goal = new Node(row + " " + col);
                if ('0' <= c && c <= '9') {
                    for (Agent agent : agents_lookup.get(c)) {
                        agent.setGoal(goal);
                    }
                }
                // For boxes
                else if ('A' <= c && c <= 'Z'){

                    for (Box box: boxes_lookup.get(c)) {
                        box.setGoal(goal);
                    }

                }
            }
            ++row;
            line = serverMessages.readLine();
        }

        // End
        // line is currently "#end"

        return new State(stringToNode,agents, NameToColor, boxes, map);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Use stderr to print to the console.
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Send client name to server.
        System.out.println("SearchClient");

        // Parse the level.
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.US_ASCII));

        State initialState = SearchClient.parseLevel(serverMessages);

        // Recursively find subcomponents of the state, s.t. the
        ArrayList<State> componentStates = initialState.allStates();

        Action[][] plan;

        try {
            // Run MaPPAlgorithm: Can be done in parallel to optimize speed
            for(State S : componentStates)
                MaPPAlgorithm.MaPPVanilla(S);

            plan = Converter.getConversion(initialState.agents);
        }

        catch (OutOfMemoryError ex){
            System.err.println("Maximum memory usage exceeded.");
            plan = null;
        }
        if (plan == null)
        {
            System.exit(0); // No plan
        }
        else
        {
            // Print the plan to the server
            for (Action[] jointAction : plan)
            {
                System.out.print(jointAction[0].name);
                for (int action = 1; action < jointAction.length; ++action)
                {
                    System.out.print("|");

                    System.out.print(jointAction[action].name);
                }
                System.out.println();
                // We must read the server's response to not fill up the stdin buffer and block the server.
                serverMessages.readLine();
            }
        }

    }
}
