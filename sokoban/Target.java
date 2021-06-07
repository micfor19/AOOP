package sokoban;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Target extends GameObject {

	public Target(int x, int y) {
		super(x, y);

		initTarget();
	}
	
	private void initTarget() {

        ImageIcon i = new ImageIcon("d:/workspace/GameProject/blankmarked.png");
        Image image = i.getImage();
        setImage(image);
    }

}
