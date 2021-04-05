import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SearchClient {
    public static State parseLevel(BufferedReader serverMessages)
            throws IOException {
        // We can assume that the level file is conforming to specification, since the server verifies this.
        // Read domain
        serverMessages.readLine(); // #domain
        serverMessages.readLine(); // hospital

        // Read Level name
        serverMessages.readLine(); // #levelname
        serverMessages.readLine(); // <name>

        // Read colours
        serverMessages.readLine(); // #colours
        Colour[] agentcolours = new Colour[10];
        Colour[] boxcolours = new Colour[26];
        String line = serverMessages.readLine();
        while (!line.startsWith("#")) {
            String[] split = line.split(":");
            //Colour colour = Colour.fromString(split[0].strip());
            Colour colour = new Colour();
            colour.setColour(split[0].strip());
            String[] entities = split[1].split(",");
            for (String entity : entities) {
                char c = entity.strip().charAt(0);
                if ('0' <= c && c <= '9') {
                    agentcolours[c - '0'] = colour;
                } else if ('A' <= c && c <= 'Z') {
                    boxcolours[c - 'A'] = colour;
                }
            }
            line = serverMessages.readLine();
        }

        // Read initial state
        // line is currently "#initial"
        int numRows = 0;
        int numCols = 0;
        ArrayList<String> levelLines = new ArrayList<>(64);
        line = serverMessages.readLine();
        while (!line.startsWith("#")) {
            levelLines.add(line);
            numCols = Math.max(numCols, line.length());
            ++numRows;
            line = serverMessages.readLine();
        }

        Map map = new Map();
        Agent[] agents = new Agent[10];
        ArrayList<Box> boxes = new ArrayList<>();

        for (int row = 1; row < numRows; ++row) {
            line = levelLines.get(row);
            for (int col = 1; col < line.length(); ++col) {
                char c = line.charAt(col);
                if (c != '+') {
                    //First add node
                    Node node = new Node(row + " " + col);
                    map.addNode(node);


                    //Link nodes between each other
                    if (line.charAt(col + 1) != '+') {
                        map.addEdge(node, new Node(row + " " + col + 1), 0, true);
                    }
                    String nextLine = levelLines.get(row + 1);
                    if (nextLine.charAt(col) != '+') {
                        map.addEdge(node, new Node(row + 1 + " " + col), 0, true);
                    }

                    //Check if it's an agent
                    if ('0' <= c && c <= '9') {
                        agents[c - '0'] = new Agent(node);
                    }
                    //Else check if it's a box
                    else if ('A' <= c && c <= 'Z') {
                        boxes.add(new Box(node, c));
                    }
                }
            }
        }

        // Read goal state
        // line is currently "#goal"
        ArrayList<Node> goals = new ArrayList<>();
        line = serverMessages.readLine();
        int row = 0;
        while (!line.startsWith("#")) {
            for (int col = 0; col < line.length(); ++col) {
                char c = line.charAt(col);
                //Need to look at this
                if (('0' <= c && c <= '9') || ('A' <= c && c <= 'Z')) {
                    Node goal = new Node(row + " " + col);
                    goal.setGoal();
                    goals.add(goal);
                }
            }

            ++row;
            line = serverMessages.readLine();
        }

        // End
        // line is currently "#end"

        return new State(agents, agentcolours, boxes, goals);
    }

    public void search() {
        return;
    }


    public static void main(String[] args)
            throws IOException {
        // Use stderr to print to the console.
        System.err.println("SearchClient initializing. I am sending this using the error output stream.");

        // Send client name to server.
        System.out.println("SearchClient");

        // We can also print comments to stdout by prefixing with a #.
        System.out.println("#This is a comment.");

        // Parse the level.
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.US_ASCII));
        State initialState = SearchClient.parseLevel(serverMessages);
        System.out.println(initialState);
        // Select search strategy.
        //Frontier frontier;
        /*
        if (args.length > 0)
        {
            switch (args[0].toLowerCase(Locale.ROOT))
            {
                case "-bfs":
                    frontier = new FrontierBFS();
                    break;
                case "-dfs":
                    frontier = new FrontierDFS();
                    break;
                case "-astar":
                    frontier = new FrontierBestFirst(new HeuristicAStar(initialState));
                    break;
                case "-wastar":
                    int w = 5;
                    if (args.length > 1)
                    {
                        try
                        {
                            w = Integer.parseUnsignedInt(args[1]);
                        }
                        catch (NumberFormatException e)
                        {
                            System.err.println("Couldn't parse weight argument to -wastar as integer, using default.");
                        }
                    }
                    frontier = new FrontierBestFirst(new HeuristicWeightedAStar(initialState, w));
                    break;
                case "-greedy":
                    frontier = new FrontierBestFirst(new HeuristicGreedy(initialState));
                    break;
                case "-sgreedy":
                    frontier = new FrontierBestFirst(new HeuristicSuggestionGreedy(initialState));
                    break;
                case "-sastar":
                    frontier = new FrontierBestFirst(new HeuristicSuggestionAStar(initialState));
                    break;
                default:
                    frontier = new FrontierBFS();
                    System.err.println("Defaulting to BFS search. Use arguments -bfs, -dfs, -astar, -wastar, or " +
                            "-greedy to set the search strategy.");
            }
        }
        else
        {
            frontier = new FrontierBFS();
            System.err.println("Defaulting to BFS search. Use arguments -bfs, -dfs, -astar, -wastar, or -greedy to " +
                    "set the search strategy.");
        }

        // Search for a plan.
        Action[][] plan;
        try
        {
            plan = SearchClient.search(initialState, frontier);
        }
        catch (OutOfMemoryError ex)
        {
            System.err.println("Maximum memory usage exceeded.");
            plan = null;
        }

        // Print plan to server.
        if (plan == null)
        {
            System.err.println("Unable to solve level.");
            System.exit(0);
        }
        else
        {
            System.err.format("Found solution of length %,d.\n", plan.length);

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
    */
    }
}