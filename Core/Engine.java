package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.List;


public class Engine implements Serializable {

    TERenderer ter;
    public static final File CWD = new File(System.getProperty("user.dir"));
    public static final File saveLocation = saveUtils.join(CWD,"SavedGame.txt");
    public static final int WIDTH = 60;
    public static final int HEIGHT = 50;
    public static final int ROOMNUM = 15;

    private final static int minRoomSide = 5;
    private final static int maxRoomSide = 13;
    private final static int radiusLineOfSight = 5;


    public List<tileRoom> roomSet = new LinkedList<>();
    public Set<avatar> avatarSet = new HashSet<>();
    public Set<Character> movementKey = new HashSet<>(Arrays.asList('W','A','S','D'));
    public Set<Character> choiceNum = new HashSet<>(Arrays.asList('0','1','2','3'));
    TETile[] protagonistChoices = {Tileset.AVATAR0,Tileset.AVATAR1,Tileset.AVATAR2,Tileset.AVATAR3};


    public long SEED;
    public Random RANDOM;
    public TETile[][] world;
    public boolean gameOver = false;
    public boolean lineOfSight = false;
    public boolean readyToQuit = false;
    public InputSource inputsource;
    public avatar protagonist;
    public int proChoice = 0;

    public gameState state;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        inputsource = new KeyboardInputSource();
        ((KeyboardInputSource)inputsource).e =this;
        ter = new TERenderer();
        ter.initialize(Engine.WIDTH,Engine.HEIGHT);
        startMenu();
        gaming();
    }

    public void startMenu(){
        while (true){
            if (inputsource.getClass() != StringInputDevice.class){
                renderStartMenu();
            }
            char c = inputsource.getNextKey();
            if (c == 'N') {
                SEED = seedParsingScreen();
                RANDOM = new Random(SEED);
                world = genRandomWorld();
                protagonist = new avatar(protagonistChoices[proChoice]);
                avatarSet.add(protagonist);
                break;
            }
            if (c == 'L') {
                System.out.println("Load");
                loadGame();
                break;
            }
            if (c == 'Q') {
                System.out.println("Quit");
                gameOver = true;
                break;
            }
            if (choiceNum.contains(c)) {
                proChoice = Integer.parseInt(""+c);
            }
        }
    }

    private void renderStartMenu(){
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monoco", Font.BOLD, 30));
        StdDraw.text(WIDTH/2, HEIGHT * (0.5 + 0.2), "CS61BL: THE GAME");
        StdDraw.setFont(new Font("Monoco", Font.BOLD, 18));
        StdDraw.text(WIDTH/2, HEIGHT * (0.5 - 0.1), "New Game(N)");
        StdDraw.text(WIDTH/2, HEIGHT * (0.5 - 0.15), "Load Game(L)");
        StdDraw.text(WIDTH/2, HEIGHT * (0.5 - 0.2), "Quit(Q)");
        StdDraw.text(WIDTH/2, HEIGHT * (0.5 - 0.25), "Use Number Key to Change Avatar");

        for (int i = 0;i < 4; i++){
            protagonistChoices[i].draw(WIDTH/2 + (i*2-4),HEIGHT * (0.5 - 0.35));
            if (i == proChoice){
                Tileset.FLOWER.draw(WIDTH/2 + (proChoice*2-4),HEIGHT * (0.5 - 0.35) + 1.1);
            } else {
                Tileset.NOTHING.draw(WIDTH/2 + (i*2-4),HEIGHT * (0.5 - 0.35) + 1.1);
            }
        }
        Font font = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font);
        StdDraw.show();
    }

    private long seedParsingScreen(){
        String seedString = "";
        char c = 'N';

        while (c != 'S'){
            if (inputsource.getClass() != StringInputDevice.class){
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.setFont(new Font("Monoco", Font.BOLD, 30));
                StdDraw.text(WIDTH/2, HEIGHT * (0.5 + 0.2), "Input your random seed:");
                StdDraw.text(WIDTH/2, HEIGHT * (0.5), seedString);
                StdDraw.show();
            }


            c = inputsource.getNextKey();
            seedString += c;
        }

        if (inputsource.getClass() != StringInputDevice.class) {
            Font font = new Font("Monaco", Font.BOLD, 14);
            StdDraw.setFont(font);
        }

        return Long.parseLong(seedString.substring(0,seedString.length()-1));
    }

    public void gaming(){
        while (!gameOver) {
            drawAllRoom(world);
            drawAllAvatar();
            fillVoid(world);
            if (inputsource.getClass() != StringInputDevice.class){
                ter.renderFrame(world,this);
                StdDraw.pause(10);
            }

            if (!inputsource.possibleNextInput()){
                break;
            }
            if (inputsource.getClass() == StringInputDevice.class || StdDraw.hasNextKeyTyped()){
                char c = inputsource.getNextKey();
                if (readyToQuit && c!= 'Q'){
                    readyToQuit = false;
                }

                if (movementKey.contains(c)){
                    protagonist.moveInWorld(c,world);
                } else if (c == 'L'){
                    lineOfSight = !lineOfSight;
                }
                else if (c == ':'){
                    readyToQuit = true;
                } else if (readyToQuit && c== 'Q'){
                    saveGame();
                    gameOver =true;
                }
            }
        }
    }

    public void saveGame(){
        if (state == null){
            state = new gameState();
        }
        state.updateState(this);
        saveUtils.writeObject(saveLocation,state);
    }

    public void loadGame(){
        // TODO
        state = saveUtils.readObject(saveLocation,gameState.class);
        world = state.world;
        lineOfSight = state.lineOfSight;
        roomSet = state.roomSet;
        avatarSet = state.avatarSet;
        SEED = state.SEED;
        protagonist = state.protagonist;
    }



    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        inputsource = new StringInputDevice(input);
        startMenu();
        gaming();
        return world;
    }


    public TETile[][] genRandomWorld(){
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        tileRoom central = new tileRoom();
        central.locX = WIDTH/2 - 3;
        central.locY = HEIGHT/2 - 3;
        roomSet.add(central);

        int tries = 0;

        for (int j = 0;j < ROOMNUM;j ++) {
            tries ++;
            if (tries > ROOMNUM*5){
                break;
            }
            // choose a room and a side to be added on
            int roomLottery = RANDOM.nextInt(roomSet.size());
            int i = 0;
            tileRoom base = central;
            for (tileRoom r : roomSet) {
                if (i == roomLottery) {
                    base = r;
                    break;
                }
                i++;
            }
            if (base.availableAP().size() == 0){
                j --;
                continue;
            }
            int sideLottery = RANDOM.nextInt(base.availableAP().size());
            int orientation = base.availableAP().get(sideLottery);
            int corridor =  base.neededCorridor(orientation);

            boolean success = tryNewRooms(base,corridor,world);

            drawAllRoom(world);
            if (!success){
                j --;
            }
        }


//        central.addAnotherRoom(newRoom,roomSet);
//        tileRoom newRoom1 = new tileRoom(7,7,2,0);
//        newRoom.addAnotherRoom(newRoom1,roomSet);
        drawAllRoom(world);
        fillVoid(world);
        return world;
    }

    // return true if new room is added
    public boolean tryNewRooms(tileRoom base,int corridor, TETile[][] world){
        // scan for collision for min room
        tileRoom minRoom = new tileRoom(minRoomSide, minRoomSide, 0,corridor);
        if (!base.isNewRoomOkay(minRoom,world)){
            return false;
        }


        // randomly try new rooms for 30 times
        int widthMax = maxRoomSide;
        int heightMax = maxRoomSide;
        boolean reduceWidth = true;
        int corrLength = 0;
        for (int i = 0; i < 30; i++){
            if (widthMax < minRoomSide || heightMax < minRoomSide){
                return false;
            }
            int width = RANDOM.nextInt(widthMax-minRoomSide) + minRoomSide;
            int height = RANDOM.nextInt(heightMax-minRoomSide) + minRoomSide;
            if (width % 2 ==0 || height % 2 == 0){
                continue;
            }
            if (corridor % 2 == 0 && width > minRoomSide){
                corrLength = RANDOM.nextInt(2);
            } else if (corridor % 2 == 1 && height > minRoomSide){
                corrLength = RANDOM.nextInt(2);
            }

            tileRoom newRoom = new tileRoom(width, height, corrLength,corridor);
            if (base.isNewRoomOkay(newRoom,world)){
                base.addAnotherRoom(newRoom, roomSet);
                return true;
            }
            if (reduceWidth){
                widthMax--;
                reduceWidth = false;
            }  else {
                heightMax--;
                reduceWidth = true;
            }


        }


        return false;
    }


    public void fillWithRandomTiles(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = randomTile();
            }
        }
    }

    private TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 2: return Tileset.WALL;
            case 0: return Tileset.FLOWER;
            case 1: return Tileset.GRASS;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.WATER;
            default: return Tileset.NOTHING;
        }
    }


    public void drawAllRoom(TETile[][] canvas){
        for (tileRoom room: roomSet){
            drawRoom(room,canvas);
        }
    }

    public void drawRoom(tileRoom room, TETile[][] canvas){
        int locX = room.locX;
        int locY = room.locY;
        for (int i = 0; i < room.width; i++){
            for (int j = 0; j < room.height; j++){

                if (room.contents[i][j] == null){
                    continue;
                }
                canvas[locX+i][locY+j] = room.contents[i][j];

            }
        }
    }

    private boolean inLineOfSight(int locX, int locY){
        int deltaX = protagonist.loc[0] - locX;
        int deltaY = protagonist.loc[1] - locY;
        return deltaX * deltaX +deltaY *deltaY < radiusLineOfSight*radiusLineOfSight;
        //return (Math.abs(deltaX) < radiusLineOfSight) && (Math.abs(deltaY) < radiusLineOfSight);
    }

    public void fillVoid(TETile[][] canvas){
        for (int i = 0; i < canvas.length; i++){
            for (int j = 0; j < canvas[0].length; j++){
                if (canvas[i][j] == null || (lineOfSight && protagonist != null && !inLineOfSight(i,j))) {
                    canvas[i][j] = Tileset.NOTHING;
                }
            }
        }
    }

    public void drawAllAvatar(){
        for (avatar a: avatarSet){
            a.drawOnWorld(world);
        }
    }

    public void renderHUD() {

        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        String tileDescription = "";
        if (x < Engine.WIDTH && y < Engine.HEIGHT) {
            tileDescription = "You see: " + world[x][y].description();
        }
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Monoco", Font.BOLD, 16));
        StdDraw.textLeft(0.1, HEIGHT-5, tileDescription);
        Font font = new Font("Monaco", Font.BOLD, 14);
        StdDraw.setFont(font);

    }


}
