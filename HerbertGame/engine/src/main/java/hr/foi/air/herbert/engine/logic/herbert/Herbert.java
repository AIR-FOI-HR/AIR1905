
package hr.foi.air.herbert.engine.logic.herbert;

import hr.foi.air.herbert.engine.logic.terrain.Terrain;

public class Herbert {
    private HerbertOrientation orientation;
    private Terrain terrain;

    public Herbert(Terrain terrain, HerbertOrientation orientation) {
        this.terrain = terrain;
        this.orientation = orientation;
    }

    public int getX() {
        return terrain.getHerbertX();
    }

    public int getY() {
        return terrain.getHerbertY();
    }

    public HerbertOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(HerbertOrientation orientation) {
        this.orientation = orientation;
    }
}