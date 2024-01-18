package byow.InputDemo;

/**
 * Created by hug.
 */
import byow.Core.Engine;
import edu.princeton.cs.introcs.StdDraw;

public class KeyboardInputSource implements InputSource {

    public Engine e = null;
    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
            if (e.protagonist!=null){
                e.renderHUD();
            }

            StdDraw.pause(5);
        }
    }



    public boolean possibleNextInput() {
        return true;
    }
}
