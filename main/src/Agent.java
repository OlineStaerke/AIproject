public class Agent extends Object {
    Plan mainPlain;
    AlternativePlan altPlans;
    Node[] privateZone;
    Node initialState;
    //Hashset of string for positions overtaken, each agent would add these positions he finds
    // himself in. This hashset would be public
    public Agent(Node node){
        this.initialState = node;
    }

    public void planAltPaths() {}

    public void planPi() {}

    public void ExecuteMove() {}

    public void bringBlack() {}

}
