/*
 * Copyright © 2019 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.epcommerce.events;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Single;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.ResourceTypeFactory;
import com.elasticpath.rest.relos.rs.events.RoleTransitionEvent;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;

@RunWith(MockitoJUnitRunner.class)
public class InitializeShopperRoleTransitionEventHandlerTest {

	@InjectMocks
	private InitializeShopperRoleTransitionEventHandler handler;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private Callable<Single<ShoppingCart>> doInitShoppingCart;

	@Test
	public void testHandleEvent() throws Exception {
		final String newUserGuid = "newUser";

		when(shoppingCartRepository.getShoppingCartForCustomer(newUserGuid))
				.thenReturn(Single.defer(doInitShoppingCart));

		RoleTransitionEvent event = ResourceTypeFactory.createResourceEntity(RoleTransitionEvent.class)
				.setOldUserGuid("oldUser")
				.setOldRole("oldRole")
				.setNewUserGuid(newUserGuid)
				.setNewRole("newRole");

		handler.handleEvent("mobee", event);

		verify(doInitShoppingCart).call();
	}
}
