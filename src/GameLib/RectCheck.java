package GameLib;

import java.awt.Rectangle;

public class RectCheck {

	static public boolean check(int x1, int y1, Rectangle rect1, int x2, int y2, Rectangle rect2){
		
		boolean ret = false;

		if(rect1==null)
			return false;
		
		if(rect2==null)
			return false;

		Rectangle _rect1 = new Rectangle(x1+rect1.x, y1+rect1.y, rect1.width, rect1.height);
		Rectangle _rect2 = new Rectangle(x2+rect2.x, y2+rect2.y, rect2.width, rect2.height);
		
		if(
		_rect1.x < (_rect2.x+_rect2.width) &&
		_rect2.x < (_rect1.x+_rect1.width) &&
		_rect1.y < (_rect2.y+_rect2.height) &&
		_rect2.y < (_rect1.y+_rect1.height)
				)
			ret = true;
		
		return ret;
	}
}
