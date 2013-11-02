package net.buddat.wplanner;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.JOptionPane;

import net.buddat.wplanner.settings.AppSettings;
import net.buddat.wplanner.settings.Settings;
import appinstance.AppLock;
import appinstance.FileListener;
import appinstance.FileListenerException;

public class WPlanner extends Application {

	private final static Logger LOGGER = Logger.getLogger(WPlanner.class
			.getName());

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group rootGroup = new Group();
		Scene rootScene = new Scene(rootGroup, 800, 600);

		primaryStage.setScene(rootScene);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent w) {
				// TODO: Prompt to save, clean up etc.
				Settings.saveSettings();
				System.exit(0);
			}
			
		});
		primaryStage.show();
	}

	public static void main(String[] args) {
		Settings.loadSettings();

		if (Boolean.parseBoolean(Settings
				.getSetting(Settings.CFG_SINGLE_INSTANCE_KEY)) == true) {
			if (!AppLock.setLock(AppSettings.PROGRAM_NAME)) {
				// Another instance is already running.
				if (args.length > 0) {
					// TODO: Pass any arguments to first instance via argument file.
				} else {
					JOptionPane.showMessageDialog(null,
							"Application is already running. Exiting.");
					System.exit(0);
				}
			} else {
				try {
					new FileListener(AppSettings.PROGRAM_NAME).listen();
				} catch (FileListenerException e) {
					LOGGER.log(
							Level.WARNING,
							"Unable to listen to file for additional maps to open. Disabling single-instance.");
					Settings.setSetting(Settings.CFG_SINGLE_INSTANCE_KEY,
							"false", true);
				}

				init(args);
			}
		} else {
			init(args);
		}
    }

	private static void init(String[] args) {
		// TODO: Auto-update resources if set to
		// TODO: Load map if argument exists.
		launch();
	}

}
