package player;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.Service;
import view.BoardPane;
import view.ControlPane;
import view.GamePane;
import view.Toast;

public class Player extends Application {
	public static final int GAME_WIDTH = 725;
	public static final int GAME_HEIGHT = 550;
	public static Service service;
	public static byte camp;
	public static boolean isTurn;

	private GamePane gamePane;
	private BoardPane boardPane;
	private ControlPane controlPane;
	
	private Toast toast;

	@Override
	public void start(Stage primaryStage) {
		initPlayer();
		
		primaryStage.setScene(new Scene(gamePane, GAME_WIDTH, GAME_HEIGHT));
		primaryStage.show();
		primaryStage.setOnCloseRequest(e-> {
			if(Service.isOnline) {
				service.distroyConnect();
			}
		});
	}
	
	private void initPlayer() {
		service = new Service(this);
		
		gamePane = new GamePane();
		boardPane = new BoardPane();
		controlPane = new ControlPane();
		
		gamePane.getChildren().addAll(boardPane, controlPane);
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	public BoardPane getBoardPane() {
		return boardPane;
	}

	public ControlPane getControlPane() {
		return controlPane;
	}
	
	public GamePane getGamePane() {
		return gamePane;
	}

	public void showMsg(Toast toast) {
		gamePane.getChildren().add(toast);
		this.toast = toast;
		Service.isPause = true;
	}
	
	public void closeMsg() {
		gamePane.getChildren().remove(toast);
		Service.isPause = false;
	}
}
