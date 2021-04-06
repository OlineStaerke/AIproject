public class Edge {
    Node Node1;
    Node Node2;
    Integer Weight;

    public Edge(Node node1, Node node2, Integer weight) {
        this.Node1 = node1;
        this.Node2 = node2;
        this.Weight = weight;
    }

    @Override
    public String toString(){

        return this.Node1.toString() + " -> " + this.Node2.toString();
    }
}
