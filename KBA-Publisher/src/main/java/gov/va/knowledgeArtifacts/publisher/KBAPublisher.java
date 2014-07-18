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

import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link KBAPublisher}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class KBAPublisher extends Application
{
	private static Logger log = LoggerFactory.getLogger(KBAPublisher.class);

	public static void main(String[] args)
	{
		launch(args);
	}

	/**
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		URL resource = KBAPublisher.class.getResource("Publisher.fxml");
		log.debug("FXML for " + KBAPublisher.class + ": " + resource);
		FXMLLoader loader = new FXMLLoader(resource);
		loader.load();
		//loader.getController();
		primaryStage.setScene(new Scene(loader.getRoot()));
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.show();
	}
}
