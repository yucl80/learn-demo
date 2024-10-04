package test;



import java.util.ArrayList;

public class DFGEntry {
    public DFGEntry(String code, int[] point, String comesFrom, ArrayList<int[]> integers) {
        this.code = code;
        this.point = point;
        this.comesFrom = comesFrom;
        this.integers = integers;
    }
    public String code;
    public int[] point;
    public String comesFrom;
    public ArrayList<int[]> integers;

    public String toString()
    {
        return code + " " + point[0] + " " + point[1] + " " + comesFrom + " " + integers.toString();
    }

}
