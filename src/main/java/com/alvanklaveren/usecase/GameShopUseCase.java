package com.alvanklaveren.usecase;

import com.alvanklaveren.enums.EProductSort;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.GameConsoleRepository;
import com.alvanklaveren.repository.ProductImageRepository;
import com.alvanklaveren.repository.ProductRepository;
import com.alvanklaveren.repository.ProductTypeRepository;
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

@Component
public class GameShopUseCase {

    @Autowired private ProductRepository productRepository;
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
    public List<GameConsoleDTO> getGameConsoles(){

        List<GameConsole> gameConsoles = gameConsoleRepository.findAll(Sort.by("sortorder").ascending());

        return GameConsoleDTO.toDto(gameConsoles, 1);
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
