public class Node{
    public String NodeId;
    boolean isTunnel = false;
    boolean isTunnelDynamic = false;
    boolean isTunnelOneWay = false;



    public Node(String id){
        this.NodeId = id;
    }
    public String getNodeId() {
        return this.NodeId;
    }



    @Override
    public String toString(){
        return "(" + this.NodeId + ")";
    }

    @Override
    public int hashCode() {
        //System.out.println(Integer.parseInt(NodeId.replaceAll("\\s+","")));
        return Integer.parseInt(NodeId.replaceAll("\\s+",""));
    }





}
