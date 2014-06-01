package net.buddat.wplanner.ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import net.buddat.wplanner.WPlanner;
import net.buddat.wplanner.settings.Settings;

public class Canvas2D extends Canvas {

	private final WPlanner mainApp;
	private final GraphicsContext gc;

	private boolean rendering = false;
	
	private int cameraXOffset = 0, cameraYOffset = 0;

	public Canvas2D(WPlanner app, double width, double height) {
		super(width, height);
		mainApp = app;
		gc = getGraphicsContext2D();
	}

	public void updateCanvas() {
		gc.setFill(Color.LIGHTBLUE);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());

		for (int i = 0; i <= this.getWidth() / 32; i++)
			for (int j = 0; j <= this.getHeight() / 32; j++) {
				gc.setStroke(Color.BLACK);
				gc.strokeRect(i * 32, j * 32, 32, 32);
			}
	}

	public WPlanner getMainApp() {
		return mainApp;
	}

	public int getCameraXOffset() {
		return cameraXOffset;
	}

	public void setCameraXOffset(int cameraXOffset) {
		this.cameraXOffset = cameraXOffset;
	}

	public int getCameraYOffset() {
		return cameraYOffset;
	}

	public void setCameraYOffset(int cameraYOffset) {
		this.cameraYOffset = cameraYOffset;
	}

	public void setRendering(boolean rendering) {
		this.rendering = rendering;
	}

	public boolean isRendering() {
		return rendering;
	}

	public static Tab buildCanvasTab(WPlanner app, TabPane parent, String name) {
		final Tab newTab = new Tab(name);

		HBox tabContent = new HBox();
		final Canvas2D tabCanvas = new Canvas2D(app, parent.getWidth(), parent.getHeight());

		// TODO: Change width and height of canvas to not include the area
		// where tabs are.
		tabCanvas.widthProperty().bind(parent.widthProperty());
		tabCanvas.heightProperty().bind(parent.heightProperty());

		newTab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event arg0) {
				if (newTab.isSelected())
					tabCanvas.setRendering(true);
				else
					tabCanvas.setRendering(false);
			}
		});

		Duration oneFrameAmt = Duration.millis(1000 / Settings.getInt(Settings.CFG_CANVAS_FPS_KEY));
		KeyFrame oneFrame = new KeyFrame(oneFrameAmt, new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (tabCanvas.isRendering())
					tabCanvas.updateCanvas();
			}
		});

		TimelineBuilder.create().cycleCount(Animation.INDEFINITE).keyFrames(oneFrame).build().play();

		tabContent.getChildren().add(tabCanvas);
		tabContent.setAlignment(Pos.CENTER);
		tabContent.getStyleClass().add("tab-hbox");

		newTab.setContent(tabContent);

		return newTab;
	}
}
