package scenes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Vector;

import subclasses.Bullet;
import subclasses.Effect;
import subclasses.Enemy;
import subclasses.Item;
import GameLib.GameCanvas;
import GameLib.GameManager;

public class GameScene extends GameCanvas {

	public final static int UP_PRESSED = 0x001;
	public final static int DOWN_PRESSED = 0x002;
	public final static int LEFT_PRESSED = 0x004;
	public final static int RIGHT_PRESSED = 0x008;
	public final static int FIRE_PRESSED = 0x010;
	
	public final static int STATUS_PLAYON = 0;//3-7(1)
	public final static int STATUS_FALL = 1;//3-7(1)
	
	public final static int MAXTIME_TWIN = 600;
	public final static int MAXTIME_MAGNET = 400;

	
	boolean isBoss;//20211111
	
	int cnt;
	
	int bg1Y, bg2Y;// 배경화면 위치
	
	int _speed;// 배경 스크롤 속도

	int myX, myY;// 플레이어 캐릭터 위치
	int status;// 플레이어 상태 //3-7(1)

	int playerWidth;// 플레이어 캐릭터 그림 1프레임의 가로 폭. 연산으로도 구할 수 있지만 자주 사용되기 때문에 따로 저장해둔다
	int myFrame;// 플레이어 캐릭터 애니메이션 프레임

	int _level;// 내부적으로 계산되는 게임 난이도
	
	int keybuff;
	int keyTime;// 키가 눌리거나 떼었을 때 얼마나 시간이 지났는가 카운팅한다
	
	boolean isPause;
	
	int _score;
	int _gold;
	int _range;
	int regen;//적 캐릭터 생성 카운터
	
	//트윈샷 처리
	int twinTime;
	boolean isTwin;
	
	//마그넷 처리
	int magnetTime;
	boolean isMagnet;

	Vector bullets;// 총알 관리. 총알의 갯수를 예상할 수 없기 때문에 가변적으로 관리한다.
	Vector enemies;// 적 캐릭터 관리.
	Vector effects;// 이펙트 관리 //3-8.
	Vector items;//아이템 관리
	
	public GameScene(GameManager manager) {

		super(manager);

		manager.nowCanvas = (GameCanvas) this;
		
		addKeyListener(this);//20211111
	}

