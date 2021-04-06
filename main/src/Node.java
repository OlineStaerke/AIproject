public class Node{
    public String NodeId;
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

    @Override
    public String toString(){
        return "(" + this.NodeId + ")";
    }

    @Override
    public int hashCode() {
        System.out.println("NODE ID: "+NodeId);
        System.out.println(Integer.parseInt(NodeId.replaceAll("\\s+","")));
        return Integer.parseInt(NodeId.replaceAll("\\s+",""));
    }





}
