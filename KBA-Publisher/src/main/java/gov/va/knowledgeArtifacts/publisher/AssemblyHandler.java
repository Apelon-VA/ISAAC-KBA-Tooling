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
import org.apache.maven.plugins.maven_assembly_plugin.assembly._1_1.Assembly;
import org.apache.maven.plugins.maven_assembly_plugin.assembly._1_1.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AssemblyHandler}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class AssemblyHandler
{
	private static Logger log = LoggerFactory.getLogger(AssemblyHandler.class);

	@SuppressWarnings("unchecked")
	public static Assembly readOrCreateBlank(File projectFolder) throws Exception
	{
		File assemblyFile = new File(new File(new File(projectFolder, "src"), "assembly"), "assembly.xml");

		if (assemblyFile.isFile())
		{
			try
			{
				JAXBContext ctx = JAXBContext.newInstance(Assembly.class);
				Unmarshaller um = ctx.createUnmarshaller();
				return ((JAXBElement<Assembly>) um.unmarshal(assemblyFile)).getValue();
			}
			catch (Exception e)
			{
				log.error("Error reading existing file", e);
				throw new Exception("Error reading existing pom.xml file: " + e.toString());
			}
		}
		else
		{
			if (assemblyFile.isDirectory())
			{
				throw new RuntimeException("Found a directory named assembly.xml, which isnt' supported");
			}
			return new Assembly();
		}
	}

	public static void writeFile(Assembly assembly, File projectFolder) throws Exception
	{
		try
		{
			File outputPath = new File(new File(projectFolder, "src"), "assembly");
			outputPath.mkdirs();
			if (!outputPath.isDirectory())
			{
				throw new Exception("Failed to create the hierarchy for the assembly file");
			}
			
			JAXBContext ctx = JAXBContext.newInstance(Assembly.class);
			Marshaller ma = ctx.createMarshaller();
			ma.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://maven.apache.org/plugins/maven-assembly-plugin/assembly/1.1.2 http://maven.apache.org/xsd/assembly-1.1.2.xsd");
			ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ma.marshal(new ObjectFactory().createAssembly(assembly), new File(outputPath, "assembly.xml"));
		}

		catch (JAXBException e)
		{
			log.error("Error writing", e);
			throw new Exception("Error writing assembly: " + e);
		}
	}
}
