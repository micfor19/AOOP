package sokoban;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Crate extends GameObject	{
	public Crate(int x, int y) {
		super(x, y);

		initCrate();
	}
	
	private void initCrate() {

        ImageIcon i = new ImageIcon("d:/workspace/GameProject/crate.png");
        Image image = i.getImage();
        setImage(image);
    }

    public void move(int x, int y) {

        int dx = getX() + x;
        int dy = getY() + y;
        
        setX(dx);
        setY(dy);
    }

}
