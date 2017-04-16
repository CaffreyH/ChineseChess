package service;

public class Move {
	public byte fx;
	public byte fy;
	public byte tx;
	public byte ty;
	
	public Move(byte fx, byte fy, byte tx, byte ty) {
		super();
		this.fx = fx;
		this.fy = fy;
		this.tx = tx;
		this.ty = ty;
	}
	
	public Move(Point fl, Point tl) {
		super();
		this.fx = fl.x;
		this.fy = fl.y;
		this.tx = tl.x;
		this.ty = tl.y;
	}
}
