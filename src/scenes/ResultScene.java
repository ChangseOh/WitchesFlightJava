package scenes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;

import GameLib.GameCanvas;
import GameLib.GameManager;

public class ResultScene extends GameCanvas {

	int cnt;
	boolean keyLock;//숫자 보여주기가 끝날 때 까지 키 입력을 막는다
	boolean isNewRecord;//신기록 달성 여부
	float viewScore[];//각 점수 간격
	int totalScore;//총점
	
	public ResultScene(GameManager manager){
		
		super(manager);
		manager.nowCanvas = (GameCanvas) this;

		addKeyListener(this);//20211111
	}
	
	@Override
	public void dblpaint(Graphics gContext) {
		// TODO Auto-generated method stub

		gContext.drawImage(basePic, 0,0, this);//바탕을 그려주고
		
		//점수 보여주기 (20~40 프레임)
		if(cnt>=40)//40 프레임 이상이 되면 최종값만 지속적으로 보여준다 
			manager.drawnum(gContext, numpic, 328, 257, manager._getScore, 8, manager.ANC_RIGHT);
		else if(20<=cnt&&cnt<40)//20~40 프레임 사이에는 변동값만 보여준다
			manager.drawnum(gContext, numpic, 328, 257, (int)((cnt-20) * viewScore[0]), 8, manager.ANC_RIGHT);
		else//자기 차례가 아니라면 0만 보여준다
			manager.drawnum(gContext, numpic, 328, 257, 0, 8, manager.ANC_RIGHT);
		
		//거리 보여주기 (40~60 프레임)
		if(cnt>=60)
			manager.drawnum(gContext, numpic, 328, 332, manager._getRange, 8, manager.ANC_RIGHT);
		else if(40<=cnt&&cnt<60)
			manager.drawnum(gContext, numpic, 328, 332, (int)((cnt-40) * viewScore[1]), 8, manager.ANC_RIGHT);
		else
			manager.drawnum(gContext, numpic, 328, 332, 0, 8, manager.ANC_RIGHT);
		
		
		//총점 보여주기 (60~80 프레임)
		if(cnt>=80)
			manager.drawnum(gContext, numpic, 328, 434, totalScore, 8, manager.ANC_RIGHT);
		else if(60<=cnt&&cnt<80)
			manager.drawnum(gContext, numpic, 328, 434, (int)((cnt-60) * viewScore[2]), 8, manager.ANC_RIGHT);
		else
			manager.drawnum(gContext, numpic, 328, 434, 0, 8, manager.ANC_RIGHT);
		
		
		//골드 보여주기 (80~100 프레임)
		if(cnt>=100)
			manager.drawnum(gContext, numpic, 328, 581, manager._getGold, 8, manager.ANC_RIGHT);
		else if(80<=cnt&&cnt<100)
			manager.drawnum(gContext, numpic, 328, 581, (int)((cnt-80) * viewScore[3]), 8, manager.ANC_RIGHT);
		else
			manager.drawnum(gContext, numpic, 328, 581, 0, 8, manager.ANC_RIGHT);

		//스탬프 보여주기
		if(isNewRecord && cnt>110)
			gContext.drawImage(stampPic, 37, 376, this);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

		cnt++;
		
		//일정 시간이 지나면 키 락을 푼다
		if(cnt==120)
			keyLock = false;
	}

	@Override
	public void SceneStart() {
		// TODO Auto-generated method stub
		cnt = 0;
		keyLock = true;
		isNewRecord = false;
		totalScore = manager._getScore + manager._getRange;
		
		//숫자를 증가시켜서 보여주기 위한 증가값
		viewScore = new float[4];
		viewScore[0] = (float)manager._getScore/20.0f;
		viewScore[1] = (float)manager._getRange/20.0f;
		viewScore[2] = (float)totalScore/20.0f;
		viewScore[3] = (float)manager._getGold/20.0f;

		manager.gold += manager._getGold;//전체 골드에 획득한 골드를 합산
		
		//최고점수를 얻었는지 미리 체크
		if(manager.highscore < totalScore){
			isNewRecord = true;
			manager.highscore = totalScore;
		}
		
		manager.SaveGame(GameManager.fname);

		super.SceneStart();
	}


	@Override
	public void Destroy() {
		// TODO Auto-generated method stub
		super.Destroy();
		manager.remove(this);
		releaseImage();
	}

	Image basePic;
	Image stampPic;
	Image numpic;// 그림 숫자
	@Override
	public void initImage() {
		// TODO Auto-generated method stub

		basePic = manager.makeImage("rsc/result/result_base.png");//배경
		stampPic = manager.makeImage("rsc/result/stamp.png");//참잘했어요
		numpic = manager.makeImage("rsc/numpic.png");// 그림숫자
	}

	@Override
	public void releaseImage() {
		// TODO Auto-generated method stub

		basePic = null;
		stampPic = null;
		numpic = null;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
		//키락이 걸려있으면 그냥 리턴한다.
		//키 이벤트를 막는게 아니라, 키 이벤트는 처리하되 아무것도 하지 않고 리턴한다.
		//이 방식으로 키 버퍼가 쌓이는 것을 방지할 수 있다.
		if(keyLock)
			return;

		Destroy();
		manager.sceneChange((GameCanvas)new TitleScene(manager));//타이틀 화면으로 전환
	}

}
