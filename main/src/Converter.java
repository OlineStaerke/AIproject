import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public final class Converter {

    private Converter(){
    }

    public static Action[][] getConversion(HashMap<Character, Agent> agents){

        int numAgents = agents.size();
        int numRounds = agents.get('0').getFinalPlan().size();
        Action[][] convPlan = new Action[numAgents][]; //A converted plan for each agent
        Action[][] ultimatePlan = new Action[numRounds][]; //The fully converted plan which will be returned
        //Increment to loop through agent Hashmap<Character, Agent>
        int inc = 0;

        for (Character characterAgentEntry : agents.keySet()) {
            Agent agent = agents.get(characterAgentEntry);
            convPlan[inc] = fromCoordsToDirections(agent.getFinalPlan()); //Conversion of each agent's plan
            inc++;
        }
        // For each round build the JointAction plan and add it to the ultimate plan
        for (int round = 0; round < numRounds; round++) {
            Action[] jointAction = new Action[numAgents];
            for (int agent = 0; agent < numAgents; agent++) {
                jointAction[agent] = convPlan[agent][round];
            }
            ultimatePlan[round] = jointAction;
        }
        return ultimatePlan;
    }

    private static Action[] fromCoordsToDirections(ArrayList<Node> plan){

        System.err.println(plan);

        Action[] convertedFinalPlan = new Action[plan.size()];
        String node1 = plan.get(0).getNodeId();
        convertedFinalPlan[0] = Action.NoOp;
        for (int action = 1; action < plan.size(); action++) {

            String node2 = plan.get(action).getNodeId();

            int rowDiff = Integer.parseInt(node2.split(" ")[0]) - Integer.parseInt(node1.split(" ")[0]);
            int colDiff = Integer.parseInt(node2.split(" ")[1]) - Integer.parseInt(node1.split(" ")[1]);

            if (rowDiff == 0 && colDiff == 0){
                convertedFinalPlan[action] = Action.NoOp;
            }
            else if (rowDiff == -1 && colDiff == 0){
                convertedFinalPlan[action] = Action.MoveN;
            }
            else if (rowDiff == 0 && colDiff == -1){
                convertedFinalPlan[action] = Action.MoveW;
            }
            else if (rowDiff == 1 && colDiff == 0){
                convertedFinalPlan[action] = Action.MoveS;
            }
            else if (rowDiff == 0 && colDiff == 1){
                convertedFinalPlan[action] = Action.MoveE;
            }

            node1 = node2;
        }

        return convertedFinalPlan;
    }


}
