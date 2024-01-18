package byow.InputDemo;

import byow.Core.Engine;

/**
 * Created by hug.
 */
public interface InputSource {

    public char getNextKey();
    public boolean possibleNextInput();
}
