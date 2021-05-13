import java.security.cert.CertificateRevokedException;
import java.util.*;

public class MaPPAlgorithm {

    public static String GetWantedMove(Agent agent) {
        if (agent.mainPlan.plan == null || agent.mainPlan.plan.size() == 0) return agent.position.NodeId;
        String wantedMove = agent.mainPlan.plan.get(0);


        // if the wanted move is a box position
        if (agent.attached_box!=null) {
            if (wantedMove.equals(agent.attached_box.position.NodeId) && agent.attached_box.mainPlan.plan.size() > 0) {
                wantedMove = agent.attached_box.mainPlan.plan.get(0);
            }
            // wantedMove = position: Stay.
            else {
                wantedMove = agent.mainPlan.plan.get(0);
            }

        }


        return wantedMove;
    }


    public static void MaPPVanilla(State state) throws InterruptedException {

        System.err.println("BEGIN: " + state.agents.values());
        for(Agent agent : state.agents.values()){
            // Finds initial plan with BFS
            agent.planGoals(state);

        }


        for(Box box : state.boxes.values()){
            for (String goal: box.Goal) {
                ArrayList<Box> boxesForGoal;

                if (state.goals.containsKey(goal)) boxesForGoal = state.goals.get(goal);
                else boxesForGoal = new ArrayList<>();

                if (!boxesForGoal.contains(box)) {
                    boxesForGoal.add(box);
                    state.goals.put(goal, boxesForGoal);
                }

            }
        }
        //Try and find a path to goal, and choose the one closesest to each goal

        for (String goal : state.goals.keySet()) {
            Plan plan = new Plan();
            ArrayList<Box> boxes = state.goals.get(goal);
            ArrayList<String> boxPositions = new ArrayList<>();
            for (Box box : boxes) {
                boxPositions.add(box.position.NodeId);
            }
            plan.createPlan(state,goal,boxPositions,new LinkedHashSet<>());
            Integer i = boxPositions.indexOf(plan.plan.get(plan.plan.size()-1));
            Box boxForGoal = boxes.get(i);
            boxForGoal.setGoal(goal,state);
            Collections.reverse(Arrays.asList(plan.plan));
            boxForGoal.planToGoal = plan.plan;
        }





        for (Agent agent : state.agents.values()) {
            agent.subgoals.SortGoal(state);
            agent.planPi(state, new LinkedHashSet(), false);
            //System.err.println(agent.subgoals.goals);
        }
        //state.UpdateOccupiedNodes();

        boolean goalIsReached = false;

        // TODO: Check if node.equals works (it might be pointing towards reference in memory, not actual value@Mathias
        // Each iteration is a processing of 1 step
        // Follows Algo 1, has "Progression step" and "repositioning step" merged to improve speed.

        ArrayList<Agent> agentsInOrder = new ArrayList<>(state.agents.values());
        Collections.sort(agentsInOrder,new Agent.CustomComparator());
        
        int round = 0;
        while(!goalIsReached){
            //System.err.println();


            // Copy of agents which are then sorted w.r.t. priority. Must be done dynamically, as order can change
           //Thread.sleep(500);

            System.err.println("-----------------------------------");
            //System.err.println(state.occupiedNodes);
            //System.err.println("Agents in order :"+agentsInOrder);
            round+=1;
            System.err.println("ROUND: "+round);

            for(Agent agent : agentsInOrder) {

                agent.subgoals.UpdateGoals(state);

                System.err.println();
                System.err.println(agent);
                System.err.println("Current SubGoal:"+agent.currentGoal);
                String wantedMove = GetWantedMove(agent);


                if ((agent.mainPlan.plan.size()> 0) && (state.blankPlan.size()==0||agent.blank||(!state.agentConflicts.contains(agent) && !agent.position.isTunnel))) {





                    if (wantedMove.equals(agent.position.NodeId) || ((agent.attached_box!=null) && (wantedMove.equals(agent.attached_box.position.NodeId)))) {
                        agent.ExecuteMove(agent, state, false);
                    }




                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {

                        //System.err.println("CONFLICT! I am agent:"+agent.ID);
                        // Bring Blank and move
                        var occupyingObject = state.occupiedNodes.get(wantedMove);


                        //System.err.println("!! I want: "+ wantedMove+" !! Occypied by :"+ occupyingObject);


                        // Agent is blocking and is not currenly removing a box
                        if (occupyingObject instanceof Agent) {
                            var occupyingAgent = (Agent) occupyingObject;

                            //When conflict only move one at a time
                           if (state.stringToNode.get(wantedMove).isTunnel) {
                                state.agentConflicts.add(occupyingAgent);
                            }
                           else {
                               state.agentConflicts.add(occupyingAgent);
                           }


                            //Avoid deadlocks.
                            //TODO: check if we can remove && occupyingAgent.mainPlan.plan.size()>0
                            if (occupyingAgent.conflicts == agent && occupyingAgent.mainPlan.plan.size()>0) {
                                //System.err.println("Same conflict agent");
                                LinkedHashSet visited = new LinkedHashSet(state.occupiedNodes.keySet());
                                visited.remove(occupyingAgent.position.NodeId);
                                occupyingAgent.planPi(state, visited, false);
                                //occupyingAgent.mainPlan.plan.remove(0);

                                occupyingAgent.blank = true;
                                occupyingObject.conflicts = null;
                                agent.conflicts =null;
                                agent.blank = false;
                            } else {
                                System.err.println("Bring blank, not same conflict agent");
                                occupyingAgent.blank = true;
                                occupyingAgent.conflicts = agent;
                                agent.blank = false;
                                occupyingAgent.bringBlank(state,occupyingAgent);
                            }

                        }
                        // Box is blocking
                        else{

                            var occupyingBox = (Box) occupyingObject;

                            if (state.stringToNode.get(wantedMove).isTunnel) {
                                state.agentConflicts.add(occupyingBox.currentowner);
                            }

                            // Your own box is blocking :(
                            // Maybe handle seperatly? idk
                            if (state.NameToColor.get(occupyingBox.ID.charAt(0)).equals(state.NameToColor.get(agent.ID.charAt(0)))){
                                System.err.println("OWN COLOUR");
                                if (occupyingBox.currentowner.attached_box == null){
                                    occupyingBox.currentowner.currentGoal = null;
                                    occupyingBox.currentowner = agent;
                                    occupyingBox.Taken = false;

                                }
                                if(agent.currentGoal.gType == SubGoals.GoalType.BoxToGoal) {
                                    ((Box) agent.currentGoal.Obj).conflict_box= occupyingBox;
                                    occupyingBox.conflict_box = (Box) agent.currentGoal.Obj;
                                }else {
                                    occupyingBox.conflict_box = null;
                                }


                                occupyingBox.currentowner.blank = true;
                                occupyingBox.currentowner.conflicts = agent;
                                occupyingBox.conflicts=agent;
                                occupyingBox.conflictRoute = agent.mainPlan.plan;
                                //System.err.println("OWNER: " + occupyingBox.owner.ID);


                                occupyingBox.currentowner.mainPlan.plan = new ArrayList<>();
                                occupyingBox.bringBlank(state,  occupyingBox.currentowner);
                                occupyingBox.blankByOwn = true;
                                //occupyingBox.conflictRoute = new ArrayList<>();


                                System.err.println("PLAN2: " + agent.mainPlan.plan);
                            } else{
                                //System.err.println("Not same colour");
                                if(agent.currentGoal!=null && agent.currentGoal.gType == SubGoals.GoalType.BoxToGoal) {
                                    ((Box) agent.currentGoal.Obj).conflict_box = occupyingBox;
                                    occupyingBox.conflict_box = (Box) agent.currentGoal.Obj;
                                }
                                else {
                                    occupyingBox.conflict_box = null;
                                }
                                occupyingBox.conflictRoute = new ArrayList<>();
                                occupyingBox.currentowner.blank = true;
                                occupyingBox.currentowner.conflicts = agent;
                                occupyingBox.conflicts = agent;
                                //System.err.println("OWNER: " + occupyingBox.owner.ID);

                                occupyingBox.bringBlank(state,occupyingBox.currentowner);
                                occupyingBox.blankByOwn = false;



                            }

                            }
                            // Do nothing, (NoOP). So the agent waits if he cannot enter a cell, or he has tried to make someone blank.
                            agent.ExecuteMove(agent,state, true);

                            //Double NoOp
                            agent.mainPlan.plan.add(0,agent.position.NodeId);
                            if (agent.attached_box!=null) {
                                agent.attached_box.mainPlan.plan.add(0,agent.attached_box.position.NodeId);
                            }




                    }
                    // Empty cell
                    else if (!state.occupiedNodes.containsKey(wantedMove)) {
                        agent.ExecuteMove(agent,state,  false);
                    } else {
                        agent.ExecuteMove(agent,state, true);
                    }
                }
                    else {
                        agent.ExecuteMove(agent,state, true);

                    }

                    //Update the subgoals
                    agent.subgoals.UpdateGoals(state);

                }

            for(Agent AA : agentsInOrder) {
                System.err.println("PLANANAN:  " + AA.ID + "  " + AA.mainPlan.plan);
                for (Box BB : AA.boxes) {
                    //System.err.println("PLANANAN:  " + BB.ID + "  " + BB.mainPlan.plan);
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

            if (round==200) {
                goalIsReached = true;
            }
            //System.err.println("GOAL IS REACHED"+goalIsReached);
            for (Agent agent : agentsInOrder) {
                System.err.println("Current Goal "+agent.currentGoal);
                //System.err.println(agent.finalPlan);
                for (Box BB : agent.boxes) {
                    //System.err.println("PLANANAN:  " + BB.finalPlan);
                }


            }



            if (noroutes) {

                for (Agent agent : agentsInOrder) {
                    agent.nextGoal = agent.subgoals.ExtractNextGoal(agent.currentGoal,state);
                    if (agent.nextGoal == null) agent.nextGoal = agent.currentGoal;
                    agent.blank = false;
                }

                Collections.sort(agentsInOrder,new Agent.CustomComparator());


                for (Agent agent : agentsInOrder) {
                    //System.err.println(agent + " "+agent.isInGoal());

                    if (!agent.isInGoal()) {
                        if (state.agentConflicts.size() > 0) {

                            state.blankPlan.addAll(agent.mainPlan.plan);
                            agent.blank = true;
                            LinkedHashSet visited = new LinkedHashSet(state.occupiedNodes.keySet());
                            visited.remove(agent.position.NodeId);
                            agent.planPi(state, visited, false);
                            break;

                        } else {
                                LinkedHashSet visited = new LinkedHashSet(state.occupiedNodes.keySet());
                                visited.remove(agent.position.NodeId);
                                agent.planPi(state, visited,false);
                                //break;



                        }
                    }
                }

            }
        }

    }




}
