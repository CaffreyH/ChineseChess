package view;

import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import player.Player;
import service.Point;
import service.Service;
import chess.Piece;

public class BoardPane extends Pane {
	public static final double GRID_SIZE = 50;
	private static final double REC_SIZE = 45;
	
	private Image bgImage;
	private ImageView bgView;
	private Rectangle choosedRec;
	private Rectangle dangerRec;
	private PieceView[][] pieceViews;
	public ArrayList<SiteView> siteViewsList;

	private Timeline choosedAnim;
	private FadeTransition chooseFts1;
	private FadeTransition chooseFts2;

	private Timeline dangerAnim;
	private FadeTransition dangerFts1;
	private FadeTransition dangerFts2;
	
	public BoardPane() {
		pieceViews = new PieceView[Service.MAX_H][Service.MAX_V];
		siteViewsList = new ArrayList<SiteView>();
		
		initPane();
		initAnim();
	}

	private void initPane() {
		setLayoutX(GRID_SIZE/2);
		setLayoutY(GRID_SIZE/2);
		setMaxWidth(GRID_SIZE*Service.MAX_H);
		setMaxHeight(GRID_SIZE*Service.MAX_V);
		
		bgImage = new Image("board.png");
		bgView = new ImageView(bgImage);
		bgView.fitWidthProperty().bind(widthProperty());
		bgView.fitHeightProperty().bind(heightProperty());
		
		choosedRec = new Rectangle(REC_SIZE, REC_SIZE);
		choosedRec.setFill(null);
		choosedRec.setStrokeWidth(4);
		choosedRec.setOpacity(0);
		choosedRec.getStrokeDashArray().addAll(REC_SIZE*2/9, REC_SIZE*5/9, 
				REC_SIZE*4/9, REC_SIZE*5/9, 
				REC_SIZE*4/9, REC_SIZE*5/9, 
				REC_SIZE*4/9, REC_SIZE*5/9, REC_SIZE*2/9);
		
		dangerRec = new Rectangle(getMaxWidth(), getMaxHeight());
		dangerRec.setFill(Color.RED);
		dangerRec.setStroke(null);
		
		getChildren().addAll(bgView, choosedRec);
	}
	
	@SuppressWarnings("restriction")
	private void initAnim() {
		double TIME = 800;
		double DELAY = 800;
		double OPACITY = 0;
		choosedRec.setOpacity(OPACITY);
		chooseFts1 = new FadeTransition();
		chooseFts1.setNode(choosedRec);
		chooseFts1.setDuration(Duration.millis(TIME));
		chooseFts1.setToValue(1);
		chooseFts1.setToValue(1);
		
		chooseFts2 = new FadeTransition();
		chooseFts2.setNode(choosedRec);
		chooseFts2.setDuration(Duration.millis(TIME));
		chooseFts2.setToValue(OPACITY);
		chooseFts2.setToValue(OPACITY);
		
		choosedAnim = new Timeline(new KeyFrame(Duration.millis(TIME+DELAY), e->{
			chooseFts1.play();
		}), new KeyFrame(Duration.millis(TIME*2+DELAY), e->{
			chooseFts2.play();
		}));
		choosedAnim.setCycleCount(Timeline.INDEFINITE);
//		choosedAnim.play();

		TIME = 300;
		DELAY = 300;
		OPACITY = 0;
		dangerRec.setOpacity(OPACITY);
		dangerFts1 = new FadeTransition();
		dangerFts1.setNode(dangerRec);
		dangerFts1.setDuration(Duration.millis(TIME));
		dangerFts1.setToValue(0.2);
		dangerFts1.setToValue(0.2);
		
		dangerFts2 = new FadeTransition();
		dangerFts2.setNode(dangerRec);
		dangerFts2.setDuration(Duration.millis(TIME));
		dangerFts2.setToValue(OPACITY);
		dangerFts2.setToValue(OPACITY);
		
		dangerAnim = new Timeline(new KeyFrame(Duration.millis(TIME+DELAY), e->{
			dangerFts1.play();
		}), new KeyFrame(Duration.millis(TIME*2+DELAY), e->{
			dangerFts2.play();
		}));
		dangerAnim.setCycleCount(Timeline.INDEFINITE);
		
	}
	
