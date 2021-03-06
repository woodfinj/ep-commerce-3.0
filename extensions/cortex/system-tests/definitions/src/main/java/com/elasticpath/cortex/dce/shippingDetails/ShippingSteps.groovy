package com.elasticpath.cortex.dce.shippingDetails

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient
import static org.assertj.core.api.Assertions.assertThat

import cucumber.api.java.en.Given
import cucumber.api.java.en.Then

import com.elasticpath.cortex.dce.CommonAssertion
import com.elasticpath.cortexTestObjects.Order

class ShippingSteps {


	@Given('^the order shipping option (.+) has cost of (.+)$')
	static void verifyShippingOptionCost(shippingOption, amount) {
		assertThat(Order.getShippingServiceLevelCost(shippingOption))
				.as("The order shipping option cost is not as expected")
				.isEqualTo(amount)
	}

	@Given('^I select a shipping option (.+)$')
	static void selectShippingOption(String optionName) {
		Order.getShippingServiceLevel(optionName)
	}

	@Then('^the shipping cost has fields amount: (.+?), currency: (.+?) and display: (.+?)$')
	static void verifyShippingOptionHasFields(String expectedAmount, String expectedCurrency, String expectedDisplayName) {
		def listCostElement = client.body.cost[0]
		CommonAssertion.assertCost(listCostElement, expectedAmount, expectedCurrency, expectedDisplayName)
	}

}
