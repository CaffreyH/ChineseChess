package view;

import player.Player;
import service.Point;
import service.Service;
import chess.Piece;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SiteView extends Circle {
	private BoardPane boardPane;
	private Piece piece;
	private Point location;
	
	public SiteView(BoardPane boardPane, Piece piece, Point location) {
		super();
		this.boardPane = boardPane;
		this.piece = piece;
		this.location = location;
		initView();
		byte[][] board = Service.copyBoard();
		if(Service.tryMove(board, piece.getLocation(), location)) {
			setFill(Color.rgb(255, 0, 0, 0.6));
		} else {
			initEvent();
		}
	}
	
	private void initView() {
		setRadius(BoardPane.GRID_SIZE/2-PieceView.IMAGE_LOCATION);
		setLayoutX(location.x*BoardPane.GRID_SIZE+BoardPane.GRID_SIZE/2);
		setLayoutY(location.y*BoardPane.GRID_SIZE+BoardPane.GRID_SIZE/2);
		if(boardPane.getPiece(location.x, location.y) != null) {
			setFill(Color.rgb(200, 0, 0, 0.3));
		} else {
			setFill(Color.rgb(200, 200, 200, 0.3));
		}
	}

	private void initEvent() {
		setOnMouseClicked(e-> {
			if(Service.isPause) return;
			Player.service.moveTo(piece.getLocation(), location);
		});
	}

}
