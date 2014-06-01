package net.buddat.wplanner;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.JOptionPane;

import net.buddat.wplanner.settings.AppSettings;
import net.buddat.wplanner.settings.Settings;
import net.buddat.wplanner.ui.ButtonBuilder;
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

	private ToggleGroup mainToolbarButtonsGroup;

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

		rootScene.getStylesheets().add(WPlanner.class.getResource("/data/gui/gui.css").toExternalForm());
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
			ToggleButton pointerButton = ButtonBuilder.createToolbarButton("/data/gui/pointer.png", null, null);
			ToggleButton penButton = ButtonBuilder.createToolbarButton("/data/gui/pen.png", null, "test tooltip");
			
			mainToolbarButtonsGroup = new ToggleGroup();
			mainToolbarButtonsGroup.getToggles().addAll(pointerButton, penButton);

			toolBar.getItems().addAll(pointerButton, penButton);
			toolBar.autosize();
		}

		BorderPane tabBorderPane = new BorderPane();
		{
			TabPane tabPane = new TabPane();
			Tab homeTab = new Tab("Home");

			HBox homeContent = new HBox();
			homeContent.setAlignment(Pos.CENTER);
			homeContent.getStyleClass().add("tab-hbox");

			homeTab.setContent(homeContent);
			homeTab.setClosable(false);

			tabPane.getTabs().add(homeTab);

			tabBorderPane.prefHeightProperty().bind(s.heightProperty());
			tabBorderPane.prefWidthProperty().bind(s.widthProperty());
			tabBorderPane.setCenter(tabPane);
		}

		((VBox) s.getRoot()).getChildren().addAll(menuBar, toolBar, tabBorderPane);
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

		try {
			Handler handler = new FileHandler(AppSettings.LOG_FILE, Settings.getInt(Settings.CFG_LOG_ROTATE_KEY),
					Settings.getInt(Settings.CFG_LOG_ROTATE_COUNT_KEY), true);
			handler.setFormatter(new SimpleFormatter());
			Logger.getLogger("").addHandler(handler);
		} catch (SecurityException e1) {
			JOptionPane.showMessageDialog(null, "Unable to create log file. Running as administrator?");
			e1.printStackTrace();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Unable to write to log file.");
			e1.printStackTrace();
		}

		LOGGER.info("==== Starting " + AppSettings.PROGRAM_NAME + " v" + AppSettings.getVersionString() + "."
				+ AppSettings.VERSION_MINOR + " ====");

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
				if (!Updater.update(res, AppSettings.RESOURCE_URL, IOUtils.getUserDataDirectoryString()))
					JOptionPane.showMessageDialog(null, "Unable to download file: " + res
							+ "\r\nAttempting to start WPlanner anyway...");
		}

		launch();
	}

}
