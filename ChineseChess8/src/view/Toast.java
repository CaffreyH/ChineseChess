package view;

import service.Service;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Toast extends StackPane {
	private Rectangle bgR;
	private VBox vBox;
	private Label label;
	private HBox hBox;
	
	public Toast(Pane parentPane, String text) {
		super();
		initPane();
		label.setText(text);
		Button okBtn = new Button("È·¶¨");
		hBox.getChildren().add(okBtn);
		okBtn.setOnMouseClicked(e-> {
			parentPane.getChildren().remove(Toast.this);
			Service.isPause = false;
		});
	}
	
	public Toast(String text, Button btn1, Button btn2) {
		super();
		initPane();
		label.setText(text);
		hBox.getChildren().addAll(btn1, btn2);
	}
	
	private void initPane() {
		setLayoutX(150);
		setLayoutY(175);
		setMaxWidth(150);
		setMaxHeight(100);
		
		bgR = new Rectangle(0, 0, 200, 150);
		bgR.setFill(Color.rgb(243, 225, 194, 0.8));
		bgR.setStroke(Color.GREY);
		bgR.setArcWidth(25);
		bgR.setArcHeight(25);
		vBox = new VBox(30);
		vBox.setAlignment(Pos.CENTER);
		label = new Label();
		label.setFont(Font.font("", FontWeight.BOLD, 20));
		hBox = new HBox(30);
		hBox.setAlignment(Pos.CENTER);
		
		vBox.getChildren().addAll(label, hBox);
		getChildren().addAll(bgR, vBox);
	}
}
