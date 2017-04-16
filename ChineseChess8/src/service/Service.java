package service;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javafx.concurrent.Task;
import javafx.scene.control.Button;
import player.Computer;
import player.Player;
import view.Toast;
import chess.Piece;

public class Service {
	public static final int MAX_H = 9;		//�������
	public static final int MAX_V = 10;		//�������
	public static final String IP = "222.210.112.210";
	public static final int PORT = 3000;

	public static final byte O_READY = 0;
	public static final byte O_CANCLE_READY = 1;
	public static final byte O_ASK_GOBACK = 2;
	public static final byte O_ALLOW_GOBACK = 3;
	public static final byte O_REFUSE_GOBACK = 4;
	public static final byte O_GIVEUP = 5;
	public static final byte O_NOWIN = 6;
	public static final byte O_ALLOW_NOWIN = 7;
	public static final byte O_REFUSE_NOWIN = 8;
	public static final byte O_FIELD = 9;
	public static final byte O_PLAYERS_COUNT = 10;
	public static final byte O_MOVE = 11;
	public static final byte O_CLOSE = 12;

	public static final byte I_START = 13;
	public static final byte I_MOVE = 14;
	public static final byte I_WIN = 15;
	public static final byte I_ASK_GOBACK = 16;
	public static final byte I_GIVEUP = 17;
	public static final byte I_NOWIN = 18;
	public static final byte I_ALLOW_NOWIN = 19;
	public static final byte I_REFUSE_NOWIN = 20;
	public static final byte I_CLOSE = 21;

	public static final byte[][] board = new byte[MAX_H][MAX_V];
	
	public static final int PVP = 0;
	public static final int PVS = 1;
	public static int mode;
	public static boolean isStart = false;
	public static boolean isPause = false;

	public static boolean isOnline;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private NetInThread netInThread;

	private Player player;
	private Computer computer;

	public Service(Player player) {
		super();
		this.player = player;
		clearBoard();
	}

	public static byte[][] copyBoard() {
		byte[][] copyBoard = new byte[MAX_H][MAX_V];
		for(int i = 0; i < MAX_H; i++) {
			for(int j = 0; j < MAX_V; j++) {
				copyBoard[i][j] = board[i][j];
			}
		}
		return copyBoard;
	}
	
	public static void showBoard(byte[][] board) {
		for(int i = 0; i < MAX_V; i++) {
			for(int j = 0; j < MAX_H; j++) {
				byte p = board[j][i];
				String s;
				if(p < 0) {
					s = " " + p;
				} else {
					s = "  " + p;
				}
				System.out.print(s);
			}
			System.out.println("");
		}
		System.out.println("-------------------------\n\n");
	}

