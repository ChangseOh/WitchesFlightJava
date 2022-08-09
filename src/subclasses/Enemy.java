package subclasses;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

import GameLib.GameManager;
import GameLib.RectCheck;
import scenes.GameScene;

public class Enemy {

	public static final int NO_PROCESS = -1;
	public static final int MOVEOUT = -2;
	static public final int EVENT_CRASH = -3;
	
	Image pic;//그림
	Image pic2;//보조그림
	Rectangle rect;//충돌체크 대상이 되는 사각형 (총알이나 플레이어)
	int x, y;//적을 표시해 줄 좌표
	public int hp;//내구			//3-7.(2)
	int speed;//아래로 내려오는 속도
	public int kind;//적 캐릭터 종류 20131211
	int cnt;//적 캐릭터 처리 카운터 20131211
	
	public GameScene gameScene;//20211111
	int hpMax;//20211111
	
	public Enemy(Image pic, Rectangle rect, int x, int y, int hp, int speed){
		
		this.pic = pic;
		this.rect = rect;
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.speed = speed;
		
		cnt = 0;

	}

	public Enemy(Image pic, Image pic2, Rectangle rect, int x, int y, int hp, int speed, int kind){
		
		this.pic = pic;
		this.pic2 = pic2;
		this.rect = rect;
		this.x = x;
		this.y = y;
		this.hp = hp;
		this.speed = speed;
		this.kind = kind;
		
		if(kind==1)
			this.hp = 10000000;
		
		this.hpMax = this.hp;//20211111
		
		cnt = 0;

	}
	
	public void draw(GameManager manager, Graphics gContext, ImageObserver _ob){

		switch(kind){
		case 0:
			gContext.drawImage(pic, x, y, _ob);
			break;
		case 1:
			if(cnt<150 && cnt%4<2)
				gContext.drawImage(pic2, x+19, 32, _ob);//초반에는 경고만 나온다
			else
				gContext.drawImage(pic, x, y, _ob);//그리고 본체가 나온다 
			break;
		case 2://20211111
			gContext.drawImage(pic, x, y, _ob);
			
			//HP바 표시
			if(kind==2) {
				gContext.drawImage(pic2, 25, 97, _ob);
				gContext.setColor(new Color(255,0,0));
				gContext.fillRect(99, 102, 354 * hp/hpMax, 10);
			}
			break;
		case 3://20211111
			gContext.drawImage(pic, x, y, _ob);
			break;
		case 4://20211111
			if(cnt<50) {
				int sx = 88*(cnt%2);
				int sw = sx + 88;
				gContext.drawImage(pic2, x, y, x+88, y+88, sx, 0, sw, 88, _ob);//광원이 점멸한다
			}
			else {
				if(cnt%2==0)
					gContext.drawImage(pic, x, y, _ob);//빔 본체
			}
			break;
		}
	}
	
	public int process(int myX, int myY, Rectangle myRect){
		
		cnt++;
		
		//20211111
		switch(kind) {
		case 0:
			y+=speed;
			break;
		case 1:
			if(cnt>=150)//자체 카운트가 150에 도달하기 전까지는 경고만 나오므로 이동이 없다 
				y+=(speed*15/10);//1/.5배 빠르다
			else
				return NO_PROCESS;//경고만 보여지고 있는 동안에는 충돌 체크가 없다
			break;
		case 2:
			if(y<-100) {
				y+=speed;
			}
			else {
				if(cnt%130==129) {
					gameScene.BossAttack();
				}
			}
			break;
		case 3:
			y+=3;
			if(y < 600 && cnt%4==0) {
				//폭발 이펙트 호출
				gameScene.DrawEffect(x+240, y);
			}
			if(y > 700) {
				//클리어 씬 전환
				gameScene.CallClaerScene();
			}
			return NO_PROCESS;//파괴된 보스는 충돌 체크가 없다
		case 4:
			if(cnt<50)
				return NO_PROCESS;//광원이 보여지고 있는 동안에는 충돌 체크가 없다
			if(cnt>100)
				return MOVEOUT;//시간이 지나면 사라진 것으로 한다
			break;
		}
		/*
		if(kind==0)
			y+=speed;
		else if(kind==1){
			if(cnt>=150)//자체 카운트가 150에 도달하기 전까지는 경고만 나오므로 이동이 없다 
				y+=(speed*15/10);//1/.5배 빠르다
			else
				return NO_PROCESS;//경고만 보여지고 있는 동안에는 충돌 체크가 없다
		}
		else
			if(kind==2) {
				if(y<537)
					y+=8;
			}
			else
				if(kind==3) {
					
				}
		*///switch문으로 정리됨
		//20211111
		
		if(myRect==null) return NO_PROCESS;
		
		if(RectCheck.check(x, y, rect, myX, myY, myRect))
			return EVENT_CRASH;
		
		if(y >= GameManager.SCREEN_HEIGHT + 60)
			return MOVEOUT;
		
		return NO_PROCESS;
	}
	
	//3-8.
	public int getX(){
		
		return x;
	}
	
	public int getY(){
		
		return y;
	}
	//3-8.

	//20211111
	public void SetDestroy() {
		kind = 3;
		rect = null;
	}
	//20211111
}
