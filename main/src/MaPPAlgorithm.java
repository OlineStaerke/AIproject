import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MaPPAlgorithm {


    public static void MaPPVanilla(State state) throws InterruptedException {

        for(Agent agent : state.agents.values()){
            // Finds initial plan with BFS
            // TODO: Make as multi-processed (not multi-threaded!)
            agent.planPi(state.map);

        }

        boolean goalIsReached = false;

        // TODO: Check if node.equals works (it might be pointing towards reference in memory, not actual value@Mathias
        // Each iteration is a processing of 1 step
        // Follows Algo 1, has "Progression step" and "repositioning step" merged to improve speed.
        while(!goalIsReached){
            System.err.println();

            // Copy of agents which are then sorted w.r.t. priority. Must be done dynamically, as order can change
            var agentsInOrder =  state.AgentsInOrder();
            Thread.sleep(1000);

            for(Agent agent : agentsInOrder){
                System.err.println(agent);
                System.err.println(agent.position);

                if (agent.mainPlan.plan.size() > 0) {

                    String wantedMove = agent.mainPlan.plan.get(0);
                    System.err.println(wantedMove);
                    System.err.println(state.occupiedNodes);


                    // ??
                    if (wantedMove.equals(agent.position.NodeId)){
                        agent.finalPlan.add(agent.position);
                        agent.mainPlan.plan.remove(0);
                    }

                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {
                        // Bring Blank and move
                        var occupyingObject = state.occupiedNodes.get(wantedMove);
                        if (occupyingObject.priority > agent.priority) {

                            occupyingObject.bringBlank(state,state.map);
                            agent.ExecuteMove(state, state.stringToNode.get(wantedMove));
                            }



                        // Do nothing, (NoOP)
                        else {
                            agent.finalPlan.add(agent.position);
                        }
                    }

                    // Empty cell
                    else if (!state.occupiedNodes.containsKey(wantedMove)) {
                        agent.ExecuteMove(state, state.stringToNode.get(wantedMove));
                    }
                    else{
                        agent.finalPlan.add(agent.position);
                    }

                }
                else{
                    agent.finalPlan.add(agent.position);
                }
            }


            goalIsReached = true;
            for(Agent agent : agentsInOrder)
                if (!agent.Goal.NodeId.equals(agent.position.NodeId)) {
                    goalIsReached = false;
                    break;
                }
            // Add similar loop for boxes.




        }





    }


}
