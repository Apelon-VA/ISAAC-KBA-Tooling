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
package gov.va.knowledgeArtifacts.publisher;

import gov.va.isaac.AppContext;
import gov.va.isaac.gui.util.ErrorMarkerUtils;
import gov.va.isaac.interfaces.gui.CommonDialogsI;
import gov.va.isaac.util.ValidBooleanBinding;
import gov.va.knowledgeArtifacts.publisher.publish.Publish;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.apache.maven.pom._4_0.Model;

/**
 * {@link PublishDialogController}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class PublishDialogController
{

	@FXML private ResourceBundle resources;
	@FXML private URL location;
	@FXML private PasswordField password;
	@FXML private Button publishButton;
	@FXML private ProgressBar progressBar;
	@FXML private TextField url;
	@FXML private TextField username;
	@FXML private TextArea status;
	
	private Model model_;
	private String classifier_;
	private File projectFolder_;
	private List<File> dataFiles_;
	private ValidBooleanBinding urlValid;

	@FXML
	void initialize()
	{
		assert password != null : "fx:id=\"password\" was not injected: check your FXML file 'PublishDialog.fxml'.";
		assert publishButton != null : "fx:id=\"publishButton\" was not injected: check your FXML file 'PublishDialog.fxml'.";
		assert progressBar != null : "fx:id=\"progressBar\" was not injected: check your FXML file 'PublishDialog.fxml'.";
		assert url != null : "fx:id=\"url\" was not injected: check your FXML file 'PublishDialog.fxml'.";
		assert username != null : "fx:id=\"username\" was not injected: check your FXML file 'PublishDialog.fxml'.";
		assert status != null : "fx:id=\"status\" was not injected: check your FXML file 'PublishDialog.fxml'.";
		
		url.setText("https://va.maestrodev.com/archiva/repository/data-files/");
		
		urlValid = new ValidBooleanBinding()
		{
			{
				setComputeOnInvalidate(true);
				bind(url.textProperty());
			}
			@Override
			protected boolean computeValue()
			{
				if (url.getText().length() > 0)
				{
					clearInvalidReason();
					return true;
				}
				else
				{
					setInvalidReason("The URL is required");
					return false;
				}
			}
		};
		
		StackPane sp = ErrorMarkerUtils.swapGridPaneComponents(url, new StackPane(), (GridPane)url.getParent());
		ErrorMarkerUtils.setupErrorMarker(url, sp, urlValid.getReasonWhyInvalid());
		
		publishButton.disableProperty().bind(urlValid.not());
		
		progressBar.setVisible(false);
		status.setVisible(false);
		
		publishButton.setOnAction((action) -> 
		{
			try
			{
				publish();
			}
			catch (Exception e)
			{
				AppContext.getServiceLocator().getService(CommonDialogsI.class).showErrorDialog("There was an error during publish", e);
			}
		});
		
	}
	
	protected void finishInit(Model model, String classifier, File projectFolder, List<File> dataFiles)
	{
		model_ = model;
		classifier_ = classifier;
		projectFolder_ = projectFolder;
		dataFiles_ = dataFiles;
	}
	
	private void publish() throws Exception
	{
		//TODO add proper handling of progress bar, status output.
		publishButton.disableProperty().unbind();
		publishButton.setDisable(true);
		progressBar.setVisible(true);
		status.setVisible(true);
		status.setText("Publishing...");
		
		Task<Integer> task = new Task<Integer>()
		{
			{
				updateProgress(-1, 50);
			}
			@Override
			protected Integer call() throws Exception
			{
				try
				{
					Publish.doPublish(model_, classifier_, projectFolder_, dataFiles_, url.getText(), username.getText(), password.getText());
					
					Platform.runLater(() -> 
					{
						status.setText("Completed");
					});
				}
				catch (Exception e)
				{
					Platform.runLater(() -> 
					{
						status.setText("Error Publishing!  " + e.toString());
					});
				}
				Platform.runLater(() -> 
				{
					progressBar.progressProperty().unbind();
					progressBar.setProgress(1.0);
					publishButton.disableProperty().bind(urlValid.not());
				});
				
				return 0;
			}
		};
		
		progressBar.progressProperty().bind(task.progressProperty());
		
		Thread th = new Thread(task);
		th.start();
	}
}
