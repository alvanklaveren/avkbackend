package com.alvanklaveren.usecase;

import com.alvanklaveren.enums.EProductSort;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utils.StringLogic;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameShopUseCase {

    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private GameConsoleRepository gameConsoleRepository;
    @Autowired private ProductTypeRepository productTypeRepository;
    @Autowired private ProductImageRepository productImageRepository;


    @Transactional(readOnly=true)
    public List<ProductDTO> getByGameConsoleAndProductType(Integer codeGameConsole, Integer codeProductType, EProductSort eProductSort, int page, int pageSize){

        List<Product> products;

        Pageable pageRequest = PageRequest.of(page, pageSize, eProductSort.getSort());

        if(eProductSort.equals(EProductSort.Rating)) {
            pageRequest = PageRequest.of(page, pageSize);
            products = productRepository.getByGameConsole_CodeAndProductType_CodeByRating(codeGameConsole, codeProductType, pageRequest);
        } else {
            products = productRepository.getByGameConsole_CodeAndProductType_Code(codeGameConsole, codeProductType, pageRequest);
        }

        products.forEach(product ->{
            product.setDescription(StringLogic.prepareMessage(product.getDescription()));
        });

        return ProductDTO.toDto(products, 3);
    }

    @Transactional(readOnly=true)
    public List<ProductDTO> search(String search, int page, int pageSize){

        List<Product> products;

        String productName = search;
        String altProductName = StringLogic.convertVersionNumbers(search);

        productName = "%" + productName.trim().replace(" ", "%") + "%";
        altProductName = "%" + altProductName.trim().replace(" ", "%");

        Pageable pageRequest = PageRequest.of(page, pageSize, EProductSort.Name_Ascending.getSort());
        products = productRepository.search(productName, altProductName, altProductName + "-%", pageRequest);

        products.forEach(product ->{
            product.setDescription(StringLogic.prepareMessage(product.getDescription()));
        });

        return ProductDTO.toDto(products, 3);
    }

    /**
     * Simple search is to quickly fill the autocomplete list when searching for games
     * This returns an array of strings, so it is a much smaller set of data then List<Product>
     *
     * @param search
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(readOnly=true)
    public List<String> simpleSearch(String search, int page, int pageSize) {

        Pageable pageRequest = PageRequest.of(page, pageSize, EProductSort.Name_Ascending.getSort());
        String productName = "%" + search.trim().replace(" ", "%") + "%";
        List<Product> products = productRepository.search(productName, productName, productName, pageRequest);

        return products.stream().map(Product::getName).collect(Collectors.toList());
    }


    @Transactional(readOnly=true)
    public List<GameConsoleDTO> getGameConsoles(){

        List<GameConsole> gameConsoles =
                gameConsoleRepository.findAll(Sort.by("sortorder").ascending().and(Sort.by("description").ascending()));

        return GameConsoleDTO.toDto(gameConsoles, 1);
    }

    @Transactional(readOnly=true)
    public List<CompanyDTO> getCompanies(){

        List<Company> companies = companyRepository.findAll(Sort.by("description").ascending());

        return CompanyDTO.toDto(companies, 1);
    }

    @Transactional(readOnly=true)
    public List<ProductTypeDTO> getProductTypes(){

        List<ProductType> productTypes = productTypeRepository.findAll(Sort.by("description").ascending());

        return ProductTypeDTO.toDto(productTypes, 1);
    }

    @Transactional(readOnly=true)
    public byte[] getProductMainImage(int codeProduct) {

        byte[] image = {};

        Sort sort = Sort.by("sortorder").ascending().and(Sort.by("code").ascending());
        List<ProductImage> images = productImageRepository.getByProduct_Code(codeProduct, sort);

        if(images == null || images.size() == 0) {
            return image;
        }

        try {
            Blob blob = images.get(0).getImage();
            int blobLength = (int) blob.length();
            image = blob.getBytes(1, blobLength);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return image;
    }


}
