package service;

public class Point {
	public byte x;
	public byte y;
	
	public Point(byte x, byte y) {
		super();
		this.x = x;
		this.y = y;
	}

	public Point(int x, int y) {
		super();
		this.x = (byte) x;
		this.y = (byte) y;
	}
}
