package net.buddat.wplanner;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import net.buddat.wplanner.settings.AppSettings;
import net.buddat.wplanner.settings.Settings;
import net.buddat.wplanner.util.IOUtils;
import net.buddat.wplanner.util.Updater;
import appinstance.AppLock;
import appinstance.FileListener;
import appinstance.FileListenerException;

public class WPlanner extends Application {

	private final static Logger LOGGER = Logger.getLogger(WPlanner.class
			.getName());

	private Stage primaryStage;
	private Scene rootScene;

	@Override
	public void start(Stage _primaryStage) throws Exception {
		this.primaryStage = _primaryStage;

		rootScene = new Scene(new VBox(),
				Settings.getInt(Settings.CFG_WINDOW_WIDTH_KEY),
				Settings.getInt(Settings.CFG_WINDOW_HEIGHT_KEY));

		if (Settings.getInt(Settings.CFG_WINDOW_POSX_KEY) != 0
				&& Settings.getInt(Settings.CFG_WINDOW_POSY_KEY) != 0) {
			primaryStage.setX(Settings
					.getInt(Settings.CFG_WINDOW_POSX_KEY));
			primaryStage.setY(Settings
					.getInt(Settings.CFG_WINDOW_POSY_KEY));
		}

		primaryStage.setTitle(AppSettings.PROGRAM_NAME + " - v"
				+ AppSettings.getVersionString());
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent w) {
				close();
			}
			
		});

		setPrimaryScene(rootScene);

		primaryStage.setScene(rootScene);
		primaryStage.show();

		// TODO: Load map if argument exists.
	}

	private void setPrimaryScene(Scene s) {
		MenuBar menuBar = new MenuBar();
		{
			Menu menuFile = new Menu("File");
			Menu menuEdit = new Menu("Edit");
			Menu menuView = new Menu("View");

			MenuItem quit = new MenuItem("Exit");
			quit.setAccelerator(KeyCombination.keyCombination("ALT+F4"));
			quit.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent t) {
					close();
				}

			});
			menuFile.getItems().addAll(new SeparatorMenuItem(), quit);

			menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
		}
		
		ToolBar toolBar = new ToolBar();
		{
			try {
				BufferedImage finalImg = ImageIO.read(getClass()
						.getResourceAsStream("/data/gui/pointer.png"));
				java.awt.Image toUse = finalImg.getScaledInstance(16, 16, BufferedImage.SCALE_SMOOTH);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ImageView testView = new ImageView(new Image(
					"/data/gui/pointer.png", 24, 24, true, true));

			ToggleButton testButton = new ToggleButton(null, testView);
			testButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					close();
				}

			});

			testButton
					.setStyle("-fx-padding: 0 0 0 0; -fx-outer-border: white;"
							+ " -fx-background-color: -fx-outer-border, -fx-body-color;"
							+ " -fx-background-insets: 0, 0; -fx-background-radius: 0px, 0px;");
			testButton.setScaleX(0.75);
			testButton.setScaleY(0.75);
			toolBar.setStyle("-fx-padding: 0 0 0 0; -fx-background-color: -fx-outer-border, -fx-body-color;");

			toolBar.getItems().add(testButton);
			toolBar.autosize();
		}

		((VBox) s.getRoot()).getChildren().addAll(menuBar, toolBar);
	}

	private void close() {
		// TODO: Prompt to save, clean up etc
		Settings.setSetting(Settings.CFG_WINDOW_POSX_KEY, ""
				+ (int) primaryStage.getX(), false);
		Settings.setSetting(Settings.CFG_WINDOW_POSY_KEY, ""
				+ (int) primaryStage.getY(), false);
		Settings.setSetting(Settings.CFG_WINDOW_WIDTH_KEY, ""
				+ (int) rootScene.getWidth(), false);
		Settings.setSetting(Settings.CFG_WINDOW_HEIGHT_KEY, ""
				+ (int) rootScene.getHeight(), false);

		Settings.saveSettings();
		System.exit(0);
	}

	public static void main(String[] args) {
		Settings.loadSettings();

		if (Settings.getBoolean(Settings.CFG_SINGLE_INSTANCE_KEY) == true) {
			if (!AppLock.setLock(AppSettings.PROGRAM_NAME)) {
				// Another instance is already running.
				if (args.length > 0) {
					// TODO: Pass any arguments to first instance via argument file.
				} else {
					JOptionPane.showMessageDialog(null,
							"Application is already running. Exiting.");
				}

				System.exit(0);
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
		if (Settings.getBoolean(Settings.CFG_AUTOUPDATE_KEY) == true) {
			for (String res : AppSettings.RESOURCE_LIST)
				Updater.update(res, AppSettings.RESOURCE_URL,
					IOUtils.getUserDataDirectoryString());
		}

		launch();
	}

}
