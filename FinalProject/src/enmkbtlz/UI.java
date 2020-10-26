package enmkbtlz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class UI extends Application {
	TextField tf;
	Label promptLabel;
	Label resultLabel;
	RadioButton generalButton;
	RadioButton zipButton;
	RadioButton phoneButton;
	static Shared shared = new Shared();

	String result;
	List<String> listInfo;

	public static void main(String[] args) {

		// Shared shared = new Shared();

		connectToDB(shared);

		Scraper scraper = new Scraper(shared);
		ScraperTwo scraperTwo = new ScraperTwo(shared);

		Crawler crawler = new Crawler(shared, scraper, scraperTwo);
		crawler.start();

		launch(args);

		try {
			scraper.join();
			scraperTwo.join();
			crawler.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void connectToDB(Shared shared) {
		try {
			String dbUrl = "jdbc:sqlserver://localhost;" + "instance=DESKTOP-1NUS1PS\\SQLEXPRESS;"
					+ "databaseName=DataStructuresProject;" + "integratedSecurity=true";
			Connection conn = DriverManager.getConnection(dbUrl);
			shared.stmnt = conn.createStatement();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Font font = new Font("Arial", 25);
		Font font15 = new Font("Arial", 15);
		Color color = Color.web("#FF0000");

		promptLabel = new Label("Enter General, Zip Code Or Area Code: ");
		promptLabel.setTextFill(color);
		promptLabel.setFont(font);
		promptLabel.setStyle("-fx-font-weight: bold");

		tf = new TextField();

		generalButton = new RadioButton("General Info");
		generalButton.setFont(font15);
		generalButton.setTextFill(color);

		zipButton = new RadioButton("Zip Codes");
		zipButton.setFont(font15);
		zipButton.setTextFill(color);

		phoneButton = new RadioButton("Phone Numbers");
		phoneButton.setFont(font15);
		phoneButton.setTextFill(color);

		generalButton.setSelected(true);

		ToggleGroup tg = new ToggleGroup();

		generalButton.setToggleGroup(tg);
		phoneButton.setToggleGroup(tg);
		zipButton.setToggleGroup(tg);

		Button find = new Button("Find");

		find.setMaxSize(100, 200);

		find.setOnAction(event -> {
			StringBuilder sb = new StringBuilder();

			if (generalButton.isSelected()) {
				result = generalInfo();
				resultLabel.setText(String.format("%s", result));

			}
			if (zipButton.isSelected()) {

				listInfo = addressInfo(tf.getText());

				for (String address : listInfo)
					sb.append(address + " \n ");

				if (sb.length() != 0) {
					resultLabel.setText(String.format("%s", sb));
				} else {
					resultLabel.setText("There are no Addresses with the Zip Code " + tf.getText());
				}

			}
			if (phoneButton.isSelected()) {
				listInfo = phoneNumberInfo(tf.getText());

				for (String phoneNumber : listInfo)
					sb.append(phoneNumber + " \n ");

				if (sb.length() != 0) {
					resultLabel.setText(String.format("%s", sb));
					// resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
				} else

				{
					resultLabel.setText("There are no Phone Numbers with the Area Code " + tf.getText());
					// resultLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");

				}

			}
		}

		);

		resultLabel = new Label();

		resultLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16px;");

		Image image = new Image("https://wallpapercave.com/wp/kzMSXyW.jpg");

		HBox hbox = new HBox(60, promptLabel, tf);

		HBox radioBox = new HBox(120, generalButton, zipButton, phoneButton);

		VBox vbox = new VBox(60, hbox, radioBox, find, resultLabel);

		vbox.setAlignment(Pos.CENTER);

		vbox.setPadding(new Insets(60));

		BackgroundImage backgroundimage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);

		Background background = new Background(backgroundimage);

		vbox.setBackground(background);
		Scene scene = new Scene(vbox, 1000, 800);

		primaryStage.setScene(scene);

		primaryStage.setTitle("Final Project");

		primaryStage.show();

	}

	public List<String> addressInfo(String zipCode) {

		if (zipCode.length() != 5) {
			List<String> list = new ArrayList<>();
			list.add("Zip Code not valid");
			return list;
		}

		return shared.addresses.stream().filter(z -> z.contains(zipCode)).collect(Collectors.toList());

	}

	public List<String> phoneNumberInfo(String areaCode) {

		if (areaCode.length() != 3) {
			List<String> list = new ArrayList<>();
			list.add("Area Code not valid");
			return list;
		}

		return shared.phoneNumbers.stream().filter(a -> a.startsWith(areaCode)).collect(Collectors.toList());

	}

	public String generalInfo() {
		StringBuilder sb = new StringBuilder();

		sb.append("Current Information: \n");

		sb.append("Current Page: " + shared.counter + "\n");

		if (shared.currentUrl != null)
			sb.append("Current url: " + shared.currentUrl + " \n");
		else
			sb.append("No current Url \n");

		if (shared.linkQueue.size() > 0)
			sb.append("There are " + shared.linkQueue.size() + " links left in the queue \n");
		else
			sb.append("There are no links in the link queue \n");

		if (shared.emailAddresses.size() > 0)
			sb.append("Email Address " + shared.emailAddresses.get(shared.emailAddresses.size() - 1) + " \n");
		else
			sb.append("There are no current email addresses \n");

		if (shared.externalLinks.size() > 0)
			sb.append("External link " + shared.externalLinks.get(shared.externalLinks.size() - 1) + " \n");
		else
			sb.append("There are no current external links \n");

		if (shared.dates.size() > 0)
			sb.append("Date " + shared.dates.get(shared.dates.size() - 1) + " \n");
		else
			sb.append("There are no current dates \n");

		if (shared.addresses.size() > 0)
			sb.append("Address " + shared.addresses.get(shared.addresses.size() - 1) + " \n");
		else
			sb.append("There are no current addresses \n");
		if (shared.phoneNumbers.size() > 0)
			sb.append("Phone number " + shared.phoneNumbers.get(shared.phoneNumbers.size() - 1) + " \n");
		else
			sb.append("There are no current phone numbers \n");

		return sb.toString();

	}

}
