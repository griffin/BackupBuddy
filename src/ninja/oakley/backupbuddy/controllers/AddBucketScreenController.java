package ninja.oakley.backupbuddy.controllers;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ninja.oakley.backupbuddy.BackupBuddy;
import ninja.oakley.backupbuddy.BucketManager;
import ninja.oakley.backupbuddy.RefreshRunnable;

public class AddBucketScreenController implements Initializable {

	private static final Logger logger = LogManager.getLogger(AddBucketScreenController.class);
	private BackupBuddy instance;

	private Scene scene;

	@FXML
	private TextField bucketNameField;

	@FXML
	private ChoiceBox<String> typeChoiceBox;

	@FXML
	private ChoiceBox<String> regionChoiceBox;

	public AddBucketScreenController(BackupBuddy instance){
		this.instance = instance;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> types = FXCollections.observableArrayList();
		ObservableList<String> regions = FXCollections.observableArrayList();

		types.addAll(BucketClass.getStrings());
		regions.addAll(BucketLocation.getStrings());

		typeChoiceBox.setItems(types);
		regionChoiceBox.setItems(regions);

	}

	@FXML
	public void onConfirm(){
		BucketManager manager = instance.getBaseController().getCurrentBucketManager();
		String name = bucketNameField.getText();
		String region = regionChoiceBox.getValue();
		String type = typeChoiceBox.getValue();
		
		if(manager == null){
			logger.error("No manager selected.");
			return;
		}
		
		if(name.isEmpty()){
			logger.error("No name entered.");
			return;
		}
		
		if(region.isEmpty()){
			logger.error("No region selected.");
			return;
		}
		
		if(type.isEmpty()){
			logger.error("No type selected.");
			return;
		}
		
		try {
			manager.createBucket(name, BucketClass.valueOf(type), BucketLocation.valueOf(region));
		} catch (IOException e) {
			logger.error(e.getMessage());
			return;
		} catch (GeneralSecurityException e) {
			logger.error(e.getMessage());
			return;
		}
		
		closeWindow();
		new Thread(new RefreshRunnable(instance)).start();;
	}

	public void openWindow(){
		if (scene == null) scene = new Scene(instance.addBucketPane);

		Stage stage = instance.getSecondaryStage();
		stage.setScene(scene);
		stage.show();
	}

	public void closeWindow(){
		Stage stage = instance.getSecondaryStage();
		stage.setScene(null);
		stage.hide();

		bucketNameField.clear();
		typeChoiceBox.setValue("");
		regionChoiceBox.setValue("");
	}
	
}
