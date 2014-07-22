/**
 * Copyright Notice
 *
 * This is a work of the U.S. Government and is not subject to copyright
 * protection in the United States. Foreign copyrights may apply.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.va.knowledgeArtifacts.publisher.guiComponents;

import gov.va.isaac.gui.util.Images;
import java.util.function.Consumer;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.apache.maven.pom._4_0.License;

/**
 * {@link LicenseComponent}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class LicenseComponent
{

	Label nameLabel, urlLabel, commentsLabel;
	Button remove;
	TextField name, url;
	TextArea comments;
	Separator separator;
	RowConstraints[] rowConstraints;
	private License license_;

	public LicenseComponent(License license)
	{
		nameLabel = new Label("License Name");
		name = new TextField();
		remove = new Button("", Images.MINUS.createImageView());
		remove.setTooltip(new Tooltip("Remove this license"));
		urlLabel = new Label("License URL");
		url = new TextField();
		commentsLabel = new Label("License Comments");
		comments = new TextArea();
		comments.setWrapText(true);
		comments.setPrefHeight(100);
		separator = new Separator(Orientation.HORIZONTAL);
		separator.setMaxWidth(Double.MAX_VALUE);

		rowConstraints = new RowConstraints[4];

		for (int i = 0; i < rowConstraints.length; i++)
		{
			rowConstraints[i] = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
			rowConstraints[i].setFillHeight(false);
			rowConstraints[i].setVgrow(Priority.NEVER);
		}
		set(license);
	}
	
	public void addToGridPane(GridPane pane, int howManyCurrentlyExist, Consumer<LicenseComponent> uc)
	{
		int startingRow = howManyCurrentlyExist * 4;
		pane.add(nameLabel, 0, startingRow);
		pane.add(name, 1, startingRow);
		pane.add(remove, 2, startingRow++);
		pane.add(urlLabel, 0, startingRow);
		pane.add(url, 1, startingRow++, 2, 1);
		pane.add(commentsLabel, 0, startingRow);
		pane.add(comments, 1, startingRow++, 2, 1);
		pane.add(separator, 0, startingRow++, 3, 1);
		
		for (RowConstraints rc : rowConstraints)
		{
			pane.getRowConstraints().add(rc);
		}
		
		remove.setOnAction((action) ->  {uc.accept(this);});
	}
	
	public void removeFromGridPane(GridPane pane)
	{
		pane.getChildren().remove(nameLabel);
		pane.getChildren().remove(name);
		pane.getChildren().remove(remove);
		pane.getChildren().remove(urlLabel);
		pane.getChildren().remove(url);
		pane.getChildren().remove(commentsLabel);
		pane.getChildren().remove(comments);
		pane.getChildren().remove(separator);
		
		for (RowConstraints rc : rowConstraints)
		{
			pane.getRowConstraints().remove(rc);
		}
	}
	
	public License getLicense()
	{
		if (comments.getText().length() == 0 && name.getText().length() == 0 && url.getText().length() == 0)
		{
			return null;
		}
		
		if (license_ == null)
		{
			license_ = new License();
		}
		license_.setComments(comments.getText());
		license_.setDistribution("repo");
		license_.setName(name.getText());
		license_.setUrl(url.getText());
		return license_;
	}
	
	private void set(License license)
	{
		license_ = license;
		if (license_ == null)
		{
			return;
		}
		comments.setText(license_.getComments());
		name.setText(license_.getName());
		url.setText(license_.getUrl());
	}
}
