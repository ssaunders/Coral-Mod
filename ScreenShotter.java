package coral;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScreenShotter {
	private static Robot robot;
	
	public ScreenShotter() {
	}
	
	public static Robot getRobot() {
		if(robot == null) {			
			try {
				robot = new Robot();
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
		return robot;
	}
	
	public static void takeScreenShot() {
		TestConfig testCfg = BlockControlBlock.getCurrentTest();
		takeScreenShot(testCfg);
	}
	
	public static void takeScreenShot(TestConfig testCfg) {
		if(testCfg == null) {
			System.err.println("!!!! Could not get take screenshot.");
			return;
		}
		
		BufferedImage screenShot = getRobot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		String picName = String.format("%d_%07.2f", testCfg.getUniqueId(), testCfg.getTimeElapsed());
		File f = new File(BlockControlBlock.getCurrentPath(true)+"Screenshots\\"+picName+".png");
		try {
			ImageIO.write(screenShot, "PNG", f);
		} catch (IOException e) {
			System.out.println("!!!! Screen size: "+Toolkit.getDefaultToolkit().getScreenSize()+" file "+f);
			e.printStackTrace();
		}
	}

}
