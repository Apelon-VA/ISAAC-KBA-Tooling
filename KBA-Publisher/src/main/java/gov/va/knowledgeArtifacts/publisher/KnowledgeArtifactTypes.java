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
package gov.va.knowledgeArtifacts.publisher;

/**
 * {@link KnowledgeArtifactTypes}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a> 
 */
public enum KnowledgeArtifactTypes
{
	EConcept("ISAAC EConcept Format"),ChangeSet("ISAAC Changeset Format"), KIE("Knowledge Is Everything (Drools)"), 
	CDSKnowledgeArtifact("CDS Knowledge Artifact"), RF2("Release Format 2"), OWL("Web Ontology Language");
	
	private String niceName_;
	
	private KnowledgeArtifactTypes(String description)
	{
		niceName_ = description;
	}
	
	public String getNiceName()
	{
		return niceName_;
	}
}
