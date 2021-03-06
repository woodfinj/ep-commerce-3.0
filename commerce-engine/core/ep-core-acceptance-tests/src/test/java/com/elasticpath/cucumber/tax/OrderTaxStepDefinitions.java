/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.tax;

import java.lang.reflect.InvocationTargetException;

import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Step definitions class for operations related to tax calculations on completed orders.
 *
 * @see ShoppingCartTaxStepDefinitions for tax calculations on shopping carts.
 */
public class OrderTaxStepDefinitions {
	
	@Autowired
	private OrderTaxStepDefinitionsHelper orderTaxStepDefinitionsHelper;
	
	/**
	 * Verifies an order's tax journal with a given expected tax journal.
	 *
	 * @param taxJournalDataTable the data of expected tax journal
	 * @throws IllegalAccessException if the field specified in the map cannot be accessed on the TaxJournal object
	 * @throws InvocationTargetException if the TaxJournal property accessor method throws an exception
	 * @throws NoSuchMethodException if the field specified in the map does not exist on the TaxJournal object
	 */
	@Then("^I expect that the tax journal should have purchase entries and reversal entries at the same tax rates as the purchase$")
	public void verifyTaxJournalEntriesForOrderAndReversal(final DataTable taxJournalDataTable)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		
		orderTaxStepDefinitionsHelper.verifyTaxJournalEntriesForOrder(taxJournalDataTable.asMaps(String.class, String.class));
	}
	
	/**
	 * Verifies an order's tax journal with a given expected tax journal.
	 *
	 * @param taxJournalDataTable the data of expected tax journal
	 * @throws IllegalAccessException if the field specified in the map cannot be accessed on the TaxJournal object
	 * @throws InvocationTargetException if the TaxJournal property accessor method throws an exception
	 * @throws NoSuchMethodException if the field specified in the map does not exist on the TaxJournal object
	 */
	@Then("^I expect that the tax journal should have purchase entries and reversal entries at the corresponding tax rates$")
	public void verifyTaxJournalEntriesForOrderShipmentModification(final DataTable taxJournalDataTable)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		
		orderTaxStepDefinitionsHelper.verifyTaxJournalEntriesForOrder(taxJournalDataTable.asMaps(String.class, String.class));
	}
	
	/**
	 * Verifies an exchange order's tax journal with a given expected tax journal.
	 *
	 * @param taxJournalDataTable the data of expected tax journal
	 * @throws IllegalAccessException if the field specified in the map cannot be accessed on the TaxJournal object
	 * @throws InvocationTargetException if the TaxJournal property accessor method throws an exception
	 * @throws NoSuchMethodException if the field specified in the map does not exist on the TaxJournal object
	 */
	@Then("^I expect that the tax journal of exchange order should have purchase entries at the new tax rates$")
	public void verifyTaxJournalEntriesForOrderExchange(final DataTable taxJournalDataTable)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		
		orderTaxStepDefinitionsHelper.verifyTaxJournalEntriesForExchange(taxJournalDataTable.asMaps(String.class, String.class));
	}
	
	/**
	 * Verifies an order's tax journal with a given expected tax journal.
	 *
	 * @param taxJournalDataTable the data of expected tax journal
	 * @throws IllegalAccessException if the field specified in the map cannot be accessed on the TaxJournal object
	 * @throws InvocationTargetException if the TaxJournal property accessor method throws an exception
	 * @throws NoSuchMethodException if the field specified in the map does not exist on the TaxJournal object
	 */
	@Then("^I expect that the tax journal should have purchase entries$")
	public void verifyTaxJournalEntriesForOrder(final DataTable taxJournalDataTable)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		
		orderTaxStepDefinitionsHelper.verifyTaxJournalEntriesForOrder(taxJournalDataTable.asMaps(String.class, String.class));
	}

	/**
	 * Verifies an order with given expected fields.
	 *
	 * @param orderDataTable the data of expected order
	 * @throws IllegalAccessException if the field specified in the map cannot be accessed on the TaxJournal object
	 * @throws InvocationTargetException if the TaxJournal property accessor method throws an exception
	 * @throws NoSuchMethodException if the field specified in the map does not exist on the TaxJournal object
	 */
	@Then("^I expect that the order should have fields$")
	public void verifyOrderFields(final DataTable orderDataTable)
			throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

		orderTaxStepDefinitionsHelper.verifyOrderEntries(orderDataTable.asMaps(String.class, String.class));
	}
}
