package net.buddat.wplanner.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ButtonBuilder {

	public static ToggleButton createToolbarToggleButton(String imageFile, EventHandler<ActionEvent> clickAction,
			String tooltip) {
		ImageView imageView = new ImageView(new Image(imageFile, 24, 24, true, true));

		final ToggleButton toolbarButton = new ToggleButton(null, imageView);
		if (clickAction != null)
			toolbarButton.setOnAction(clickAction);

		toolbarButton.getStyleClass().add("tool-bar-btn");
		toolbarButton.setScaleX(0.8);
		toolbarButton.setScaleY(0.8);

		if (tooltip != null)
			toolbarButton.setTooltip(new Tooltip(tooltip));

		return toolbarButton;
	}

	public static Button createToolbarButton(String imageFile, EventHandler<ActionEvent> clickAction, String tooltip) {
		ImageView imageView = new ImageView(new Image(imageFile, 24, 24, true, true));

		final Button toolbarButton = new Button(null, imageView);
		if (clickAction != null)
			toolbarButton.setOnAction(clickAction);

		toolbarButton.getStyleClass().add("tool-bar-btn");
		toolbarButton.setScaleX(0.8);
		toolbarButton.setScaleY(0.8);

		if (tooltip != null)
			toolbarButton.setTooltip(new Tooltip(tooltip));

		return toolbarButton;
	}
}