	@Override
	public void dblpaint(Graphics gContext) {
		// TODO Auto-generated method stub

		// 배경을 그리고
		drawBG(gContext);
		
		// 적 캐릭터를 그리고
		drawEnemy(gContext);
		
		// 플레이어가 발사한 총알을 그리고
		drawBullet(gContext);
		
		// 플레이어를 그리고
		drawPlayer(gContext);
		
		//아이템을 그린다
		drawItem(gContext);
		
		//이펙트를 그린다
		drawEffect(gContext);//3-8.
		
		//UI를 그린다
		drawUI(gContext);
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

		if (bg1Y >= GameManager.SCREEN_HEIGHT)
			bg1Y = -GameManager.SCREEN_HEIGHT + bg1Y % GameManager.SCREEN_HEIGHT;//화면을 벗어난 배경그림 1의 위치를 되돌린다
		if (bg2Y >= GameManager.SCREEN_HEIGHT)
			bg2Y = -GameManager.SCREEN_HEIGHT + bg2Y % GameManager.SCREEN_HEIGHT;//화면을 벗어난 배경그림 2의 위치를 되돌린다


		processEnemy();
		processBullet();
		processEffect();//3-8.
		processItem();
		
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
	Image neuroi[];// 적 캐릭터
	Image effect;// effect_boom//3-8.
	Image effect2;// effect_fire//3-8.
	Image ui1;// 상단 UI
	Image ui2;// 하단 UI
	Image numpic;// 그림 숫자
	Image itempic[];//아이템
	Image v2pic;//v2로켓 20131211
	Image cautionpic;//로켓라인 경고 20131211
	Image boss;
	Image bossAttack, bossAttack2;
	Image bossHpBase;
	
	@Override
	public void initImage() {
		// TODO Auto-generated method stub

		bg1 = manager.makeImage("rsc/game/ground.png");
		bg2 = manager.makeImage("rsc/game/ground.png");// 게임 배경. 무한스크롤을 위해 2개
														// 사용한다
		player = manager.makeImage("rsc/game/lyne.png");// 리네짱
		
		bullet = manager.makeImage("rsc/game/mybullet"+String.format("%02d", manager.powlevel+1)+ ".png");// 총알 20131211 파워레벨에 따라 다른 총알 그림을 읽어들인다 

		neuroi = new Image[6];// 배열 형식의 Image 객체는 반드시 initImage 안에서 초기화한다.
		for (int i = 0; i < 6; i++)
			neuroi[i] = manager.makeImage("rsc/game/neuroi_0" + (i + 1)
					+ ".png");// 적 캐릭터

		effect = manager.makeImage("rsc/game/effect_boom.png");//3-8.
		effect2 = manager.makeImage("rsc/game/effect_fire.png");//3-8.

		numpic = manager.makeImage("rsc/numpic.png");// 그림숫자
		ui1 = manager.makeImage("rsc/game/gameui_01.png");// 상단UI
		ui2 = manager.makeImage("rsc/game/gameui_02.png");// 하단UI
		
		itempic = new Image[4];
		itempic[0] = manager.makeImage("rsc/game/coin.png");//골드
		itempic[1] = manager.makeImage("rsc/game/jewel.png");//빅골드
		itempic[2] = manager.makeImage("rsc/game/twinshot.png");//트윈샷
		itempic[3] = manager.makeImage("rsc/game/magnet.png");//마그넷
		
		//20131211
		v2pic = manager.makeImage("rsc/game/v2neuroi.png");//V2로켓
		cautionpic = manager.makeImage("rsc/game/caution.png");//경고선
		//20131211
		
		
		//20211111
		boss = manager.makeImage("rsc/game/x-3.png");//보스
		
		bossAttack = manager.makeImage("rsc/game/beam_ready.png");//보스의 공격 예고
		bossAttack2 = manager.makeImage("rsc/game/beamCenter.png");//빔 공격
		
		bossHpBase = manager.makeImage("rsc/game/bosshp_base.png");
		//20211111
	}

	@Override
	public void releaseImage() {
		// TODO Auto-generated method stub

		bg1 = null;
		bg2 = null;// 게임 배경. 무한스크롤을 위해 2개 사용한다

		player = null;// 리네짱
		
		bullet = null;//총알
		for (int i = 0; i < 6; i++)
			neuroi[i] = null;// 적 캐릭터
		
		effect  = null;//3-8.
		effect2  = null;//3-8.

		numpic = null;// 그림 숫자
		ui1 = null;// 상단UI
		ui2 = null;// 하단UI
		
		for(int i=0;i<4;i++)
			itempic[i] = null;

		v2pic = null;//20131211
		cautionpic = null;//20131211
		
		boss = null;//20211111
		bossAttack = null;//20211111
		bossAttack2 = null;//20211111
		
		bossHpBase = null;//20211111
	}

	@Override
	public void SceneStart() {
		// TODO Auto-generated method stub
		// 별도의 씬 초기화를 위해 SceneStart를 오버라이드하고, 마지막에 super를 호출한다
		
		isBoss = false;//20211111

		cnt = 0;

		// 배경용 좌표 (계산하기 편하게)
		bg1Y = 0;
		bg2Y = -800;// 배경화면 위치

		// 게임 관련 정보 초기화
		_speed = 4;// 배경 스크롤 속도
		_level = 0;//레벨 초기화

		//플레이어 정보 초기화
		playerWidth = player.getWidth(this) / 5;
		myX = (GameManager.SCREEN_WIDTH - playerWidth) / 2;// 화면 중앙
		myY = 550;// 고정
		myFrame = 2;//정중앙 = 중립 상태 프레임부터
		
		isPause = false;

		bullets = new Vector();// 총알 관리. 총알의 갯수를 예상할 수 없기 때문에 가변적으로 관리한다.
		enemies = new Vector();// 적 캐릭터 관리.
		effects = new Vector();//이펙트 관리 //3-8.
		items = new Vector();// 아이템 관리

		bullets.clear();
		enemies.clear();
		effects.clear();//3-8.
		items.clear();
		
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

		case KeyEvent.VK_1:
			isPause = !isPause;
			break;
		}
	}

	// 여기서부터 오리지널 함수
	
	void drawBG(Graphics gContext){
		
		gContext.drawImage(bg1, 0, bg1Y, this);
		gContext.drawImage(bg2, 0, bg2Y, this);
	}
	
