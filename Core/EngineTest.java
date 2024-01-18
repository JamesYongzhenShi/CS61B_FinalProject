package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class EngineTest {

    @Test
    public void interactWithKeyboard() {
    }

    @Test
    public void interactWithInputString() {

        String[] tests = {"n7685817615627686380s","n865562189400100566s", "n8272166368955537510s", "n8554565356223690293s"};
        Engine e = new Engine();
        TETile[][] temp = e.interactWithInputString("n7685817615627686380s");
        for (String s : tests) {
            e.interactWithInputString(s);
            for (int i = 0; i < temp.length; i++) {
                TETile[] tiles = temp[i];
                for (int j = 0; j < tiles.length; j++) {
                }
            }
        }
    }

    public static void renderHUD(TETile[][] world) {
        int level =1;
        int flowers = 1;
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        String tileDescription = "";
        if (x < Engine.WIDTH && y < Engine.HEIGHT) {
            tileDescription = "You see: " + world[x][y].description();
        }
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.setFont(new Font("Monoco", Font.BOLD, 40));
        StdDraw.text(0.3, 0.3, "LEVEL  " + level);
        StdDraw.setFont(new Font("Monoco", Font.PLAIN, 40));
        StdDraw.textLeft(7.1, 1.5, "wild flowers: " + flowers + " " + "/ 3");
        StdDraw.textLeft(1.5, 28.5, tileDescription);
        StdDraw.textRight(Engine.WIDTH - 2, 28.5, "Controls (C)");

        StdDraw.text((float) Engine.WIDTH / 2, 28.5,
                    "Mouse over to read object's description.");
    }


    public static void main1(String[] args) {
        String[] tests = {"n7685817615627686380s","n865562189400100566s", "n8272166368955537510s", "n8554565356223690293s"};
        Engine e = new Engine();
        TETile[][] temp = e.interactWithInputString("n8272166368955537510s");
        renderHUD(temp);
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        Engine e = new Engine();
        ter.initialize(Engine.WIDTH,Engine.HEIGHT);
        TETile[][] temp = e.interactWithInputString("n205990625849168503sssdawssddaadsaa");
        ter.renderFrame(temp,e);

        while (true){
            if (StdDraw.hasNextKeyTyped()) break;
        }


        ter = new TERenderer();
        e = new Engine();
        ter.initialize(Engine.WIDTH,Engine.HEIGHT);
        temp = e.interactWithInputString("n205990625849168503sssdawssddaadsaa");
        ter.renderFrame(temp,e);
    }

}