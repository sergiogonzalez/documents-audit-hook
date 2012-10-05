/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.sympo.listener.liferay.portlet.documentlibrary.model;

import java.util.List;

import com.liferay.portal.ModelListenerException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouterUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.model.BaseModelListener;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.sympo.util.Attribute;
import com.sympo.util.AttributesBuilder;
import com.sympo.util.AuditMessageBuilder;
import com.sympo.util.EventTypes;

/**
 * @author Sergio Gonz‡lez
 */
public class DLFileEntryListener extends BaseModelListener<DLFileEntry> {

	public void onBeforeCreate(DLFileEntry dlFileEntry) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.ADD, dlFileEntry);
	}

	public void onBeforeRemove(DLFileEntry dlFileEntry) throws ModelListenerException {
		auditOnCreateOrRemove(EventTypes.DELETE, dlFileEntry);
	}

	public void onBeforeUpdate(DLFileEntry newDLFileEntry) throws ModelListenerException {
		try {
			DLFileEntry oldDLFileEntry = DLFileEntryLocalServiceUtil.getDLFileEntry(newDLFileEntry.getFileEntryId());

			List<Attribute> attributes = getModifiedAttributes(
				newDLFileEntry, oldDLFileEntry);

			if (!attributes.isEmpty()) {
				AuditMessage auditMessage =
					AuditMessageBuilder.buildAuditMessage(
						EventTypes.UPDATE, DLFileEntry.class.getName(),
						newDLFileEntry.getFileEntryId(), attributes);

				AuditRouterUtil.route(auditMessage);
			}
		}
		catch (Exception e) {
			throw new ModelListenerException(e);
		}
	}

	protected void auditOnCreateOrRemove(String eventType, DLFileEntry dlFileEntry)
		throws ModelListenerException {

		try {
			AuditMessage auditMessage = AuditMessageBuilder.buildAuditMessage(
				eventType, DLFileEntry.class.getName(), dlFileEntry.getFileEntryId(), null);

			JSONObject additionalInfo = auditMessage.getAdditionalInfo();

			additionalInfo.put("title", dlFileEntry.getTitle());
			additionalInfo.put("description", dlFileEntry.getDescription());
			additionalInfo.put("mimeType", dlFileEntry.getMimeType());
			additionalInfo.put("fileEntryTypeId", dlFileEntry.getFileEntryTypeId());

			AuditRouterUtil.route(auditMessage);
		}
		catch (Exception e) {
			throw new ModelListenerException(e);
		}
	}

	protected List<Attribute> getModifiedAttributes(
		DLFileEntry newDLFileEntry, DLFileEntry oldDLFileEntry) {

		AttributesBuilder attributesBuilder = new AttributesBuilder(
			newDLFileEntry, oldDLFileEntry);

		attributesBuilder.add("title");
		attributesBuilder.add("description");
		attributesBuilder.add("mimeType");
		attributesBuilder.add("fileEntryTypeId");

		List<Attribute> attributes = attributesBuilder.getAttributes();

		return attributes;
	}

}