	public void addPiece(Piece piece) {
		boolean isMine = piece.getCamp() == Player.camp;
		PieceView pieceView = new PieceView(this, piece, isMine);
		pieceViews[piece.getLocation().x][piece.getLocation().y] = pieceView;
		getChildren().add(pieceView);
	}
	
	public Piece getPiece(int x, int y) {
		PieceView pieceView = pieceViews[x][y];
		if(pieceView == null) {
			return null;
		}
		return pieceViews[x][y].getPiece();
	}
	
	public void showSites(Piece piece) {
		removeSites();
		ArrayList<Point> sitesList = piece.getSitesList();
		for(int i = 0; i < sitesList.size(); i++) {
			SiteView siteView = new SiteView(this, piece, sitesList.get(i));
			siteViewsList.add(siteView);
		}
		getChildren().addAll(siteViewsList);
		
	}
	
	public void removeSites() {
		getChildren().removeAll(siteViewsList);
		siteViewsList.clear();
	}

	public void removePiece(int x, int y) {
		if(pieceViews[x][y] != null) {
			getChildren().remove(pieceViews[x][y]);
		}
	}

	public void moveTo(Point fl, Point tl) {
		removePiece(tl.x, tl.y);
		pieceViews[tl.x][tl.y] = pieceViews[fl.x][fl.y];
		pieceViews[fl.x][fl.y].moveTo(tl);
		pieceViews[fl.x][fl.y] = null;
		setRecLocation(tl);
		if(Service.isKingDanger(Service.findKing(Service.board).getLocation(),
				Service.board)) {
			//ÅÐ¶ÏÊÇ·ñÊäµô±ÈÈü
			boolean isField = true;
			for(int i = 0; i < Service.MAX_H; i++) {
				for(int j = 0; j < Service.MAX_V; j++) {
					Piece piece = getPiece(i, j);
					if(piece != null && piece.getCamp() == Player.camp) {
						ArrayList<Point> sitesList = piece.getSitesList();
						for(int k = 0;k < sitesList.size();k++) {
							byte[][] pieces = Service.copyBoard();
							if(!Service.tryMove(pieces, piece.getLocation(), sitesList.get(k))) {
								isField = false;
								break;
							}
						}
					}
				}
			}
			if(isField) {
				Player.service.field();
			} else if(dangerAnim.getStatus() == Animation.Status.STOPPED) {
				getChildren().add(2, dangerRec);
				dangerAnim.play();
			}
		} else if(dangerAnim.getStatus() == Animation.Status.RUNNING) {
			getChildren().remove(dangerRec);
			dangerAnim.stop();
			dangerRec.setOpacity(0);
		}
		removeSites();
		Player.service.calcuAllSites();
	}

	private void setRecLocation(Point location) {
		if(choosedAnim.getStatus() == Animation.Status.STOPPED) {
			choosedAnim.play();
			choosedRec.setStroke(Color.WHITE);
		}
		choosedRec.setLayoutX(location.x*GRID_SIZE+(GRID_SIZE-REC_SIZE)/2);
		choosedRec.setLayoutY(location.y*GRID_SIZE+(GRID_SIZE-REC_SIZE)/2);
	}
	
	public void clearBoard() {
		for(int i = 0; i < Service.MAX_H; i++) {
			for(int j = 0; j < Service.MAX_V; j++) {
				removePiece(i, j);
			}
		}
		pieceViews = new PieceView[Service.MAX_H][Service.MAX_V];
		siteViewsList = new ArrayList<SiteView>();
		if(choosedAnim.getStatus() == Animation.Status.RUNNING) {
			choosedAnim.stop();
			choosedRec.setOpacity(0);
		}
	}
	
	public void stopAnim() {
		if(dangerAnim.getStatus() == Animation.Status.RUNNING) {
			getChildren().remove(dangerRec);
			dangerAnim.stop();
			dangerRec.setOpacity(0);
		}
		if(choosedAnim.getStatus() == Animation.Status.RUNNING) {
			choosedAnim.stop();
			choosedRec.setOpacity(0);
		}
	}
}
