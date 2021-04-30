public class Tuple {
    public String agentpos;
    public String boxpos;

    public Tuple(String s1, String s2) {
        this.agentpos = s1;
        this.boxpos = s2;
    }

    @Override
    public int hashCode() {
        var foo1 = agentpos.split(" ");
        var foo2 = boxpos.split(" ");
        return Integer.parseInt(foo1[0]) + Integer.parseInt(foo1[1])
                + (Integer.parseInt(foo2[0]) + Integer.parseInt(foo2[1])) * 12345;
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Tuple)) return false;

        var objTuple = (Tuple) obj;
        return objTuple.agentpos.equals(agentpos) && objTuple.boxpos.equals(boxpos);
    }
}
