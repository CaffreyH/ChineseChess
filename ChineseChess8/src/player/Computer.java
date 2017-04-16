package player;

import java.util.ArrayList;

import service.Move;
import service.Point;
import service.Service;
import chess.Piece;


public class Computer {
	public static final int K_W = 10000;
	public static final int M_W = 150;
	public static final int E_W = 150;
	public static final int H_W = 200;
	public static final int R_W = 350;
	public static final int C_W = 300;
	public static final int P_W = 100;
	
	private byte camp;
	private Player player;
	private Node bestNode;
	private int maxGride;

	public Computer(byte camp, Player player) {
		super();
		this.camp = camp;
		this.player = player;
	}

	public void yourTurn() {
		byte[][] board = copyBoard(Service.board);
		reversalBoard(board);
		Node rootNode = new Node();
		rootNode.board = board;
		rootNode.move = null;
		rootNode.gride = evaluate(rootNode.board, camp);
		rootNode.root = null;
		bestNode = null;
		maxGride = -100000;
		int deep = 3;
		search(rootNode, deep);
		Node nextNode = findRoot(bestNode);
		Move move = nextNode.move;
		moveTo(move.fx, (byte) (9-move.fy), move.tx, (byte) (9-move.ty));
	}
	
	private Node findRoot(Node node) {
		Node root = node;
		while(root.root.root != null) {
			root = root.root;
		}
		return root;
	}
	
	private void search(Node node, int deep) {
		if(deep == 0) {
			if(node.gride > maxGride) {
				bestNode = node;
				maxGride = node.gride;
			}
			return;
		}
		deep--;
		ArrayList<Move> movesList = calcuAllMoves(node.board, (byte) ((deep%2*2-1)*-camp));
		ArrayList<Node> nodesList = new ArrayList<Node>();
		for(int i = 0; i < movesList.size(); i++) {
			Move move = movesList.get(i);
			byte[][] board = copyBoard(node.board);
			board[move.tx][move.ty] = board[move.fx][move.fy];
			board[move.fx][move.fy] = 0;
			reversalBoard(board);
			Node newNode = new Node();
			newNode.board = board;
			newNode.move = move;
			newNode.gride = evaluate(newNode.board, (byte) ((deep%2*2-1)*-camp));
			newNode.root = node;
			nodesList.add(newNode);
		}
		select(nodesList, deep);
		for(int i = 0; i < nodesList.size(); i++) {
			search(nodesList.get(i), deep);
		}
	}
	
	private void select(ArrayList<Node> nodesList, int deep) {
		if(deep == 1) {
			Node bestNode = nodesList.get(0);
			int gride = bestNode.gride;
			for(int i = 1; i < nodesList.size(); i++) {
				if(nodesList.get(i).gride > gride) {
					bestNode = nodesList.get(i);
					gride = bestNode.gride;
				}
			}
			nodesList.clear();
			nodesList.add(bestNode);
		} else {
			
		}
	}
	
	private int evaluate(byte[][] board, byte camp) {
		int power = 0;
		for(int i = 0; i < Service.MAX_H; i++) {
			for(int j = 0; j < Service.MAX_V; j++) {
				if(board[i][j] != 0) {
					power += pieceWeight(board[i][j]);
				}
			}
		}
		return power*camp;
	}
	
	private int pieceWeight(byte name) {
		int camp = name > 0 ? 1 : -1;
		switch(name*camp) {
		case Piece.K:
			return camp*K_W;
		case Piece.M:
			return camp*M_W;
		case Piece.E:
			return camp*E_W;
		case Piece.H:
			return camp*H_W;
		case Piece.R:
			return camp*R_W;
		case Piece.C:
			return camp*C_W;
		default:
			return camp*P_W;
		}
	}
	
	private ArrayList<Move> calcuAllMoves(byte[][] board, byte camp) {
		ArrayList<Move> movesList = new ArrayList<Move>();
		for(int i = 0; i < Service.MAX_H; i++) {
			for(int j = 0; j < Service.MAX_V; j++) {
				if(board[i][j]*camp > 0) {
					ArrayList<Point> sitesList = Service.calcuPieceSites(board, new Point(i, j));
					for(int k = 0; k < sitesList.size(); k++) {
						movesList.add(new Move(new Point(i, j), sitesList.get(k)));
					}
				}
			}
		}
		return movesList;
	}
	
	private class Node {
		public byte[][] board;
		public int gride;
		public Move move;
		public Node root;
	}
	
	private byte[][] copyBoard(byte[][] board) {
		byte[][] newBoard = new byte[Service.MAX_H][Service.MAX_V];
		for(int i = 0; i < Service.MAX_H; i++) {
			for(int j = 0; j < Service.MAX_V; j++) {
				newBoard[i][j] = board[i][j];
			}
		}
		return newBoard;
	}
	
	private void reversalBoard(byte[][] board) {
		for(int i = 0; i < Service.MAX_H; i++) {
			for(int j = 0; j < Service.MAX_V/2; j++) {
				byte p = board[i][j];
				board[i][j] = board[i][Service.MAX_V-1-j];
				board[i][Service.MAX_V-1-j] = p;
			}
		}
	}
	
