package com.alvanklaveren.rest;

import com.alvanklaveren.AVKConfig;
import com.alvanklaveren.enums.EProductSort;
import com.alvanklaveren.model.*;
import com.alvanklaveren.usecase.GameShopUseCase;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins={AVKConfig.crossOrigin})
@RequestMapping("/backend/gameshop")
public class GameShopController {

    @Autowired
    private GameShopUseCase gameShopUseCase;

    public GameShopController() { }

    @RequestMapping(value = "/getProductList", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<List<ProductDTO>> getProductList(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        int codeGameConsole = jsonObject.getInt("codeGameConsole");
        int codeProductType = jsonObject.getInt("codeProductType");
        int sortId = jsonObject.optInt("sortId");
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<ProductDTO> productDTOs =
                gameShopUseCase.getByGameConsoleAndProductType(
                        codeGameConsole, codeProductType, EProductSort.getById(sortId), page, pageSize
                );

        return new ResponseEntity<>(productDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/searchProductList", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<List<ProductDTO>> searchProductList(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String search = jsonObject.getString("productName");
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<ProductDTO> productDTOs = gameShopUseCase.search(search, page, pageSize);

        return new ResponseEntity<>(productDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/simpleSearch", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<List<String>> simpleSearch(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String search = jsonObject.getString("productName");
        int page = jsonObject.optInt("page", 0);
        int pageSize = jsonObject.optInt("pageSize", 20);

        List<String> productNames = gameShopUseCase.simpleSearch(search, page, pageSize);

        return new ResponseEntity<>(productNames, HttpStatus.OK);
    }


    @RequestMapping(value = "/getProductMainImage", method = {RequestMethod.GET, RequestMethod.OPTIONS}, produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getProductMainImage(@RequestParam int codeProduct) {

        return gameShopUseCase.getProductMainImage(codeProduct);
    }

    @RequestMapping(value = "/getProductSortList", method = {RequestMethod.GET, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<List<EProductSortDTO>> getProductSortList() {

        List<EProductSortDTO> eProductSortDTOs = new ArrayList<>();

        Arrays.stream(EProductSort.values()).forEach(s -> {
            EProductSortDTO eProductSortDTO = new EProductSortDTO();
            eProductSortDTO.id = s.getId();
            eProductSortDTO.description = s.getDescription();
            eProductSortDTOs.add(eProductSortDTO);
        });

        return new ResponseEntity<>(eProductSortDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/getGameConsoleList", method = {RequestMethod.GET, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<List<GameConsoleDTO>> getGameConsoleList() {

        List<GameConsoleDTO> gameConsoleDTOs = new ArrayList<>();
        gameConsoleDTOs.add( new GameConsoleDTO(0, "All", 0) );
        gameConsoleDTOs.addAll(gameShopUseCase.getGameConsoles());


        return new ResponseEntity<>(gameConsoleDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/getProductTypeList", method = {RequestMethod.GET, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<List<ProductTypeDTO>> getProductTypeList() {

        List<ProductTypeDTO> productTypeDtos = new ArrayList<>();
        productTypeDtos.add( new ProductTypeDTO(0, "All") );
        productTypeDtos.addAll(gameShopUseCase.getProductTypes());

        return new ResponseEntity<>(productTypeDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/getCompanyList", method = {RequestMethod.GET, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<List<CompanyDTO>> getCompanyList() {

        List<CompanyDTO> companyDTOs = gameShopUseCase.getCompanies();

        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/save", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<ProductDTO> save(@RequestBody ProductDTO productDTO) {

        productDTO = gameShopUseCase.save(productDTO);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/uploadImage", method = {RequestMethod.POST, RequestMethod.OPTIONS}, produces="application/json")
    public ResponseEntity<ProductDTO> uploadImage(@RequestBody String request) {

        System.out.println(request);

        JSONObject jsonObject = new JSONObject(request);
        Integer codeProduct = jsonObject.getInt("codeProduct");
        File file = (File) jsonObject.get("imageFile");

        ProductDTO productDTO = gameShopUseCase.uploadImage(codeProduct, file);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

}
