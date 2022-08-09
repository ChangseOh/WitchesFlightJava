package scenes;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;

import GameLib.GameCanvas;
import GameLib.GameManager;

public class TitleScene extends GameCanvas{

	int cnt = 0;
	
	public TitleScene(GameManager manager){
		
		super(manager);

		manager.nowCanvas = (GameCanvas)this;
		
		manager.LoadGame("wf.dat");
	}
	
	@Override
	public void dblpaint(Graphics gContext) {
		// TODO Auto-generated method stub
		gContext.drawImage(bg, 0, 0, this);
		gContext.drawImage(highscore, 75, 416, this);
		gContext.drawImage(mygold, 75, 507, this);
		
		manager.drawnum(gContext, numpic, 383,407, manager.highscore, 8, manager.ANC_RIGHT);
		manager.drawnum(gContext, numpic, 383,500, manager.gold, 6, manager.ANC_RIGHT);
		
		if(cnt%30<15)
			gContext.drawImage(pushany, (manager.SCREEN_WIDTH-pushany.getWidth(this)) / 2, 666, this);
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		cnt ++;
	}

	@Override
	public void Destroy() {
		// TODO Auto-generated method stub
		super.Destroy();
		manager.remove(this);//GameManager의 firstScene에서 이 씬(클래스)을 add했으므로, remove하여 제거한다.
		releaseImage();
	}

	Image bg;//타이틀 바탕
	Image highscore;//최고점수
	Image mygold;//소지중 골드
	Image pushany;//아무키나 누르세요
	Image numpic;//숫자그림
	@Override
	public void initImage() {
		// TODO Auto-generated method stub
		bg = manager.makeImage("rsc/title/title.png");
		highscore = manager.makeImage("rsc/title/high.png");
		mygold = manager.makeImage("rsc/title/gold.png");
		pushany = manager.makeImage("rsc/title/pushany.png");
		numpic = manager.makeImage("rsc/numpic.png");
	}

	@Override
	public void releaseImage() {
		// TODO Auto-generated method stub
		bg = null;
		highscore = null;
		mygold = null;
		pushany = null;
		numpic = null;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		Destroy();
		manager.sceneChange((GameCanvas)new GameScene(manager));
	}

	
}
