package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class ProductDTO {

    public Integer code;
    public String name;
    public String description;
    public int year;
    public int version;

    public GameConsoleDTO gameConsole;
    public ProductTypeDTO productType;
    public CompanyDTO company;

    public static List<ProductDTO> toDto(List<Product> products){
        return products.stream().map(ProductDTO::toDto).collect(Collectors.toList());
    }

    public static ProductDTO toDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDTO dto = new ProductDTO();
        dto.code = product.getCode();
        dto.name = product.getName();
        dto.description = product.getDescription();
        dto.year = product.getYear();
        dto.version = product.getVersion();

        dto.gameConsole = GameConsoleDTO.toDto(product.getGameConsole());
        dto.productType = ProductTypeDTO.toDto(product.getProductType());
        dto.company = CompanyDTO.toDto(product.getCompany());

        return dto;
    }

}
