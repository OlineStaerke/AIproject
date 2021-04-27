import java.security.cert.CertificateRevokedException;
import java.util.*;

public class MaPPAlgorithm {


    public static void MaPPVanilla(State state) throws InterruptedException {

        for(Agent agent : state.agents.values()){
            // Finds initial plan with BFS
            // TODO: Make as multi-processed (not multi-threaded!)
            agent.planPi(state.map, new LinkedHashSet());

        }

        boolean goalIsReached = false;

        // TODO: Check if node.equals works (it might be pointing towards reference in memory, not actual value@Mathias
        // Each iteration is a processing of 1 step
        // Follows Algo 1, has "Progression step" and "repositioning step" merged to improve speed.

        ArrayList<Agent> agentsInOrder = new ArrayList<>(state.agents.values());
        Collections.sort(agentsInOrder,new Agent.CustomComparator());
        while(!goalIsReached){
            System.err.println();


            // Copy of agents which are then sorted w.r.t. priority. Must be done dynamically, as order can change

            Thread.sleep(100);

            System.err.println("-----------------------------------");
            System.err.println(state.occupiedNodes);

            System.err.println("Agents in order :"+agentsInOrder);


            //agentsInOrder = new ArrayList<>(newAgentsInOrder);

            for(Agent agent : agentsInOrder) {

                System.err.println("//////////////////////");
                System.err.println(agent);



                if ((agent.mainPlan.plan.size()> 0) && (state.blankPlan.size()<=0||agent.blank)) {


                    String wantedMove= agent.mainPlan.plan.get(0);



                    // wantedMove = position: Stay.
                    if (wantedMove.equals(agent.position.NodeId)) {
                        agent.ExecuteMove(agent,state,state.stringToNode.get(wantedMove));

                    }


                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {
                        System.out.println("CONFLICT! I am agent:"+agent.ID);
                        // Bring Blank and move
                        var occupyingObject = state.occupiedNodes.get(wantedMove);


                            System.err.println("!! I want: "+ wantedMove+" !! Occypied by :"+ occupyingObject);

                            occupyingObject.bringBlank(state, agent);
                            //newAgentsInOrder.remove(occupyingObject);
                            //newAgentsInOrder.add(0,(Agent) occupyingObject);
                            ((Agent) occupyingObject).blank = true;
                            ((Agent) occupyingObject).conflicts = agent;

                            agent.blank = false;

                            // Do nothing, (NoOP). So the agent waits if he cannot enter a cell, or he has tried to make someone blank.

                            agent.finalPlan.add(agent.position);
                            agent.finalPlanString.add(agent.position.getNodeId());


                    }
                    // Empty cell
                    else if (!state.occupiedNodes.containsKey(wantedMove)) {

                        agent.ExecuteMove(agent,state, state.stringToNode.get(wantedMove));
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
            Boolean noroutes = true;
            for(Agent agent : agentsInOrder) {


                if (!agent.isInGoal()) {
                    goalIsReached = false;
                }

                if (agent.mainPlan.plan.size()>0) {
                    noroutes = false;
                }

            }
            if (noroutes) {
                for(Agent agent : agentsInOrder) {


                   if (!agent.isInGoal()) {

                     

                       agent.blank = true;

                       LinkedHashSet visited = new LinkedHashSet();
                       visited.remove(agent.position.NodeId);

                       agent.planPi(state.map,visited);
                       if (agent.mainPlan.plan.size()>0) {
                           state.blankPlan = new ArrayList<>(agent.mainPlan.plan);
                           break;
                       }
                   }
                }

                }

        }

    }


}
