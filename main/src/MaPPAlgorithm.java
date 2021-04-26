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
        ArrayList<Agent> newAgentsInOrder =  state.AgentsInOrder();
        ArrayList<Agent> agentsInOrder = new ArrayList<>(newAgentsInOrder);
        Collections.sort(agentsInOrder,new Agent.CustomComparator());
        while(!goalIsReached){
            System.err.println();


            // Copy of agents which are then sorted w.r.t. priority. Must be done dynamically, as order can change

            Thread.sleep(100);

            System.err.println("-----------------------------------");
            System.err.println(state.occupiedNodes);


            ArrayList<Agent> checkInOrder = new ArrayList<>(newAgentsInOrder);
            System.err.println("Agents in order :"+agentsInOrder);


            //agentsInOrder = new ArrayList<>(newAgentsInOrder);

            for(Agent agent : agentsInOrder) {

                System.err.println("//////////////////////");
                System.err.println("stateblankplan:"+state.blankPlan.size());


                System.err.println("MAINPLAN"+agent.mainPlan.plan);
                System.err.println("BLANK:"+agent.blank);
                System.err.println("CONFLICTS:"+agent.conflicts);
                checkInOrder.remove(agent);

                System.err.println(agent);



                if ((agent.mainPlan.plan.size()> 0) && (state.blankPlan.size()<=0||agent.blank)) {


                    String wantedMove= agent.mainPlan.plan.get(0);



                    // wantedMove = position: Stay.
                    if (wantedMove.equals(agent.position.NodeId)) {
                        agent.ExecuteMove(agent,state,state.stringToNode.get(wantedMove));
                        /**
                        System.err.println("A");
                        agent.finalPlan.add(agent.position);
                        agent.finalPlanString.add(agent.position.getNodeId());
                        agent.mainPlan.plan.remove(0);


                        if (agent.blank && state.blankPlan.size()>0) {
                            state.blankPlan.remove(0);
                        }
                         **/



                    }


                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {
                        System.out.println("CONFLICT! I am agent:"+agent.ID);
                        // Bring Blank and move
                        var occupyingObject = state.occupiedNodes.get(wantedMove);


                       // if (checkInOrder.contains(occupyingObject) || occupyingObject.mainPlan.plan.size()<=0) {
                            System.err.println("!! I want: "+ wantedMove+" !! Occypied by :"+ occupyingObject);

                            occupyingObject.bringBlank(state, state.map, agent);
                            //newAgentsInOrder.remove(occupyingObject);
                            //newAgentsInOrder.add(0,(Agent) occupyingObject);
                            ((Agent) occupyingObject).blank = true;


                            //((Agent) occupyingObject).conflicts = new ArrayList<>();
                            //if (agent.blank) {
                             //   ((Agent) occupyingObject).conflicts = agent.conflicts;

                           // }
                            //else {((Agent) occupyingObject).conflicts = agent;}

                           ((Agent) occupyingObject).conflicts = agent;

                            agent.blank = false;


                            agent.problemnode = wantedMove;
                            //((Agent) occupyingObject).conflicts.addAll(agent.conflicts);
                            //agent.conflicts = new ArrayList<>();
                            //agent.mainPlan.plan.add(0,agent.position.NodeId);
                            //agent.mainPlan.plan.add(0,agent.position.NodeId);





                        //}

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
                    //if (agent.mainPlan.plan.size()==0) agent.blank = false;//agent.setAgentsFree(state);

                }
                //if (agent.blank && agent.mainPlan.plan.size()==0 && !agent.isInGoal()) {
                //    agent.setAgentsFree(state);
                //}
                if (agent.mainPlan.plan.size()>0) {
                    noroutes = false;
                }

                //else agent.priority = 11;


            }
            if (noroutes) {
                for(Agent agent : agentsInOrder) {


                   if (!agent.isInGoal()) {

                       System.err.println("AGENT!");

                       agent.blank = true;
                       System.err.println("OCC"+state.occupiedNodes);
                       LinkedHashSet visited = new LinkedHashSet();
                       //visited.remove(agent.position.NodeId);

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
