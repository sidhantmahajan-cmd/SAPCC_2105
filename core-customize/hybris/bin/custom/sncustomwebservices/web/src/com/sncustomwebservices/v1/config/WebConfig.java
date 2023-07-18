/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sncustomwebservices.v1.config;


import de.hybris.platform.servicelayer.config.ConfigurationService;
import com.sncustomwebservices.request.mapping.handler.CommerceHandlerMapping;

import javax.annotation.Resource;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.resource.ResourceUrlProvider;


/**
 * Spring configuration which replace <mvc:annotation-driven> tag. It allows override default
 * RequestMappingHandlerMapping with our own mapping handler
 */

@Configuration
@ImportResource({ "WEB-INF/config/v1/springmvc-v1-servlet.xml" })
public class WebConfig extends DelegatingWebMvcConfiguration
{

	private static final String LEGACY_CONTENT_NEGOTIATION = "sncustomwebservices.content.negotiation.legacy";

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource(name = "messageConvertersV1")
	private List<HttpMessageConverter<?>> messageConvertersV1;

	@Resource
	private List<HandlerExceptionResolver> exceptionResolversV1;

	private ApplicationContext applicationContext;

	@Override
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping(final ContentNegotiationManager mvcContentNegotiationManager,
			final FormattingConversionService mvcConversionService, final ResourceUrlProvider mvcResourceUrlProvider)
	{
		final CommerceHandlerMapping handlerMapping = new CommerceHandlerMapping("v1");
		handlerMapping.setOrder(0);
		handlerMapping.setDetectHandlerMethodsInAncestorContexts(true);
		handlerMapping.setInterceptors(getInterceptors(mvcConversionService, mvcResourceUrlProvider));
		handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager);
		return handlerMapping;
	}

	@Override
	protected void configureMessageConverters(final List<HttpMessageConverter<?>> converters)
	{
		converters.addAll(messageConvertersV1);
	}

	@Override
	protected void configureHandlerExceptionResolvers(final List<HandlerExceptionResolver> exceptionResolvers)
	{
		final ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
		exceptionHandlerExceptionResolver.setApplicationContext(applicationContext);
		exceptionHandlerExceptionResolver.setContentNegotiationManager(mvcContentNegotiationManager());
		exceptionHandlerExceptionResolver.setMessageConverters(getMessageConverters());
		exceptionHandlerExceptionResolver.afterPropertiesSet();

		exceptionResolvers.add(exceptionHandlerExceptionResolver);
		exceptionResolvers.addAll(exceptionResolversV1);
		exceptionResolvers.add(new ResponseStatusExceptionResolver());
		exceptionResolvers.add(new DefaultHandlerExceptionResolver());
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		super.setApplicationContext(applicationContext);
		this.applicationContext = applicationContext;
	}

	@Override
	public void configureContentNegotiation(final ContentNegotiationConfigurer configurer)
	{
		if (isLegacyContentNegotiationEnabled())
		{
			configurer.favorPathExtension(false).favorParameter(true);
		}
	}

	protected boolean isLegacyContentNegotiationEnabled()
	{
		return configurationService.getConfiguration().getBoolean(LEGACY_CONTENT_NEGOTIATION, false);
	}

}
