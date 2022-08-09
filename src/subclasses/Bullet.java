package subclasses;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Vector;

import GameLib.RectCheck;

public class Bullet {

	public static final int NO_PROCESS = -1;
	public static final int MOVEOUT = -2;
	// 이 게임에서는 플레이어의 총알 밖에 존재하지 않기 때문에..

	Image pic;//그림
	Rectangle rect;//총알의 충돌체크 대상이 되는 사각형
	int x, y;//총알을 표시해 줄 좌표
	public int pow;//총알의 위력
	
	public Bullet(Image pic, Rectangle rect, int x, int y, int pow){
		this.pic = pic;
		this.rect = rect;
		this.x = x;
		this.y = y;
		this.pow = pow;
	}
	
	public void draw(Graphics gc, ImageObserver _ob){
		
		gc.drawImage(pic, x, y, _ob);
	}
	
	public int process(Vector enemies){

		int ret = NO_PROCESS;
		
		//적 캐릭터와의 명중을 판단한다
		Enemy _buff;
		for(int i=0;i<enemies.size(); i++){
			_buff = (Enemy)enemies.elementAt(i);
			
			//20211111
			if(_buff.kind==4)
				continue;
			//20211111
			
			if(RectCheck.check(x, y, rect, _buff.x, _buff.y, _buff.rect)){
				ret = i;
				break;
			}
		}
		
		//이동 처리한다
		y-=16;
		if(y<=-10)
			ret = MOVEOUT;
		
		return ret;
	}
	
	public int getX(){
		
		return x;
	}
	
	public int getY(){
		
		return y;
	}

//	public void process_another(GameScene _scene){
//
//		//이동 처리한다
//		y-=16;
//		if(y<=-10)
//			_scene.deleteBullet(this);
//		
//	}
}
