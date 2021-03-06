/*
 * Copyright (c) Elastic Path Software Inc., 2005
 */
package com.elasticpath.service.rules;

import com.elasticpath.base.exception.EpServiceException;


/**
 * The exception for promo code already exists.
 * @author wliu
 */
public class DuplicatePromoCodeException extends EpServiceException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Creates a new <code>DuplicatePromoCodeException</code> object with the given message.
	 *
	 * @param message the reason for this <code>DuplicatePromoCodeException</code>.
	 */
	public DuplicatePromoCodeException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>DuplicatePromoCodeException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>DuplicatePromoCodeException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>DuplicatePromoCodeException</code>.
	 */
	public DuplicatePromoCodeException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
