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
            // Copy of agents which are then sorted w.r.t. priority. Must be done dynamically, as order can change
            state.SetBlanked(false);
            var agentsInOrder =  state.AgentsInOrder();
            Thread.sleep(1000);

            for(Agent agent : agentsInOrder){
                System.out.println(agent.color);
                System.out.println(agent.position);

                if (agent.mainPlan.plan.size() > 0) {

                    String wantedMove = agent.mainPlan.plan.get(0);
                    System.out.println(wantedMove);

                    // Agent has in this step been pushed away from mainPlan (pi).
                    if (agent.blanked){
                        agent.finalPlan.add(agent.position);
                    }

                    // Agent has been moved by bring blank. Maybe it selected correct move of mainplan
                    else if (wantedMove.equals(agent.position.NodeId)){
                        agent.finalPlan.add(agent.position);
                        agent.mainPlan.plan.remove(0);
                    }

                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {
                        // Bring Blank and move
                        var occupyingAgent = state.occupiedNodes.get(wantedMove);
                        if (occupyingAgent.priority > agent.priority) {
                            occupyingAgent.bringBlank();
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

            // repositioning step
            // An agent can reposition iff: Agent is not on pi. Agent has not moved this iteration.
            for(Agent agent : agentsInOrder) {
                    agent.reposition(state.map);
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
