/**
 *
 */
package com.sncustomwebservices.v2.helper;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.search.facetdata.ProductSearchPageWsDTO;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.sncustomwebservices.facades.CustomProductFacade;


/**
 * @author Anand.Mund
 *
 */

@Component
public class CustomProductHelper extends AbstractHelper {

	@Resource(name = "customProductFacade")
	private CustomProductFacade customProductFacade;

	public ProductSearchPageWsDTO searchProductByName(final String productName) {

		final List<ProductData> product = customProductFacade.getProductsByName(productName);
		final List<ProductWsDTO> productDto = new LinkedList<ProductWsDTO>();
		final ProductSearchPageWsDTO searchProduct = new ProductSearchPageWsDTO();
		
		for (final ProductData data : product) {
			productDto.add(getDataMapper().map(data, ProductWsDTO.class));
		}

		//return getDataMapper().map(product, ProductSearchPageWsDTO.class);
		searchProduct.setProducts(productDto);
		return searchProduct;
	}

}
