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
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.va.knowledgeArtifacts.publisher.publish;

import gov.va.knowledgeArtifacts.publisher.types.SpecialFile;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.maven.pom._4_0.License;
import org.apache.maven.pom._4_0.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Zip}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a> 
 */
public class Zip
{
	private static Logger log = LoggerFactory.getLogger(Zip.class);
	ZipFile zf;
	
	public File createZipFile(Model model, String classifier, String dataType, File projectFolder, List<File> dataFiles) throws ZipException, IOException
	{
		Path tempFolder = Files.createTempDirectory("KBAPublish-");
		
		String classifierTemp = "";
		if (classifier.trim().length() > 0)
		{
			classifierTemp = "-" + classifier.trim();
		}
		zf = new ZipFile(new File(tempFolder.toFile(), model.getName() + "-" + model.getVersion() + classifierTemp + "." + dataType + ".zip"));
		ZipParameters zp = new ZipParameters();
		zp.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
		zp.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		zp.setDefaultFolderPath(projectFolder.getAbsolutePath());
		String rootFolder = model.getName() + "-" + model.getVersion() + classifierTemp + "." + dataType;
		zp.setRootFolderInZip(rootFolder);
		zp.setIncludeRootFolder(true);
		for (File f : dataFiles)
		{
			if (f.isFile())
			{
				log.info("Adding " + f.getAbsolutePath());
				zf.addFile(f, zp);
			}
			else
			{
				log.info("Adding " + f.getAbsolutePath());
				zf.addFolder(f, zp);
			}
		}
		
		for (SpecialFile sf : SpecialFile.SPECIAL_FILES)
		{
			String source = sf.getSource();
			source = source.replaceAll("\\$\\{basedir\\}", projectFolder.getAbsolutePath());
			File sourceFile = new File(source);
			String target = sf.getOutputDirectory();
			target = target.replaceAll("\\$\\{artifactId\\}", model.getArtifactId());
			target = target.replaceAll("\\$\\{groupId\\}", model.getGroupId());
			zp.setRootFolderInZip(rootFolder + "/" + target);
			
			if (sf.filter())
			{
				Charset charset = StandardCharsets.UTF_8;
				String content = new String(Files.readAllBytes(sourceFile.toPath()), charset);
				content = content.replaceAll("\\$\\{java.version\\}", System.getProperty("java.version"));
				content = content.replaceAll("\\$\\{java.vendor\\}", System.getProperty("java.vendor"));
				content = content.replaceAll("\\$\\{project.name\\}", model.getName());
				content = content.replaceAll("\\$\\{project.version\\}", model.getVersion());
				content = content.replaceAll("\\$\\{project.groupId\\}", model.getGroupId());
				content = content.replaceAll("\\$\\{project.organization.name\\}", (model.getOrganization() == null ? "" : model.getOrganization().getName()));
				
				if (model.getLicenses() != null)
				{
					int i = 0;
					for (License l : model.getLicenses().getLicense())
					{
						content = content.replaceAll("\\$\\{project.licenses\\[" + i + "\\].name\\}", l.getName());
						content = content.replaceAll("\\$\\{project.licenses\\[" + i + "\\].comments\\}", l.getComments());
						content = content.replaceAll("\\$\\{project.licenses\\[" + i + "\\].distribution\\}", l.getDistribution());
						content = content.replaceAll("\\$\\{project.licenses\\[" + i++ + "\\].url\\}", l.getUrl());
					}
				}
				
				
				File tempFile = new File(zf.getFile().getParentFile(), sourceFile.getName());
				Files.write(tempFile.toPath(), content.getBytes(charset));
				log.info("Adding " + sourceFile.getAbsolutePath());
				zp.setDefaultFolderPath(tempFile.getParentFile().getAbsolutePath());
				zf.addFile(tempFile, zp);
			}
			else
			{
				log.info("Adding " + sourceFile.getAbsolutePath());
				zp.setDefaultFolderPath(sourceFile.getParentFile().getAbsolutePath());
				zf.addFile(sourceFile, zp);
			}
		}
		
		
		return zf.getFile();
		
	}
	
	public ProgressMonitor getProgressMonitor()
	{
		if (zf != null)
		{
			return zf.getProgressMonitor();
		}
		return null;
	}
}
