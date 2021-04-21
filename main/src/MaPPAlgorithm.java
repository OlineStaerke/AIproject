import java.security.cert.CertificateRevokedException;
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
        ArrayList<Agent> newAgentsInOrder =  state.AgentsInOrder();
        while(!goalIsReached){
            System.err.println();


            // Copy of agents which are then sorted w.r.t. priority. Must be done dynamically, as order can change

            Thread.sleep(500);

            System.err.println("-----------------------------------");
            System.err.println(state.occupiedNodes);

            ArrayList<Agent> agentsInOrder = new ArrayList<>(newAgentsInOrder);
            ArrayList<Agent> checkInOrder = new ArrayList<>(newAgentsInOrder);



            for(Agent agent : agentsInOrder) {
                checkInOrder.remove(agent);

                System.err.println(agent);
                System.err.println("mainplan:"+ agent.mainPlan.plan);


                if (agent.mainPlan.plan.size() > 0) {


                    String wantedMove = agent.mainPlan.plan.get(0);
                    System.err.println("wantedmove:" + wantedMove);


                    // wantedMove = position: Stay.
                    if (wantedMove.equals(agent.position.NodeId)) {
                        agent.finalPlan.add(agent.position);
                        agent.finalPlanString.add(agent.position.getNodeId());
                        agent.mainPlan.plan.remove(0);


                    }


                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {
                        System.out.println("CONFLICT! I am agent:"+agent.ID);
                        // Bring Blank and move
                        var occupyingObject = state.occupiedNodes.get(wantedMove);

                        // Check here if occupied cell is your box
                        if (state.NameToColor.get(agent.ID).equals(state.NameToColor.get(occupyingObject.ID))) {
                            // Two cases:
                            // Box should be moved
                            // Box should not be moved, but is blocking for same color agent


                        }
                        System.err.println("CHECK"+checkInOrder);
                        if (checkInOrder.contains(occupyingObject) || occupyingObject.mainPlan.plan.size()<=0) {
                            System.err.println("!! I want: "+ wantedMove+" !! Occypied by :"+ occupyingObject);

                            occupyingObject.bringBlank(state, state.map, agent,agent.position.NodeId);
                            newAgentsInOrder.remove(occupyingObject);
                            newAgentsInOrder.add(0,(Agent) occupyingObject);

                            //((Agent) occupyingObject).conflicts = new ArrayList<>();
                            ((Agent) occupyingObject).conflicts.add(agent);
                            agent.problemnode = wantedMove;
                            //((Agent) occupyingObject).conflicts.addAll(agent.conflicts);
                            //agent.conflicts = new ArrayList<>();
                            agent.mainPlan.plan.add(0,agent.position.getNodeId());





                        }

                        // Do nothing, (NoOP). So the agent waits if he cannot enter a cell, or he has tried to make someone blank.

                        agent.finalPlan.add(agent.position);
                        agent.finalPlanString.add(agent.position.getNodeId());


                    }
                    // Empty cell
                    else if (!state.occupiedNodes.containsKey(wantedMove)) {
                        agent.ExecuteMove(state, state.stringToNode.get(wantedMove));
                    } else {

                        agent.finalPlan.add(agent.position);
                        agent.finalPlanString.add(agent.position.getNodeId());
                    }
                }
                    else {
                        //agent.setAgentsFree(state);
                        agent.finalPlan.add(agent.position);
                        agent.finalPlanString.add(agent.position.getNodeId());


                        // Agent is not in goal, proceed with next subgoal
                    /***
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
***/

                    }
                }


            // Boxes are automatically checked in agent.isInGoal
            state.UpdateOccupiedNodes();

            goalIsReached = true;
            for(Agent agent : agentsInOrder) {
                if (!agent.isInGoal()) {
                    goalIsReached = false;
                    if (agent.mainPlan.plan.size()==0) agent.setAgentsFree(state);
                }
                else agent.priority = 11;


            }







        }





    }


}
