/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sncustomwebservices.v1.controller;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.catalog.CatalogOption;

import java.util.EnumSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import static de.hybris.platform.commercefacades.catalog.CatalogOption.BASIC;
import static de.hybris.platform.commercefacades.catalog.CatalogOption.CATEGORIES;


/**
 * Unit test for {@link CatalogsController}
 */
@UnitTest
public class CatalogsControllerTest
{
	private static final String OPTIONS = BASIC + "," + CATEGORIES;

	private final CatalogsController catalogsController = new CatalogsController();

	@Test
	public void testGetOptions()
	{
		final Set<CatalogOption> catalogOptions = catalogsController.getOptions(OPTIONS);
		Assert.assertNotNull("Set of catalog options should not be null", catalogOptions);
		Assert.assertEquals("Set of catalog options should be enum set of BASIC, CATEGORIES options", EnumSet.of(BASIC, CATEGORIES),
				catalogOptions);
	}
}
