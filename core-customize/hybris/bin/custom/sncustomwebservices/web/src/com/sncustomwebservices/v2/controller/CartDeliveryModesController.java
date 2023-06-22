/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sncustomwebservices.v2.controller;

import de.hybris.platform.commercefacades.order.data.DeliveryModesData;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.DeliveryModeWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import com.sncustomwebservices.exceptions.UnsupportedDeliveryModeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;


@Controller
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "Cart Delivery Modes")
public class CartDeliveryModesController extends BaseCommerceController
{
	private static final Logger LOG = LoggerFactory.getLogger(CartDeliveryModesController.class);

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@GetMapping(value = "/{cartId}/deliverymode")
	@ResponseBody
	@ApiOperation(nickname = "getCartDeliveryMode", value = "Get the delivery mode selected for the cart.", notes = "Returns the delivery mode selected for the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public DeliveryModeWsDTO getCartDeliveryMode(
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		LOG.debug("getCartDeliveryMode");
		return getDataMapper().map(getSessionCart().getDeliveryMode(), DeliveryModeWsDTO.class, fields);
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@PutMapping(value = "/{cartId}/deliverymode")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "replaceCartDeliveryMode", value = "Sets the delivery mode for a cart.", notes = "Sets the delivery mode with a given identifier for the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void replaceCartDeliveryMode(
			@ApiParam(value = "Delivery mode identifier (code)", required = true) @RequestParam(required = true) final String deliveryModeId)
			throws UnsupportedDeliveryModeException
	{
		setCartDeliveryModeInternal(deliveryModeId);
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@DeleteMapping(value = "/{cartId}/deliverymode")
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(nickname = "removeCartDeliveryMode", value = "Deletes the delivery mode from the cart.", notes = "Deletes the delivery mode from the cart.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public void removeCartDeliveryMode()
	{
		LOG.debug("removeCartDeliveryMode");
		if (!getCheckoutFacade().removeDeliveryMode())
		{
			throw new CartException("Cannot reset delivery mode!", CartException.CANNOT_RESET_DELIVERYMODE);
		}
	}

	@Secured({ "ROLE_CUSTOMERGROUP", "ROLE_GUEST", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_TRUSTED_CLIENT" })
	@GetMapping(value = "/{cartId}/deliverymodes")
	@ResponseBody
	@ApiOperation(nickname = "getCartDeliveryModes", value = "Get all delivery modes for the current store and delivery address.", notes =
			"Returns all delivery modes supported for the "
					+ "current base store and cart delivery address. A delivery address must be set for the cart, otherwise an empty list will be returned.")
	@ApiBaseSiteIdUserIdAndCartIdParam
	public DeliveryModeListWsDTO getCartDeliveryModes(
			@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields)
	{
		LOG.debug("getCartDeliveryModes");
		final DeliveryModesData deliveryModesData = new DeliveryModesData();
		deliveryModesData.setDeliveryModes(getCheckoutFacade().getSupportedDeliveryModes());

		return getDataMapper().map(deliveryModesData, DeliveryModeListWsDTO.class, fields);
	}
}
