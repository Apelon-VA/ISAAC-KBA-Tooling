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
import gov.va.isaac.util.UpdateableBooleanBinding;
import gov.va.knowledgeArtifacts.publisher.guiComponents.DependencyComponent;
import gov.va.knowledgeArtifacts.publisher.guiComponents.LicenseComponent;
import gov.va.knowledgeArtifacts.publisher.guiComponents.UserComponent;
import gov.va.knowledgeArtifacts.publisher.types.KnowledgeArtifactType;
import gov.va.knowledgeArtifacts.publisher.types.SpecialFile;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.maven.plugins.maven_assembly_plugin.assembly._1_1.Assembly;
import org.apache.maven.plugins.maven_assembly_plugin.assembly._1_1.Assembly.FileSets;
import org.apache.maven.plugins.maven_assembly_plugin.assembly._1_1.Assembly.Files;
import org.apache.maven.plugins.maven_assembly_plugin.assembly._1_1.Assembly.Formats;
import org.apache.maven.plugins.maven_assembly_plugin.assembly._1_1.FileItem;
import org.apache.maven.plugins.maven_assembly_plugin.assembly._1_1.FileSet;
import org.apache.maven.pom._4_0.Build;
import org.apache.maven.pom._4_0.Build.Plugins;
import org.apache.maven.pom._4_0.Contributor;
import org.apache.maven.pom._4_0.Dependency;
import org.apache.maven.pom._4_0.Developer;
import org.apache.maven.pom._4_0.License;
import org.apache.maven.pom._4_0.Model;
import org.apache.maven.pom._4_0.Model.Contributors;
import org.apache.maven.pom._4_0.Model.Dependencies;
import org.apache.maven.pom._4_0.Model.Developers;
import org.apache.maven.pom._4_0.Model.Licenses;
import org.apache.maven.pom._4_0.Organization;
import org.apache.maven.pom._4_0.Plugin;
import org.apache.maven.pom._4_0.Plugin.Executions;
import org.apache.maven.pom._4_0.PluginExecution;
import org.apache.maven.pom._4_0.PluginExecution.Configuration;
import org.apache.maven.pom._4_0.PluginExecution.Configuration.Descriptors;
import org.apache.maven.pom._4_0.PluginExecution.Goals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link PublisherController}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class PublisherController
{
	private static Logger log = LoggerFactory.getLogger(PublisherController.class);
	@FXML private GridPane contributorsGridPane;	
	@FXML private TextField orgName;
	@FXML private TextField groupId;
	@FXML private ComboBox<String> dataType;
	@FXML private HBox bottomBox;
	@FXML private Button save;
	@FXML private TextArea description;
	@FXML private TextField orgUrl;
	@FXML private Button addDataFile;
	@FXML private TextField version;
	@FXML private TextField projectFolder;
	@FXML private TextField url;
	@FXML private Button addDataFolder;
	@FXML private Button projectFolderFileChooser;
	@FXML private GridPane dependenciesGridPane;
	@FXML private GridPane developersGridPane;
	@FXML private BorderPane root;
	@FXML private Button publish;
	@FXML private TextField name;
	@FXML private Button removeDataFile;
	@FXML private TextField artifactId;
	@FXML private GridPane licenseGridPane;
	@FXML private ListView<File> dataFiles;
	
	@FXML private Button addLicenseButton;
	@FXML private Button addContributorButton;
	@FXML private Button addDeveloperButton;
	@FXML private Button addDependencyButton;
	
	private File projectFolder_ = null;
	private Model model_;
	private Assembly assembly_;
	
	private ArrayList<LicenseComponent> licenseComponents = new ArrayList<>();
	private ArrayList<UserComponent> developerComponents = new ArrayList<>();
	private ArrayList<UserComponent> contributorComponents = new ArrayList<>();
	private ArrayList<DependencyComponent> dependenciesComponents = new ArrayList<>();
	
	private UpdateableBooleanBinding projectFolderValid, dataFilesValid, dataTypeValid, nameValid, descriptionValid, groupIdValid,  artifactIdValid, versionValid, 
		allRequiredReady;
	
	@FXML
	void initialize()
	{
		assert contributorsGridPane != null : "fx:id=\"contributorsTab\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert orgName != null : "fx:id=\"orgName\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert groupId != null : "fx:id=\"groupId\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert dataType != null : "fx:id=\"dataType\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert bottomBox != null : "fx:id=\"bottomBox\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert save != null : "fx:id=\"save\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert description != null : "fx:id=\"description\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert orgUrl != null : "fx:id=\"orgUrl\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert addDataFile != null : "fx:id=\"addDataFile\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert version != null : "fx:id=\"version\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert projectFolder != null : "fx:id=\"projectFolder\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert url != null : "fx:id=\"url\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert addDataFolder != null : "fx:id=\"addDataFolder\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert projectFolderFileChooser != null : "fx:id=\"projectFolderFileChooser\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert dependenciesGridPane != null : "fx:id=\"dependenciesGridPane\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert developersGridPane != null : "fx:id=\"developersGridPane\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert root != null : "fx:id=\"root\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert publish != null : "fx:id=\"publish\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert name != null : "fx:id=\"name\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert removeDataFile != null : "fx:id=\"removeDataFile\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert artifactId != null : "fx:id=\"artifactId\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert licenseGridPane != null : "fx:id=\"licenseGridPane\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert dataFiles != null : "fx:id=\"dataFiles\" was not injected: check your FXML file 'Publisher.fxml'.";
		
		assert addLicenseButton != null : "fx:id=\"addLicenseButton\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert addContributorButton != null : "fx:id=\"addLicenseButton\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert addDeveloperButton != null : "fx:id=\"addLicenseButton\" was not injected: check your FXML file 'Publisher.fxml'.";
		assert addDependencyButton != null : "fx:id=\"addCompileDependencyButton\" was not injected: check your FXML file 'Publisher.fxml'.";
		

		for (KnowledgeArtifactType type : KnowledgeArtifactType.values())
		{
			dataType.getItems().add(type.getNiceName());
		}

		projectFolderFileChooser.setOnAction((actionEvent) -> {
			DirectoryChooser fc = new DirectoryChooser();
			fc.setTitle("Select the folder where the project will be created");
			File f = fc.showDialog(root.getScene().getWindow());
			if (f != null)
			{
				readProjectFolder(f);
			}
		});
		
		addDataFolder.setOnAction((actionEvent) -> {
			DirectoryChooser fc = new DirectoryChooser();
			fc.setTitle("Select a folder of data files to be included in the artifact");
			File f = fc.showDialog(root.getScene().getWindow());
			if (f != null)
			{
				addUnique(Arrays.asList(f));
			}
		});
		
		addDataFile.setOnAction((actionEvent) -> {
			FileChooser fc = new FileChooser();
			fc.setTitle("Select one or more data files to be included in the artifact");
			List<File> files = fc.showOpenMultipleDialog(root.getScene().getWindow());
			if (files != null && files.size() > 0)
			{
				addUnique(files);
			}
		});
		
		
		BooleanBinding itemSelected = new BooleanBinding()
		{
			{
				bind(dataFiles.getSelectionModel().getSelectedIndices());
			}
			@Override
			protected boolean computeValue()
			{
				return dataFiles.getSelectionModel().getSelectedItems().size() > 0;
			}
		};
		
		dataFiles.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
		removeDataFile.disableProperty().bind(itemSelected.not());
		removeDataFile.setOnAction((actionEvent) -> {
			for (File f : dataFiles.getSelectionModel().getSelectedItems())
			{
				dataFiles.getItems().remove(f);
			}
		});
		
		artifactId.textProperty().bind(name.textProperty());
		artifactId.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				artifactId.textProperty().unbind();
			}
		});
		
		/**
		 * Add Validator markers / error messages to required fields
		 */
		
		projectFolderValid = new UpdateableBooleanBinding()
		{
			{
				setComputeOnInvalidate(true);
				addBinding(projectFolder.textProperty());
			}
			/**
			 * @see gov.va.isaac.util.BooleanBinding#computeValue()
			 */
			@Override
			protected boolean computeValue()
			{
				if (projectFolder_ == null)
				{
					setInvalidReason("The Model Folder field is required");
					return false;
				}
				else
				{
					clearInvalidReason();
					return true;
				}
			}
		};
		StackPane sp = ErrorMarkerUtils.swapGridPaneComponents(projectFolder, new StackPane(), (GridPane)projectFolder.getParent());
		ErrorMarkerUtils.setupErrorMarker(projectFolder, sp, projectFolderValid.getReasonWhyInvalid());
		
		dataFilesValid = new UpdateableBooleanBinding()
		{
			{
				setComputeOnInvalidate(true);
				addBinding(dataFiles.getItems());
			}
			/**
			 * @see gov.va.isaac.util.BooleanBinding#computeValue()
			 */
			@Override
			protected boolean computeValue()
			{
				if (dataFiles.getItems().size() == 0)
				{
					setInvalidReason("You must deploy at least 1 data file.");
					return false;
				}
				else
				{
					clearInvalidReason();
					return true;
				}
			}
		};
		sp = ErrorMarkerUtils.swapGridPaneComponents(dataFiles, new StackPane(), (GridPane)dataFiles.getParent());
		ErrorMarkerUtils.setupErrorMarker(dataFiles, sp, dataFilesValid.getReasonWhyInvalid());
		
		dataTypeValid = new UpdateableBooleanBinding()
		{
			{
				setComputeOnInvalidate(true);
				addBinding(dataType.getEditor().textProperty());
			}
			/**
			 * @see gov.va.isaac.util.BooleanBinding#computeValue()
			 */
			@Override
			protected boolean computeValue()
			{
				if (dataType.getEditor().getText().length() == 0)
				{
					setInvalidReason("The Data Type field is required");
					return false;
				}
				else
				{
					clearInvalidReason();
					return true;
				}
			}
		};
		sp = ErrorMarkerUtils.swapGridPaneComponents(dataType, new StackPane(), (GridPane)dataType.getParent());
		ErrorMarkerUtils.setupErrorMarker(dataType, sp, dataTypeValid.getReasonWhyInvalid());
		
		nameValid = setupSimpleValidator(name, "The Name field is required");
		descriptionValid = setupSimpleValidator(description, "The Description field is required");
		groupIdValid = setupSimpleValidator(groupId, "The Group ID field is required");
		artifactIdValid = setupSimpleValidator(artifactId, "The Artifact ID field is required");
		versionValid = setupSimpleValidator(version, "The Version field is required");
		
		allRequiredReady = new UpdateableBooleanBinding()
		{
			{
				setComputeOnInvalidate(true);
				addBinding(projectFolderValid, dataFilesValid, dataTypeValid, nameValid, descriptionValid, groupIdValid, artifactIdValid, versionValid);
			}
			@Override
			protected boolean computeValue()
			{
				setInvalidReason(getInvalidReasonFromAllBindings());
				return allBindingsValid();
			}
		};
		
		save.disableProperty().bind(allRequiredReady.not());
		sp = ErrorMarkerUtils.swapHBoxComponents(save, new StackPane(), bottomBox);
		ErrorMarkerUtils.setupDisabledInfoMarker(save, sp, allRequiredReady.getReasonWhyInvalid());
		
		publish.disableProperty().bind(allRequiredReady.not());
		sp = ErrorMarkerUtils.swapHBoxComponents(publish, new StackPane(), bottomBox);
		ErrorMarkerUtils.setupDisabledInfoMarker(publish, sp, allRequiredReady.getReasonWhyInvalid());
		
		save.setOnAction((actionEvent) -> 
		{
			save();
			AppContext.getServiceLocator().getService(CommonDialogsI.class).showInformationDialog("Save Complete", "The project has been created and saved."
					+ "  Execute 'mvn publish' within the folder '" + projectFolder_.getAbsolutePath() + "' to publish.", root.getScene().getWindow());
		});

		publish.setOnAction((actionEvent) -> {publish();});
		
		addLicenseButton.setOnAction((actionEvent) -> {licenseTabAddRow(null);});
		addContributorButton.setOnAction((actionEvent) -> {contributorsTabAddRow(null);});
		addDeveloperButton.setOnAction((actionEvent) -> {developerTabAddRow(null);});
		addDependencyButton.setOnAction((actionEvent) -> {dependenciesTabAddRow(null);});
		
		licenseGridPane.getChildren().clear();
		licenseGridPane.getRowConstraints().clear();
		licenseTabAddRow(null);
		
		developersGridPane.getChildren().clear();
		developersGridPane.getRowConstraints().clear();
		developerTabAddRow(null);
		
		contributorsGridPane.getChildren().clear();
		contributorsGridPane.getRowConstraints().clear();
		contributorsTabAddRow(null);
		
		dependenciesGridPane.getChildren().clear();
		dependenciesGridPane.getRowConstraints().clear();
		dependenciesTabAddRow(null);
	}
	private void addUnique(List<File> files)
	{
		HashSet<String> current = new HashSet<>();
		for (File f : dataFiles.getItems())
		{
			current.add(f.getAbsolutePath());
		}
		
		for (File f : files)
		{
			if (!current.contains(f.getAbsolutePath()))
			{
				dataFiles.getItems().add(f);
				current.add(f.getAbsolutePath());
			}
		}
	}
	
	private void save()
	{
		KnowledgeArtifactType type = KnowledgeArtifactType.parse(dataType.getValue());

		if (type == null)
		{
			assembly_.setId(dataType.getValue());
		}
		else
		{
			assembly_.setId(type.getClassifier());
		}
		
		assembly_.setBaseDirectory("${artifactId}-${version}-" + assembly_.getId());
		
		Formats formats = new Formats();
		formats.getFormat().add("zip");
		assembly_.setFormats(formats);
		
		Files files = new Files();
		FileSets fileSets = new FileSets();
		
		for (File f : dataFiles.getItems())
		{
			if (f.isFile())
			{
				FileItem fileItem = new FileItem();
				fileItem.setSource(f.getAbsolutePath());
				files.getFile().add(fileItem);
			}
			else
			{  //directory
				FileSet fs = new FileSet();
				fs.setDirectory(f.getAbsolutePath());
				fs.setOutputDirectory(f.getName());
				fileSets.getFileSet().add(fs);
			}
		}
		
		//The code that puts the pom and other things in the zip file
		
		for (SpecialFile sf : SpecialFile.SPECIAL_FILES)
		{
			FileItem fi = new FileItem();
			fi.setSource(sf.getSource());
			fi.setOutputDirectory(sf.getOutputDirectory());
			fi.setFiltered(sf.filter());
			files.getFile().add(fi);
		}
		
		assembly_.setFiles(files);
		assembly_.setFileSets(fileSets);
		
		model_.setModelVersion("4.0.0");
		model_.setPackaging("pom");
		
		model_.setName(name.getText());
		model_.setDescription(description.getText());
		model_.setUrl(url.getText());
		model_.setGroupId(groupId.getText());
		model_.setArtifactId(artifactId.getText());
		model_.setVersion(version.getText());
		Organization o = new Organization();
		o.setName(orgName.getText());
		o.setUrl(orgUrl.getText());
		model_.setOrganization(o);

		Dependencies dependencies = new Dependencies();
		for (DependencyComponent dc : dependenciesComponents)
		{
			Dependency d = dc.getDependency();
			if (d != null)
			{
				dependencies.getDependency().add(d);
			}
		}
		model_.setDependencies(dependencies);
		
		Licenses licenses = new Licenses();
		for (LicenseComponent lc : licenseComponents)
		{
			License l = lc.getLicense();
			if (l != null)
			{
				licenses.getLicense().add(l);
			}
		}
		model_.setLicenses(licenses);
		
		Developers developers = new Developers();
		for (UserComponent uc : developerComponents)
		{
			Developer d = uc.getDeveloper();
			if (d != null)
			{
				developers.getDeveloper().add(d);
			}
		}
		model_.setDevelopers(developers);
		
		Contributors contributors = new Contributors();
		for (UserComponent uc : contributorComponents)
		{
			Contributor c = uc.getContributor();
			if (c != null)
			{
				contributors.getContributor().add(c);
			}
		}
		model_.setContributors(contributors);
		
		Build build = new Build();
		Plugins plugins = new Plugins();
		Plugin plugin = new Plugin();
		plugin.setArtifactId("maven-assembly-plugin");
		Executions executions = new Executions();
		PluginExecution execution = new PluginExecution();
		execution.setId("zip");
		Goals goals = new Goals();
		goals.getGoal().add("attached");
		execution.setGoals(goals);
		execution.setPhase("package");
		Configuration configuration = new Configuration();
		
		Descriptors descriptors = new Descriptors();
		descriptors.getDescriptor().add("${basedir}/src/assembly/assembly.xml");
		configuration.setDescriptors(descriptors);

		execution.setConfiguration(configuration);
		executions.getExecution().add(execution);
		plugin.setExecutions(executions);
		plugins.getPlugin().add(plugin);
		build.setPlugins(plugins);
		model_.setBuild(build);
		
		try
		{
			PomHandler.writeFile(model_, projectFolder_);
			AssemblyHandler.writeFile(assembly_, projectFolder_);
			
			writeLicenseFile();
			writeManifestFile();
		}
		catch (Exception e)
		{
			AppContext.getServiceLocator().getService(CommonDialogsI.class).showErrorDialog(e.getMessage(), e);
		}
	}
	
	private void readProjectFolder(File file)
	{
		projectFolder_ = file;
		log.info("Reading project folder " + projectFolder_.getAbsolutePath() );
		projectFolder.setText(projectFolder_.getAbsolutePath());
		try
		{
			model_ = PomHandler.read(projectFolder_);
			
			name.setText(convertNull(model_.getName()));
			description.setText(convertNull(model_.getDescription()));
			url.setText(convertNull(model_.getUrl()));
			groupId.setText(convertNull(model_.getGroupId()));
			artifactId.textProperty().unbind();
			artifactId.setText(convertNull(artifactId.getText()));
			version.setText(convertNull(model_.getVersion()));
			if (model_.getOrganization() != null)
			{
				orgName.setText(convertNull(model_.getOrganization().getName()));
				orgUrl.setText(convertNull(model_.getOrganization().getUrl()));
			}
		}
		catch (Exception e)
		{
			AppContext.getServiceLocator().getService(CommonDialogsI.class).showErrorDialog("There was an error reading the existing pom file.  You may continue"
					+ " but the existing file will be completely overwritten upon save or publish", e);
			model_ = new Model();
		}
		try
		{
			//TODO this mechanism of handling files won't cope well with absolute paths and jumping from one system to another...
			assembly_ = AssemblyHandler.readOrCreateBlank(projectFolder_);
			
			KnowledgeArtifactType type = KnowledgeArtifactType.parse(assembly_.getId());
			if (type != null)
			{
				dataType.getSelectionModel().select(type.getNiceName());
			}
			else
			{
				dataType.setValue(convertNull(assembly_.getId()));
			}

			dataFiles.getItems().clear();
			FileSets fileSets = assembly_.getFileSets();
			if (fileSets != null)
			{
				for (FileSet fs : fileSets.getFileSet())
				{
					dataFiles.getItems().add(new File(fs.getDirectory()));
				}
			}
			
			Files files = assembly_.getFiles();
			if (files != null)
			{
				for (FileItem fi : files.getFile())
				{
					boolean skip = false;
					for (SpecialFile sf : SpecialFile.SPECIAL_FILES)
					{
						if (sf.getSource().equals(fi.getSource()))
						{
							skip = true;
							break;
						}
					}
					
					if (!skip)
					{
						dataFiles.getItems().add(new File(fi.getSource()));
					}
				}
			}
			
			for (DependencyComponent dc : dependenciesComponents)
			{
				dc.removeFromGridPane(dependenciesGridPane);
			}
			dependenciesComponents.clear();
			if (model_.getDependencies() != null)
			{
				for (Dependency d : model_.getDependencies().getDependency())
				{
					dependenciesTabAddRow(d);
				}
			}
			
			for (LicenseComponent lc : licenseComponents)
			{
				lc.removeFromGridPane(licenseGridPane);
			}
			licenseComponents.clear();
			if (model_.getLicenses() != null)
			{
				for (License l : model_.getLicenses().getLicense())
				{
					licenseTabAddRow(l);
				}
			}
			
			for (UserComponent uc : developerComponents)
			{
				uc.removeFromGridPane(developersGridPane);
			}
			developerComponents.clear();
			if (model_.getDevelopers() != null)
			{
				for (Developer d : model_.getDevelopers().getDeveloper())
				{
					developerTabAddRow(d);
				}
			}
			
			for (UserComponent uc : contributorComponents)
			{
				uc.removeFromGridPane(contributorsGridPane);
			}
			contributorComponents.clear();
			if (model_.getContributors() != null)
			{
				for (Contributor c : model_.getContributors().getContributor())
				{
					contributorsTabAddRow(c);
				}
			}
		}
		catch (Exception e)
		{
			AppContext.getServiceLocator().getService(CommonDialogsI.class).showErrorDialog("There was an error reading the existing assembly file.  You may continue"
					+ " but the existing file will be completely overwritten upon save or publish", e);
			assembly_ = new Assembly();
		}
	}
	
	private void publish()
	{
		try
		{
			save();
			
			URL resource = PublishDialogController.class.getResource("PublishDialog.fxml");
			log.debug("FXML for " + PublishDialogController.class + ": " + resource);
			FXMLLoader loader = new FXMLLoader(resource);
			loader.load();
			PublishDialogController uc = loader.getController();
			
			Scene scene = new Scene(loader.getRoot());
			Stage stage = new Stage(StageStyle.DECORATED);
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.initOwner(root.getScene().getWindow());
			uc.finishInit(model_, assembly_.getId(), projectFolder_, dataFiles.getItems());
			stage.sizeToScene();
			stage.show();
		}
		catch (Exception e)
		{
			log.error("Error Publishing", e);
			AppContext.getServiceLocator().getService(CommonDialogsI.class).showErrorDialog("Error Publishing", e);
		}
	}
	
	private String convertNull(String input)
	{
		if (input == null)
		{
			return "";
		}
		return input;
	}
	
	private UpdateableBooleanBinding setupSimpleValidator(TextInputControl tic, String invalidReason)
	{
		UpdateableBooleanBinding ubb = new UpdateableBooleanBinding()
		{
			{
				setComputeOnInvalidate(true);
				addBinding(tic.textProperty());
			}
			/**
			 * @see gov.va.isaac.util.BooleanBinding#computeValue()
			 */
			@Override
			protected boolean computeValue()
			{
				if (tic.getText().length() == 0)
				{
					setInvalidReason(invalidReason);
					return false;
				}
				else
				{
					clearInvalidReason();
					return true;
				}
			}
		};
		
		StackPane sp = ErrorMarkerUtils.swapGridPaneComponents(tic, new StackPane(), (GridPane)tic.getParent());
		ErrorMarkerUtils.setupErrorMarker(tic, sp, ubb.getReasonWhyInvalid());
		return ubb;
	}
	
	private void writeLicenseFile() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (LicenseComponent lc : licenseComponents)
		{
			License l = lc.getLicense();
			if (l != null)
			{
				sb.append("Name:  ${project.licenses[" + i + "].name}");
				sb.append("\r\n");
				sb.append("URL:  ${project.licenses[" + i + "].url}");
				sb.append("\r\n");
				sb.append("Comments:  ${project.licenses[" + i + "].comments}");
				sb.append("\r\n");
				sb.append("Distribution:  ${project.licenses[" + i++ + "].distribution}");
				sb.append("\r\n");
			}
		}
		
		java.nio.file.Files.write(new File(new File(new File(projectFolder_, "src"), "assembly"), "LICENSE.txt").toPath(), sb.toString().getBytes(), 
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	private void writeManifestFile() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Manifest-Version: 1.0");
		sb.append("\r\n");
		sb.append("Created-By: ${java.version} (${java.vendor})");
		sb.append("\r\n");
		sb.append("Implementation-Title: ${project.name}");
		sb.append("\r\n");
		sb.append("Implementation-Version: ${project.version}");
		sb.append("\r\n");
		sb.append("Implementation-Vendor-Id: ${project.groupId}");
		sb.append("\r\n");
		sb.append("Implementation-Vendor: ${project.organization.name}");
		sb.append("\r\n");
		
		java.nio.file.Files.write(new File(new File(new File(projectFolder_, "src"), "assembly"), "MANIFEST.MF").toPath(), sb.toString().getBytes(), 
				StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
	}
	
	private void licenseTabAddRow(License l)
	{
		LicenseComponent lc = new LicenseComponent(l);
		lc.addToGridPane(licenseGridPane, licenseComponents.size(), new Consumer<LicenseComponent>()
		{
			@Override
			public void accept(LicenseComponent t)
			{
				licenseComponents.remove(lc);
				lc.removeFromGridPane(licenseGridPane);
			}
		});
		licenseComponents.add(lc);
	}
	
	private void developerTabAddRow(Developer d)
	{
		UserComponent uc = new UserComponent(d);
		uc.addToGridPane(developersGridPane, developerComponents.size(), new Consumer<UserComponent>()
		{
			@Override
			public void accept(UserComponent uc)
			{
				developerComponents.remove(uc);
				uc.removeFromGridPane(developersGridPane);
			}
		});
		developerComponents.add(uc);
	}
	
	private void contributorsTabAddRow(Contributor c)
	{
		UserComponent uc = new UserComponent(c);
		uc.addToGridPane(contributorsGridPane, contributorComponents.size(), new Consumer<UserComponent>()
		{
			@Override
			public void accept(UserComponent uc)
			{
				contributorComponents.remove(uc);
				uc.removeFromGridPane(contributorsGridPane);
			}
		});
		contributorComponents.add(uc);
	}
	
	private void dependenciesTabAddRow(Dependency d)
	{
		DependencyComponent uc = new DependencyComponent(d);
		uc.addToGridPane(dependenciesGridPane, dependenciesComponents.size(), new Consumer<DependencyComponent>()
		{
			@Override
			public void accept(DependencyComponent uc)
			{
				dependenciesComponents.remove(uc);
				uc.removeFromGridPane(dependenciesGridPane);
			}
		});
		dependenciesComponents.add(uc);
	}
}
