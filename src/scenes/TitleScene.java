package scenes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import GameLib.GameCanvas;
import GameLib.GameManager;

public class TitleScene extends GameCanvas{

	int cnt;
	int bg1Y, bg2Y;// 배경화면 위치 20131211
	boolean isPopup;//안내 팝업 처리 20131211
	String popupMessage;//팝업 메시지 20131211
	Timer _timer;//타이머 20131211
	int nextPowGold;//다음 레벨업 골드 20131211
	int viewGold;//보여주는 골드 20131211
	int minusGold;//골드 차감시 간격 20131211
	
	public TitleScene(GameManager manager){
		
		super(manager);

		manager.nowCanvas = (GameCanvas)this;
		
		manager.LoadGame("wf.dat");

		//20131211
		nextPowGold = (manager.powlevel/4)*500+(manager.powlevel+1)*100;
		if(manager.powlevel==29)
			nextPowGold = 0;
		
		_timer = new Timer();
		
		viewGold = manager.gold;
		//20131211

		addKeyListener(this);//20211111
	}
	
	@Override
	public void dblpaint(Graphics gContext) {
		// TODO Auto-generated method stub
		
		//20131211 전체적으로 신규 작성
		
		gContext.drawImage(bg, 0, bg1Y, this);
		gContext.drawImage(bg2, 0, bg2Y, this);
		
		if(cnt<60)
			gContext.drawImage(chrpic, 30, GameManager.SCREEN_HEIGHT+200 - cnt*14, this);
		else
			gContext.drawImage(chrpic, 30, 182 + (cnt%60<30?-(cnt%30):(cnt%30)-30)/3, this);
		
		if(80<=cnt&& cnt<130)
			gContext.drawImage(logo, -600 + (cnt-80)*12, 100, this);
		else if(cnt>=130)
			gContext.drawImage(logo, 30, 100, this);
		
		gContext.drawImage(highscore, 75, 516, this);
		gContext.drawImage(mygold, 75, 607, this);
		
		manager.drawnum(gContext, numpic, 383,507, manager.highscore, 8, GameManager.ANC_RIGHT);
		manager.drawnum(gContext, numpic, 383,600, viewGold, 6, GameManager.ANC_RIGHT);
		
		if(cnt%30<15)
			gContext.drawImage(pushany, (GameManager.SCREEN_WIDTH-pushany.getWidth(this)) / 2, 465, this);
		
		if(cnt>130){
			gContext.drawImage(buypower, 19, 674, this);
		
			manager.drawnum(gContext, numpic, 142,693, manager.powlevel+1, 2, GameManager.ANC_LEFT);
			manager.drawnum(gContext, numpic, 218,724, (manager.powlevel+2)>30?30:manager.powlevel+2, 2, GameManager.ANC_LEFT);
			manager.drawnum(gContext, numpic, 211,761, nextPowGold, 0, GameManager.ANC_RIGHT);
		}
		
		drawPopup(gContext);//20131211
		
		gContext.drawImage(allgrd, GameManager.SCREEN_WIDTH-allgrd.getWidth(null)-8, 34, this);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		cnt ++;

		bg1Y += 1;
		bg2Y += 1;

		if (bg1Y >= GameManager.SCREEN_HEIGHT)
			bg1Y = -GameManager.SCREEN_HEIGHT + bg1Y % GameManager.SCREEN_HEIGHT;//화면을 벗어난 배경그림 1의 위치를 되돌린다
		if (bg2Y >= GameManager.SCREEN_HEIGHT)
			bg2Y = -GameManager.SCREEN_HEIGHT + bg2Y % GameManager.SCREEN_HEIGHT;//화면을 벗어난 배경그림 2의 위치를 되돌린다
		
		if(viewGold>manager.gold){
			viewGold-=minusGold;
			if(viewGold<manager.gold)
				viewGold = manager.gold;
		}else
			if(viewGold<manager.gold){
				viewGold+=minusGold;
				if(viewGold>manager.gold)
					viewGold = manager.gold;
			}
		
	}

	@Override
	public void Destroy() {
		// TODO Auto-generated method stub
		super.Destroy();
		manager.remove(this);//GameManager의 firstScene에서 이 씬(클래스)을 add했으므로, remove하여 제거한다.
		releaseImage();
	}

	Image bg;//타이틀 바탕
	