	void drawPlayer(Graphics gContext) {
		
		manager.drawImageRect(gContext, player, myX, myY,
				myFrame*playerWidth, 0, playerWidth, player.getHeight(this),
				manager.ANC_LEFT);
		
		if(isTwin){
			gContext.setColor(new Color(1,1,1));//배경 상자의 색을 지정
			gContext.fillRect(myX, myY-10, playerWidth, 7);//최대치일때 기준으로 박스를 그린다
			gContext.setColor(new Color(255,0,0));//막대그래프의 색을 지정
			gContext.fillRect(myX+1, myY-9, (playerWidth-2) * twinTime/MAXTIME_TWIN, 5);//남은 시간을 기준으로 막대그래프를 그린다 
		}

		if(isMagnet){
			gContext.setColor(new Color(1,1,1));
			gContext.fillRect(myX, myY, playerWidth, 7);
			gContext.setColor(new Color(255,128,0));
			gContext.fillRect(myX+1, myY+1, (playerWidth-2) * magnetTime/MAXTIME_MAGNET, 5);
		}
	}
	void drawEnemy(Graphics gContext) {

		Enemy _buff;
		for (int i = 0; i < enemies.size(); i++) {
			_buff = (Enemy) enemies.elementAt(i);
			_buff.draw(manager, gContext, this);
		}
	}

	void drawBullet(Graphics gContext) {
		Bullet _buff;
		for (int i = 0; i < bullets.size(); i++) {
			_buff = (Bullet) bullets.elementAt(i);
			_buff.draw(gContext, this);
		}

	}

	//3-8.
	void drawEffect(Graphics gContext) {
		Effect _buff;
		for (int i = 0; i < effects.size(); i++) {
			_buff = (Effect) effects.elementAt(i);
			_buff.draw(manager, gContext, this);
		}

	}
	//3-8.

	void drawUI(Graphics gContext){
		
		gContext.drawImage(ui1, 6, 14, this);
		gContext.drawImage(ui2, 6, 741, this);

		manager.drawnum(gContext, numpic, 197, 39, _score, 8, manager.ANC_RIGHT);
		manager.drawnum(gContext, numpic, 464, 39, _range / 10, 6,
				manager.ANC_RIGHT);
		manager.drawnum(gContext, numpic, 147, 766, _gold, 6, manager.ANC_RIGHT);
	}
	
	void drawItem(Graphics gContext){
		Item _buff;
		for (int i = 0; i < items.size(); i++) {
			_buff = (Item) items.elementAt(i);
			_buff.draw(gContext, this);
		}
	}
	
	void keyProcerss() {
		
		if(status == STATUS_FALL)//3-7.(1)
			return;
		
		switch (keybuff) {
		case LEFT_PRESSED:
		
			if (myX > -20)
				myX -= 10;

			if (keyTime > 1 && keyTime % 7 == 0 && myFrame > 0)
				myFrame--;// 캐릭터의 왼쪽 기울어짐을 묘사한다

			break;
		case RIGHT_PRESSED:
			
			if (myX < GameManager.SCREEN_WIDTH + 20 - playerWidth)
				myX += 10;

			if (keyTime > 1 && keyTime % 7 == 0 && myFrame < 4)
				myFrame++;// 캐릭터의 오른쪽 기울어짐을 묘사한다

			break;
		}
	}
	void processBullet() {
		Bullet _buff;
		for (int i = bullets.size()-1; i >=0 ; i--) {
			_buff = (Bullet) bullets.elementAt(i);

			int idx = _buff.process(enemies);

			switch (idx) {
			case Bullet.NO_PROCESS:// 아무런 변화도 없다
				break;
			case Bullet.MOVEOUT:// 화면 밖으로 사라졌다
				bullets.remove(i);
				break;
			default://적에게 명중

				Effect _effect2 = new Effect( effect2, _buff.getX() + bullet.getWidth(this)/2 + manager.RAND(-5, 5), _buff.getY() + player.getHeight(this)/2 + manager.RAND(-2, 2), 3, 2);
				effects.add(_effect2);
				
				//3-7.(2)
				Enemy _temp = (Enemy) enemies.elementAt(idx);
				
				int eHp = _temp.hp;//총알의 파워를 감소시키기 위한 버퍼값

				_temp.hp -= _buff.pow;//적의 hp를 깎는다
				
				_buff.pow -= eHp;//총알의 파워를 깎는다

				if(_buff.pow<=0){
					//총알의 파워가 바닥나 소멸 처리
					bullets.remove(_buff);
				}

				if (_temp.hp <= 0) {
					// 적의 HP가 바닥나 파괴 처리
					if(_temp.kind==2) {
						_temp.SetDestroy();
					}
					else {
						
						Effect _effect = new Effect( effect, _temp.getX() + neuroi[0].getWidth(this)/2, _temp.getY() + neuroi[0].getHeight(this)/2, 6, 4);//3-8.
						effects.add(_effect);//3-8.
						
						enemies.remove(_temp);
						
						// 점수 가산
						_score += (50 + _level * 10);
						
						// 아이템 등장
						int itemkind = 0;
						if(manager.RAND(1, 20)==5)
							itemkind = 1;//빅골드
						else if(manager.RAND(1, 50)==10)
							itemkind = 2;//트윈샷
						else if(manager.RAND(1, 50)==10)
							itemkind = 3;//마그넷
						Item newitem = new Item(manager, itempic[itemkind], _temp.getX(), _temp.getY(), itemkind);
						items.add(newitem);
					}
				}
				//3-7.(2)
				break;
			}

		}
	}

