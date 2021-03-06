/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.search.solr;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.elasticpath.domain.catalogview.SizeType;
import com.elasticpath.domain.search.SortAttributeType;

/**
 * Facet constants.
 */
public final class FacetConstants {
	/**
	 * A character that cannot be typed in the keyboard for separating applied facets.
	 * Revisit this later to find a better solution.
	 */
	public static final String APPLIED_FACETS_SEPARATOR = "\0";
	/**
	 * Brand.
	 */
	public static final String BRAND = "Brand";
	/**
	 * Price.
	 */
	public static final String PRICE = "Price";
	/**
	 * Price.
	 */
	public static final String FEATURED = "Featured";
	/**
	 * Price.
	 */
	public static final String SALES_COUNT = "Sales Count";
	/**
	 * Relevance.
	 */
	public static final String RELEVANCE = "Relevance";
	/**
	 * Category.
	 */
	public static final String CATEGORY = "Category";
	/**
	 * Product Name.
	 */
	public static final String PRODUCT_NAME = "Product Name";
	/**
	 * Product Sku Code.
	 */
	public static final String PRODUCT_SKU_CODE = "Product Sku Code";
	/**
	 * Ranged Attribute used for facet identifier type.
	 */
	public static final String RANGED_ATTRIBUTE = "Ranged Attribute";
	/**
	 * Attibute Type used for facet identifier type.
	 */
	public static final String ATTRIBUTE = "Attribute";
	/**
	 * Sku Option Type used for facet identifier type.
	 */
	public static final String SKU_OPTION = "Sku Option";
	/**
	 * Size Type used for facet identifier type.
	 */
	public static final String SIZE = "Size";
	/**
	 * Facet value display name used for facet value identifier.
	 */
	public static final String FACET_VALUE_DISPLAY_NAME = "Facet Value Display Name";
	/**
	 * Facet value filter used for facet value identifier.
	 */
	public static final String FACET_VALUE_FILTER = "Facet Value Filter";
	/**
	 * Facet value count used for facet value identifier.
	 */
	public static final String FACET_VALUE_COUNT = "Facet Value Count";
	/**
	 * Weight.
	 */
	public static final String HEIGHT = SizeType.HEIGHT.getLabel();
	/**
	 * Height.
	 */
	public static final String WEIGHT = SizeType.WEIGHT.getLabel();
	/**
	 * Length.
	 */
	public static final String LENGTH = SizeType.LENGTH.getLabel();
	/**
	 * Width.
	 */
	public static final String WIDTH = SizeType.WIDTH.getLabel();
	/**
	 * All indexed size attributes.
	 */
	public static final Set<String> SIZE_ATTRIBUTES = ImmutableSet.of(HEIGHT, WEIGHT, LENGTH, WIDTH);

	/**
	 * Attribute label.
	 */
	public static final String ATTRIBUTE_LABEL = "(ATR) ";

	/**
	 * Field label.
	 */
	public static final String FIELD_LABEL = "(FLD) ";

	/**
	 * Sort field group.
	 */
	public static final Map<String, SortAttributeType> SORT_FIELD_GROUP = ImmutableMap.of(PRODUCT_NAME, SortAttributeType.BOOLEAN_TYPE,
			PRICE, SortAttributeType.NUMBER_TYPE,
			SALES_COUNT, SortAttributeType.NUMBER_TYPE,
			FEATURED, SortAttributeType.BOOLEAN_TYPE,
			RELEVANCE, SortAttributeType.STRING_TYPE);

	private FacetConstants() {
		// prevent instantiation
	}
}
