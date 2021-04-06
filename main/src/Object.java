public class Object implements Comparable<Object> {
    int priority;
    char color;
    Node position;
    Node Goal;

    public char getColor(){
        return color;
    }
    public void setColor(char color) {
        this.color = color;
    }
    public void setGoal(Node goal) {
        this.Goal = goal;
    }





    // Allows comparison and sorting w.r.t priority
    @Override
    public int compareTo(Object o) {
        return Integer.compare(priority, o.priority);
    }


}
