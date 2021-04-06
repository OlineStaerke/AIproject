import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class Converter {

    private Converter(){
    }

    public static void getConversion(HashMap<Integer, Agent> agents){

        int numAgents = agents.size();
        for (int agent = 0; agent < numAgents; agent++){

            ArrayList<Node> finalPlan = agents.get(agent).getFinalPlan();
            Iterator<Node> iter = finalPlan.iterator();
            iter.next();
            while (iter.hasNext()){

                String node1 = iter.next().getNodeId();
                String node2 = iter.next().getNodeId();
                System.err.println(node1);
                System.err.println(node2);
            }
        }
    }


}
