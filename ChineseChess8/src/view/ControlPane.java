package view;

import player.Player;
import service.Service;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ControlPane extends VBox {
	private NetPane netPane = new NetPane(this);
	private ComPane comPane = new ComPane(this);
	public static boolean isAnimOn = false;

	public ControlPane() {
		super(50);
		setLayoutX(525);
		setLayoutY(25);
		
		initPane();
	}

	private void initPane() {
		getChildren().addAll(netPane, comPane);
	}

	public NetPane getNetPane() {
		return netPane;
	}

	public ComPane getComPane() {
		return comPane;
	}
	
	public void chooseMode(int mode) {
		if(mode == Service.PVP) {
			if(!Service.isOnline) {
				if(!Player.service.connectServer()) {
					Player.service.showMsg("无法连接服务器");
					return ;
				}
			}
			isAnimOn = true;
			netPane.choose();
			comPane.disChoose();
		} else {
			isAnimOn = true;
			comPane.choose();
			netPane.disChoose();
			if(netPane.isReady) {
				netPane.cancleReady();
				Player.service.showMsg("已取消准备");
			}
		}
	}
}

class MyBtn extends Button {
	public MyBtn(String s) {
		super(s);
		setMinWidth(125);
		setMinHeight(40);
		setFont(Font.font(18));
	}
}
