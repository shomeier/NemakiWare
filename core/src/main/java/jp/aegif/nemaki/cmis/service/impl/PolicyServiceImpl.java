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
package jp.aegif.nemaki.cmis.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.PermissionMapping;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.commons.collections.CollectionUtils;

import jp.aegif.nemaki.businesslogic.ContentService;
import jp.aegif.nemaki.cmis.aspect.CompileService;
import jp.aegif.nemaki.cmis.aspect.ExceptionService;
import jp.aegif.nemaki.cmis.aspect.type.TypeManager;
import jp.aegif.nemaki.cmis.service.PolicyService;
import jp.aegif.nemaki.model.Content;
import jp.aegif.nemaki.model.Policy;
import jp.aegif.nemaki.util.cache.NemakiCachePool;
import jp.aegif.nemaki.util.constant.DomainType;
import jp.aegif.nemaki.util.lock.ThreadLockService;

public class PolicyServiceImpl implements PolicyService {

	private ContentService contentService;
	private CompileService compileService;
	private ExceptionService exceptionService;
	private TypeManager typeManager;
	private ThreadLockService threadLockService;
	private NemakiCachePool nemakiCachePool;

	@Override
	public void applyPolicy(CallContext callContext, String repositoryId, String policyId, String objectId,
			ExtensionsData extension) {
		exceptionService.invalidArgumentRequiredString("objectId", objectId);
		exceptionService.invalidArgumentRequiredString("policyId", policyId);

		Lock objectLock = threadLockService.getWriteLock(repositoryId, objectId);
		Lock policyLock = threadLockService.getReadLock(repositoryId, policyId);
		try {
			objectLock.lock();
			policyLock.lock();

			// //////////////////
			// General Exception
			// //////////////////
			Content content = contentService.getContent(repositoryId, objectId);
			exceptionService.objectNotFound(DomainType.OBJECT, content, objectId);
			exceptionService.permissionDenied(callContext, repositoryId, PermissionMapping.CAN_ADD_POLICY_OBJECT,
					content);
			Policy policy = contentService.getPolicy(repositoryId, policyId);
			exceptionService.objectNotFound(DomainType.OBJECT, policy, policyId);
			exceptionService.permissionDenied(callContext, repositoryId, PermissionMapping.CAN_ADD_POLICY_POLICY,
					policy);

			// //////////////////
			// Specific Exception
			// //////////////////
			TypeDefinition td = typeManager.getTypeDefinition(repositoryId, content);
			if (!td.isControllablePolicy())
				exceptionService.constraint(objectId,
						"appyPolicy cannot be performed on the object whose controllablePolicy = false");

			// //////////////////
			// Body of the method
			// //////////////////
			contentService.applyPolicy(callContext, repositoryId, policyId, objectId, extension);

			nemakiCachePool.get(repositoryId).removeCmisCache(objectId);

		} finally {
			objectLock.unlock();
			policyLock.unlock();
		}
	}

	@Override
	public void removePolicy(CallContext callContext, String repositoryId, String policyId, String objectId,
			ExtensionsData extension) {

		exceptionService.invalidArgumentRequiredString("objectId", objectId);
		exceptionService.invalidArgumentRequiredString("policyId", policyId);

		Lock objectLock = threadLockService.getWriteLock(repositoryId, objectId);
		Lock policyLock = threadLockService.getReadLock(repositoryId, policyId);
		try {
			objectLock.lock();
			policyLock.lock();

			// //////////////////
			// General Exception
			// //////////////////
			Content content = contentService.getContent(repositoryId, objectId);
			exceptionService.objectNotFound(DomainType.OBJECT, content, objectId);
			exceptionService.permissionDenied(callContext, repositoryId, PermissionMapping.CAN_REMOVE_POLICY_OBJECT,
					content);
			Policy policy = contentService.getPolicy(repositoryId, policyId);
			exceptionService.objectNotFound(DomainType.OBJECT, policy, policyId);
			exceptionService.permissionDenied(callContext, repositoryId, PermissionMapping.CAN_REMOVE_POLICY_POLICY,
					policy);

			// //////////////////
			// Body of the method
			// //////////////////
			contentService.removePolicy(callContext, repositoryId, policyId, objectId, extension);

			nemakiCachePool.get(repositoryId).removeCmisCache(objectId);

		} finally {
			objectLock.unlock();
			policyLock.unlock();
		}
	}

	@Override
	public List<ObjectData> getAppliedPolicies(CallContext callContext, String repositoryId, String objectId,
			String filter, ExtensionsData extension) {
		// //////////////////
		// General Exception
		// //////////////////
		exceptionService.invalidArgumentRequiredString("objectId", objectId);
		Content content = contentService.getContent(repositoryId, objectId);
		exceptionService.objectNotFound(DomainType.OBJECT, content, objectId);
		exceptionService.permissionDenied(callContext, repositoryId, PermissionMapping.CAN_GET_APPLIED_POLICIES_OBJECT,
				content);

		// //////////////////
		// Body of the method
		// //////////////////
		List<Policy> policies = contentService.getAppliedPolicies(repositoryId, objectId, extension);

		List<Lock> locks = threadLockService.readLocks(repositoryId, policies);
		try {
			threadLockService.bulkLock(locks);

			List<ObjectData> objects = new ArrayList<ObjectData>();
			if (!CollectionUtils.isEmpty(policies)) {
				for (Policy policy : policies) {
					objects.add(compileService.compileObjectData(callContext, repositoryId, policy, filter, true,
							IncludeRelationships.NONE, null, true));
				}
			}
			return objects;

		} finally {
			threadLockService.bulkUnlock(locks);
		}
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setCompileService(CompileService compileService) {
		this.compileService = compileService;
	}

	public ExceptionService getExceptionService() {
		return exceptionService;
	}

	public void setExceptionService(ExceptionService exceptionService) {
		this.exceptionService = exceptionService;
	}

	public void setTypeManager(TypeManager typeManager) {
		this.typeManager = typeManager;
	}

	public void setThreadLockService(ThreadLockService threadLockService) {
		this.threadLockService = threadLockService;
	}

	public void setNemakiCachePool(NemakiCachePool nemakiCachePool) {
		this.nemakiCachePool = nemakiCachePool;
	}
}
