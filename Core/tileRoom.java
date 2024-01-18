package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.*;

public class tileRoom implements Serializable {
    tileRoom[] connectedRoom = new tileRoom[4];//left, right, up, down;

    TETile[][] contents;
    TETile WALL = Tileset.WALL;
    TETile FLOOR = Tileset.GRASS;
    TETile DOOR = Tileset.UNLOCKED_DOOR;

    int width, height, selfCorridor, corrLength;

    public int locX, locY;

    public tileRoom(){
        this(7,7);
    }

    public tileRoom(int width, int height){
        contents = new TETile[width][height];
        drawRectangleRoom(0,0,width,height);
        this.width = width;
        this.height = height;
        selfCorridor = -1;
    }

    public tileRoom(int width, int height, int corrLength, int selfCorridor){
        this.corrLength = corrLength;
        this.selfCorridor = selfCorridor;
        if (selfCorridor % 2 == 0){
            contents = new TETile[width][height];
            drawRectangleRoom(corrLength,0,width-corrLength,height);
            drawCorridor(corrLength,height/2);
            this.width = width;
            this.height = height;
        } else {
            contents = new TETile[height][width];
            drawRectangleRoom(corrLength,0,height-corrLength,width);
            drawCorridor(corrLength,width/2);
            this.width = height;
            this.height = width;
        }
        for (int i = 0; i < selfCorridor; i ++){
            rotateRoom();
        }
        contents[this.corrPoint()[0]][this.corrPoint()[1]] = DOOR;

    }

    // loc here is the opening door of a room on the right
    private void drawCorridor(int locX, int locY){
        for (int i = locX; i >= 0; i--){
            contents[i][locY+1] = WALL;
            contents[i][locY] = FLOOR;
            contents[i][locY-1] = WALL;
        }
    }

    private void drawRectangleRoom(int locX, int locY, int width, int height){
        assert width >= 5 && height >= 5;
        // construct WALL
        for (int i = 0; i < width; i++){
            contents[locX+i][locY] = WALL;
            contents[locX+i][locY+height-1] = WALL;
        }
        for (int i = 0; i < height; i++){
            contents[locX][locY+i] = WALL;
            contents[locX+width-1][locY+i] = WALL;
        }

        //construct FLOOR
        for (int i = 1; i < width-1;i++){
            for (int j = 1; j < height-1; j++){
                contents[locX+i][locY+j] = FLOOR;
            }
        }

    }


    private tileRoom getParentRoom(){
        return connectedRoom[selfCorridor];
    }

    public int[] corrPoint(){
        switch (selfCorridor){
            case 0:
                return new int[]{0, height/2};
            case 1:
                return new int[]{width/2, height-1};
            case 2:
                return new int[]{width-1, height/2};
            case 3:
                return new int[]{width/2, 0};
        }
        return null;
    }


    public int[] accessPoint(int orientation){
        if (connectedRoom[orientation] != null || orientation == selfCorridor){
            System.out.println("No AP at here at Ori: " + orientation + " The room's corr is " + selfCorridor);
            return null;
        }
        if (selfCorridor == -1){
            switch (orientation){
                case 0:
                    return new int[]{0, height/2};
                case 1:
                    return new int[]{width/2, height-1};
                case 2:
                    return new int[]{width-1, height/2};
                case 3:
                    return new int[]{width/2, 0};
            }
        }
        if (selfCorridor == 0){
            switch (orientation){
                case 1:
                    return new int[]{width/2 + corrLength/2, height-1};
                case 2:
                    return new int[]{width-1, height/2};
                case 3:
                    return new int[]{corrLength/2 +width/2, 0};
            }
        }
        if (selfCorridor == 1){
            switch (orientation){
                case 0:
                    return new int[]{0, (height-corrLength)/2};
                case 2:
                    return new int[]{width-1, (height-corrLength)/2};
                case 3:
                    return new int[]{width/2, 0};
            }
        }
        if (selfCorridor == 2){
            switch (orientation){
                case 0:
                    return new int[]{0, height/2};
                case 1:
                    return new int[]{(width-corrLength)/2, height-1};
                case 3:
                    return new int[]{(width-corrLength)/2, 0};
            }
        }
        if (selfCorridor == 3){
            switch (orientation){
                case 0:
                    return new int[]{0, height/2+corrLength/2};
                case 1:
                    return new int[]{width/2, height-1};
                case 2:
                    return new int[]{width-1, height/2+corrLength/2};
            }
        }
        return null;
    }

    public void addAnotherRoom(tileRoom newRoom,List<tileRoom> roomSet){
        int orientation = newRoom.neededOrientation();
        int[] AP = this.accessPoint(orientation);
        contents[AP[0]][AP[1]] = newRoom.DOOR;
        AP[0] = AP[0] + this.locX;
        AP[1] = AP[1] + this.locY;
        newRoom.locX = AP[0] - newRoom.corrPoint()[0];
        newRoom.locY = AP[1] - newRoom.corrPoint()[1];
        connectedRoom[orientation] = newRoom;
        roomSet.add(newRoom);
    }

    public boolean isNewRoomOkay(tileRoom newRoom, TETile[][] world){
        int orientation = newRoom.neededOrientation();
        int[] AP = this.accessPoint(orientation);
        AP[0] = AP[0] + this.locX;
        AP[1] = AP[1] + this.locY;
        int locX = AP[0] - newRoom.corrPoint()[0];
        int locY = AP[1] - newRoom.corrPoint()[1];
        for (int i = locX;i < locX +newRoom.width;i ++){
            for (int j = locY; j < locY + newRoom.height;j ++){
                if (i >= Engine.WIDTH || j >= Engine.HEIGHT || (world[i][j] != null && i != locX && i !=  locX +newRoom.width && j != locY && j != locY + newRoom.height)){
                    return false;
                }
            }
        }
        return true;
    }


    public void rotateRoom() {
        TETile[][] res = new TETile[height][width];
        for (int i = 0; i < width; ++i){
            for (int j = 0; j < height; ++j){
                res[j][width-1-i] = contents[i][j];
            }
        }
        int temp1= width;
        width = height; height = temp1;
        contents = res;
    }

    public List<Integer> availableAP(){
        List<Integer> res = new LinkedList<>();
        for (int i = 0; i < 4; i++){
            if (connectedRoom[i] == null && i != selfCorridor){
                res.add(i);
            }
        }
        return res;
    }

    public int neededOrientation(){
        switch (selfCorridor) {
            case 0:
                return 2;
            case 1:
                return 3;
            case 2:
                return 0;
            case 3:
                return 1;
            default:
                return -1;
        }
    }

    public int neededCorridor(int ori){
        switch (ori) {
            case 2:
                return 0;
            case 3:
                return 1;
            case 0:
                return 2;
            case 1:
                return 3;
            default:
                return -1;
        }
    }

    @Override
    public String toString(){
        return TETile.toString(this.contents);
    }

}