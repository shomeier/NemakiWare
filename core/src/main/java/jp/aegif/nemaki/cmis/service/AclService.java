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
 * You should have received a copy of the GNU General Public License along with NemakiWare.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     linzhixing(https://github.com/linzhixing) - initial API and implementation
 ******************************************************************************/
/**
 * This file is part of NemakiWare.
 *
 * NemakiWare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NemakiWare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NemakiWare. If not, see <http://www.gnu.org/licenses/>.
 */
package jp.aegif.nemaki.cmis.service;

import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.server.CallContext;

import jp.aegif.nemaki.util.spring.aspect.log.LogParam;

/**
 * Discovery Service interface.
 */
public interface AclService {

	Acl getAcl(@LogParam("context") CallContext context, @LogParam("repositoryId") String repositoryId,
			@LogParam("objectId") String objectId, @LogParam("onlyBasicPermissions") Boolean onlyBasicPermissions,
			ExtensionsData extension);

	/**
	 * Applies a new ACL to an object. Since it is not possible to transmit an "add
	 * ACL" and a "remove ACL" via AtomPub, the merging has to be done on the client
	 * side. The ACEs provided here is supposed to the new complete ACL.<br/>
	 * 
	 * TODO re-design ACL system in Nemaki
	 * 
	 * @param repositoryId TODO
	 */
	Acl applyAcl(@LogParam("callContext") CallContext callContext, @LogParam("repositoryId") String repositoryId,
			@LogParam("objectId") String objectId, @LogParam("aces") Acl aces,
			@LogParam("aclPropagation") AclPropagation aclPropagation);

}
