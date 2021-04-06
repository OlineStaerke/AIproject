import java.util.*;

class Map {

    // We use Hashmap to store the edges in the graph
    public java.util.Map<String, List<String>> map = new HashMap<>();

    public void Map(){}
    // This function adds a new vertex to the graph
    public void addNode(String s)
    {
        if (!map.containsKey(s)) map.put(s, new LinkedList<String>());
    }

    // This function adds the edge
    // between source to destination
    public void addEdge(String node1,
                        String node2,Integer weight,
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


    public List<String> getAdjacent(String s) {
        return(map.get(s));
    }

    // Prints the adjacency list of each vertex.
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        for (String v : map.keySet()) {
            builder.append(v.toString() + ": ");
            for (String w : map.get(v)) {
                builder.append(w.toString() + " ");
            }
            builder.append("\n");
        }

        return (builder.toString());
    }
}