	//3-8.
	void processEffect() {
		
		for(int i=effects.size()-1; i>=0; i--){
			
			Effect _buff = (Effect)effects.get(i);
			
			if ( !_buff.process()){
				effects.remove(_buff);
			}
		}
	}
	//3-8.
	
	void myProcess(){
		
		//3-7.(1)
		if(status == STATUS_FALL){
			
			if(cnt%5==0){
				Effect _effect = new Effect( effect, myX + playerWidth/2 + manager.RAND(-30, 30), myY + player.getHeight(this)/2 + manager.RAND(-30, 30), 6, 4);
				effects.add(_effect);
			}
			
			myY+=5;//아래로 내려보낸다
			
			if(myY>GameManager.SCREEN_HEIGHT + 60){//화면 밖으로 사라졌으면
				
				Destroy();
				manager._getGold =_gold;
				manager._getRange = _range;
				manager._getScore = _score;
				manager.sceneChange((GameCanvas)new ResultScene(manager));//결과화면으로 전환
			}
			
			return;
		}
		//3-7.(1)
		_range += (_speed / 2);
		
		if (keybuff == 0 && keyTime > 1 && keyTime % 7 == 0) {
			if (myFrame < 2)
				myFrame++;
			else if (myFrame > 2)
				myFrame--;
			// 키에서 손을 놓았으면 캐릭터를 다시 중립 상태로 되돌린다.
		}
		
		if (cnt % 7 == 0) {

			int _x = myX + playerWidth / 2 - 12;
			int _y = myY - 17;

			int powRange = manager.powlevel/6;//20131211 총알 레벨에 따라 영향받는 범위도 넓혀준다
			if(isTwin){
				//트윈샷
				for(int i=0;i<2;i++){
					
					Bullet _bullet = new Bullet(bullet,
							new Rectangle(5-powRange, 1, 10+powRange*2, 33), _x-15+i*30, _y, 1+manager.powlevel);//20131211 총알 레벨을 파워에 가산해주고 레벨에 따라 유효충돌영역도 넓게 준다
					bullets.add(_bullet);
				}
				
			}else{
				// 싱글샷
				Bullet _bullet = new Bullet(bullet,
						new Rectangle(5-powRange, 1, 10+powRange*2, 33), _x, _y, 1+manager.powlevel);//20131211 총알 레벨을 파워에 가산해주고 레벨에 따라 유효충돌영역도 넓게 준다
				bullets.add(_bullet);
			}

		}
		//트윈샷 처리중
		if(isTwin){
			if(twinTime--==0)
				isTwin = false;
		}

		//마그넷 처리중
		if(isMagnet){
			if(magnetTime--==0)
				isMagnet = false;
		}
	}

	//총알 소거 방법 2
//	public void deleteBullet(Bullet _deleteObj){
//
//		bullets.remove(_deleteObj);
//	}

