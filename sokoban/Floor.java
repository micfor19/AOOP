package sokoban;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Floor extends GameObject {
	
	public Floor(int x, int y) {
		super(x, y);

		initFloor();
	}
	
	private void initFloor() {

        ImageIcon i = new ImageIcon("d:/workspace/GameProject/blank.png");
        Image image = i.getImage();
        setImage(image);
    }

}
