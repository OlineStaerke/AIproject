import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public final class Converter {

    public static Action[][] getConversion(HashMap<String, Agent> agents)  {

        int numAgents = agents.size();

        // Remove the first move on the plan (This is always noOp)
        for (Agent A : agents.values()){
            var q = A.finalPlan.remove(0);
            System.err.println("First index of final plan removed, action was: " + q);
        }

        int numRounds = agents.get("0").getFinalPlan().size();

        Action[][] convPlan = new Action[numAgents][]; //A converted plan for each agent
        Action[][] ultimatePlan = new Action[numRounds][]; //The fully converted plan which will be returned
        //Increment to loop through agent Hashmap<Character, Agent>
        int inc = 0;

        for (Agent A : agents.values()) {
            convPlan[inc] = fromCoordsToDirections(A.getFinalPlan(), A.boxes); //Conversion of each agent's plan
            System.err.println("Final plan (Agent " + A.ID + "): " + A.getFinalPlan());
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

    private static Action[] fromCoordsToDirections(ArrayList<Node> plan, ArrayList<Box> boxes) {


        Action[] convertedFinalPlan = new Action[plan.size()];
        String node1 = plan.get(0).getNodeId();
        convertedFinalPlan[0] = Action.NoOp;

        for (int action = 1; action < plan.size(); action++) {
            String node2 = plan.get(action).getNodeId();


            int rowDiff = Integer.parseInt(node2.split(" ")[0]) - Integer.parseInt(node1.split(" ")[0]);
            int colDiff = Integer.parseInt(node2.split(" ")[1]) - Integer.parseInt(node1.split(" ")[1]);

            int boxRowDiff = 0;
            int boxColDiff = 0;
            String boxMove1 = "";
            String boxMove2 = "";

            for(Box B: boxes){
                if (B.finalPlan.size() != plan.size()){
                    System.err.println("############### FINAL PLANS OF DIFFERENT LENGTH! ###############");
                    System.err.println(B.finalPlan);
                    System.err.println(plan);

                    return null;

                }

                boxMove1 = B.finalPlan.get(action-1).NodeId;
                boxMove2 = B.finalPlan.get(action).NodeId;

                if (!boxMove1.equals(boxMove2)){
                    boxRowDiff = Integer.parseInt(boxMove2.split(" ")[0]) - Integer.parseInt(boxMove1.split(" ")[0]);
                    boxColDiff  = Integer.parseInt(boxMove2.split(" ")[1]) - Integer.parseInt(boxMove1.split(" ")[1]);
                    break;
                }

            }

            if (boxColDiff != 0 ||  boxRowDiff != 0){

                // Negativ: agent above box. Positive if agent is below
                int AgentToBoxRowDiff = Integer.parseInt(node1.split(" ")[0]) - Integer.parseInt(boxMove1.split(" ")[0]);

                // Positive: if agent is to the east. Negative if agent is to the west
                int AgentToBoxColDiff = Integer.parseInt(node1.split(" ")[1]) - Integer.parseInt(boxMove1.split(" ")[1]);

                // Box Moved north
                if (boxRowDiff == -1){

                    // Agent moved north
                    if (rowDiff == -1){

                        if (AgentToBoxRowDiff < 0){ convertedFinalPlan[action] = Action.PullNN; }
                        else{convertedFinalPlan[action] = Action.PushNN;}
                    }

                    // Agent moved west
                    else if(colDiff == -1){
                        if (AgentToBoxRowDiff < 0){ convertedFinalPlan[action] = Action.PullWN; }
                        else{convertedFinalPlan[action] = Action.PushWN;}

                    }
                    // Agent moved east
                    else if(colDiff == 1){
                        if (AgentToBoxColDiff < 0){ convertedFinalPlan[action] = Action.PushEN; }
                        else{convertedFinalPlan[action] = Action.PullEN;}

                    }
                }

                // Box Moved south
                else if(boxRowDiff == 1){

                    // Agent moved south
                    if(rowDiff == 1){
                        if (AgentToBoxRowDiff < 0){ convertedFinalPlan[action] = Action.PushSS; }
                        else{convertedFinalPlan[action] = Action.PullSS;}
                    }
                    // Agent moved west
                    else if(colDiff == -1){
                        if (AgentToBoxRowDiff > 0) {convertedFinalPlan[action] = Action.PullWS;
                        }
                        else{convertedFinalPlan[action] = Action.PushWS;}
                    }

                    // Agent moved east
                    else if(colDiff == 1){
                        if (AgentToBoxRowDiff > 0) {convertedFinalPlan[action] = Action.PullES; }
                        else{convertedFinalPlan[action] = Action.PushES;}
                    }
                }

                // Box Moved East
                else if(boxColDiff == 1){
                    // Agent moved north
                    if (rowDiff == -1){
                        if (AgentToBoxColDiff > 0){ convertedFinalPlan[action] = Action.PullNE; }
                        else{convertedFinalPlan[action] = Action.PushNE;}
                    }

                    // Agent moved south
                    else if(rowDiff == 1){
                        if (AgentToBoxRowDiff < 0){ convertedFinalPlan[action] = Action.PushSE; }
                        else{convertedFinalPlan[action] = Action.PullSE;}
                    }

                    // Agent moved east
                    else if(colDiff == 1){
                        if (AgentToBoxColDiff > 0){ convertedFinalPlan[action] = Action.PullEE; }
                        else{convertedFinalPlan[action] = Action.PushEE;}
                    }
                }


                // Box moved west
                else if(boxColDiff == -1){
                    // Agent moved north
                    if (rowDiff == -1){
                        if (AgentToBoxRowDiff > 0){ convertedFinalPlan[action] = Action.PushNW; }
                        else{convertedFinalPlan[action] = Action.PullNW;}
                    }

                    // Agent moved south
                    else if(rowDiff == 1){
                        if (AgentToBoxColDiff < 0){ convertedFinalPlan[action] = Action.PullSW; }
                        else{convertedFinalPlan[action] = Action.PushSW;}

                    }
                    // Agent moved west
                    else if(colDiff == -1){
                        if (AgentToBoxColDiff < 0){ convertedFinalPlan[action] = Action.PullWW; }
                        else{convertedFinalPlan[action] = Action.PushWW;}
                    }
                }
            }


            else {
                if (rowDiff == 0 && colDiff == 0) {
                    convertedFinalPlan[action] = Action.NoOp;
                } else if (rowDiff == -1 && colDiff == 0) {
                    convertedFinalPlan[action] = Action.MoveN;
                } else if (rowDiff == 0 && colDiff == -1) {
                    convertedFinalPlan[action] = Action.MoveW;
                } else if (rowDiff == 1 && colDiff == 0) {
                    convertedFinalPlan[action] = Action.MoveS;
                } else if (rowDiff == 0 && colDiff == 1) {
                    convertedFinalPlan[action] = Action.MoveE;
                }
            }

            node1 = node2;
        }

        return convertedFinalPlan;
    }


}
