package hr.foi.air.herbert.engine.logic.terrain;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import hr.foi.air.herbert.engine.logic.GameController;
import hr.foi.air.herbert.engine.logic.herbert.HerbertOrientation;
import hr.foi.air.herbert.engine.common.events.OnGameControllerListener;
import hr.foi.air.herbert.engine.parser.InputCode;
import hr.foi.air.herbert.engine.parser.Lex;

/**
 * Created by filkamilip on 20.04.17..
 */

public class TerrainLogic {
    /*
    Herbert values:
     */
    private int X;
    private int Y;
    private HerbertOrientation orientation;

    private String levelName;
    private int terrainSize;
    private TerrainList terrainList = TerrainList.getInstance();
    private GameController gameController;
    private String[] splittedRow;
    private Terrain terrainCurrentLevel;

    public int getX() {
        return X;
    }
    public void setX(int x) {
        X = x;
    }
    public int getY() {
        return Y;
    }
    public void setY(int y) {
        Y = y;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public void setTerrainSize(int terrainSize) {
        this.terrainSize = terrainSize;
    }

    public void emptyTerrainList(){
        terrainList.clear();
    }

    private static TerrainLogic instance;
    OnGameControllerListener listener;

    /**
     * Constructor
     * @param listener
     */
    public TerrainLogic(OnGameControllerListener listener) {
        X=terrainSize/2;
        Y=terrainSize/2;
        this.orientation=HerbertOrientation.Up;

        this.listener = listener;

        gameController = new GameController(listener);
    }

    //Singleton. Pozvan iz PlayActivity
    public static TerrainLogic getInstance(OnGameControllerListener listener)
    {
        if (instance == null)
            instance = new TerrainLogic(listener);
        return instance;
    }

    public int getTerrainSize(){
        return terrainSize;
    }

    public void setOrientation(HerbertOrientation orientation) {
        this.orientation = orientation;
    }
    public HerbertOrientation getOrientation() {
        return orientation;
    }

    public String parseUserCode(String userCode){
        emptyTerrainList();
        gameController.resetFoodLeft();

        String parseResult = "";
        try {
            parseResult = new InputCode().getMoves(userCode);
            if (!parseCodeToTerrains(parseResult)) {
                //All OK - ParseResult contains only s l r characters.
                parseResult = "";
            }
        }
        catch (Lex.HerbertException e) {
            listener.OnCodeWithError(e.getDescription());
        }
        catch (Exception e) {
            listener.OnCodeWithError("Greska u kodu!");
        }
        finally {
            return parseResult;
        }
    }

    public boolean parseCodeToTerrains(String parsedCode)
    {
        boolean ok = true;
        for (char s: parsedCode.toCharArray()) {
            if (!parseSingleMove(s)) {
                ok = false;
                break;
            }
        }

        return ok;
    }

    private boolean parseSingleMove(char s)
    {
        //new empty terrain initialized with empty blocks and herbert on it.


        boolean ok = true;

        //initializing with previous state or loading new state from file
        if(terrainList.isEmpty()) {
            //ovo se odvija samo na početku kada treba naći lokaciju Herberta
            Terrain t = new Terrain(terrainSize);
            t.CopyFromTerrain(terrainCurrentLevel);
            setX(terrainCurrentLevel.getHerbert().getX());
            setY(terrainCurrentLevel.getHerbert().getY());
            setOrientation(HerbertOrientation.Up);

            terrainList.add(t);
        }

        Terrain t = new Terrain(terrainSize);
        t.CopyFromTerrain(terrainList.getTerrain(terrainList.getSize() - 1));

        switch (s) {
            case 's':
                move(terrainSize, t.getMark(getX(), getY()), t);
                break;
            case 'l':
                rotateLeft();
                break;
            case 'r':
                rotateRight();
                break;
        }

        terrainList.add(t);
        return ok;
    }

    /*
    Following methods are used for moving and rotating Herbert
     */
    public void move(int terrainSize, TerrainMark mark, Terrain t){
        t.setMark(X, Y, mark.getMark()-255);
        boolean zid = false;

        switch(orientation){
            case Up:
                /*
                First if always checks if is it on the edge of terrain or if it is going
                eat wall.

                Second if checks wall collision.

                Third if statement checks if it is going to eat food or not and in that case
                it increases score and decreases the number of food left on the screen

                TODO: Add overalll food left number decrease when food is eaten
                 */
                if (X==0)
                    break;
                else if ((t.getMark(X - 1, Y).getMark() & TerrainMark.Zid) == TerrainMark.Zid) {
                    zid = true;
                    break;
                }
                else if ((t.getMark(X - 1, Y).getMark() & TerrainMark.Hrana) == TerrainMark.Hrana) {
                    t.incScore();
                    gameController.foodLeftdec();
                }
                else
                    t.decScore();
                X--;
                break;

            case Right:
                if (Y==terrainSize-1)
                    break;
                else if ((t.getMark(X, Y+1).getMark() & TerrainMark.Zid) == TerrainMark.Zid) {
                    zid = true;
                    break;
                }
                else if ((t.getMark(X, Y+1).getMark() & TerrainMark.Hrana) == TerrainMark.Hrana) {
                    t.incScore();
                    gameController.foodLeftdec();
                }
                else t.decScore();
                Y++;
                break;
            case Down:
                if (X==terrainSize-1){
                    break;
                }
                else if ((t.getMark(X + 1, Y).getMark() & TerrainMark.Zid)==TerrainMark.Zid) {
                    zid=true;
                    break;
                }
                else if ((t.getMark(X + 1, Y).getMark() & TerrainMark.Hrana) == TerrainMark.Hrana) {
                    t.incScore();
                    gameController.foodLeftdec();
                }
                else
                    t.decScore();
                X++;
                break;
            case Left:
                if (Y==0)
                    break;
                else if ((t.getMark(X, Y - 1).getMark() & TerrainMark.Zid)==TerrainMark.Zid) {
                    zid = true;
                    break;
                }
                else if ((t.getMark(X, Y - 1).getMark() & TerrainMark.Hrana)==TerrainMark.Hrana) {
                    t.incScore();
                    gameController.foodLeftdec();
                }
                else
                    t.decScore();
                Y--;
                break;
        }
        /*
        Ovo treba provjeriti
         */
        t.setMark(X,Y,TerrainMark.Herbert);

        //if (zid) return;
        switch(orientation){
            case Up:
                if ((t.getMark(X,Y).getMark() & TerrainMark.Up) != TerrainMark.Up)
                    t.setMark(X, Y, t.getMark(X,Y).getMark()+TerrainMark.Up);
                break;
            case Down:
                if ((t.getMark(X,Y).getMark() & TerrainMark.Down) != TerrainMark.Down)
                    t.setMark(X, Y, t.getMark(X,Y).getMark()+TerrainMark.Down);
                break;
            case Right:
                if ((t.getMark(X,Y).getMark() & TerrainMark.Right) != TerrainMark.Right)
                    t.setMark(X, Y, t.getMark(X,Y).getMark()+TerrainMark.Right);
                break;
            case Left:
                if((t.getMark(X,Y).getMark() & TerrainMark.Left) != TerrainMark.Left)
                    t.setMark(X, Y, t.getMark(X,Y).getMark()+TerrainMark.Left);
                break;
        }
    }

    public void rotateLeft(){
        switch(orientation){
            case Up:
                setOrientation(HerbertOrientation.Left);
                break;
            case Right:
                setOrientation(HerbertOrientation.Up);
                break;
            case Down:
                setOrientation(HerbertOrientation.Right);
                break;
            case Left:
                setOrientation(HerbertOrientation.Down);
                break;
        }
    }

    public void rotateRight(){
        switch(orientation){
            case Up:
                setOrientation(HerbertOrientation.Right);
                break;
            case Right:
                setOrientation(HerbertOrientation.Down);
                break;
            case Down:
                setOrientation(HerbertOrientation.Left);
                break;
            case Left:
                setOrientation(HerbertOrientation.Up);
                break;
        }
    }

    public Terrain loadCurrentLevelTerrain(Context context) throws IOException {
        Terrain terrain = new Terrain(getTerrainSize());

        BufferedReader reader = null;
        try {
            AssetManager assetManager = context.getAssets();
            InputStream is=assetManager.open("maps/"+levelName+".txt");
            reader = new BufferedReader(new InputStreamReader(is));

            for(int i = 0; i < getTerrainSize() && reader.ready(); i++) {
                // split using tab
                splittedRow = reader.readLine().split("\t");
                for(int j = 0; j < getTerrainSize(); j++) {
                    terrain.setMark(i,j, Integer.parseInt(""+splittedRow[j]));
                }
            }

            terrainCurrentLevel = terrain;
            gameController.countFood(terrainCurrentLevel);
            return terrain;

        } catch(IOException e) {
            throw e;
        } finally {
            assert reader != null;
            reader.close();
        }
    }
}