import java.util.*;

public class MaPPAlgorithm {



    public static void MaPPVanilla(State state) throws InterruptedException {
        // Init all goals for the agents
        Plan plan = new Plan();

        for(Agent agent : state.agents.values()){
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

        //Assign goals to each box, and their plan to goal
        for (String goal : state.goals.keySet()) {
            ArrayList<Box> boxes = state.goals.get(goal);
            ArrayList<String> boxPositions = new ArrayList<>();
            for (Box box : boxes) {
                boxPositions.add(box.position.NodeId);
            }
            plan.createPlan(state,goal,boxPositions,new LinkedHashSet<>(),null);
            int i = boxPositions.indexOf(plan.plan.get(plan.plan.size()-1));
            Box boxForGoal = boxes.get(i);
            boxForGoal.setGoal(goal,state);
            Collections.reverse(Arrays.asList(plan.plan));
            boxForGoal.planToGoal = plan.plan;
        }




        // Create the initial plan for each agent
        for (Agent agent : state.agents.values()) {
            agent.subgoals.SortGoal(state);
            agent.planPi(state, new LinkedHashSet(), false);
            plan.createPlan(state, agent.position.NodeId, agent.Goal, new LinkedHashSet<>(),agent);
            agent.planToGoal = plan.plan;
        }


        for (Agent agent : state.agents.values()) {
            agent.findPriority(state);
        }

        boolean goalIsReached = false;

        ArrayList<Agent> agentsInOrder = new ArrayList<>(state.agents.values());
        Collections.sort(agentsInOrder,new Agent.CustomComparator());
        
        int round = 0;
        while(!goalIsReached){

            round+=1;

            // Loop over each agent, in order by priority
            for(Agent agent : agentsInOrder) {

                agent.subgoals.UpdateGoals(state);

                String wantedMove = agent.getWantedMoveWithBox();


                if ((agent.mainPlan.plan.size()> 0) && (state.blankPlan.size()==0||
                    agent.blank||(!state.agentConflicts.contains(agent) && !agent.position.isTunnel))) {

                    // Agent can move freely
                    if (wantedMove.equals(agent.position.NodeId) || ((agent.attached_box!=null)
                            && (wantedMove.equals(agent.attached_box.position.NodeId)))) {
                        agent.executeMove(state, false);
                    }




                    // Agent wants to move into an occupied cell
                    else if (state.occupiedNodes.containsKey(wantedMove)) {

                        // Bring Blank and move
                        var occupyingObject = state.occupiedNodes.get(wantedMove);


                        // Agent is blocking
                        if (occupyingObject instanceof Agent) {
                            var occupyingAgent = (Agent) occupyingObject;

                            //When conflict only move one at a time
                            if (state.stringToNode.get(wantedMove).isTunnel) {
                                state.agentConflicts.add(occupyingAgent);
                            } else {
                                state.agentConflicts.add(occupyingAgent);
                            }


                            //Avoid deadlocks, the conflict of the occupying agent is current agent
                            if (occupyingAgent.conflicts == agent && occupyingAgent.mainPlan.plan.size() > 0) {
                                LinkedHashSet visited = new LinkedHashSet(state.occupiedNodes.keySet());
                                visited.remove(occupyingAgent.position.NodeId);
                                occupyingAgent.planPi(state, visited, false);
                                occupyingAgent.blank = true;
                                occupyingObject.conflicts = null;
                                agent.conflicts = null;
                                agent.blank = false;
                            } else {

                                occupyingAgent.blank = true;
                                occupyingAgent.conflicts = agent;
                                agent.blank = false;
                                occupyingAgent.bringBlank(state, occupyingAgent);
                            }

                        }
                        // Box is blocking
                        else {
                            agent.stuck++;
                            //Agent is stuck, recompute path
                            if (agent.stuck == 1) {
                                LinkedHashSet visited = new LinkedHashSet<>();
                                visited.add(wantedMove);
                                agent.planPi(state, visited, true);
                            } else {

                                var occupyingBox = (Box) occupyingObject;

                                if (state.stringToNode.get(wantedMove).isTunnel) {
                                    state.agentConflicts.add(occupyingBox.currentowner);
                                }

                                Agent oldowner = null;
                                // Your own box is blocking
                                if (state.NameToColor.get(occupyingBox.ID.charAt(0)).equals(state.NameToColor.get(agent.ID.charAt(0)))) {

                                    // Reassign owners
                                    if (occupyingBox.currentowner.attached_box != occupyingBox) {
                                        if (occupyingBox.currentowner.currentGoal.Obj.equals(occupyingBox)) {
                                            oldowner = occupyingBox.currentowner;
                                            oldowner.currentGoal = null;
                                        }

                                        occupyingBox.currentowner = agent;
                                        occupyingBox.Taken = false;
                                    }


                                    if (agent.currentGoal != null && agent.currentGoal.gType == SubGoals.GoalType.BoxToGoal) {
                                        ((Box) agent.currentGoal.Obj).conflict_box = occupyingBox;
                                        occupyingBox.conflict_box = (Box) agent.currentGoal.Obj;
                                    } else {
                                        occupyingBox.conflict_box = null;
                                    }


                                    occupyingBox.currentowner.blank = true;
                                    occupyingBox.currentowner.conflicts = agent;
                                    occupyingBox.conflicts = agent;
                                    occupyingBox.conflictRoute = agent.mainPlan.plan;

                                    occupyingBox.bringBlank(state, occupyingBox.currentowner);
                                    occupyingBox.blankByOwn = true;

                                    if (oldowner != null) oldowner.planPi(state, new LinkedHashSet(), false);

                                } else {
                                    if (occupyingBox.currentowner == null) {
                                        occupyingBox.findOwner(state);
                                    }

                                    if (agent.currentGoal != null && agent.currentGoal.gType == SubGoals.GoalType.BoxToGoal) {
                                        ((Box) agent.currentGoal.Obj).conflict_box = occupyingBox;
                                        occupyingBox.conflict_box = (Box) agent.currentGoal.Obj;
                                    } else {
                                        occupyingBox.conflict_box = null;
                                    }
                                    occupyingBox.conflictRoute = new ArrayList<>();
                                    occupyingBox.currentowner.blank = true;
                                    occupyingBox.currentowner.conflicts = agent;
                                    occupyingBox.conflicts = agent;

                                    occupyingBox.bringBlank(state, occupyingBox.currentowner);
                                    occupyingBox.blankByOwn = false;


                                }

                            }
                        }
                            // Do nothing, (NoOP). So the agent waits if he cannot enter a cell, or he has tried to make someone blank.
                            agent.executeMove(state, true);
                            //Double NoOp
                            agent.mainPlan.plan.add(0,agent.position.NodeId);
                            if (agent.attached_box!=null) {
                                agent.attached_box.mainPlan.plan.add(0,agent.attached_box.position.NodeId);
                            }




                    }
                    // Empty cell
                    else if (!state.occupiedNodes.containsKey(wantedMove)) {
                        agent.stuck = 0;
                        agent.executeMove(state,  false);
                    } else {
                        agent.executeMove(state, true);
                    }
                }
                    else {
                        agent.executeMove(state, true);

                    }

                    //Update the subgoals
                    agent.subgoals.UpdateGoals(state);
                }

            state.UpdateOccupiedNodes();

            goalIsReached = true;
            Boolean noroutes = true;

            for(Agent agent : agentsInOrder) {
                for (Box b : agent.boxes) {


                    if (b.finalPlan.size()<agent.finalPlan.size()-1) {
                        b.finalPlan.add(b.position);
                        b.finalPlanString.add(b.position.NodeId);

                    }
                    if (b.finalPlan.size()>agent.finalPlan.size()-1) {
                        b.finalPlan.remove(b.finalPlan.size()-1);
                        b.finalPlanString.remove(b.finalPlan.size()-1);
                    }

                }

                  if (!agent.isInGoal()) {
                    goalIsReached = false;
                }
                if (agent.mainPlan.plan.size()>0) {
                    noroutes = false;
                }


            }

            if (round==20000) {
                goalIsReached = true;
            }


            if (noroutes) {

                for (Agent agent : agentsInOrder) {
                    agent.nextGoal = agent.subgoals.ExtractNextGoal(agent.currentGoal,state);
                    if (agent.nextGoal == null) agent.nextGoal = agent.currentGoal;
                    agent.blank = false;
                    if (agent.nextGoal!=null) agent.nextGoal.Obj.findPriority(state);
                }

                Collections.sort(agentsInOrder,new Agent.CustomComparator());

                for (Agent agent : agentsInOrder) {

                    if (!agent.isInGoal()) {
                        if (state.agentConflicts.size() > 0) {
                            agent.subgoals.UpdateGoals(state);
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

                        }
                    }
                }

            }
        }

    }





}
