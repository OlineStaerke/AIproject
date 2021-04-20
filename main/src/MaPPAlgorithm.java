import java.util.*;

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

            for(Agent agent : agentsInOrder) {
                if (agent.hasMoved) continue;


                System.err.println(agent);
                System.err.println(agent.mainPlan.plan);
                System.err.println("finalplan size:"+agent.finalPlan.size());


                if (agent.mainPlan.plan.size() > 0) {

                    String wantedMove = agent.mainPlan.plan.get(0);
                    System.err.println(wantedMove);
                    System.err.println(state.occupiedNodes);


                    // wantedMove = position: Stay.
                    if (wantedMove.equals(agent.position.NodeId)) {
                        agent.finalPlan.add(agent.position);
                        agent.mainPlan.plan.remove(0);
                    }


                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {
                        System.out.println("CONFLICT!");
                        // Bring Blank and move
                        var occupyingObject = state.occupiedNodes.get(wantedMove);

                        // Check here if occupied cell is your box
                        if (state.NameToColor.get(agent.ID).equals(state.NameToColor.get(occupyingObject.ID))) {
                            // Two cases:
                            // Box should be moved
                            // Box should not be moved, but is blocking for same color agent


                        }
                        if (occupyingObject.priority >= agent.priority) {

                            occupyingObject.bringBlank(state, state.map, agent.mainPlan.plan);
                            if (occupyingObject.position.isTunnel) occupyingObject.priority = agent.priority;


                        }
                        // Do nothing, (NoOP). So the agent waits if he cannot enter a cell, or he has tried to make someone blank.
                        agent.finalPlan.add(agent.position);



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
            state.UpdateOccupiedNodes();

            goalIsReached = true;
            for(Agent agent : agentsInOrder) {
                agent.hasMoved = false;
                if (!agent.isInGoal()) {
                    goalIsReached = false;
                }
                else agent.priority = 11;
            }





        }





    }


}
