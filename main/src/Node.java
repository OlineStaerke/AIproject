public class Node {
    String NodeId;
    boolean isGoal = false;

    public Node(String id){
        this.NodeId = id;
    }
    public String getNodeId() {
        return this.NodeId;
    }

    public void setGoal(){
        this.isGoal = true;
    }

    public boolean isGoal(){
        return isGoal;
    }
}
