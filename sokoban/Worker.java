package sokoban;

import java.awt.Image;
import javax.swing.ImageIcon;

public class Worker extends GameObject {

	public Worker(int x, int y) {
		super(x, y);

		initWorker();
	}
	
	private void initWorker() {

        ImageIcon i = new ImageIcon("d:/workspace/GameProject/player.png");
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
