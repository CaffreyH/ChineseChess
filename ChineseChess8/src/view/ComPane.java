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
import chess.Piece;

public class ComPane extends StackPane {
	public static final String START = "开 始 游 戏";
	public static final String RESTART = "重 新 开 始";
	public static final String END = "结 束 游 戏";
	
	private Image image = new Image("com.png");
	private ImageView iView = new ImageView(image);
	private VBox vBox = new VBox(20);
	private Rectangle bgR = new Rectangle(0, 0, 175, 225);
	private Button startBtn;
	private Button endBtn;
	
	private ControlPane p;
	
	public ComPane(ControlPane p) {
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
		
		startBtn = new MyBtn(START);
		endBtn = new MyBtn(END);
		vBox.getChildren().addAll(startBtn, endBtn);
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
			p.chooseMode(Service.PVS);
		});
		startBtn.setOnMouseClicked(e-> {
			Player.service.fightComputer(Piece.RED);
			startBtn.setText(RESTART);
		});
		endBtn.setOnMouseClicked(e-> {
			Player.service.endGame();
			startBtn.setText(START);
		});
	}
}
