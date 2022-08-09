package subclasses;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import GameLib.GameManager;

public class Effect {

	Image pic;//그림
	int x, y;//표시 좌표
	int totalframe;//총 프레임 수
	int step;//다음 프레임으로 넘어가기 위해 필요한 클럭
	int frame;//현재 보여주는 프레임
	int nowstep;//현재 클럭
	
	public Effect(Image pic, int x, int y, int step, int totalframe){

		this.pic = pic;
		this.x = x;
		this.y = y;
		this.step = step;
		this.totalframe = totalframe;
		
		nowstep = 0;
		frame = 0;
		
	}
	
	public void draw(GameManager manager, Graphics gContext, ImageObserver _ob){
		
		manager.drawImageRect(gContext, pic, x, y, (pic.getWidth(_ob)/totalframe)*frame, 0, pic.getWidth(_ob)/totalframe, pic.getHeight(_ob), GameManager.ANC_CENTER);
	}
	
	public boolean process(){

		nowstep++;
		if(nowstep==step){
			frame++;
			nowstep = 0;
			if(frame==totalframe)
				return false;
		}
		return true;
	}
	
}