	private void sendAction(byte action) {
		try {
			dos.writeByte(action);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void distroyConnect() {
		isOnline = false;
		sendAction(O_CLOSE);
	}

	public boolean connectServer() {
		try {
			socket = new Socket(IP, PORT);
			dis = new DataInputStream(socket.getInputStream());
			dos = new DataOutputStream(socket.getOutputStream());
			isOnline = true;
			netInThread = new NetInThread();
			netInThread.start();
		} catch (IOException e) {
			return false;
		}
		mode = PVP;		//��ģʽ��Ϊ��Ҷ�ս
		return true;
	}

	public void ready() {
		sendAction(O_READY);
	}
	
	public void cancleReady() {
		sendAction(O_CANCLE_READY);
	}

	public void moveTo(Point fl, Point tl) {
		board[tl.x][tl.y] = board[fl.x][fl.y];
		board[fl.x][fl.y] = 0;
		player.getBoardPane().moveTo(fl, tl);
		if(mode == PVP) {
			Player.isTurn = false;
			sendAction(O_MOVE);
			sendAction(fl.x);
			sendAction(fl.y);
			sendAction(tl.x);
			sendAction(tl.y);
		} else if(mode == PVS) {
			Player.isTurn = false;
			computer.yourTurn();
		}
	}

	private void createGame() {
		player.getBoardPane().clearBoard();
		
		player.getBoardPane().addPiece(new Piece(Piece.K, -Player.camp, new Point(4, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.M, -Player.camp, new Point(3, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.M, -Player.camp, new Point(5, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.E, -Player.camp, new Point(2, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.E, -Player.camp, new Point(6, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.H, -Player.camp, new Point(1, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.H, -Player.camp, new Point(7, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.R, -Player.camp, new Point(0, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.R, -Player.camp, new Point(8, 0)));
		player.getBoardPane().addPiece(new Piece(Piece.C, -Player.camp, new Point(1, 2)));
		player.getBoardPane().addPiece(new Piece(Piece.C, -Player.camp, new Point(7, 2)));
		player.getBoardPane().addPiece(new Piece(Piece.P, -Player.camp, new Point(0, 3)));
		player.getBoardPane().addPiece(new Piece(Piece.P, -Player.camp, new Point(2, 3)));
		player.getBoardPane().addPiece(new Piece(Piece.P, -Player.camp, new Point(4, 3)));
		player.getBoardPane().addPiece(new Piece(Piece.P, -Player.camp, new Point(6, 3)));
		player.getBoardPane().addPiece(new Piece(Piece.P, -Player.camp, new Point(8, 3)));

		player.getBoardPane().addPiece(new Piece(Piece.K, Player.camp, new Point(4, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.M, Player.camp, new Point(3, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.M, Player.camp, new Point(5, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.E, Player.camp, new Point(2, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.E, Player.camp, new Point(6, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.H, Player.camp, new Point(1, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.H, Player.camp, new Point(7, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.R, Player.camp, new Point(0, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.R, Player.camp, new Point(8, 9)));
		player.getBoardPane().addPiece(new Piece(Piece.C, Player.camp, new Point(1, 7)));
		player.getBoardPane().addPiece(new Piece(Piece.C, Player.camp, new Point(7, 7)));
		player.getBoardPane().addPiece(new Piece(Piece.P, Player.camp, new Point(0, 6)));
		player.getBoardPane().addPiece(new Piece(Piece.P, Player.camp, new Point(2, 6)));
		player.getBoardPane().addPiece(new Piece(Piece.P, Player.camp, new Point(4, 6)));
		player.getBoardPane().addPiece(new Piece(Piece.P, Player.camp, new Point(6, 6)));
		player.getBoardPane().addPiece(new Piece(Piece.P, Player.camp, new Point(8, 6)));

		initBoard();
		calcuAllSites();
		isStart = true;
		isPause = false;
		if(mode == PVP) {
			player.getControlPane().getNetPane().startGame();
		}
	}
	
	public void endGame() {
		isStart = false;
		if(mode == PVP) {
			player.getControlPane().getNetPane().endGame();
		}
		player.getBoardPane().removeSites();
		player.getBoardPane().stopAnim();
	}

	public void clearBoard() {
		for(int i = 0; i < MAX_H; i++) {
			for(int j = 0; j < MAX_V; j++) {
				board[i][j] = 0;
			}
		}
	}

	public void initBoard() {
		for(int i = 0; i < MAX_H; i++) {
			for(int j = 0; j < MAX_V; j++) {
				Piece piece = player.getBoardPane().getPiece(i, j);
				if(piece != null) {
					board[i][j] = (byte) (piece.getName()*piece.getCamp());
				} else {
					board[i][j] = 0;
				}
			}
		}
	}

	public void calcuAllSites() {
		for(int i = 0; i < MAX_H; i++) {
			for(int j = 0; j < MAX_V; j++) {
				Piece piece = player.getBoardPane().getPiece(i, j);
				if(piece != null && piece.getCamp() == Player.camp) {
					piece.setSitesList(calcuPieceSites(board, piece.getLocation()));
				}
			}
		}
	}

	public static ArrayList<Point> calcuPieceSites(byte[][] board, Point location) {
		byte name = board[location.x][location.y];
		byte camp = Piece.RED;
		if(name < 0) {
			name = (byte) -name;
			camp = Piece.BLACK;
		}
		switch(name) {
		case Piece.K:
			return calcuKing(board, location, camp);
		case Piece.M:
			return calcuMandarin(board, location, camp);
		case Piece.E:
			return calcuElephant(board, location, camp);
		case Piece.H:
			return calcuHorse(board, location, camp);
		case Piece.R:
			return calcuRook(board, location, camp);
		case Piece.C:
			return calcuCannon(board, location, camp);
		default:
			return calcuPawn(board, location, camp);
		}
	}

	private static ArrayList<Point> calcuKing(byte[][] board, Point location, byte camp) {
		ArrayList<Point> sitesList = new ArrayList<Point>();
		byte locationX = location.x;
		byte locationY = location.y;		//Ŀ��λ�ã������жϴ�λ���Ƿ����
		if(locationY-1 >= 7){
			//���Ŀ��λ���ǣ��գ��򣨵��ӣ�
			if(board[locationX][locationY-1] == 0
					|| board[locationX][locationY-1]*camp < 0){
				sitesList.add(new Point(locationX, locationY-1));
			}
		}
		if(locationY+1 <= 9){
			//���Ŀ��λ���ǣ��գ��򣨵��ӣ�
			if(board[locationX][locationY+1] == 0
					|| board[locationX][locationY+1]*camp < 0){
				sitesList.add(new Point(locationX, locationY+1));
			}
		}
		//���Ŀ��λ���ڣ�3-5�У�֮��
		if(locationX-1 >= 3){
			//���Ŀ��λ���ǣ��գ��򣨵��ӣ�
			if(board[locationX-1][locationY] == 0
					|| board[locationX-1][locationY]*camp < 0){
				sitesList.add(new Point(locationX-1, locationY));
			}
		}
		if(locationX+1 <= 5){
			//���Ŀ��λ���ǣ��գ��򣨵��ӣ�
			if(board[locationX+1][locationY] == 0
					|| board[locationX+1][locationY]*camp < 0){
				sitesList.add(new Point(locationX+1, locationY));
			}
		}
		return sitesList;
	}

	private static ArrayList<Point> calcuMandarin(byte[][] board, Point location, byte camp) {
		ArrayList<Point> sitesList = new ArrayList<Point>();
		byte locationX = location.x;		//Ŀ��λ�ã������жϴ�λ���Ƿ����
		int centerX = 4;
		int centerY = 8;
		//���������λ�ã��ж��Ľ��Ƿ����
		if(locationX == 4){
			if(board[centerX-1][centerY-1] == 0
					|| board[centerX-1][centerY-1]*camp < 0){
				sitesList.add(new Point(centerX-1, centerY-1));
			}
			if(board[centerX-1][centerY+1] == 0
					|| board[centerX-1][centerY+1]*camp < 0){
				sitesList.add(new Point(centerX-1, centerY+1));
			}
			if(board[centerX+1][centerY-1] == 0
					|| board[centerX+1][centerY-1]*camp < 0){
				sitesList.add(new Point(centerX+1, centerY-1));
			}
			if(board[centerX+1][centerY+1] == 0
					|| board[centerX+1][centerY+1]*camp < 0){
				sitesList.add(new Point(centerX+1, centerY+1));
			}
		}
		//������Ľǣ��ж������Ƿ����
		else{
			if(board[centerX][centerY] == 0
					|| board[centerX][centerY]*camp < 0){
				sitesList.add(new Point(centerX, centerY));
			}
		}
		return sitesList;
	}

	private static ArrayList<Point> calcuElephant(byte[][] board, Point location, byte camp) {
		ArrayList<Point> sitesList = new ArrayList<Point>();
		byte locationX = location.x;
		byte locationY = location.y;		//Ŀ��λ�ã������жϴ�λ���Ƿ����
		//�ж�Ŀ��λ���Ƿ��ڼ�����Ӫ��δ�����ġ�
		//���Ͻ�
		if(locationY-2 >= 5 && locationX-2 >= 0
				&& board[locationX-1][locationY-1] == 0){
			//���Ŀ��λ���ǣ��գ�
			if(board[locationX-2][locationY-2] == 0
					|| board[locationX-2][locationY-2]*camp < 0){
				sitesList.add(new Point(locationX-2, locationY-2));
			}
		}
		//���Ͻ�
		if(locationY-2 >= 5 && locationX+2 <= 8
				&& board[locationX+1][locationY-1] == 0){
			//���Ŀ��λ���ǣ��գ�
			if(board[locationX+2][locationY-2] == 0
					|| board[locationX+2][locationY-2]*camp < 0){
				sitesList.add(new Point(locationX+2, locationY-2));
			}
		}
		//���½�
		if(locationY+2 <= 9 && locationX-2 >= 0
				&& board[locationX-1][locationY+1] == 0){
			//���Ŀ��λ���ǣ��գ�
			if(board[locationX-2][locationY+2] == 0
					|| board[locationX-2][locationY+2]*camp < 0){
				sitesList.add(new Point(locationX-2, locationY+2));
			}
		}
		//���½�
		if(locationY+2 <= 9 && locationX+2 <= 8
				&& board[locationX+1][locationY+1] == 0){
			//���Ŀ��λ���ǣ��գ�
			if(board[locationX+2][locationY+2] == 0
					|| board[locationX+2][locationY+2]*camp < 0){
				sitesList.add(new Point(locationX+2, locationY+2));
			}
		}
		return sitesList;
	}

	private static ArrayList<Point> calcuHorse(byte[][] board, Point location, byte camp) {
		ArrayList<Point> sitesList = new ArrayList<Point>();
		byte locationX = location.x;
		byte locationY = location.y;		//Ŀ��λ�ã������жϴ�λ���Ƿ����
		//��8������λ�÷�Ϊ�������¸�����
		//��
		if(locationX-2 >= 0 && board[locationX-1][locationY] == 0){
			if(locationY-1 >= 0){
				if(board[locationX-2][locationY-1] == 0
						|| board[locationX-2][locationY-1]*camp < 0){
					sitesList.add(new Point(locationX-2, locationY-1));
				}
			}
			if(locationY+1 <= 9){
				if(board[locationX-2][locationY+1] == 0
						|| board[locationX-2][locationY+1]*camp < 0){
					sitesList.add(new Point(locationX-2, locationY+1));
				}
			}
		}
		//��
		if(locationX+2 <= 8 && board[locationX+1][locationY] == 0){
			if(locationY-1 >= 0){
				if(board[locationX+2][locationY-1] == 0
						|| board[locationX+2][locationY-1]*camp < 0){
					sitesList.add(new Point(locationX+2, locationY-1));
				}
			}
			if(locationY+1 <= 9){
				if(board[locationX+2][locationY+1] == 0
						|| board[locationX+2][locationY+1]*camp < 0){
					sitesList.add(new Point(locationX+2, locationY+1));
				}
			}
		}
		//��
		if(locationY-2 >= 0 && board[locationX][locationY-1] == 0){
			if(locationX-1 >= 0){
				if(board[locationX-1][locationY-2] == 0
						|| board[locationX-1][locationY-2]*camp < 0){
					sitesList.add(new Point(locationX-1, locationY-2));
				}
			}
			if(locationX+1 <= 8){
				if(board[locationX+1][locationY-2] == 0
						|| board[locationX+1][locationY-2]*camp < 0){
					sitesList.add(new Point(locationX+1, locationY-2));
				}
			}
		}
		//��
		if(locationY+2 <= 9 && board[locationX][locationY+1] == 0){
			if(locationX-1 >= 0){
				if(board[locationX-1][locationY+2] == 0
						|| board[locationX-1][locationY+2]*camp < 0){
					sitesList.add(new Point(locationX-1, locationY+2));
				}
			}
			if(locationX+1 <= 8){
				if(board[locationX+1][locationY+2] == 0
						|| board[locationX+1][locationY+2]*camp < 0){
					sitesList.add(new Point(locationX+1, locationY+2));
				}
			}
		}
		return sitesList;
	}

	private static ArrayList<Point> calcuRook(byte[][] board, Point location, byte camp) {
		ArrayList<Point> sitesList = new ArrayList<Point>();
		byte locationX = location.x;
		byte locationY = location.y;		//Ŀ��λ�ã������жϴ�λ���Ƿ����
		//�ж������������ŵĿ���λ��
		//��
		for(int x = locationX-1; x >= 0 && (board[x][locationY] == 0 || 
				board[x][locationY]*camp < 0); x--){
			if(board[x][locationY] == 0){
				sitesList.add(new Point(x, locationY));
			}
			else{
				sitesList.add(new Point(x, locationY));
				break;
			}
		}
		//��
		for(int x = locationX+1; x <= 8 && (board[x][locationY] == 0 || 
				board[x][locationY]*camp < 0); x++){
			if(board[x][locationY] == 0){
				sitesList.add(new Point(x, locationY));
			}
			else{
				sitesList.add(new Point(x, locationY));
				break;
			}
		}
		//��
		for(int y = locationY-1; y >= 0 && (board[locationX][y] == 0 || 
				board[locationX][y]*camp < 0); y--){
			if(board[locationX][y] == 0){
				sitesList.add(new Point(locationX, y));
			}
			else{
				sitesList.add(new Point(locationX, y));
				break;
			}
		}
		//��
		for(int y = locationY+1; y <= 9 && (board[locationX][y] == 0 || 
				board[locationX][y]*camp < 0); y++){
			if(board[locationX][y] == 0){
				sitesList.add(new Point(locationX, y));
			}
			else{
				sitesList.add(new Point(locationX, y));
				break;
			}
		}
		return sitesList;
	}

	private static ArrayList<Point> calcuCannon(byte[][] board, Point location, byte camp) {
		ArrayList<Point> sitesList = new ArrayList<Point>();
		byte locationX = location.x;
		byte locationY = location.y;		//Ŀ��λ�ã������жϴ�λ���Ƿ����
		for(int x = locationX-1; x >= 0; x--){
			if(board[x][locationY] == 0){
				sitesList.add(new Point(x, locationY));
			}
			else if(x > 0){
				do{
					x--;
				}while(x > 0 && board[x][locationY] == 0);
				if(board[x][locationY] == 0){
					break;
				}
				if(board[x][locationY]*camp < 0){
					sitesList.add(new Point(x, locationY));
				}
				break;
			}
		}
		for(int x = locationX+1; x <= 8; x++){
			if(board[x][locationY] == 0){
				sitesList.add(new Point(x, locationY));
			}
			else if(x < 8){
				do{
					x++;
				}while(x < 8 && board[x][locationY] == 0);
				if(board[x][locationY] == 0){
					break;
				}
				if(board[x][locationY]*camp < 0){
					sitesList.add(new Point(x, locationY));
				}
				break;
			}
		}
		for(int y = locationY-1; y >= 0; y--){
			if(board[locationX][y] == 0){
				sitesList.add(new Point(locationX, y));
			}
			else if(y > 0){
				do{
					y--;
				}while(y > 0 && board[locationX][y] == 0);
				if(board[locationX][y] == 0){
					break;
				}
				if(board[locationX][y]*camp < 0){
					sitesList.add(new Point(locationX, y));
				}
				break;
			}
		}
		for(int y = locationY+1; y <= 9; y++){
			if(board[locationX][y] == 0){
				sitesList.add(new Point(locationX, y));
			}
			else if(y < 9){
				do{
					y++;
				}while(y < 9 && board[locationX][y] == 0);
				if(board[locationX][y] == 0){
					break;
				}
				if(board[locationX][y]*camp < 0){
					sitesList.add(new Point(locationX, y));
				}
				break;
			}
		}
		return sitesList;
	}

	private static ArrayList<Point> calcuPawn(byte[][] board, Point location, byte camp) {
		ArrayList<Point> sitesList = new ArrayList<Point>();
		byte locationX = location.x;
		byte locationY = location.y;		//Ŀ��λ�ã������жϴ�λ���Ƿ����
		//������ڵз���Ӫ������������ƶ�
		if(locationY <= 4 && locationY >=0){
			if(locationX-1 >= 0){
				if(board[locationX-1][locationY] == 0
						|| board[locationX-1][locationY]*camp < 0){
					sitesList.add(new Point(locationX-1, locationY));
				}
			}
			if(locationX+1 <= 8){
				if(board[locationX+1][locationY] == 0
						|| board[locationX+1][locationY]*camp < 0){
					sitesList.add(new Point(locationX+1, locationY));
				}
			}
		}
		if(locationY-1 >= 0){
			if(board[locationX][locationY-1] == 0
					|| board[locationX][locationY-1]*camp < 0){
				sitesList.add(new Point(locationX, locationY-1));
			}
		}
		return sitesList;
	}

	private class NetInThread extends Thread {

		public void run() {
			while(isOnline) {
				try {
					switch(dis.readByte()) {
					case I_START:
						Player.camp = dis.readByte();
						Player.isTurn = Player.camp == 1;
						new CreateGameTask().run();
						break;
					case I_MOVE:
						new MoveTask(dis.readByte(), dis.readByte(),
								dis.readByte(), dis.readByte()).run();
						break;
					case I_WIN:
						new WinTask("��Ӯ�ˣ�").run();
						break;
					case I_GIVEUP:
						new WinTask("����Ͷ���ˣ�").run();
						break;
					case I_NOWIN:
						new AskNowinTask().run();
						break;
					case I_ALLOW_NOWIN:
						new NowinTask().run();
						break;
					case I_REFUSE_NOWIN:
						new MsgTask("���־ܾ�����").run();
						break;
					case I_CLOSE:
						new CloseTask().run();
						break;
					}
				} catch(IOException e) {

				}
			}
		}

	}

	public class MoveTask extends Task<Void> {
		private byte fx;
		private byte fy;
		private byte tx;
		private byte ty;

		public MoveTask(byte fx, byte fy, byte tx, byte ty) {
			super();
			this.fx = fx;
			this.fy = fy;
			this.tx = tx;
			this.ty = ty;
		}

		@Override
		protected Void call() throws Exception {
			return null;
		}

		@Override
		protected void succeeded() {
			Point fl = new Point(fx, fy);
			Point tl = new Point(tx, ty);
			board[tl.x][tl.y] = board[fl.x][fl.y];
			board[fl.x][fl.y] = 0;
			player.getBoardPane().moveTo(fl, tl);
			Player.isTurn = true;
		}

	}
	
	private class CloseTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			return null;
		}

		@Override
		protected void succeeded() {
			player.showMsg(new Toast(player.getGamePane(), "�����ѶϿ�����"));
			endGame();
		}
		
	}

	private class CreateGameTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			return null;
		}

		@Override
		protected void succeeded() {
			createGame();
		}

	}
	
	private class WinTask extends Task<Void> {
		private String msg;

		public WinTask(String msg) {
			this.msg = msg;
		}
		
		@Override
		protected Void call() throws Exception {
			return null;
		}

		@Override
		protected void succeeded() {
			win(msg);
		}
		
	}
	
	private class AskNowinTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			return null;
		}

		@Override
		protected void succeeded() {
			String s = "�����������";
			Button allow = new Button("ͬ��");
			Button refuse = new Button("�ܾ�");
			Toast toast = new Toast(s, allow, refuse);
			player.showMsg(toast);
			
			allow.setOnMouseClicked(e-> {
				sendAction(O_ALLOW_NOWIN);
				player.closeMsg();
				player.showMsg(new Toast(player.getGamePane(), "����"));
				endGame();
			});
			refuse.setOnMouseClicked(e-> {
				sendAction(O_REFUSE_NOWIN);
				player.closeMsg();
			});
		}
	}
	
	private class NowinTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			return null;
		}

		@Override
		protected void succeeded() {
			player.showMsg(new Toast(player.getGamePane(), "����"));
			endGame();
		}
		
	}
	
	private class MsgTask extends Task<Void> {
		private String msg;
		
		public MsgTask(String msg) {
			this.msg = msg;
		}

		@Override
		protected Void call() throws Exception {
			return null;
		}

		@Override
		protected void succeeded() {
			player.showMsg(new Toast(player.getGamePane(), msg));
		}
		
	}

	public static Piece findKing(byte[][] board) {
		for(int i = 3; i <= 5; i++) {
			for(int j = 7; j <= 9; j++) {
				if(board[i][j] == Piece.K*Player.camp) {
					return new Piece(Piece.K, Player.camp, new Point(i, j));
				}
			}
		}
		return null;
	}

	public static boolean isKingDanger(Point location, byte[][] board) {
		byte x = location.x;
		byte y = location.y;
		//������ ������
		//��
		for(int i = x-1; i >= 0 ; i--) {
			//�˴���û�����ӣ������ѭ��
			if(board[i][y] == 0) {
				continue;
			}
			//��һ�����ֵ�������Ϊ�з������򷵻���
			if(board[i][y] == -Piece.R*Player.camp) {
				return true;
			}
			i--;
			//�ڶ������ֵ�������Ϊ�з��ڣ��򷵻���
			while(i >= 0) {
				if(board[i][y] == -Piece.C*Player.camp) {
					return true;
				} else if(board[i][y] != 0) {
					break;
				}
				i--;
			}
			break;
		}
		//��
		for(int i = x+1; i < MAX_H ; i++) {
			//�˴���û�����ӣ������ѭ��
			if(board[i][y] == 0) {
				continue;
			}
			//��һ�����ֵ�������Ϊ�з������򷵻���
			if(board[i][y] == -Piece.R*Player.camp) {
				return true;
			}
			i++;
			//�ڶ������ֵ�������Ϊ�з��ڣ��򷵻���
			while(i < MAX_H) {
				if(board[i][y] == -Piece.C*Player.camp) {
					return true;
				} else if(board[i][y] != 0) {
					break;
				}
				i++;
			}
			break;
		}
		//��
		for(int i = y-1; i >= 0 ; i--) {
			//�˴���û�����ӣ������ѭ��
			if(board[x][i] == 0) {
				continue;
			}
			//��һ�����ֵ�������Ϊ�з������򷵻���
			if(board[x][i] == -Piece.R*Player.camp) {
				return true;
			}
			i--;
			//�ڶ������ֵ�������Ϊ�з��ڣ��򷵻���
			while(i >= 0) {
				if(board[x][i] == -Piece.C*Player.camp) {
					return true;
				} else if(board[x][i] != 0) {
					break;
				}
				i--;
			}
			break;
		}
		//��
		for(int i = y+1; i < MAX_V ; i++) {
			//�˴���û�����ӣ������ѭ��
			if(board[x][i] == 0) {
				continue;
			}
			//��һ�����ֵ�������Ϊ�з������򷵻���
			if(board[x][i] == -Piece.R*Player.camp) {
				return true;
			}
			i++;
			//�ڶ������ֵ�������Ϊ�з��ڣ��򷵻���
			while(i < MAX_V) {
				if(board[x][i] == -Piece.C*Player.camp) {
					return true;
				} else if(board[x][i] != 0) {
					break;
				}
				i++;
			}
			break;
		}
		//������������ ��
		if((x >= 0 && board[x-1][y] == -Piece.P*Player.camp)
				|| (x < MAX_H && board[x+1][y] == -Piece.P*Player.camp)
				|| (y >= 0 && board[x][y-1] == -Piece.P*Player.camp)) {
			return true;
		}
		//�˽��� ����8������λ�÷�Ϊ���ϡ����¡����ϡ����¸�����
		//����
		if(x-1 >= 0 && y-1 >= 0 && board[x-1][y-1] == 0) {
			if(x-2 >= 0 && board[x-2][y-1] == -Piece.H*Player.camp) {
				return true;
			}
			if(y-2 >= 0 && board[x-1][y-2] == -Piece.H*Player.camp) {
				return true;
			}
		}
		//����
		if(x-1 >= 0 && y+1 < MAX_V && board[x-1][y+1] == 0) {
			if(x-2 >= 0 && board[x-2][y+1] == -Piece.H*Player.camp) {
				return true;
			}
			if(y+2 < MAX_V && board[x-1][y+2] == -Piece.H*Player.camp) {
				return true;
			}
		}
		//����
		if(x+1 < MAX_H && y-1 >= 0 && board[x+1][y-1] == 0) {
			if(x+2 < MAX_H && board[x+2][y-1] == -Piece.H*Player.camp) {
				return true;
			}
			if(y-2 >= 0 && board[x+1][y-2] == -Piece.H*Player.camp) {
				return true;
			}
		}
		//����
		if(x+1 >= 0 && y+1 < MAX_V && board[x+1][y+1] == 0) {
			if(x+2 >= 0 && board[x+2][y+1] == -Piece.H*Player.camp) {
				return true;
			}
			if(y+2 < MAX_V && board[x+1][y+2] == -Piece.H*Player.camp) {
				return true;
			}
		}
		return false;
	}

	public static boolean tryMove(byte[][] pieces, Point fl, Point tl) {
		pieces[tl.x][tl.y] = pieces[fl.x][fl.y];
		pieces[fl.x][fl.y] = 0;
		return isKingDanger(findKing(pieces).getLocation(), pieces);
	}

	public void field() {
		if(mode == PVP) {
			sendAction(O_FIELD);
		}
		player.showMsg(new Toast(player.getGamePane(), "�����ˣ�"));
		endGame();
	}
	
	public void win(String msg) {
		player.showMsg(new Toast(player.getGamePane(), msg));
		endGame();
	}
	
	public void giveup() {
		String s = "�Ƿ����䣿";
		Button ok = new Button("ȷ��");
		Button cancle = new Button("ȡ��");
		Toast toast = new Toast(s, ok, cancle);
		player.showMsg(toast);
		
		ok.setOnMouseClicked(e-> {
			sendAction(O_GIVEUP);
			player.closeMsg();
			endGame();
		});
		cancle.setOnMouseClicked(e-> {
			player.closeMsg();
		});
	}
	
	public void askNowin() {
		String s = "�Ƿ���壿";
		Button ok = new Button("ȷ��");
		Button cancle = new Button("ȡ��");
		Toast toast = new Toast(s, ok, cancle);
		player.showMsg(toast);
		
		ok.setOnMouseClicked(e-> {
			sendAction(O_NOWIN);
			player.closeMsg();
			isPause = true;
		});
		cancle.setOnMouseClicked(e-> {
			player.closeMsg();
		});
	}
	
	public void fightComputer(byte camp) {
		mode = PVS;
		computer = new Computer((byte) (camp*-1), player);
		Player.camp = camp;
		createGame();
		Player.isTurn = true;
	}
	
	public void showMsg(String s) {
		player.showMsg(new Toast(player.getGamePane(), s));
	}
}
