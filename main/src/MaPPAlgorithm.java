import java.util.*;

public class MaPPAlgorithm {


    public static void MaPPVanilla(State state) throws InterruptedException {

        // TODO: Make as multi-processed (not multi-threaded!)
        for(Agent agent : state.agents.values()) agent.planPi(state.map);
        for(Box B : state.boxes.values()) B.planPi(state.map);


        boolean goalIsReached = false;

        // Each iteration is a processing of 1 step
        while(!goalIsReached){
            System.err.println();

            // Copy of agents which are then sorted w.r.t. priority. Must be done dynamically, as order can change
            var agentsInOrder =  state.AgentsInOrder();

            for(Agent agent : agentsInOrder) {
                // See hasMoved assignment from bringBlank
                if (agent.hasMoved) continue;

                if (agent.mainPlan.plan.size() > 0) {

                    String wantedMove = agent.mainPlan.plan.get(0);

                    // wantedMove = position: Stay.
                    if (wantedMove.equals(agent.position.NodeId)) {
                        agent.finalPlan.add(agent.position);
                        agent.mainPlan.plan.remove(0);
                    }


                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {

                        var occupyingObject = state.occupiedNodes.get(wantedMove);

                        // Check here if occupied cell is your box
                        if (state.NameToColor.get(agent.ID).equals(state.NameToColor.get(occupyingObject.ID))) {
                            // Two scenarios:
                            // 1: The box is blocking the owner agent
                            // 2: The box should be moved




                        }
                        if (occupyingObject.priority >= agent.priority) {

                            occupyingObject.bringBlank(state, state.map, agent.mainPlan.plan);
                            if (occupyingObject.position.isTunnel) occupyingObject.priority = agent.priority;


                            // Do nothing, (NoOP). So the agent waits if he cannot enter a cell, or he has tried to make someone blank.
                            agent.finalPlan.add(agent.position);

                        }


                    }
                    // Empty cell
                    else if (!state.occupiedNodes.containsKey(wantedMove)) {
                        agent.ExecuteMove(state, state.stringToNode.get(wantedMove));
                    } else {
                        agent.finalPlan.add(agent.position);
                    }
                }
                else {
                    agent.finalPlan.add(agent.position);

                    // Agent is not in goal, proceed with next subgoal
                    if (!agent.isInGoal()) {
                        for (Box B : agent.boxes) {
                            if (!B.isInGoal()) {
                                agent.mainPlan.createPlan(state.map, agent.position.NodeId,
                                        B.position.NodeId, new LinkedHashSet<>());
                                break;
                            }
                        }
                        // All boxes are in goals, go to finish.
                        if (agent.mainPlan.plan.size() == 0) {
                            agent.planPi(state.map);
                        }
                    }

                }
            }


            // Boxes are automatically checked in agent.isInGoal
            goalIsReached = true;
            for(Agent agent : agentsInOrder) {
                agent.hasMoved = false;
                if (!agent.isInGoal()) {
                    goalIsReached = false;
                }
            }





        }





    }


}