	Image bg2;//타이틀 바탕 스크롤용 20131211
	Image logo;//게임 로고 20131211
	Image chrpic;//캐릭터 그림 20131211
	Image buypower;//총알 레벨 업 버튼 20131211
	Image allgrd;//전체이용가 20131211
	
	Image highscore;//최고점수
	Image mygold;//소지중 골드
	Image pushany;//아무키나 누르세요
	Image numpic;//숫자그림
	@Override
	public void initImage() {
		// TODO Auto-generated method stub
		bg = manager.makeImage("rsc/game/ground.png");
		bg2 = manager.makeImage("rsc/game/ground.png");//20131211
		
		logo = manager.makeImage("rsc/title/logo.png");//20131211
		chrpic = manager.makeImage("rsc/title/lyne_title.png");//20131211
		buypower = manager.makeImage("rsc/title/powerup.png");//20131211
		allgrd = manager.makeImage("rsc/title/all_grd.png");//20131211
		
		highscore = manager.makeImage("rsc/title/high.png");
		mygold = manager.makeImage("rsc/title/gold.png");
		pushany = manager.makeImage("rsc/title/pushany.png");
		numpic = manager.makeImage("rsc/numpic.png");
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("씬에서 키 릴리즈Typed");
		super.keyTyped(e);
	}

	@Override
	public void releaseImage() {
		// TODO Auto-generated method stub
		bg = null;
		bg2 = null;//20131211
		
		logo = null;//20131211
		chrpic = null;//20131211
		buypower = null;//20131211
		allgrd = null;//20131211
		
		highscore = null;
		mygold = null;
		pushany = null;
		numpic = null;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		System.out.println("씬에서 키 릴리즈");
		//20131211
		if(isPopup)
			return;
		//20131211

		if(e.getKeyCode()==KeyEvent.VK_1){
			System.out.println(this.hasFocus());
			return;
		}
		
		if(e.getKeyCode()==KeyEvent.VK_P && cnt>130 && viewGold==manager.gold){
			buyPower();
			return;
		}
		
		Destroy();
		manager.sceneChange((GameCanvas)new GameScene(manager));
	}

	@Override
	public void SceneStart() {
		// TODO Auto-generated method stub
		
		cnt = 0;

		// 배경용 좌표 (계산하기 편하게)
		bg1Y = 0;
		bg2Y = -800;// 배경화면 위치
		
		super.SceneStart();
	}

	//20131211
	void popupOn(String message){
		
		isPopup = true;
		popupMessage = message;
		_timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				popupOff();
			}
		}, 3000);
	}
	
	void popupOff(){
		
		isPopup = false;
		_timer.cancel();
	}
	
	void drawPopup(Graphics gContext){
		
		if(!isPopup)
			return;
		
		gContext.setColor(new Color(0,0,0));
		gContext.fillRect(0,370, 480,60);
		gContext.setColor(new Color(255,255,255));
		FontMetrics fm = gContext.getFontMetrics();
		gContext.drawString(popupMessage,
				(GameManager.SCREEN_WIDTH-fm.stringWidth(popupMessage))/2,
				GameManager.SCREEN_HEIGHT/2);

		gContext.setColor(new Color(255,255,255));
		gContext.drawLine(0, 372, 480, 372);
		gContext.drawLine(0, 428, 480, 428);
	}

	
//	@Override
//	public void mouseClicked(MouseEvent e) {
//		// TODO Auto-generated method stub
//		if(isPopup)
//			return;
//	
//		//19,674,101,755
//		int _x = e.getX();
//		int _y = e.getY();
//		if(19<=_x&&_x<=101 && 674<=_y&&_y<=755 && cnt>130 && viewGold==manager.gold){
//			
//			buyPower();
//			return;
//		}
//		
//		super.mouseClicked(e);
//	}
	//20131211

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("mouse clicked");
		super.mouseClicked(e);
	}

	void buyPower(){
		
		if(manager.gold<nextPowGold){
			popupOn("골드가 부족합니다.");
			return;
		}
		
		if(manager.powlevel==29){
			popupOn("현재 최강 상태입니다.");
			return;
		}

		manager.powlevel++;
		manager.gold-=nextPowGold;
		minusGold = Math.abs(manager.gold-viewGold)/20;
		if(minusGold<1)
			minusGold = 1;
		manager.SaveGame(GameManager.fname);
		nextPowGold = (manager.powlevel/4)*500+(manager.powlevel+1)*100;
		if(manager.powlevel==29)
			nextPowGold = 0;
		
		return;
	}
}
