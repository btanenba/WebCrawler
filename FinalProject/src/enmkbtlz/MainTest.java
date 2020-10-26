package enmkbtlz;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainTest  extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Label label  =new Label("LETSSEE IF U CHANGE");
		TextField tb = new TextField("Enter");
		
		
		
		
		
		Button b = new Button("Click");
		
		HBox hbx = new HBox(label, tb,b);
		
		Scene scene = new Scene(hbx);
		
		primaryStage.setTitle("WE bETTER GET A 100");
		b.setOnAction(e -> {
			int number = Integer.parseInt(tb.getText());
			label.setText("hi" + number+5);
		});
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		
	}

}
