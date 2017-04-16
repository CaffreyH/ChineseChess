package view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class GamePane extends Pane {
	private Image bgImage;
	private ImageView bgView;
	
	public GamePane() {
		super();
		initPane();
	}

	private void initPane() {
		bgImage = new Image("bgImage.jpg");
		bgView = new ImageView(bgImage);
		bgView.fitWidthProperty().bind(widthProperty());
		bgView.fitHeightProperty().bind(heightProperty());
		
		getChildren().add(bgView);
	}

}
