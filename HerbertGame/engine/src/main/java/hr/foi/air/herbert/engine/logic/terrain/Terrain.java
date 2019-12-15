package hr.foi.air.herbert.engine.logic.terrain;

import hr.foi.air.herbert.engine.logic.herbert.Herbert;
import hr.foi.air.herbert.engine.logic.herbert.HerbertOrientation;

/**
 * Created by filkamilip on 20.04.17..
 */

public class Terrain {
    private int score;
    private TerrainMark[][] terrainMarks;
    private int size;
    private Herbert herbert;

    public TerrainMark[][] getTerrainMarks(){
        return terrainMarks;
    }

    public Terrain(int size)
    {
        score = 0;
        this.size = size;
        terrainMarks = new TerrainMark[size][size];
        setHerbert(new Herbert(this, HerbertOrientation.Up));
        InitializeEmpty();
    }

    public int getSize()
    {
        return size;
    }

    public TerrainMark getMark(int X, int Y)
    {
        X = Math.min(X, size);
        Y = Math.min(Y, size);

        return terrainMarks[X][Y];
    }

    public void setMark(int X, int Y, int mark)
    {
        X = Math.min(X, size);
        Y = Math.min(Y, size);

        terrainMarks[X][Y].setMark(mark);
    }

    private void InitializeEmpty() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                terrainMarks[i][j] = new TerrainMark(TerrainMark.Prazno);
            }
        }
    }

    public void CopyFromTerrain(Terrain existingTerrain)
    {
        int size = this.getSize() > existingTerrain.getSize() ? existingTerrain.getSize() : this.getSize();
        setScore(existingTerrain.score);
        for (int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                terrainMarks[i][j].setMark(existingTerrain.terrainMarks[i][j].getMark());
    }

    public Herbert getHerbert()
    {
        return herbert;
    }

    public void setHerbert(Herbert herbert)
    {
        this.herbert = herbert;
    }

    public int getHerbertX()
    {
        for (int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                if ((terrainMarks[i][j].getMark() & TerrainMark.Herbert) == TerrainMark.Herbert)
                    return i;
        return -1;
    }

    public int getHerbertY()
    {
        for (int i = 0; i < size; i++)
            for(int j = 0; j < size; j++)
                if ((terrainMarks[i][j].getMark() & TerrainMark.Herbert) == TerrainMark.Herbert)
                    return j;
        return -1;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public void incScore(){
        this.score+=100;
    }
    public void decScore(){
        this.score-=1;
    }
}
