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

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.maven.pom._4_0.Model;
import org.apache.maven.pom._4_0.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link PomHandler}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class PomHandler
{
	private static Logger log = LoggerFactory.getLogger(PomHandler.class);

	public static Model readOrCreateBlank(File projectFolder) throws Exception
	{
		File pom = new File(projectFolder, "pom.xml");
		Model model;

		if (pom.isFile())
		{
			try
			{
				JAXBContext ctx = JAXBContext.newInstance(Model.class);
				Unmarshaller um = ctx.createUnmarshaller();
				JAXBElement<Model> element = (JAXBElement<Model>) um.unmarshal(pom);
				model = element.getValue();
			}
			catch (Exception e)
			{
				log.error("Error reading existing file", e);
				throw new Exception("Error reading existing pom.xml file: " + e.toString());
			}
		}
		else
		{
			if (pom.isDirectory())
			{
				throw new RuntimeException("Found a directory named pom.xml, which isnt' supported");
			}
			model = new Model();
		}
		setDefaults(model);
		return model;
	}

	private static void setDefaults(Model model)
	{
		model.setModelVersion("4.0.0");
		model.setPackaging("zip");

	}

	public static void writeFile(Model model, File projectFolder) throws Exception
	{
		try
		{
			JAXBContext ctx = JAXBContext.newInstance(Model.class);
			Marshaller ma = ctx.createMarshaller();
			ma.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd");
			ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ma.marshal(new ObjectFactory().createProject(model), new File(projectFolder, "pom.xml"));
		}

		catch (JAXBException e)
		{
			log.error("Error writing", e);
			throw new Exception("Error writing pom: " + e);
		}
	}
}
