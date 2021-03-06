/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.prototypes;

import io.reactivex.Single;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentResource;
import com.elasticpath.rest.resource.itemdefinitions.constant.ItemDefinitionResourceConstants;

/**
 * Item Definition Component prototype for Info operation.
 */
public class InfoItemDefinitionComponentPrototype implements ItemDefinitionComponentResource.Info {

	private static final Single<ResourceInfo> INFO_SINGLE = Single.just(ResourceInfo.builder()
			.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
			.build());

	@Override
	public Single<ResourceInfo> onInfo() {
		return INFO_SINGLE;
	}
}
