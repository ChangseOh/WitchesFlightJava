package scenes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Vector;

import subclasses.Bullet;

import GameLib.GameCanvas;
import GameLib.GameManager;

public class GameScene extends GameCanvas {

	public final static int UP_PRESSED = 0x001;
	public final static int DOWN_PRESSED = 0x002;
	public final static int LEFT_PRESSED = 0x004;
	public final static int RIGHT_PRESSED = 0x008;
	public final static int FIRE_PRESSED = 0x010;

	int cnt;
	
	int bg1Y, bg2Y;// 배경화면 위치
	
	int _speed;// 배경 스크롤 속도

	int myX, myY;// 플레이어 캐릭터 위치

	int playerWidth;// 플레이어 캐릭터 그림 1프레임의 가로 폭. 연산으로도 구할 수 있지만 자주 사용되기 때문에 따로 저장해둔다
	int myFrame;// 플레이어 캐릭터 애니메이션 프레임

	int keybuff;
	int keyTime;// 키가 눌리거나 떼었을 때 얼마나 시간이 지났는가 카운팅한다
	
	boolean isPause;

	Vector bullets;// 총알 관리. 총알의 갯수를 예상할 수 없기 때문에 가변적으로 관리한다.
	
	public GameScene(GameManager manager) {

		super(manager);

		manager.nowCanvas = (GameCanvas) this;
	}

	@Override
	public void dblpaint(Graphics gContext) {
		// TODO Auto-generated method stub

		// 배경을 그리고
		gContext.drawImage(bg1, 0, bg1Y, this);
		gContext.drawImage(bg2, 0, bg2Y, this);

		// 플레이어를 그리고
		manager.drawImageRect(gContext, player, myX, myY,
				myFrame*playerWidth, 0, playerWidth, player.getHeight(null),
				manager.ANC_LEFT);
		
		// 플레이어가 발사한 총알을 그리고
		drawBullet(gContext);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		if (isPause)
			return;
		
		cnt++;
		keyTime++;

		bg1Y += _speed;
		bg2Y += _speed;

		if (bg1Y >= manager.SCREEN_HEIGHT)
			bg1Y = -manager.SCREEN_HEIGHT + bg1Y % manager.SCREEN_HEIGHT;//화면을 벗어난 배경그림 1의 위치를 되돌린다
		if (bg2Y >= manager.SCREEN_HEIGHT)
			bg2Y = -manager.SCREEN_HEIGHT + bg2Y % manager.SCREEN_HEIGHT;//화면을 벗어난 배경그림 2의 위치를 되돌린다


		processBullet();
		
		keyProcerss();
		myProcess();

	}

	@Override
	public void Destroy() {
		// TODO Auto-generated method stub
		super.Destroy();
		manager.remove(this);// GameManager의 firstScene에서 이 씬(클래스)을 add했으므로,
								// remove하여 제거한다.
		releaseImage();
	}

	Image bg1, bg2;// 게임 배경. 무한스크롤을 위해 2개 사용한다

	Image player;// 리네짱

	Image bullet;// 총알

	@Override
	public void initImage() {
		// TODO Auto-generated method stub

		bg1 = manager.makeImage("rsc/game/ground.png");
		bg2 = manager.makeImage("rsc/game/ground.png");// 게임 배경. 무한스크롤을 위해 2개
														// 사용한다
		player = manager.makeImage("rsc/game/lyne.png");// 리네짱
		
		bullet = manager.makeImage("rsc/game/mybullet01.png");// 총알
	}

	@Override
	public void releaseImage() {
		// TODO Auto-generated method stub

		bg1 = null;
		bg2 = null;// 게임 배경. 무한스크롤을 위해 2개 사용한다

		player = null;// 리네짱
		
		bullet = null;//총알
	}

	@Override
	public void SceneStart() {
		// TODO Auto-generated method stub
		// 별도의 씬 초기화를 위해 SceneStart를 오버라이드하고, 마지막에 super를 호출한다

		cnt = 0;

		// 배경용 좌표 (계산하기 편하게)
		bg1Y = 0;
		bg2Y = -800;// 배경화면 위치

		// 게임 관련 정보 초기화
		_speed = 4;// 배경 스크롤 속도

		//플레이어 정보 초기화
		playerWidth = player.getWidth(this) / 5;
		myX = (manager.SCREEN_WIDTH - playerWidth) / 2;// 화면 중앙
		myY = 550;// 고정
		myFrame = 2;//정중앙 = 중립 상태 프레임부터
		
		isPause = false;

		bullets = new Vector();// 총알 관리. 총알의 갯수를 예상할 수 없기 때문에 가변적으로 관리한다.
		bullets.clear();
		
		super.SceneStart();
	}


	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		keyTime = 0;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			keybuff |= LEFT_PRESSED;// 멀티키의 누르기 처리
			break;
		case KeyEvent.VK_RIGHT:
			keybuff |= RIGHT_PRESSED;
			break;
		default:
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

		keyTime = 0;

		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			keybuff &= ~LEFT_PRESSED;// 멀티키의 떼기 처리
			break;
		case KeyEvent.VK_RIGHT:
			keybuff &= ~RIGHT_PRESSED;
			break;

		}
	}

	// 여기서부터 오리지널 함수

	void drawBullet(Graphics gContext) {
		Bullet _buff;
		for (int i = 0; i < bullets.size(); i++) {
			_buff = (Bullet) bullets.elementAt(i);
			_buff.draw(gContext, this);
		}

	}
	
	void keyProcerss() {
		
		switch (keybuff) {
		case LEFT_PRESSED:
		
			if (myX > -20)
				myX -= 10;

			if (keyTime > 1 && keyTime % 7 == 0 && myFrame > 0)
				myFrame--;// 캐릭터의 왼쪽 기울어짐을 묘사한다

			break;
		case RIGHT_PRESSED:
			
			if (myX < manager.SCREEN_WIDTH + 20 - playerWidth)
				myX += 10;

			if (keyTime > 1 && keyTime % 7 == 0 && myFrame < 4)
				myFrame++;// 캐릭터의 오른쪽 기울어짐을 묘사한다

			break;
		}
	}
	void processBullet() {
		Bullet _buff;
		for (int i = 0; i < bullets.size(); i++) {
			_buff = (Bullet) bullets.elementAt(i);

			//방법 1
			int idx = _buff.process();

			switch (idx) {
			case Bullet.NO_PROCESS:// 아무런 변화도 없다
				break;
			case Bullet.MOVEOUT:// 화면 밖으로 사라졌다
				bullets.remove(i);
				break;
			}

//			//방법 2
//			_buff.process_another(this);
		}
	}
	
	void myProcess(){
		
		if (keybuff == 0 && keyTime > 1 && keyTime % 7 == 0) {
			if (myFrame < 2)
				myFrame++;
			else if (myFrame > 2)
				myFrame--;
			// 키에서 손을 놓았으면 캐릭터를 다시 중립 상태로 되돌린다.
		}
		
		if (cnt % 20 == 0) {

			int _x = myX + playerWidth / 2 - 12;
			int _y = myY - 17;

			// 싱글샷
			Bullet _bullet = new Bullet(bullet,
					new Rectangle(6, 1, 12, 33), _x, _y, 1);
			bullets.add(_bullet);
		}
		
	}

	//총알 소거 방법 2
//	public void deleteBullet(Bullet _deleteObj){
//
//		bullets.remove(_deleteObj);
//	}
	
}
