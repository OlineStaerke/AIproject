import java.util.*;

class Map {

    // We use Hashmap to store the edges in the graph
    private java.util.Map<Node, List<Node>> map = new HashMap<>();

    public void Map(){}
    // This function adds a new vertex to the graph
    public void addNode(Node s)
    {
        map.put(s, new LinkedList<Node>());
    }

    // This function adds the edge
    // between source to destination
    public void addEdge(Node node1,
                        Node node2,Integer weight,
                        boolean bidirectional)
    {

        if (!map.containsKey(node1))
            addNode(node1);

        if (!map.containsKey(node2))
            addNode(node2);

        map.get(node1).add(node2);
        if (bidirectional == true) {
            map.get(node2).add(node1);
        }
    }


    public List<Node> getAdjacent(Node s) {
        return(map.get(s));
    }

    // Prints the adjacency list of each vertex.
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        for (Node v : map.keySet()) {
            builder.append(v.toString() + ": ");
            for (Node w : map.get(v)) {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }

        return (builder.toString());
    }
}

