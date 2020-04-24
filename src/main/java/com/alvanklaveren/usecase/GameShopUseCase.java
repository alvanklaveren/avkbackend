package com.alvanklaveren.usecase;

import com.alvanklaveren.enums.EProductSort;
import com.alvanklaveren.model.GameConsole;
import com.alvanklaveren.model.GameConsoleDTO;
import com.alvanklaveren.model.Product;
import com.alvanklaveren.model.ProductDTO;
import com.alvanklaveren.repository.GameConsoleRepository;
import com.alvanklaveren.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import utils.StringLogic;

import java.util.List;

@Component
public class GameShopUseCase {

    @Autowired private ProductRepository productRepository;
    @Autowired private GameConsoleRepository gameConsoleRepository;

    @Transactional(readOnly=true)
    public List<ProductDTO> getByGameConsole(Integer codeGameConsole, EProductSort eProductSort, int page, int pageSize){

        Pageable pageRequest = PageRequest.of(page, pageSize, eProductSort.getSort());

        List<Product> products = productRepository.getByGameConsole_Code(codeGameConsole, pageRequest);

        return ProductDTO.toDto(products, 1);
    }

    @Transactional(readOnly=true)
    public List<ProductDTO> getByGameConsoleAndProductType(Integer codeGameConsole, Integer codeProductType, EProductSort eProductSort, int page, int pageSize){

        List<Product> products;

        Pageable pageRequest = PageRequest.of(page, pageSize, eProductSort.getSort());

        if(codeGameConsole <= 0){
            // retrieve only the most recently added products
            products = productRepository.getAllProducts(pageRequest);
        } else {

            products = (codeProductType <= 0)
                    ? productRepository.getByGameConsole_Code(codeGameConsole, pageRequest)
                    : productRepository.getByGameConsole_CodeAndProductType_Code(codeGameConsole, codeProductType, pageRequest);
        }

        products.forEach(product ->{
            product.setDescription(StringLogic.prepareMessage(product.getDescription()));
        });

        return ProductDTO.toDto(products, 1);
    }

    @Transactional(readOnly=true)
    public List<GameConsoleDTO> getGameConsoles(){

        List<GameConsole> gameConsoles = gameConsoleRepository.findAll(Sort.by("sortorder").ascending());

        return GameConsoleDTO.toDto(gameConsoles, 1);
    }


}
