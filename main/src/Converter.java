import java.util.ArrayList;
import java.util.HashMap;

public final class Converter {

    private Converter(){
    }

    public static void getConversion(HashMap<Integer, Agent> agents){

        int numAgents = agents.size();
        for (int agent = 0; agent < numAgents; agent++){

            ArrayList<Node> finalPlan = agents.get(agent).getFinalPlan();

            for (Node node: finalPlan) {


            }
        }
    }
}
