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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import org.apache.maven.pom._4_0.Contributor;
import org.apache.maven.pom._4_0.Developer;

/**
 * {@link UserComponent}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class UserComponent
{
	Label nameLabel, emailLabel, urlLabel, timezoneLabel, organizationLabel, organizationURLLabel;
	Button remove;
	TextField name, email, url, timezone, organization, organizationURL;
	Separator separator;
	RowConstraints[] rowConstraints;
	
	private Contributor contributor_;
	private Developer developer_;

	public UserComponent(Contributor contributor)
	{
		this();
		set(contributor);
	}
	
	public UserComponent(Developer developer)
	{
		this();
		set(developer);
	}
	
	private UserComponent()
	{
		nameLabel = new Label("Name");
		name = new TextField();
		remove = new Button("", Images.MINUS.createImageView());
		remove.setTooltip(new Tooltip("Remove this User"));
		emailLabel = new Label("Email Address");
		email = new TextField();
		urlLabel = new Label("URL");
		url = new TextField();
		timezoneLabel = new Label("Timezone");
		timezone = new TextField();
		organizationLabel = new Label("Organization");
		organization = new TextField();
		organizationURLLabel = new Label("Organization URL");
		organizationURL= new TextField();

		separator = new Separator(Orientation.HORIZONTAL);
		separator.setMaxWidth(Double.MAX_VALUE);

		rowConstraints = new RowConstraints[7];
		for (int i = 0; i < rowConstraints.length; i++)
		{
			rowConstraints[i] = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE);
			rowConstraints[i].setFillHeight(false);
			rowConstraints[i].setVgrow(Priority.NEVER);
		}
	}
	
	public void addToGridPane(GridPane pane, int howManyCurrentlyExist, Consumer<UserComponent> uc)
	{
		int startingRow = howManyCurrentlyExist * 7;
		pane.add(nameLabel, 0, startingRow);
		pane.add(name, 1, startingRow);
		pane.add(remove, 2, startingRow++);
		pane.add(emailLabel, 0, startingRow);
		pane.add(email, 1, startingRow++, 2, 1);
		pane.add(urlLabel, 0, startingRow);
		pane.add(url, 1, startingRow++, 2, 1);
		pane.add(timezoneLabel, 0, startingRow);
		pane.add(timezone, 1, startingRow++, 2, 1);
		pane.add(organizationLabel, 0, startingRow);
		pane.add(organization, 1, startingRow++, 2, 1);
		pane.add(organizationURLLabel, 0, startingRow);
		pane.add(organizationURL, 1, startingRow++, 2, 1);
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
		pane.getChildren().remove(emailLabel);
		pane.getChildren().remove(email);
		pane.getChildren().remove(urlLabel);
		pane.getChildren().remove(url);
		pane.getChildren().remove(timezone);
		pane.getChildren().remove(timezoneLabel);
		pane.getChildren().remove(organizationLabel);
		pane.getChildren().remove(organization);
		pane.getChildren().remove(organizationURLLabel);
		pane.getChildren().remove(organizationURL);
		pane.getChildren().remove(separator);
		
		for (RowConstraints rc : rowConstraints)
		{
			pane.getRowConstraints().remove(rc);
		}
	}
	
	public Developer getDeveloper()
	{
		if (name.getText().length() == 0 && email.getText().length() == 0 && url.getText().length() == 0 && timezone.getText().length() == 0 
				&& organization.getText().length() == 0 && organizationURL.getText().length() == 0)
		{
			return null;
		}
		
		if (developer_ == null)
		{
			developer_ = new Developer();
		}
		
		developer_.setName(name.getText());
		developer_.setEmail(email.getText());
		developer_.setUrl(url.getText());
		developer_.setTimezone(timezone.getText());
		developer_.setOrganization(organization.getText());
		developer_.setOrganizationUrl(organizationURL.getText());
		return developer_;
	}
	
	public Contributor getContributor()
	{
		if (name.getText().length() == 0 && email.getText().length() == 0 && url.getText().length() == 0 && timezone.getText().length() == 0 
				&& organization.getText().length() == 0 && organizationURL.getText().length() == 0)
		{
			return null;
		}
		if (contributor_ == null)
		{
			contributor_ = new Contributor();
		}
		
		contributor_.setName(name.getText());
		contributor_.setEmail(email.getText());
		contributor_.setUrl(url.getText());
		contributor_.setTimezone(timezone.getText());
		contributor_.setOrganization(organization.getText());
		contributor_.setOrganizationUrl(organizationURL.getText());
		return contributor_;
	}
	
	private void set(Contributor contributor)
	{
		contributor_ = contributor;
		if (contributor_ == null)
		{
			return;
		}
		name.setText(contributor_.getName());
		email.setText(contributor_.getEmail());
		url.setText(contributor_.getUrl());
		timezone.setText(contributor_.getTimezone());
		organization.setText(contributor_.getOrganization());
		organizationURL.setText(contributor_.getOrganizationUrl());
	}
	
	private void set(Developer developer)
	{
		developer_ = developer;
		if (developer_ == null)
		{
			return;
		}
		name.setText(developer_.getName());
		email.setText(developer_.getEmail());
		url.setText(developer_.getUrl());
		timezone.setText(developer_.getTimezone());
		organization.setText(developer_.getOrganization());
		organizationURL.setText(developer_.getOrganizationUrl());
	}
}
