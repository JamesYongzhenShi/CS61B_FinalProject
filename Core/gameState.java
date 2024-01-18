package byow.Core;

import byow.TileEngine.TETile;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class gameState implements Serializable {
    public TETile[][] world;
    public boolean lineOfSight;
    public List<tileRoom> roomSet;
    public Set<avatar> avatarSet;
    public long SEED;
    public avatar protagonist;


    public void updateState(Engine e){
        world = e.world;
        lineOfSight = e.lineOfSight;
        roomSet = e.roomSet;
        avatarSet = e.avatarSet;
        SEED = e.SEED;
        protagonist = e.protagonist;
    }
}
