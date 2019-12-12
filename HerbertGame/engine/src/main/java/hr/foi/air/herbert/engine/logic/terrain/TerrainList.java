package hr.foi.air.herbert.engine.logic.terrain;

import android.util.Log;

import java.util.ArrayList;

import hr.foi.air.herbert.engine.logic.PlayHerbert;

/**
 * Created by filkamilip on 20.04.17..
 */

public class TerrainList extends ArrayList<Terrain> {

    private static TerrainList instance;
    private TerrainList() {
    }

    /**
     * Returns static single instance of the class.
     * @return
     */
    public static TerrainList getInstance()
    {
        if (instance == null)
            instance = new TerrainList();
        return instance;
    }

    /**
     * Adds new object to the list.
     * @param terrain Object to be added to the list.
     */
    @Override
    public boolean add(Terrain terrain)
    {
        return super.add(terrain);
    }

    /**
     * Returns Terrain object at given index.
     * @param index Zero-based location.
     * @return Terrain object at given index.
     */
    public Terrain getTerrain(int index)
    {
        return this.get(index);
    }

    public void playHerbertStepByStep(PlayHerbert playHerbert, String codeString){
        Log.d("TerrainList", "Moram reproducirat sve korake herberta");
        //TODO: Dodati odgodu crtanja
        for (Terrain terrain : instance) {
            Log.d("TerrainList", "PlayHerbert! Reproduciraj korak!");
            playHerbert.playHerbertStep(terrain);
        }
    }
    /**
     * Returns number of items in the list collection.
     * @return
     */
    public int getSize()
    {
        return this.size();
    }
}