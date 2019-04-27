/*******************************************************************************
 * Copyright (c) 2013 aegif.
 *
 * This file is part of NemakiWare.
 *
 * NemakiWare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NemakiWare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public Licensealong with NemakiWare.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     linzhixing(https://github.com/linzhixing) - initial API and implementation
 ******************************************************************************/
package jp.aegif.nemaki.util.constant;

import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;

public enum NodeType {
	TYPE_DEFINITION("typeDefinition"), PROPERTY_DEFINITION_CORE("propertyDefinitionCore"),
	PROPERTY_DEFINITION_DETAIL("propertyDefinitionDetail"), CMIS_DOCUMENT(BaseTypeId.CMIS_DOCUMENT.value()),
	CMIS_FOLDER(BaseTypeId.CMIS_FOLDER.value()), CMIS_RELATIONSHIP(BaseTypeId.CMIS_RELATIONSHIP.value()),
	CMIS_POLICY(BaseTypeId.CMIS_POLICY.value()), CMIS_ITEM(BaseTypeId.CMIS_ITEM.value()), ATTACHMENT("attachment"),
	RENDITION("rendition"), VERSION_SERIES("versionSeries"), CHANGE("change"), USER("user"), GROUP("group"),
	PATCH("patch");

	private final String value;

	NodeType(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static NodeType fromValue(String v) {
		for (NodeType ot : NodeType.values()) {
			if (ot.value.equals(v)) {
				return ot;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
