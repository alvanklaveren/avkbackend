package com.alvanklaveren.rest;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.enums.EProductSort;
import com.alvanklaveren.enums.EProductStatus;
import com.alvanklaveren.model.*;
import com.alvanklaveren.usecase.gameshop.GameShopUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.alvanklaveren.enums.EProductSort.EProductSortDTO;

@RestController
@RequestMapping("/backend/gameshop")
@AllArgsConstructor
@Slf4j
public class GameShopController {

    @Autowired
    private final GameShopUseCase gameShopUseCase;

    @PostMapping(value = "/getProductList", produces="application/json")
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

        return ResponseEntity.ok(productDTOs);
    }

    @PostMapping(value = "/searchProductList", produces="application/json")
    public ResponseEntity<List<ProductDTO>> searchProductList(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String search = jsonObject.getString("productName");
        int page = jsonObject.getInt("page");
        int pageSize = jsonObject.getInt("pageSize");

        List<ProductDTO> productDTOs = gameShopUseCase.search(search, page, pageSize);

        return ResponseEntity.ok(productDTOs);
    }

    @PostMapping(value = "/simpleSearch", produces="application/json")
    public ResponseEntity<List<String>> simpleSearch(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        String search = jsonObject.getString("productName");
        int page = jsonObject.optInt("page", 0);
        int pageSize = jsonObject.optInt("pageSize", 20);

        List<String> productNames = gameShopUseCase.simpleSearch(search, page, pageSize);

        return ResponseEntity.ok(productNames);
    }

    @GetMapping(value = "/getProductMainImage", produces= MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] getProductMainImage(@RequestParam int codeProduct) {

        return gameShopUseCase.getProductMainImage(codeProduct);
    }

    @GetMapping(value = "/getProductStatusList", produces="application/json")
    public ResponseEntity<List<EProductStatus.EProductStatusDTO>> getProductStatusList() {

        List<EProductStatus.EProductStatusDTO> eProductStatusDTOs = new ArrayList<>();

        eProductStatusDTOs = Arrays.stream(EProductStatus.values())
                .map(eProductStatus -> {
                    EProductStatus.EProductStatusDTO eProductStatusDTO = new EProductStatus.EProductStatusDTO();
                    eProductStatusDTO.id = eProductStatus.getId();
                    eProductStatusDTO.description = eProductStatus.getDescription();
                    return eProductStatusDTO;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(eProductStatusDTOs);
    }

    @GetMapping(value = "/getProductSortList", produces="application/json")
    public ResponseEntity<List<EProductSort.EProductSortDTO>> getProductSortList() {

        List<EProductSortDTO> eProductSortDTOs = new ArrayList<>();

        Arrays.stream(EProductSort.values()).forEach(s -> {
            EProductSortDTO eProductSortDTO = new EProductSortDTO();
            eProductSortDTO.id = s.getId();
            eProductSortDTO.description = s.getDescription();
            eProductSortDTOs.add(eProductSortDTO);
        });

        return ResponseEntity.ok(eProductSortDTOs);
    }

    @GetMapping(value = "/getGameConsoleList", produces="application/json")
    public ResponseEntity<List<GameConsoleDTO>> getGameConsoleList() {

        List<GameConsoleDTO> gameConsoleDTOs = new ArrayList<>();
        gameConsoleDTOs.add( new GameConsoleDTO(0, "All", 0) );
        gameConsoleDTOs.addAll(gameShopUseCase.getGameConsoles());

        return ResponseEntity.ok(gameConsoleDTOs);
    }

    @GetMapping(value = "/getProductTypeList", produces="application/json")
    public ResponseEntity<List<ProductTypeDTO>> getProductTypeList() {

        List<ProductTypeDTO> productTypeDTOs = new ArrayList<>();
        productTypeDTOs.add( new ProductTypeDTO(0, "All") );
        productTypeDTOs.addAll(gameShopUseCase.getProductTypes());

        return ResponseEntity.ok(productTypeDTOs);
    }

    @GetMapping(value = "/getRatingUrls", produces="application/json")
    public ResponseEntity<List<RatingUrlDTO>> getRatingUrls() {

        List<RatingUrlDTO> ratingUrlDTOs = new ArrayList<>(gameShopUseCase.getRatingUrls());

        return ResponseEntity.ok(ratingUrlDTOs);
    }

    @GetMapping(value = "/getCompanyList", produces="application/json")
    public ResponseEntity<List<CompanyDTO>> getCompanyList() {

        List<CompanyDTO> companyDTOs = gameShopUseCase.getCompanies();

        return ResponseEntity.ok(companyDTOs);
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value = "/addCompany", produces="application/json")
    public ResponseEntity<CompanyDTO> addCompany(@RequestBody String companyName) {

        CompanyDTO companyDTO = gameShopUseCase.addCompany(companyName);
        return ResponseEntity.ok(companyDTO);
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value = "/save", produces="application/json")
    public ResponseEntity<ProductDTO> save(@RequestBody ProductDTO productDTO) {

        productDTO = gameShopUseCase.save(productDTO);
        return ResponseEntity.ok(productDTO);
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value = "/saveProductRating", produces="application/json")
    public ResponseEntity<String> saveProductRating(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        int codeProduct = jsonObject.getInt("codeProduct");
        int codeRatingUrl =  jsonObject.getInt("codeRatingUrl");
        int rating =  jsonObject.getInt("rating");

        gameShopUseCase.saveProductRating(codeProduct, codeRatingUrl, rating);
        return ResponseEntity.ok("");
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value = "/delete", produces="application/json")
    public ResponseEntity<String> delete(@RequestBody Integer codeProduct) {

        gameShopUseCase.delete(codeProduct);

        JSONObject response = new JSONObject();
        response.put("result", Boolean.TRUE.toString());

        return ResponseEntity.ok(response.toString());
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value = "/deleteProductRating", produces="application/json")
    public ResponseEntity<String> deleteProductRating(@RequestBody Integer codeProductRating) {

        gameShopUseCase.deleteProductRating(codeProductRating);

        JSONObject response = new JSONObject();
        response.put("result", Boolean.TRUE.toString());

        return ResponseEntity.ok(response.toString());
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value="/uploadImage", produces = "application/json")
    public ResponseEntity<ProductDTO> uploadImage(@RequestParam("imageFile") MultipartFile file,
                                                  @RequestParam("codeProduct") Integer codeProduct){

        ProductDTO productDTO = gameShopUseCase.uploadImage(codeProduct, file);

        return ResponseEntity.ok(productDTO);
    }

    @Secured({EClassification.ROLE_ADMIN})
    @PostMapping(value="/uploadImageAlt", produces = "application/json")
    public ResponseEntity<ProductDTO> uploadImageAlt(@RequestBody String request) {

        JSONObject jsonObject = new JSONObject(request);
        Integer codeProduct = jsonObject.getInt("codeProduct");
        String fileContent = jsonObject.getString("fileContent");

        ProductDTO productDTO = gameShopUseCase.uploadImageAlt(codeProduct, fileContent);
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping(value = {"/gameshopmobile/{codeGameConsole}/{codeProductType}",
                         "/gameshopmobile/{codeGameConsole}/{codeProductType}/{description}"},
                         produces="application/json")
    public ResponseEntity<List<ProductMobileDTO>> getGameShopMobile(
            @PathVariable("codeGameConsole") Integer codeGameConsole,
            @PathVariable("codeProductType") Integer codeProductType,
            @PathVariable(required = false) String description) {

        // example usages of this function:
        //  http://localhost:5000/backend/gameshop/gameshopmobile/2/1
        //  http://localhost:5000/backend/gameshop/gameshopmobile/0/0/Kingdom%Hearts

        List<ProductDTO> productDTOs =
                gameShopUseCase.getProductsForMobile(codeGameConsole, codeProductType, description);

        List<ProductMobileDTO> productMobileDTOs = productDTOs.stream()
                .map(productDTO -> {
                    byte[] productImage = gameShopUseCase.getProductMainImage(productDTO.code);
                    return new ProductMobileDTO(productDTO, productImage);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(productMobileDTOs);
    }

    /** Returns the actual, not-yet-html-formatted, description stored in the database
     *
     * @param codeProduct
     * @return the (original) description
     */
    @PostMapping(value = "/getProductDescription", produces="application/json")
    public ResponseEntity<String> getProductList(@RequestBody Integer codeProduct) {

        ProductDTO productDTO = gameShopUseCase.getProduct(codeProduct);

        JSONObject result = new JSONObject();
        result.put("description", productDTO.description);
        return ResponseEntity.ok(result.toString());
    }

    // helper class to generate content for mobile app in play store
    static class ProductMobileDTO {
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
