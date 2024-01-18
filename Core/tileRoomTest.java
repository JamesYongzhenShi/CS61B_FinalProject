package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import org.junit.Test;

import static org.junit.Assert.*;

public class tileRoomTest {

    @Test
    public void testCentral(){
        tileRoom central = new tileRoom();
        TERenderer ter = new TERenderer();
        ter.initialize(Engine.WIDTH,Engine.HEIGHT);
        //ter.renderFrame(central.contents);
    }

    @Test
    public void testCentral2(){
        System.out.println(TETile.toString(new tileRoom().contents));
    }

    @Test
    public void testRotate(){
        tileRoom test = new tileRoom(5,7);
        System.out.println(test);
        test.rotateRoom();
        System.out.println(test);
    }

    @Test
    public void testCorridor(){
        tileRoom test = new tileRoom(7,7,2,1);
        System.out.println(test);

    }
}