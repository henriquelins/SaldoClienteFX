package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Strings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SobreFormController implements Initializable {

	@FXML
	private ImageView imageView;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	
		imageView.setImage(new Image(getClass().getResourceAsStream(Strings.getIcone())));
		
	}


}
