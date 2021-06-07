package sokoban;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Wall extends GameObject	{
	public Wall(int x, int y) {
		super(x, y);

		initWall();
	}
	
	private void initWall() {

        ImageIcon i = new ImageIcon("d:/workspace/GameProject/wall.png");
        Image image = i.getImage();
        setImage(image);
    }

}
