import java.security.cert.CertificateRevokedException;
import java.util.*;

public class MaPPAlgorithm {


    public static void MaPPVanilla(State state) throws InterruptedException {

        for(Agent agent : state.agents.values()){
            // Finds initial plan with BFS
            agent.planGoals(state, new LinkedHashSet());

        }

        boolean goalIsReached = false;

        // TODO: Check if node.equals works (it might be pointing towards reference in memory, not actual value@Mathias
        // Each iteration is a processing of 1 step
        // Follows Algo 1, has "Progression step" and "repositioning step" merged to improve speed.

        ArrayList<Agent> agentsInOrder = new ArrayList<>(state.agents.values());
        Collections.sort(agentsInOrder,new Agent.CustomComparator());
        int round = 0;
        while(!goalIsReached){
            System.err.println();


            // Copy of agents which are then sorted w.r.t. priority. Must be done dynamically, as order can change

            //
            //
            Thread.sleep(100);

            System.err.println("-----------------------------------");
            System.err.println(state.occupiedNodes);

            System.err.println("Agents in order :"+agentsInOrder);


            //agentsInOrder = new ArrayList<>(newAgentsInOrder);
            round+=1;

            for(Agent agent : agentsInOrder) {

                agent.subgoals.UpdateGoals();

                System.err.println("//////////////////////");
                System.err.println("ROUND"+round);
                System.err.println();
                System.err.println(agent);
                System.err.println("MAINPLAN:"+agent.mainPlan.plan);
                System.err.println("IN GOAL "+agent.isInGoal());
                System.err.println("Current SubGoal:"+agent.currentGoal);
                System.err.println("all goals: "+agent.subgoals.goals);



                if ((agent.mainPlan.plan.size()> 0) && (state.blankPlan.size()<=0||agent.blank||(!state.agentConflicts.contains(agent) && !agent.position.isTunnel))) {
                    String wantedMove = agent.mainPlan.plan.get(0);

                    // if the wanted move is a box position
                    if (agent.attached_box!=null) {
                        if (wantedMove == agent.attached_box.position.NodeId && agent.attached_box.mainPlan.plan.size()>0) {
                            wantedMove = agent.attached_box.mainPlan.plan.get(0);
                        }
                        // wantedMove = position: Stay.
                        else {
                            wantedMove = agent.position.NodeId;
                        }

                    }






                    if (agent.attached_box!=null && wantedMove.equals(agent.attached_box.position.NodeId)) {
                        agent.ExecuteMove(agent,state, false);
                    }
                    else if (wantedMove.equals(agent.position.NodeId) ) {
                        agent.ExecuteMove(agent,state,false);

                    }



                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {
                        System.out.println("CONFLICT! I am agent:"+agent.ID);
                        // Bring Blank and move
                        var occupyingObject = state.occupiedNodes.get(wantedMove);


                            System.err.println("!! I want: "+ wantedMove+" !! Occypied by :"+ occupyingObject);


                            // Agent is blocking and is not currenly removing a box
                            if (occupyingObject instanceof Agent) {
                                var occupyingAgent = (Agent) occupyingObject;

                               if (state.stringToNode.get(wantedMove).isTunnel) {
                                    state.agentConflicts.add(occupyingAgent);
                                }

                                if (occupyingAgent.conflicts == agent && occupyingAgent.mainPlan.plan.size()>0) {
                                    System.err.println("Same conflict agent");
                                    LinkedHashSet visited = new LinkedHashSet(state.occupiedNodes.keySet());
                                    visited.remove(occupyingAgent.position.NodeId);
                                    occupyingAgent.planPi(state, visited);
                                    //occupyingAgent.mainPlan.plan.remove(0);
                                    occupyingAgent.blank = true;
                                    occupyingObject.conflicts = null;
                                    agent.conflicts =null;
                                    agent.blank = false;
                                } else {
                                    System.err.println("bring blank!!");

                                    occupyingAgent.blank = true;
                                    occupyingAgent.conflicts = agent;

                                    agent.blank = false;

                                    occupyingAgent.bringBlank(state,  occupyingAgent,wantedMove);
                                }

                            }
                            // Box is blocking
                            else{
                                var occupyingBox = (Box) occupyingObject;

                                if (state.stringToNode.get(wantedMove).isTunnel) {
                                    state.agentConflicts.add(occupyingBox.owner);
                                }

                                // Your own box is blocking :(
                                // Maybe handle seperatly? idk
                                if (state.NameToColor.get(occupyingBox.ID).equals(state.NameToColor.get(agent.ID))){
                                    System.err.println("OWN COLOUR");
                                    System.err.println();
                                    occupyingBox.owner.blank = true;
                                    occupyingBox.owner.conflicts = agent;
                                    //System.err.println("OWNER: " + occupyingBox.owner.ID);

                                    occupyingBox.owner.mainPlan.plan = new ArrayList<>();
                                    occupyingBox.bringBlank(state,  occupyingBox.owner, wantedMove);

                                    //System.err.println("PLAN2: " + agent.mainPlan.plan);
                                } else{

                                    occupyingBox.owner.blank = true;
                                    occupyingBox.owner.conflicts = agent;
                                    //System.err.println("OWNER: " + occupyingBox.owner.ID);

                                    occupyingBox.bringBlank(state,occupyingBox.owner,wantedMove);



                                }

                            }

                            // Do nothing, (NoOP). So the agent waits if he cannot enter a cell, or he has tried to make someone blank.
                            agent.ExecuteMove(agent,state, true);




                    }
                    // Empty cell
                    else if (!state.occupiedNodes.containsKey(wantedMove)) {

                        agent.ExecuteMove(agent,state,  false);
                    } else {

                        agent.ExecuteMove(agent,state, true);
                    }
                }
                    else {
                        //agent.setAgentsFree(state);

                        agent.ExecuteMove(agent,state, true);


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

                     //Update the subgoals
                    agent.subgoals.UpdateGoals();

                for(Agent AA : agentsInOrder) {
                    System.err.println("PLANANAN:  " + AA.ID + "  " + AA.mainPlan.plan);

                }
                }


            // Boxes are automatically checked in agent.isInGoal
            state.UpdateOccupiedNodes();



            goalIsReached = true;
            Boolean noroutes = true;
            Boolean anyAgentBlank = false;
            for(Agent agent : agentsInOrder) {



                if (!agent.isInGoal()) {
                    goalIsReached = false;
                }
                if (agent.blank) {
                    anyAgentBlank = true;
                }
                if (agent.mainPlan.plan.size()>0) {
                    noroutes = false;
                }

            }

            if (noroutes) {

                for (Agent agent : agentsInOrder) {


                    if (!agent.isInGoal()) {


                        if (state.agentConflicts.size() > 0) {
                            agent.blank = true;

                            LinkedHashSet visited = new LinkedHashSet(state.occupiedNodes.keySet());
                            visited.remove(agent.position.NodeId);


                            agent.planPi(state, visited);

                            //state.agentConflicts.remove(agent);
                            // if (agent.mainPlan.plan.size()>0) {
                            //  state.blankPlan = new ArrayList<>(agent.mainPlan.plan);
                            //if (anyAgentBlank) break;
                            break;
                            //}
                        } else {
                            if (agent.mainPlan.plan.size() == 0) {

                                LinkedHashSet visited = new LinkedHashSet(state.occupiedNodes.keySet());
                                visited.remove(agent.position.NodeId);
                                agent.planPi(state, visited);
                            }


                        }
                    }
                }


                }
        }

    }


}
