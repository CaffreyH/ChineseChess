package view;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import player.Player;
import service.Service;

public class NetPane extends StackPane {
	public static final String READY = "准  备";
	public static final String CANCLE_READY = "取 消 准 备";
	public static final String GIVEUP = "认  输";
	public static final String NOWIN = "和  棋";
	
	private Image image = new Image("net.png");
	private ImageView iView = new ImageView(image);
	private VBox vBox = new VBox(20);
	private Rectangle bgR = new Rectangle(0, 0, 175, 225);
	private Button readyBtn;
	private Button giveupBtn;
	private Button nowinBtn;
	
	private ControlPane p;
	
	public boolean isReady = false;
	
	public NetPane(ControlPane p) {
		super();
		this.p = p;
		initPane();
		initEvent();
	}
	
	private void initPane() {
		iView.setFitWidth(175);
		iView.setFitHeight(225);
		bgR.setFill(Color.rgb(242, 200, 142, 0.5));
		getChildren().addAll(bgR, vBox, iView);
		
		readyBtn = new MyBtn(READY);
		giveupBtn = new MyBtn(GIVEUP);
		nowinBtn = new MyBtn(NOWIN);
		vBox.getChildren().add(readyBtn);
		vBox.setAlignment(Pos.CENTER);
		
	}
	
	public void choose() {
		FadeTransition ft = new FadeTransition();
		ft.setToValue(0);
		ft.setDuration(Duration.millis(500));
		ft.setNode(iView);
		ft.play();
		ft.setOnFinished(e-> {
			getChildren().remove(iView);
			ControlPane.isAnimOn = false;
		});
	}
	
	public void disChoose() {
		try {
			getChildren().add(iView);
			FadeTransition ft = new FadeTransition();
			ft.setToValue(1);
			ft.setDuration(Duration.millis(500));
			ft.setNode(iView);
			ft.play();
			ft.setOnFinished(e-> {
				ControlPane.isAnimOn = false;
			});
		} catch(Exception e) {
			
		}
	}
	
	private void initEvent() {
		iView.setOnMouseClicked(e-> {
			if(Service.isPause) return;
			if(ControlPane.isAnimOn) return;
			if(Service.isStart) {
				Player.service.showMsg("游戏已经开始， 请\n结束这局游戏！");
				return;
			}
			p.chooseMode(Service.PVP);
			Service.mode = Service.PVP;
		});
		readyBtn.setOnMouseClicked(e-> {
			if(Service.isPause) return;
			if(isReady) {
				cancleReady();
			} else {
				Player.service.ready();
				readyBtn.setText(CANCLE_READY);
				isReady = true;
			}
		});
		giveupBtn.setOnMouseClicked(e-> {
			if(Service.isPause) return;
			Player.service.giveup();
		});
		nowinBtn.setOnMouseClicked(e-> {
			if(Service.isPause) return;
			Player.service.askNowin();
		});
	}
	
	public void cancleReady() {
		Player.service.cancleReady();
		readyBtn.setText(READY);
		isReady = false;
	}
	
	public void startGame() {
		vBox.getChildren().remove(readyBtn);
		vBox.getChildren().addAll(giveupBtn, nowinBtn);
	}
	
	public void endGame() {
		vBox.getChildren().add(readyBtn);
		vBox.getChildren().removeAll(giveupBtn, nowinBtn);
		readyBtn.setText(READY);
		isReady = false;
	}
}