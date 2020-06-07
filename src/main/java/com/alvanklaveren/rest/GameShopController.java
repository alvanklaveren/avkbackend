package com.alvanklaveren.rest;

import com.alvanklaveren.enums.EProductSort;
import com.alvanklaveren.model.*;
import com.alvanklaveren.usecase.GameShopUseCase;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.alvanklaveren.security.SecurityConstants.ROLE_ADMIN;

@RestController
@RequestMapping("/backend/gameshop")
public class GameShopController {

    private static final Logger LOG = LoggerFactory.getLogger(GameShopController.class);

    @Autowired
    private GameShopUseCase gameShopUseCase;

    public GameShopController() { }

    @RequestMapping(value = "/getProductList", method = {RequestMethod.POST}, produces="application/json")
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

    @RequestMapping(value = "/searchProductList", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<List<ProductDTO>> searchProductList(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String search = jsonObject.getString("productName");
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<ProductDTO> productDTOs = gameShopUseCase.search(search, page, pageSize);

        return new ResponseEntity<>(productDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/simpleSearch", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<List<String>> simpleSearch(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String search = jsonObject.getString("productName");
        int page = jsonObject.optInt("page", 0);
        int pageSize = jsonObject.optInt("pageSize", 20);

        List<String> productNames = gameShopUseCase.simpleSearch(search, page, pageSize);

        return new ResponseEntity<>(productNames, HttpStatus.OK);
    }

    @RequestMapping(value = "/getProductMainImage", method = {RequestMethod.GET}, produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getProductMainImage(@RequestParam int codeProduct) {

        return gameShopUseCase.getProductMainImage(codeProduct);
    }

    @RequestMapping(value = "/getProductSortList", method = {RequestMethod.GET}, produces="application/json")
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

    @RequestMapping(value = "/getGameConsoleList", method = {RequestMethod.GET}, produces="application/json")
    public ResponseEntity<List<GameConsoleDTO>> getGameConsoleList() {

        List<GameConsoleDTO> gameConsoleDTOs = new ArrayList<>();
        gameConsoleDTOs.add( new GameConsoleDTO(0, "All", 0) );
        gameConsoleDTOs.addAll(gameShopUseCase.getGameConsoles());

        return new ResponseEntity<>(gameConsoleDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/getProductTypeList", method = {RequestMethod.GET}, produces="application/json")
    public ResponseEntity<List<ProductTypeDTO>> getProductTypeList() {

        List<ProductTypeDTO> productTypeDtos = new ArrayList<>();
        productTypeDtos.add( new ProductTypeDTO(0, "All") );
        productTypeDtos.addAll(gameShopUseCase.getProductTypes());

        return new ResponseEntity<>(productTypeDtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/getRatingUrls", method = {RequestMethod.GET}, produces="application/json")
    public ResponseEntity<List<RatingUrlDTO>> getRatingUrls() {

        List<RatingUrlDTO> ratingUrlDTOs = new ArrayList<>();
        ratingUrlDTOs.addAll(gameShopUseCase.getRatingUrls());

        return new ResponseEntity<>(ratingUrlDTOs, HttpStatus.OK);
    }

    @RequestMapping(value = "/getCompanyList", method = {RequestMethod.GET}, produces="application/json")
    public ResponseEntity<List<CompanyDTO>> getCompanyList() {

        List<CompanyDTO> companyDTOs = gameShopUseCase.getCompanies();

        return new ResponseEntity<>(companyDTOs, HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN})
    @RequestMapping(value = "/addCompany", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<CompanyDTO> addCompany(@RequestBody String companyName) {

        CompanyDTO companyDTO = gameShopUseCase.addCompany(companyName);
        return new ResponseEntity<>(companyDTO, HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN})
    @RequestMapping(value = "/save", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<ProductDTO> save(@RequestBody ProductDTO productDTO) {

        productDTO = gameShopUseCase.save(productDTO);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN})
    @RequestMapping(value = "/saveProductRating", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<String> saveProductRating(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        int codeProduct = jsonObject.getInt("codeProduct");
        int codeRatingUrl =  jsonObject.getInt("codeRatingUrl");
        int rating =  jsonObject.getInt("rating");

        gameShopUseCase.saveProductRating(codeProduct, codeRatingUrl, rating);
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN})
    @RequestMapping(value = "/delete", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<String> delete(@RequestBody Integer codeProduct) {

        gameShopUseCase.delete(codeProduct);

        JSONObject response = new JSONObject();
        response.put("result", "true");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN})
    @RequestMapping(value = "/deleteProductRating", method = {RequestMethod.POST}, produces="application/json")
    public ResponseEntity<String> deleteProductRating(@RequestBody Integer codeProductRating) {

        gameShopUseCase.deleteProductRating(codeProductRating);

        JSONObject response = new JSONObject();
        response.put("result", "true");

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN})
    @RequestMapping(value="/uploadImage", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<ProductDTO> uploadImage(@RequestParam("imageFile") MultipartFile file, @RequestParam("codeProduct") Integer codeProduct){

        ProductDTO productDTO = gameShopUseCase.uploadImage(codeProduct, file);

        return new ResponseEntity<>(productDTO, new HttpHeaders(), HttpStatus.OK);
    }

    @Secured({ROLE_ADMIN})
    @RequestMapping(value="/uploadImageAlt", method = {RequestMethod.POST}, produces = "application/json")
    public ResponseEntity<ProductDTO> uploadImageAlt(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        Integer codeProduct = jsonObject.getInt("codeProduct");
        String fileContent = jsonObject.getString("fileContent");

        ProductDTO productDTO = gameShopUseCase.uploadImageAlt(codeProduct, fileContent);
        return new ResponseEntity<>(productDTO, new HttpHeaders(), HttpStatus.OK);
    }

    @RequestMapping(value = {"/gameshopmobile/{codeGameConsole}/{codeProductType}",
                             "/gameshopmobile/{codeGameConsole}/{codeProductType}/{description}"},
                    method = {RequestMethod.GET}, produces="application/json")
    public ResponseEntity<List<ProductMobileDTO>> getGameShopMobile(@PathVariable("codeGameConsole") Integer codeGameConsole, @PathVariable("codeProductType") Integer codeProductType, @PathVariable(required = false) String description) {

        // example usages of this function:
        //  http://localhost:5000/backend/gameshop/gameshopmobile/0/0
        //  http://localhost:5000/backend/gameshop/gameshopmobile/2/1
        //  http://localhost:5000/backend/gameshop/gameshopmobile/0/0/Kingdom%Hearts

        List<ProductDTO> productDTOs = gameShopUseCase.getProductsForMobile(codeGameConsole, codeProductType, description);

        List<ProductMobileDTO> productMobileDTOs = new ArrayList<>();
        for(ProductDTO productDTO : productDTOs) {
            byte[] productImage = gameShopUseCase.getProductMainImage(productDTO.code);
            ProductMobileDTO productMobileDTO = new ProductMobileDTO(productDTO, productImage);
            productMobileDTOs.add(productMobileDTO);
        }

        return new ResponseEntity<>(productMobileDTOs, HttpStatus.OK);
    }

    // helper class to generate content for mobile app in playstore
    class ProductMobileDTO {
        public Integer code;
        public String name;
        public String description;
        public String gameConsole;
        public String productType;
        public String company;
        public byte[] productImage;

        ProductMobileDTO(ProductDTO productDTO, byte[] productImage) {
            this.code = productDTO.code;
            this.name = productDTO.name;
            this.description = productDTO.description;
            this.gameConsole = productDTO.gameConsole.description;
            this.productType = productDTO.productType.description;
            this.company = productDTO.company.description;
            this.productImage = productImage;
        }
    }

}