	private void moveTo(byte fx, byte fy, byte tx, byte ty) {
		Service.board[tx][ty] = Service.board[fx][fy];
		Service.board[fx][fy] = 0;
		player.getBoardPane().moveTo(new Point(fx, fy), new Point(tx, ty));
		Player.isTurn = true;
	}

//	private Point findKing(byte[][] board, byte camp) {
//		Point location = null;
//		for(int i = 3; i <= 5; i++) {
//			for(int j = 7; j <= 9; j++) {
//				if(board[i][j] == Piece.K*camp) {
//					location = new Point(i, j);
//				}
//			}
//		}
//		return location;
//	}
//	
//	//判断是否将军
//	private boolean isKingDanger(byte[][] board, byte camp) {
//		Point kingLocation = findKing(board, camp);
//		byte x = kingLocation.x;
//		byte y = kingLocation.y;
//		//横纵找 车、炮
//		//左
//		for(int i = x-1; i >= 0 ; i--) {
//			//此处若没有棋子，则继续循环
//			if(board[i][y] == 0) {
//				continue;
//			}
//			//第一个出现的棋子若为敌方车，则返回真
//			if(board[i][y] == -Piece.R*camp) {
//				return true;
//			}
//			i--;
//			//第二个出现的棋子若为敌方炮，则返回真
//			while(i >= 0) {
//				if(board[i][y] == -Piece.C*camp) {
//					return true;
//				} else if(board[i][y] != 0) {
//					break;
//				}
//				i--;
//			}
//			break;
//		}
//		//右
//		for(int i = x+1; i < Service.MAX_H ; i++) {
//			//此处若没有棋子，则继续循环
//			if(board[i][y] == 0) {
//				continue;
//			}
//			//第一个出现的棋子若为敌方车，则返回真
//			if(board[i][y] == -Piece.R*camp) {
//				return true;
//			}
//			i++;
//			//第二个出现的棋子若为敌方炮，则返回真
//			while(i < Service.MAX_H) {
//				if(board[i][y] == -Piece.C*camp) {
//					return true;
//				} else if(board[i][y] != 0) {
//					break;
//				}
//				i++;
//			}
//			break;
//		}
//		//上
//		for(int i = y-1; i >= 0 ; i--) {
//			//此处若没有棋子，则继续循环
//			if(board[x][i] == 0) {
//				continue;
//			}
//			//第一个出现的棋子若为敌方车，则返回真
//			if(board[x][i] == -Piece.R*camp) {
//				return true;
//			}
//			i--;
//			//第二个出现的棋子若为敌方炮，则返回真
//			while(i >= 0) {
//				if(board[x][i] == -Piece.C*camp) {
//					return true;
//				} else if(board[x][i] != 0) {
//					break;
//				}
//				i--;
//			}
//			break;
//		}
//		//下
//		for(int i = y+1; i < Service.MAX_V ; i++) {
//			//此处若没有棋子，则继续循环
//			if(board[x][i] == 0) {
//				continue;
//			}
//			//第一个出现的棋子若为敌方车，则返回真
//			if(board[x][i] == -Piece.R*camp) {
//				return true;
//			}
//			i++;
//			//第二个出现的棋子若为敌方炮，则返回真
//			while(i < Service.MAX_V) {
//				if(board[x][i] == -Piece.C*camp) {
//					return true;
//				} else if(board[x][i] != 0) {
//					break;
//				}
//				i++;
//			}
//			break;
//		}
//		//上左右相邻找 兵
//		if((y >= 6) && ((x-1 >= 0 && board[x-1][y] == -Piece.P*camp)
//				|| (x+1 < Service.MAX_H && board[x+1][y] == -Piece.P*camp)
//				|| (board[x][y-1] == -Piece.P*camp))) {
//			return true;
//		}
//		//八角找 马，将8个可行位置分为左上、左下、右上、右下各两个
//		//左上
//		if(x-1 >= 0 && y-1 >= 0 && board[x-1][y-1] == 0) {
//			if(x-2 >= 0 && board[x-2][y-1] == -Piece.H*camp) {
//				return true;
//			}
//			if(y-2 >= 0 && board[x-1][y-2] == -Piece.H*camp) {
//				return true;
//			}
//		}
//		//左下
//		if(x-1 >= 0 && y+1 < Service.MAX_V && board[x-1][y+1] == 0) {
//			if(x-2 >= 0 && board[x-2][y+1] == -Piece.H*camp) {
//				return true;
//			}
//			if(y+2 < Service.MAX_V && board[x-1][y+2] == -Piece.H*camp) {
//				return true;
//			}
//		}
//		//右上
//		if(x+1 < Service.MAX_H && y-1 >= 0 && board[x+1][y-1] == 0) {
//			if(x+2 < Service.MAX_H && board[x+2][y-1] == -Piece.H*camp) {
//				return true;
//			}
//			if(y-2 >= 0 && board[x+1][y-2] == -Piece.H*camp) {
//				return true;
//			}
//		}
//		//右下
//		if(x+1 >= 0 && y+1 < Service.MAX_V && board[x+1][y+1] == 0) {
//			if(x+2 >= 0 && board[x+2][y+1] == -Piece.H*camp) {
//				return true;
//			}
//			if(y+2 < Service.MAX_V && board[x+1][y+2] == -Piece.H*camp) {
//				return true;
//			}
//		}
//		return false;
//	}
}
