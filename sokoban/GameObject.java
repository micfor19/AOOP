package sokoban;

import java.awt.Image;

// a template for any sort of object in the game (worker, box, wall, target)
public class GameObject {

    private final int SPACE = 32;

    private int x;
    private int y;
    private Image image;

    public GameObject(int x, int y) {
        
        this.x = x;
        this.y = y;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image img) {
        image = img;
    }

    public int getX() {
        
        return x;
    }

    public int getY() {
        
        return y;
    }

    public void setX(int x) {
        
        this.x = x;
    }

    public void setY(int y) {
        
        this.y = y;
    }

    public boolean isLeftCollision(GameObject other) {
        
        return getX() - SPACE == other.getX() && getY() == other.getY();
    }

    public boolean isRightCollision(GameObject other) {
        
        return getX() + SPACE == other.getX() && getY() == other.getY();
    }

    public boolean isTopCollision(GameObject other) {
        
        return getY() - SPACE == other.getY() && getX() == other.getX();
    }

    public boolean isBottomCollision(GameObject other) {
        
        return getY() + SPACE == other.getY() && getX() == other.getX();
    }
}
