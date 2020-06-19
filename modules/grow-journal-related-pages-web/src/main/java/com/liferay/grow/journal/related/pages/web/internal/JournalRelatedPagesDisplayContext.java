/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.grow.journal.related.pages.web.internal;

import com.liferay.asset.display.page.constants.AssetDisplayPageWebKeys;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetLink;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetLinkLocalServiceUtil;
import com.liferay.info.display.contributor.InfoDisplayObjectProvider;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalArticleConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Marcell Gyöpös
 */
public class JournalRelatedPagesDisplayContext {

	public JournalRelatedPagesDisplayContext(
		HttpServletRequest httpServletRequest) {

		_httpServletRequest = httpServletRequest;

		InfoDisplayObjectProvider infoDisplayObjectProvider =
			(InfoDisplayObjectProvider)httpServletRequest.getAttribute(
				AssetDisplayPageWebKeys.INFO_DISPLAY_OBJECT_PROVIDER);

		if (infoDisplayObjectProvider != null) {
			_journalArticle =
				(JournalArticle)infoDisplayObjectProvider.getDisplayObject();

			_assetEntry = AssetEntryLocalServiceUtil.fetchEntry(
				JournalArticle.class.getName(),
				_journalArticle.getResourcePrimKey());
		}
	}

	public String getFriendlyURLBase() {
		ThemeDisplay _themeDisplay =
			(ThemeDisplay)_httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		StringBundler sb = new StringBundler(4);

		sb.append(_themeDisplay.getPortalURL());

		Group group = _themeDisplay.getScopeGroup();

		boolean privateLayout = false;

		if (_journalArticle != null) {
			Layout layout = _journalArticle.getLayout();

			if (layout != null) {
				privateLayout = layout.isPrivateLayout();
			}
		}

		if (privateLayout) {
			sb.append(PortalUtil.getPathFriendlyURLPrivateGroup());
		}
		else {
			sb.append(PortalUtil.getPathFriendlyURLPublic());
		}

		sb.append(group.getFriendlyURL());

		sb.append(JournalArticleConstants.CANONICAL_URL_SEPARATOR);

		return sb.toString();
	}

	public List<AssetEntry> getRelatedPages() throws PortalException {
		if (_journalArticle == null)

			return Collections.emptyList();

		List<AssetEntry> relatedAssets = new ArrayList<>();
		List<AssetLink> assetEntries = AssetLinkLocalServiceUtil.getDirectLinks(
			_assetEntry.getEntryId(), false);

		for (AssetLink al : assetEntries) {
			long aei1 = al.getEntryId1();
			long aei2 = al.getEntryId2();

			if (aei1 == _assetEntry.getEntryId()) {
				relatedAssets.add(
					AssetEntryLocalServiceUtil.getAssetEntry(aei2));
			}
			else {
				relatedAssets.add(
					AssetEntryLocalServiceUtil.getAssetEntry(aei1));
			}
		}

		return relatedAssets;
	}

	private AssetEntry _assetEntry = null;
	private HttpServletRequest _httpServletRequest;
	private JournalArticle _journalArticle = null;

}