public class Node{
    public String NodeId;
    boolean isGoal = false;
    boolean isTunnel = false;


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

    @Override
    public String toString(){
        return "(" + this.NodeId + ")";
    }

    @Override
    public int hashCode() {
        System.out.println(Integer.parseInt(NodeId.replaceAll("\\s+","")));
        return Integer.parseInt(NodeId.replaceAll("\\s+",""));
    }





}
