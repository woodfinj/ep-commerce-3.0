/*
 * Copyright © 2019 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.offersearchresults.prototypes;

import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.SelectorRepository;
import com.elasticpath.rest.definition.offersearches.SortAttributeSelectorChoiceIdentifier;
import com.elasticpath.rest.definition.offersearches.SortAttributeSelectorChoiceResource;
import com.elasticpath.rest.definition.offersearches.SortAttributeSelectorIdentifier;
import com.elasticpath.rest.helix.data.annotation.RequestIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.selector.SelectResult;

/**
 * Select a sort attribute.
 */
public class SelectSortAttributeSelectorChoicePrototype implements SortAttributeSelectorChoiceResource.SelectWithResult {

	private final SortAttributeSelectorChoiceIdentifier identifier;

	private final SelectorRepository<SortAttributeSelectorIdentifier, SortAttributeSelectorChoiceIdentifier> repository;

	/**
	 * Constructor.
	 * @param identifier identifier
	 * @param repository repository
	 */
	@Inject
	public SelectSortAttributeSelectorChoicePrototype(
			@RequestIdentifier final SortAttributeSelectorChoiceIdentifier identifier,
			@ResourceRepository final SelectorRepository<SortAttributeSelectorIdentifier, SortAttributeSelectorChoiceIdentifier> repository) {
		this.identifier = identifier;
		this.repository = repository;
	}

	@Override
	public Single<SelectResult<SortAttributeSelectorIdentifier>> onSelectWithResult() {
		return repository.selectChoice(identifier);
	}
}
