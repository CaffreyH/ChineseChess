package chess;

import java.util.ArrayList;

import javafx.scene.image.Image;
import service.Point;

public class Piece {
	public static final byte RED = 1;
	public static final byte BLACK = -1;
	
	public static final byte K = 1;
	public static final byte M = 2;
	public static final byte E = 3;
	public static final byte H = 4;
	public static final byte R = 5;
	public static final byte C = 6;
	public static final byte P = 7;
	
	private byte camp;
	private byte name;
	private Image image;
	private Point location;
	private ArrayList<Point> sitesList;
	
	public Piece(byte name, int camp, Point location) {
		super();
		this.camp = (byte) camp;
		this.location = location;
		this.name = name;
		initImage();
	}
	
	private void initImage(){
		String path = null;
		switch(camp) {
		case RED:
			path = "pieces\\red_";
			break;
		case BLACK:
			path = "pieces\\black_";
			break;
		}
		
		switch(name) {
		case K:
			path += "king.png";
			break;
		case M:
			path += "mandarin.png";
			break;
		case E:
			path += "elephant.png";
			break;
		case H:
			path += "horse.png";
			break;
		case R:
			path += "rook.png";
			break;
		case C:
			path += "cannon.png";
			break;
		case P:
			path += "pawn.png";
			break;
		}
		image = new Image(path);
	}
	
	public byte getCamp() {
		return camp;
	}
	
	public byte getName() {
		return name;
	}
	
	public Image getImage() {
		return image;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public ArrayList<Point> getSitesList() {
		return sitesList;
	}
	
	public void setLocation(Point location) {
		this.location = location;
	}
	
	public void setSitesList(ArrayList<Point> sitesList) {
		this.sitesList = sitesList;
	}

}
