package hr.foi.air.herbert.engine.logic.terrain;

/**
 * Created by vinka.prpic on 14.6.2017..
 */

public class TerrainMark {
    public static final int Prazno = 1;
    public static final int Hrana = 2;
    public static final int Zid = 4;
    public static final int Otrov = 8;
    public static final int Up = 16;
    public static final int Right = 32;
    public static final int Down = 64;
    public static final int Left = 128;
    public static final int Herbert = 256;

    private int mark;

    public TerrainMark(int mark) {
        this.mark = mark;
    }

    public TerrainMark()
    {
        this.mark = Prazno;
    }

    public int getMark(){
        return mark;
    }
    public void setMark(int mark) { this.mark = mark; }
}
