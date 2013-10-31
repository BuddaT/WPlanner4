package net.buddat.wplanner;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.JOptionPane;

import appinstance.AppLock;

public class WPlanner extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO: Start polling argument file from multiple instances.

		Group rootGroup = new Group();
		Scene rootScene = new Scene(rootGroup, 800, 600);

		primaryStage.setScene(rootScene);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent w) {
				// TODO: Prompt to save, clean up etc.
				System.exit(0);
			}
			
		});
		primaryStage.show();
	}

	public static void main(String[] args) {
		if (!AppLock.setLock("WPlanner")) {
			// Another instance is already running.
			// TODO: Pass any arguments to first instance via argument file.
			if (args.length > 0) {

			} else {
				JOptionPane.showMessageDialog(null,
						"Application is already running. Exiting.");
				System.exit(0);
			}
		} else {
			launch();
        }
    }

}
