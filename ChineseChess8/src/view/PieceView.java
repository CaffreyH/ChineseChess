package view;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import player.Player;
import service.Point;
import service.Service;
import chess.Piece;

public class PieceView extends Pane {
	public static final int IMAGE_LOCATION = 4;
	private BoardPane boardPane;
	private Piece piece;
	private ImageView imageView;

	public PieceView(BoardPane boardPane, Piece piece, boolean isMine) {
		super();
		this.boardPane = boardPane;
		this.piece = piece;
		initView();
		if(isMine) {
			initEvent();
		}
	}
	
	private void initView() {
		setMaxWidth(BoardPane.GRID_SIZE);
		setMaxHeight(BoardPane.GRID_SIZE);
		setLayoutX(piece.getLocation().x*BoardPane.GRID_SIZE);
		setLayoutY(piece.getLocation().y*BoardPane.GRID_SIZE);
		
		imageView = new ImageView(piece.getImage());
		imageView.fitWidthProperty().bind(maxWidthProperty().subtract(IMAGE_LOCATION*2));
		imageView.fitHeightProperty().bind(maxHeightProperty().subtract(IMAGE_LOCATION*2));
		imageView.setLayoutX(IMAGE_LOCATION);
		imageView.setLayoutY(IMAGE_LOCATION);
		getChildren().add(imageView);
	}
	
	public void moveTo(Point tl) {
		setLayoutX(tl.x*BoardPane.GRID_SIZE);
		setLayoutY(tl.y*BoardPane.GRID_SIZE);
		piece.setLocation(tl);
	}

	public Piece getPiece() {
		return piece;
	}
	
	public void initEvent() {
		setOnMouseClicked(e-> {
			if(Service.isPause) return;
			if(isClicked(e.getX(), e.getY()) && Player.isTurn && Service.isStart) {
				boardPane.showSites(piece);
			}
		});
	}
	
	protected boolean isClicked(double x, double y) {
		double radius = BoardPane.GRID_SIZE/2-IMAGE_LOCATION;
		double centerX = BoardPane.GRID_SIZE/2;
		double centerY = BoardPane.GRID_SIZE/2;
		
		return (x-centerX)*(x-centerX)+(y-centerY)*(y-centerY) <= radius*radius;
	}
}
