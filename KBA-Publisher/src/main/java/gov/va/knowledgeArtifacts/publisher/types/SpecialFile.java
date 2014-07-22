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
package gov.va.knowledgeArtifacts.publisher.types;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link SpecialFile}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a> 
 */
public class SpecialFile
{
	private String source_;
	private String outputDirectory_;
	private boolean filter_ = false;
	
	public static List<SpecialFile> SPECIAL_FILES = new ArrayList<>();
	static {
		SPECIAL_FILES.add(new SpecialFile("${basedir}/pom.xml", "META-INF/maven/${groupId}/${artifactId}/"));
		SPECIAL_FILES.add(new SpecialFile("${basedir}/src/assembly/assembly.xml", "META-INF/maven/${groupId}/${artifactId}/src/assembly/"));
		SPECIAL_FILES.add(new SpecialFile("${basedir}/src/assembly/LICENSE.txt", "META-INF/", true));
		SPECIAL_FILES.add(new SpecialFile("${basedir}/src/assembly/LICENSE.txt", "META-INF/maven/${groupId}/${artifactId}/src/assembly/", false));
		SPECIAL_FILES.add(new SpecialFile("${basedir}/src/assembly/MANIFEST.MF", "META-INF/", true));
		SPECIAL_FILES.add(new SpecialFile("${basedir}/src/assembly/MANIFEST.MF", "META-INF/maven/${groupId}/${artifactId}/src/assembly/", false));
	}
	
	public SpecialFile(String source, String outputDirectory)
	{
		this(source, outputDirectory, false);
	}
	
	public SpecialFile(String source, String outputDirectory, boolean filter)
	{
		source_ = source;
		outputDirectory_ = outputDirectory;
		filter_ = filter;
	}

	/**
	 * @return the source_
	 */
	public String getSource()
	{
		return source_;
	}

	/**
	 * @return the outputDirectory_
	 */
	public String getOutputDirectory()
	{
		return outputDirectory_;
	}

	/**
	 * @return the filter_
	 */
	public boolean filter()
	{
		return filter_;
	}
}
