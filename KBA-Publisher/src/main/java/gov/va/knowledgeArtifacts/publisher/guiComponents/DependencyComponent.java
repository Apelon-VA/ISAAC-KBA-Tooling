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

import gov.va.isaac.AppContext;
import gov.va.isaac.gui.util.Images;
import gov.va.isaac.interfaces.gui.CommonDialogsI;
import java.util.function.Consumer;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.apache.maven.pom._4_0.Dependency;

/**
 * {@link DependencyComponent}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class DependencyComponent
{

	Label groupIdLabel, artifactIdLabel, versionLabel, typeLabel, classifierLabel, dependencyTypeLabel;
	Button remove;
	TextField groupId, artifactId, version, type, classifier;
	ChoiceBox<String> dependencyType;
	Separator separator;
	RowConstraints[] rowConstraints;
	
	private Dependency dependency_;

	public DependencyComponent(Dependency dependency)
	{
		groupIdLabel = new Label("Group ID");
		groupId = new TextField();
		remove = new Button("", Images.MINUS.createImageView());
		remove.setTooltip(new Tooltip("Remove this Dependency"));
		artifactIdLabel = new Label("Artifact ID");
		artifactId = new TextField();
		versionLabel = new Label("Version");
		version = new TextField();
		typeLabel = new Label("Type");
		type = new TextField();
		classifierLabel = new Label("Classifier");
		classifier = new TextField();
		dependencyTypeLabel = new Label("Dependency Type");
		dependencyType = new ChoiceBox<>();
		dependencyType.getItems().add("Compile only (not needed when this artifact is used, but used to create this artifact");
		dependencyType.getItems().add("Runtime only (not needed to build this artifact, but needed to use this artifact");
		dependencyType.getItems().add("Compile and Runtime");
		dependencyType.setPrefWidth(Double.MAX_VALUE);
		dependencyType.getSelectionModel().select(2);

		separator = new Separator(Orientation.HORIZONTAL);
		separator.setMaxWidth(Double.MAX_VALUE);

		rowConstraints = new RowConstraints[7];
		for (int i = 0; i < rowConstraints.length; i++)
		{
			rowConstraints[i] = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
			rowConstraints[i].setFillHeight(false);
			rowConstraints[i].setVgrow(Priority.NEVER);
		}
		set(dependency);
	}
	
	public void addToGridPane(GridPane pane, int howManyCurrentlyExist, Consumer<DependencyComponent> uc)
	{
		int startingRow = howManyCurrentlyExist * 7;
		pane.add(groupIdLabel, 0, startingRow);
		pane.add(groupId, 1, startingRow);
		pane.add(remove, 2, startingRow++);
		pane.add(artifactIdLabel, 0, startingRow);
		pane.add(artifactId, 1, startingRow++, 2, 1);
		pane.add(versionLabel, 0, startingRow);
		pane.add(version, 1, startingRow++, 2, 1);
		pane.add(typeLabel, 0, startingRow);
		pane.add(type, 1, startingRow++, 2, 1);
		pane.add(classifierLabel, 0, startingRow);
		pane.add(classifier, 1, startingRow++, 2, 1);
		pane.add(dependencyTypeLabel, 0, startingRow);
		pane.add(dependencyType, 1, startingRow++, 2, 1);
		pane.add(separator, 0, startingRow++, 3, 1);
		
		for (RowConstraints rc : rowConstraints)
		{
			pane.getRowConstraints().add(rc);
		}
		
		remove.setOnAction((action) ->  {uc.accept(this);});
	}
	
	public void removeFromGridPane(GridPane pane)
	{
		pane.getChildren().remove(groupIdLabel);
		pane.getChildren().remove(groupId);
		pane.getChildren().remove(remove);
		pane.getChildren().remove(artifactIdLabel);
		pane.getChildren().remove(artifactId);
		pane.getChildren().remove(versionLabel);
		pane.getChildren().remove(version);
		pane.getChildren().remove(type);
		pane.getChildren().remove(typeLabel);
		pane.getChildren().remove(classifier);
		pane.getChildren().remove(classifierLabel);
		pane.getChildren().remove(dependencyType);
		pane.getChildren().remove(dependencyTypeLabel);
		pane.getChildren().remove(separator);
		
		for (RowConstraints rc : rowConstraints)
		{
			pane.getRowConstraints().remove(rc);
		}
	}
	
	public Dependency getDependency()
	{
		if (groupId.getText().length() == 0 && artifactId.getText().length() == 0 && version.getText().length() == 0 && type.getText().length() == 0
				&& classifier.getText().length() == 0)
		{
			return null;
		}
		
		if (dependency_ == null)
		{
			dependency_ = new Dependency();
		}
		
		dependency_.setGroupId(groupId.getText());
		dependency_.setArtifactId(artifactId.getText());
		dependency_.setVersion(version.getText());
		dependency_.setType(type.getText());
		dependency_.setClassifier(classifier.getText());
		
		if (dependencyType.getValue().startsWith("Compile only"))
		{
			dependency_.setScope("compile");
			dependency_.setOptional(true);
		}
		else if (dependencyType.getValue().startsWith("Runtime only"))
		{
			dependency_.setScope("runtime");
			dependency_.setOptional(false);
		}
		else
		{
			dependency_.setScope("compile");
			dependency_.setOptional(false);
		}
		return dependency_;
	}
	
	private void set(Dependency dependency)
	{
		dependency_ = dependency;
		if (dependency_ == null)
		{
			return;
		}
		
		groupId.setText(dependency_.getGroupId());
		artifactId.setText(dependency_.getArtifactId());
		version.setText(dependency_.getVersion());
		type.setText(dependency_.getType());
		classifier.setText(dependency_.getClassifier());
		
		if (dependency_.getScope().equalsIgnoreCase("runtime"))
		{
			dependencyType.getSelectionModel().select(1);
		}
		else if (dependency_.getScope().equalsIgnoreCase("compile"))
		{
			if (dependency_.isOptional())
			{
				dependencyType.getSelectionModel().select(0);
			}
			else
			{
				dependencyType.getSelectionModel().select(2);
			}
		}
		else
		{
			if (dependency_.getScope().length() > 0)
			{
				AppContext.getServiceLocator().getService(CommonDialogsI.class).showErrorDialog("Unsupported Dependency Scope", "Unsupported Dependency Scope", 
					"The dependency scope " + dependency_.getScope() + " is unsupported, and will be overwritten if you save");
			}
		}
	}
}
