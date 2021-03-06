/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.catalog.webservice.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.catalog.CatalogReaderCapability;
import com.elasticpath.catalog.entity.offer.Offer;
import com.elasticpath.catalog.entity.offer.OfferReaderCapability;
import com.elasticpath.catalog.reader.FindAllResponse;
import com.elasticpath.catalog.reader.impl.FindAllResponseImpl;
import com.elasticpath.catalog.reader.impl.PaginationResponseImpl;
import com.elasticpath.catalog.spi.CatalogProjectionPluginProvider;
import com.elasticpath.catalog.webservice.exception.InvalidRequestParameterException;
import com.elasticpath.service.misc.TimeService;

/**
 * Test for {@link OfferServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class OfferServiceImplTest {

	private static final String STORE_CODE = "store";
	private static final String CODE = "code";
	private static final String START_AFTER_STRING = "startAfterString";

	@Mock
	private OfferReaderCapability reader;

	@Mock
	private TimeService timeService;

	private OfferServiceImpl offerService;

	@Before
	public void setUp() {
		final CatalogProjectionPluginProvider catalogProjectionPluginProvider = mock(CatalogProjectionPluginProvider.class,
				Mockito.RETURNS_DEEP_STUBS);
		final Optional<CatalogReaderCapability> offerReaderCapability = Optional.of(reader);

		when(catalogProjectionPluginProvider.getCatalogProjectionPlugin().getReaderCapability(any())).thenReturn(offerReaderCapability);

		offerService = new OfferServiceImpl(catalogProjectionPluginProvider, timeService);
	}

	@Test
	public void testThatOfferReaderCapabilityIsCalledInGetMethod() {
		when(reader.get(STORE_CODE, CODE)).thenReturn(Optional.empty());

		offerService.get(STORE_CODE, CODE);

		verify(reader).get(STORE_CODE, CODE);
	}

	@Test
	public void testThatOfferReaderCapabilityCallFindAllMethodIfLimitIsCorrect() {
		final int testLimit = 2;
		final FindAllResponse<Offer> testResponse = new FindAllResponseImpl<>(
				new PaginationResponseImpl(testLimit, START_AFTER_STRING, false), ZonedDateTime.now(), Collections.emptyList());

		when(reader.findAll(any(), any(), any())).thenReturn(testResponse);

		offerService.getAllOffers(STORE_CODE, String.valueOf(testLimit), START_AFTER_STRING, null, null);

		verify(reader).findAll(any(), any(), any());
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void testThatOfferServiceThrowExceptionIfParameterIsLessThanZero() {
		final String invalidLimit = "-1";

		offerService.getAllOffers(STORE_CODE, invalidLimit, START_AFTER_STRING, null, null);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void testThatOfferServiceThrowExceptionIfParameterIsLetter() {
		final String invalidLimit = "x";

		offerService.getAllOffers(STORE_CODE, invalidLimit, START_AFTER_STRING, null, null);
	}

	@Test
	public void testThatOfferServiceConvertsValidDate() {
		when(timeService.getCurrentTime()).thenReturn(new Date());
		offerService.getAllOffers(STORE_CODE, "2", START_AFTER_STRING, "2018-01-01T14:47:00+00:00", null);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void testThatOfferServiceThrowExceptionIfInvalidDate() {
		offerService.getAllOffers(STORE_CODE, "2", START_AFTER_STRING, "A2018-01-01T14:47:00+00:00", null);
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void testThatModifiedSinceOffsetCannotBeSpecifiedIfModifiedSinceNotPresent() {
		offerService.getAllOffers(STORE_CODE, "2", START_AFTER_STRING, null, "5");
	}

	@Test(expected = InvalidRequestParameterException.class)
	public void testThatModifiedSinceMustBeInThePast() {
		when(timeService.getCurrentTime()).thenReturn(new Date());
		offerService.getAllOffers(STORE_CODE, "2", START_AFTER_STRING, "3018-01-01T14:47:00+00:00", "5");
	}
	
}