	void processEnemy() {
		
		if(!isBoss) {//20211111
			//보스 캐릭터 등장 중에는 다른 적 캐릭터가 나오지 않게 하고 싶다

			// V2로켓을 생성합니다. 파괴가 불가능할 정도로 HP가 높고 덩치가 큽니다. 단, 본체 등장 전에는 잠시 경고 표지가 나옵니다 
			if(cnt > 200 && manager.RAND(0,1000) < 5*_level){//어느 정도는 레벨이 높아질수록 나오기 쉬워지는 편
	
				System.out.println("V2네우로이 생성");
				Enemy _enemy = new Enemy(v2pic, cautionpic, new Rectangle(39,49, 44,258), manager.RAND(61,358), -360, 10000000, 6 + _speed, 1);//v2로켓 전용 생성자 호출
				enemies.add(_enemy);
			}
			
			// 네우로이를 생성합니다. 한꺼번에 일렬로 5대를 생성합니다.
			if (cnt > 100
					&& (cnt % 90 == 0 || (manager.RAND(0, 10) == 5 && cnt % 45 == 0))) {
				 //기본적으로는 일정 시간마다 생성하지만, 게임에 변화를 주기 위해 10% 확률로 그 반 간격으로도 생성합니다.
	
				int localLevel = (regen%20) / 5;
				
				int neuroiLevel[] = { 0, 0, 0, 0, 0 };
				int setCnt = 0;
				while (setCnt < localLevel) {
					int idx = manager.RAND(0, 4);
					if (neuroiLevel[idx] == 0) {
						neuroiLevel[idx] = 1;
						setCnt++;
					}
				}
				
				for (int i = 0; i < 5; i++) {
	
					int imsiLevel = _level + neuroiLevel[i] + (1<=_level&&_level<=3&&manager.RAND(1,20)==5&&neuroiLevel[i]==1?1:0);
					if(imsiLevel>5)
						imsiLevel = 5;
	
					Enemy _enemy;
					_enemy = new Enemy(neuroi[imsiLevel],new Rectangle(33, 6, 76, 81),
									i * 96 - 23, -80,5 * (imsiLevel*2), 6 + _speed);
					enemies.add(_enemy);
				}
				regen++;
				
				if(regen%20==0)
					levelup();
				
			}
		}

		Enemy _buff;
		for (int i = enemies.size()-1; i >=0; i--) {
			_buff = (Enemy) enemies.elementAt(i);
			int ret = _buff.process(myX, myY, new Rectangle(12, 20, 55, 50));
			
			if( ret == Enemy.EVENT_CRASH && status == STATUS_PLAYON ){//3-7.(1)
				
				status = STATUS_FALL;//3-7.(1)
				isTwin = false;
				isMagnet = false;
				System.out.println("충돌 발생");
			}
			
			if (ret == Enemy.MOVEOUT){
				enemies.remove(_buff);// 화면 밖으로 나감
			}
		}

	}
	
	void processItem(){
		
		Item _buff;
		for(int i = items.size()-1; i>=0; i--){
			
			_buff = (Item)items.elementAt(i);
			int magnet = 0;
			if(isMagnet)
				magnet++;
			switch(_buff.process(myX, myY, new Rectangle(12, 20, 55, 50), magnet)){
			case Item.MOVEOUT:
				
				items.remove(_buff);
				break;
			case Item.TAKED:
				
				switch(_buff.getKind()){
				case 0://코인
					_gold += 1;
					break;
				case 1://빅코인
					_gold += (_level+1)*10;
					break;
				case 2://트윈샷
					twinTime = MAXTIME_TWIN;
					isTwin = true;
					break;
				case 3://마그넷
					magnetTime = MAXTIME_MAGNET;
					isMagnet = true;
					break;
				}
				items.remove(_buff);
				break;
			}
		}
	}

	void levelup(){
		
		if (_level < 5)
			_level++;// 레벨은 5까지

		if (_speed < 20)
			_speed += 2;

		CreateBoss();//20211111
	}
	
	//20211111
	void CreateBoss() {
		
		if(isBoss)
			return;
		
		Enemy bosschr = new Enemy(boss, bossHpBase, new Rectangle(8,60,464,222), 0, -1074, 200, 6, 2);
		enemies.add(bosschr);
		bosschr.gameScene = this;
		isBoss = true;
	}
	
	public void DrawEffect(int x, int y) {

		x+=manager.RAND(-200,200);
		y+=(manager.RAND(-100,100)+180);
		Effect _effect = new Effect( effect, x, y, 6, 4);
		effects.add(_effect);

		int itemkind = 0;
		if(manager.RAND(0, 10)==7)
			itemkind = 1;
		Item newitem = new Item(manager, itempic[itemkind], x, y, itemkind);
		items.add(newitem);
	}
	public void CallClaerScene() {
		Destroy();
		manager._getGold =_gold;
		manager._getRange = _range;
		manager._getScore = _score;
		manager.sceneChange((GameCanvas)new ClearScene(manager));
	}
	
	public void BossAttack() {

		int skipper = manager.RAND(0, 4); 
		for (int i = 0; i < 5; i++) {
			
			if(i==skipper)
				continue;
			
			Enemy _enemy = new Enemy(bossAttack2, bossAttack,
					new Rectangle(23, 0, 35, 700), i * 96, 165, 100, 1, 4);
			enemies.add(_enemy);
		}

	}
	//20211111
}
