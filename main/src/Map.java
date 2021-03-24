import java.util.*;

class Map {

    // We use Hashmap to store the edges in the graph
    private java.util.Map<Node, List<Edge>> map = new HashMap<>();

    public void Map(){}
    // This function adds a new vertex to the graph
    public void addNode(Node s)
    {
        map.put(s, new LinkedList<Edge>());
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
        Edge edge = new Edge(node1,node2,weight);
        map.get(node1).add(edge);
        if (bidirectional == true) {
            map.get(node2).add(edge);
        }
    }



    public List<Edge> getAdjacent(Node s) {
        return(map.get(s));
    }

    // Prints the adjancency list of each vertex.
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        for (Node v : map.keySet()) {
            builder.append(v.toString() + ": ");
            for (Edge w : map.get(v)) {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }

        return (builder.toString());
    }
}

