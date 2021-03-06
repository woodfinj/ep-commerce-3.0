/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.catalog.webservice;

import java.util.Collections;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.http.HttpStatus;

import com.elasticpath.catalog.reader.FindAllResponse;
import com.elasticpath.catalog.webservice.request.entity.RequestBody;
import com.elasticpath.catalog.webservice.request.validator.RequestBodyValidator;
import com.elasticpath.catalog.webservice.services.OfferService;

/**
 * An Offer rest endpoints route builder.
 */
public class OfferWebServiceRouteBuilder extends RouteBuilder {

	private static final String REQUESTED_CODES_AMOUNT = "requestedCodesAmount";
	private static final String EMPTY_STRING = "";
	private final OfferService offerService;
	private final RequestBodyValidator requestBodyValidator;

	/**
	 * Constructor.
	 *
	 * @param offerService is service for reading {@link com.elasticpath.catalog.entity.offer.Offer}.
	 * @param requestBodyValidator validator of POST request body.
	 */
	public OfferWebServiceRouteBuilder(final OfferService offerService, final RequestBodyValidator requestBodyValidator) {
		this.offerService = offerService;
		this.requestBodyValidator = requestBodyValidator;
	}

	@Override
	public void configure() {
		onException(Exception.class)
				.handled(true)
				.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.BAD_REQUEST::value)
				.setBody(() -> EMPTY_STRING);

		from("direct:getOffer")
				.bean(offerService, "get")
				.choice()
					.when(body().method("isPresent"))
						.setBody(body().method("get"))
				.endChoice()
				.otherwise()
					.setBody(constant(null))
					.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.NOT_FOUND::value);

		from("direct:getOffers")
				.bean(offerService, "getAllOffers")
				.transform()
				.body(FindAllResponse.class, response -> response);

		from("direct:offerevents")
				.validate(exchange -> requestBodyValidator.validate(exchange.getIn().getBody(RequestBody.class)))
				.validate(body().method("getEventType.getEventClass").isEqualTo("CatalogEventType"))
				.validate(body().method("getEventType.getName").isEqualTo("OFFERS_UPDATED"))
				.validate(body().method("getGuid").isEqualTo("AGGREGATE"))
				.validate(body().method("getData.get(type)").isEqualTo("offer"))
				.validate(body().method("getData.get(store)").isEqualToIgnoreCase(header("storeCode")))
				.setProperty(REQUESTED_CODES_AMOUNT, body().method("getData.get(codes).size"))
				.bean(offerService, "getLatestOffersWithCodes")
				.choice()
					.when(body().method("size").isNotEqualTo(exchangeProperty(REQUESTED_CODES_AMOUNT)))
						.setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.PARTIAL_CONTENT::value)
				.end()
				.setBody(exchange -> Collections.singletonMap("results", exchange.getIn().getBody()));
	}

}
