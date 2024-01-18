package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class avatar implements Serializable {
    public int[] loc;
    boolean controllable;
    TETile representation = Tileset.AVATAR0;

    Set<TETile> impassable = new HashSet<>();


    public avatar(TETile representation){
        loc = new int[2];
        loc[0] = Engine.WIDTH/2;
        loc[1] = Engine.HEIGHT/2;
        controllable = true;
        this.representation = representation;
        impassable.add(Tileset.WALL);
    }

    public void drawOnWorld(TETile[][] world){
        world[loc[0]][loc[1]] = representation;
    }

    public void moveInWorld(char c,TETile[][] world){
        switch (c) {
            case 'W':
                moveTo(loc[0], loc[1] + 1, world);
                break;
            case 'A':
                moveTo(loc[0] - 1, loc[1], world);
                break;
            case 'S':
                moveTo(loc[0], loc[1] - 1, world);
                break;
            case 'D':
                moveTo(loc[0] + 1, loc[1], world);
                break;
            default:
                System.out.println("Unsupported Movement");
                break;
        }
    }

    private void moveTo(int locX,int locY,TETile[][] world){
        if (locX >= Engine.WIDTH || locY >= Engine.HEIGHT || impassable.contains(world[locX][locY])){
            return;
        }
        loc[0] = locX;
        loc[1] = locY;
    }


}
