package com.alvanklaveren.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductDTO extends AbstractDTO{

    public Integer code;
    public String name;
    public String description;
    public int year;
    public int version;

    public GameConsoleDTO gameConsole;
    public ProductTypeDTO productType;
    public CompanyDTO company;

    public Set<ProductRatingDTO> productRatings;

    public static List<ProductDTO> toDto(List<Product> products, int level){
        return products.stream().map(p -> ProductDTO.toDto(p, level)).collect(Collectors.toList());
    }

    public static ProductDTO toDto(Product product, int level) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.code = product.getCode();
        dto.name = product.getName();
        dto.description = product.getDescription();
        dto.year = product.getYear();
        dto.version = product.getVersion();

        if(--level >= 0) {
            dto.gameConsole = GameConsoleDTO.toDto(product.getGameConsole(), level);
            dto.productType = ProductTypeDTO.toDto(product.getProductType(), level);
            dto.company = CompanyDTO.toDto(product.getCompany(), level);

            if(product.getProductRatings() != null) {
                dto.productRatings = ProductRatingDTO.toDto(product.getProductRatings(), level);
            }
        }

        return dto;
    }

}
