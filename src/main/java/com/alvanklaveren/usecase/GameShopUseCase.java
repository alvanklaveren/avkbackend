package com.alvanklaveren.usecase;

import com.alvanklaveren.enums.EProductSort;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.*;
import com.alvanklaveren.utils.StringLogic;
import com.mysql.cj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GameShopUseCase {

    private static final Logger LOG = LoggerFactory.getLogger(GameShopUseCase.class);

    @Autowired private ProductRepository productRepository;
    @Autowired private CompanyRepository companyRepository;
    @Autowired private ProductRatingRepository productRatingRepository;
    @Autowired private GameConsoleRepository gameConsoleRepository;
    @Autowired private ProductTypeRepository productTypeRepository;
    @Autowired private ProductImageRepository productImageRepository;
    @Autowired private RatingUrlRepository ratingUrlRepository;


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

        productName = "%" + productName.trim().toLowerCase().replace(" ", "%") + "%";
        altProductName = "%" + altProductName.trim().toLowerCase().replace(" ", "%");

        if(pageSize > 0) {
            Pageable pageRequest = PageRequest.of(page, pageSize, EProductSort.Name_Ascending.getSort());
            products = productRepository.search(productName, altProductName, altProductName + "-%", pageRequest);
        } else {
            products = productRepository.search(productName, altProductName, altProductName + "-%");
        }

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
    public List<RatingUrlDTO> getRatingUrls(){

        List<RatingUrl> ratingUrls = ratingUrlRepository.findAll(Sort.by("url").ascending());

        return RatingUrlDTO.toDto(ratingUrls, 1);
    }

    @Transactional(readOnly=true)
    public byte[] getProductMainImage(int codeProduct) {

        byte[] image = {};

        Sort sort = Sort.by("sortorder").ascending().and(Sort.by("code").descending());
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

        // resize image to decrease the load of data being pushed to browser
//        BufferedImage bufferedImage = createImageFromBytes(image);
//        bufferedImage = scale(bufferedImage,0.5);
//        image = toBytes(bufferedImage);

        return image;
    }

    @Transactional
    public void delete(Integer codeProduct) {

        Product product = productRepository.getOne(codeProduct);

        List<ProductRating> productRatings = productRatingRepository.getByProduct_Code(codeProduct, Sort.by("code"));
        if(productRatings != null && productRatings.size() > 0) {
            productRatingRepository.deleteAll(productRatings);
        }

        List<ProductImage> productImages = productImageRepository.getByProduct_Code(codeProduct, Sort.by("code"));
        if(productImages != null && productImages.size() > 0) {
            productImageRepository.deleteAll(productImages);
        }

        productRepository.delete(product);
    }

    @Transactional
    public void deleteProductRating(Integer codeProductRating) {

        ProductRating productRating = productRatingRepository.getOne(codeProductRating);
        productRatingRepository.delete(productRating);
    }

    @Transactional
    public CompanyDTO addCompany(String description) {

        if(StringUtils.isNullOrEmpty(description)){
            return null;
        }

        String searchDescription = description.trim().toLowerCase().replace(" ", "%");
        List<Company> companies = companyRepository.findByDescription(searchDescription);

        if (companies.size() > 0){
            return CompanyDTO.toDto(companies.get(0), 0);
        }

        Company company = new Company();
        company.setDescription(description);
        company = companyRepository.save(company);

        return CompanyDTO.toDto(company, 0);
    };

    @Transactional
    public ProductDTO save(ProductDTO productDTO){

        Product product = (productDTO.code == null) ? new Product() : productRepository.getOne(productDTO.code);

        product.setCode(productDTO.code);
        product.setName(productDTO.name);
        product.setDescription(productDTO.description);
        product.setYear(productDTO.year);
        product.setVersion(productDTO.version);

        GameConsole gameConsole = gameConsoleRepository.getByCode(productDTO.gameConsole.code);
        ProductType productType = productTypeRepository.getByCode(productDTO.productType.code);
        Company company = companyRepository.getByCode(productDTO.company.code);

        product.setGameConsole(gameConsole);
        product.setProductType(productType);
        product.setCompany(company);

        product = productRepository.saveAndFlush(product);

        return ProductDTO.toDto(product, 3);
    }

    @Transactional
    public ProductRatingDTO saveProductRating(Integer codeProduct, Integer codeRatingUrl, Integer rating) {

        Product product = productRepository.getOne(codeProduct);
        RatingUrl ratingUrl = ratingUrlRepository.getOne(codeRatingUrl);

        ProductRating productRating = new ProductRating();
        productRating.setProduct(product);
        productRating.setRatingUrl(ratingUrl);
        productRating.setRating(rating);

        productRating = productRatingRepository.save(productRating);

        return ProductRatingDTO.toDto(productRating, 0);
    }

        @Transactional
    public ProductDTO uploadImage(Integer codeProduct, MultipartFile file){

        Product product = productRepository.getOne(codeProduct);

        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setSortorder(0);

        try {
            Blob blob = new SerialBlob(file.getBytes());
            productImage.setImage(blob);
        } catch (Exception e){
            e.printStackTrace();
        }

        productImageRepository.save(productImage);

        product = productRepository.getOne(codeProduct);

        return ProductDTO.toDto(product, 3);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsForMobile(Integer codeGameConsole, Integer codeProductType, String description){

        List<ProductDTO> productDTOs;

        List<Product> products;
        if(StringUtils.isNullOrEmpty(description)) {
            if(codeGameConsole == 0 && codeProductType == 0) {
                // this fetches EVERYTHING.. so limit it to 24
                Pageable pageRequest = PageRequest.of(0,24, Sort.by("code").descending());
                products = productRepository.getByGameConsole_CodeAndProductType_Code(codeGameConsole, codeProductType, pageRequest);
            } else {
                products = productRepository.getByGameConsole_CodeAndProductType_Code(codeGameConsole, codeProductType);
            }
            productDTOs = ProductDTO.toDto(products, 3);
        } else {
            productDTOs = search(description, 0, 0);
        }

        return productDTOs;
    }

    private BufferedImage scale(BufferedImage source, double ratio) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        double xScale = (double) w / source.getWidth();
        double yScale = (double) h / source.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(xScale,yScale);
        g2d.drawRenderedImage(source, at);
        g2d.dispose();
        return bi;
    }

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] toBytes(BufferedImage img){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }
}
