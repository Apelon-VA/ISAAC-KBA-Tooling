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

/**
 * {@link KnowledgeArtifactType}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a> 
 */
public enum KnowledgeArtifactType
{
	IBDB("ISAAC Database Format", "bdb"), EConcept("ISAAC EConcept Format", "ec"),ChangeSet("ISAAC Changeset Format", "ec_cs"), KIE("Knowledge Is Everything (Drools)", "kie"), 
	CDSKnowledgeArtifact("CDS Knowledge Artifact", "cds_ka"), RF2("Release Format 2", "RF2"), OWL("Web Ontology Language", "owl");
	
	private String niceName_;
	private String classifier_;
	
	private KnowledgeArtifactType(String description, String classifier)
	{
		niceName_ = description;
		classifier_ = classifier;
	}
	
	public String getNiceName()
	{
		return niceName_;
	}
	
	public String getClassifier()
	{
		return classifier_;
	}
	
	public static KnowledgeArtifactType parse(String value)
	{
		if (value == null)
		{
			return null;
		}
		for (KnowledgeArtifactType type : KnowledgeArtifactType.values())
		{
			if (value.toLowerCase().equals(type.name().toLowerCase()) || value.toLowerCase().equals(type.getNiceName().toLowerCase()) ||
					value.toLowerCase().equals(type.getClassifier().toLowerCase()))
			{
				return type;
			}
		}
		return null;
	}
}
