package com.alvanklaveren.usecase;

import com.alvanklaveren.enums.EClassification;
import com.alvanklaveren.enums.ECodeTable;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.*;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alvanklaveren.enums.ECodeTable.*;

@Component
@Slf4j
@AllArgsConstructor
public class AdministratorUseCase {

    @Autowired private final CompanyRepository companyRepository;
    @Autowired private final ConstantsRepository constantsRepository;
    @Autowired private final ForumUserRepository forumUserRepository;
    @Autowired private final RatingUrlRepository ratingUrlRepository;
    @Autowired private final TranslationRepository translationRepository;
    @Autowired private final GameConsoleRepository gameConsoleRepository;
    @Autowired private final ProductTypeRepository productTypeRepository;
    @Autowired private final ClassificationRepository classificationRepository;

    private static final Map<ECodeTable, Class<? extends AbstractDTO>> codeTableMap = new HashMap<>();
    static {
        codeTableMap.put(GameConsole, GameConsoleDTO.class);
        codeTableMap.put(ProductType, ProductTypeDTO.class);
        codeTableMap.put(Translation, TranslationDTO.class);
        codeTableMap.put(RatingUrl, RatingUrlDTO.class);
        codeTableMap.put(Companies, CompanyDTO.class);
    }


    @Transactional(readOnly = true)
    public ConstantsDTO getByCode(Integer code) {

        Constants constants = constantsRepository.getByCode(code);
        return ConstantsDTO.toDto(constants, 0);
    }

    @Transactional(readOnly = true)
    public ConstantsDTO getById(String id) {

        List<Constants> constantsList = constantsRepository.getById(id);
        if(constantsList.size() == 0){
            return null;
        }

        return ConstantsDTO.toDto(constantsList.get(0), 0);
    }

    @Transactional
    public ConstantsDTO save(ConstantsDTO constantsDTO) {

        Constants constants = constantsRepository.getByCode(constantsDTO.code);
        constants.setStringValue(constantsDTO.stringValue);

        try {
            if(constantsDTO.blobValue != null && constantsDTO.blobValue.length > 0) {
                Blob blob = new SerialBlob(constantsDTO.blobValue);
                constants.setBlobValue(blob);
            }
            constants.setBlobValue(null);
        } catch (Exception e){
            e.printStackTrace();
        }

        constants = constantsRepository.save(constants);

        return ConstantsDTO.toDto(constants, 0);
    }

    @Transactional
    public ConstantsDTO uploadImage(Integer codeConstants, MultipartFile file){

        Constants constants = constantsRepository.getByCode(codeConstants);

        try {
            Blob blob = new SerialBlob(file.getBytes());
            constants.setBlobValue(blob);
        } catch (Exception e){
            e.printStackTrace();
        }

        constants = constantsRepository.save(constants);

        return ConstantsDTO.toDto(constants, 0);
    }

    @Transactional
    public ConstantsDTO uploadImageAlt(Integer codeConstants, String fileContent){

        byte[] imageByte = Base64.decodeBase64(fileContent.getBytes());

        Constants constants = constantsRepository.getByCode(codeConstants);

        try {
            Blob blob = new SerialBlob(imageByte);
            constants.setBlobValue(blob);
        } catch (Exception e){
            e.printStackTrace();
        }

        constants = constantsRepository.save(constants);

        return ConstantsDTO.toDto(constants, 0);
    }

    @Transactional(readOnly=true)
    public byte[] getConstantsImage(Integer codeConstants) {

        byte[] image = {};

        Constants constants = constantsRepository.getByCode(codeConstants);

        try {
            Blob blob = constants.getBlobValue();
            if(blob != null) {
                int blobLength = (int) blob.length();
                image = blob.getBytes(1, blobLength);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return image;
    }

    @Transactional(readOnly=true)
    public List<ForumUserDTO> getUsers() {

        List<ForumUser> forumUsers = forumUserRepository.findAll();
        return ForumUserDTO.toDto(forumUsers, 1);
    }

    @Transactional(readOnly=true)
    public List<ClassificationDTO> getClassifications() {

        List<Classification> classifications = classificationRepository.findAll();
        return ClassificationDTO.toDto(classifications, 1);
    }

    @Transactional
    public ForumUserDTO saveUser(ForumUserDTO forumUserDTO) {

        if(StringUtils.isNullOrEmpty(forumUserDTO.emailAddress)) {
            // without an email address, user cannot receive a (new) password
            throw new RuntimeException("Email address is missing.");
        }

        ForumUser forumUser;
        Classification classification;

        boolean isNewUser = (forumUserDTO.code == null || forumUserDTO.code <= 0);

        if (isNewUser) {

            forumUser = new ForumUser();
            EClassification eClassification = EClassification.get(forumUserDTO);
            switch(eClassification){
                case Administrator: case Unknown:
                    // for security reasons, never allow a new user to immediately get administrator privileges.
                    // the same goes for Unknown. In both cases, default to GUEST
                    eClassification = EClassification.Guest;
                    break;
                default:
            }

            classification = classificationRepository.getByCode(eClassification.getCode());
        } else {

            forumUser = forumUserRepository.getByCode(forumUserDTO.code);
            classification = classificationRepository.getByCode(forumUserDTO.classification.code);
        }

        forumUser.setCode(forumUserDTO.code);
        forumUser.setDisplayName(forumUserDTO.displayName);
        forumUser.setUsername(forumUserDTO.username);
        forumUser.setPassword(forumUserDTO.password);
        forumUser.setClassification(classification);
        forumUser.setEmailAddress(forumUserDTO.emailAddress);
        forumUser = forumUserRepository.save(forumUser);

        return ForumUserDTO.toDto(forumUser, 1);
    }

    @Transactional
    public CompanyDTO saveCompany(CompanyDTO companyDTO){

        if(StringUtils.isNullOrEmpty(companyDTO.description)) {
            throw new RuntimeException("company description is empty");
        }

        boolean isNewCompany = (companyDTO.code == null || companyDTO.code <= 0);
        Company company = (isNewCompany) ? new Company() : companyRepository.getByCode(companyDTO.code);

        company.setCode(companyDTO.code);
        company.setDescription(companyDTO.description);
        company.setVersion(companyDTO.version);

        company = companyRepository.save(company);

        return CompanyDTO.toDto(company, 0);
    }

    @Transactional
    public ProductTypeDTO saveProductType(ProductTypeDTO productTypeDTO) {

        if (StringUtils.isNullOrEmpty(productTypeDTO.description)) {
            throw new RuntimeException("product type description is empty");
        }

        boolean isNewProductType = (productTypeDTO.code == null || productTypeDTO.code <= 0);
        ProductType productType = (isNewProductType) ? new ProductType() : productTypeRepository.getByCode(productTypeDTO.code);

        productType.setCode(productTypeDTO.code);
        productType.setDescription(productTypeDTO.description);
        productType.setVersion(productTypeDTO.version);

        productType = productTypeRepository.save(productType);

        return ProductTypeDTO.toDto(productType, 0);
    }

    @Transactional
    public RatingUrlDTO saveRatingUrl(RatingUrlDTO ratingUrlDTO) {

        if (StringUtils.isNullOrEmpty(ratingUrlDTO.url)) {
            throw new RuntimeException("url is empty");
        }

        boolean isNewRatingUrl = (ratingUrlDTO.code == null || ratingUrlDTO.code <= 0);
        RatingUrl ratingUrl = (isNewRatingUrl) ? new RatingUrl() : ratingUrlRepository.getByCode(ratingUrlDTO.code);

        ratingUrl.setCode(ratingUrlDTO.code);
        ratingUrl.setUrl(ratingUrlDTO.url);
        ratingUrl.setVersion(ratingUrlDTO.version);

        ratingUrl = ratingUrlRepository.save(ratingUrl);

        return RatingUrlDTO.toDto(ratingUrl, 0);
    }

    @Transactional
    public TranslationDTO saveTranslation(TranslationDTO translationDTO) {

        if (StringUtils.isNullOrEmpty(translationDTO.original)) {
            throw new RuntimeException("url is empty");
        }

        Translation translation;

        if(translationDTO.code == null || translationDTO.code <= 0) {
            translation = new Translation();
        } else {
            translation = translationRepository.getOne(translationDTO.code);
            translation.setVersion(translationDTO.version);
        }

        translation.setOriginal(translationDTO.original);
        translation.setNl(translationDTO.nl);
        translation.setUs(translationDTO.us);

        translation = translationRepository.save(translation);

        return TranslationDTO.toDto(translation, 0);
    }

    @Transactional
    public GameConsoleDTO saveGameConsole(GameConsoleDTO gameConsoleDTO){

        if(StringUtils.isNullOrEmpty(gameConsoleDTO.description)) {
            // without an email address, user cannot receive a (new) password
            throw new RuntimeException("game console description is empty");
        }

        boolean isNewCompany = (gameConsoleDTO.code == null || gameConsoleDTO.code <= 0);
        GameConsole gameConsole = (isNewCompany) ? new GameConsole() : gameConsoleRepository.getByCode(gameConsoleDTO.code);

        gameConsole.setCode(gameConsoleDTO.code);
        gameConsole.setDescription(gameConsoleDTO.description);
        gameConsole.setSortorder(gameConsoleDTO.sortorder);
        gameConsole.setVersion(gameConsoleDTO.version);

        // default company = { code:6, description:'-' }
        int codeCompany = gameConsoleDTO.company != null
                        ? gameConsoleDTO.company.code : (isNewCompany ? 6 : gameConsole.getCompany().getCode());

        Company company = companyRepository.getByCode(codeCompany);
        gameConsole.setCompany(company);

        gameConsole = gameConsoleRepository.save(gameConsole);

        return GameConsoleDTO.toDto(gameConsole, 1);
    }

    @Transactional
    public boolean deleteUser(Integer codeForumUser) {

        try {
            ForumUser forumUser = forumUserRepository.getByCode(codeForumUser);
            forumUserRepository.delete(forumUser);
        } catch(Exception e) {
            // delete fails when user either does not exist or user is already connected to forum messages.
            log.error(e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    @Transactional
    public boolean deleteCompany(Integer code) {

        try {
            Company company = companyRepository.getByCode(code);
            companyRepository.delete(company);
        } catch(Exception e) {
            // delete fails when user either does not exist or user is already connected to forum messages.
            log.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public boolean deleteGameConsole(Integer code) {

        try {
            GameConsole gameConsole = gameConsoleRepository.getByCode(code);
            gameConsoleRepository.delete(gameConsole);
        } catch(Exception e) {
            // delete fails when user either does not exist or user is already connected to forum messages.
            log.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public boolean deleteProductType(Integer code) {

        try {
            ProductType productType = productTypeRepository.getByCode(code);
            productTypeRepository.delete(productType);
        } catch(Exception e) {
            // delete fails when user either does not exist or user is already connected to forum messages.
            log.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public boolean deleteRatingUrl(Integer code) {

        try {
            RatingUrl ratingUrl = ratingUrlRepository.getByCode(code);
            ratingUrlRepository.delete(ratingUrl);
        } catch(Exception e) {
            // delete fails when user either does not exist or user is already connected to forum messages.
            log.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public boolean deleteTranslation(Integer code) {

        try {
            Translation translation = translationRepository.getOne(code);
            translationRepository.delete(translation);
        } catch(Exception e) {
            // delete fails when user either does not exist or user is already connected to forum messages.
            log.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public void saveCodeTable(ECodeTable eCodeTable, String codeTableRow){

        AbstractDTO dto = AbstractDTO.mapToDTO(codeTableMap.get(eCodeTable), codeTableRow);
        if (dto instanceof CompanyDTO) {
            saveCompany((CompanyDTO) dto);
        } else if (dto instanceof GameConsoleDTO) {
            saveGameConsole((GameConsoleDTO) dto);
        } else if (dto instanceof ProductTypeDTO) {
            saveProductType((ProductTypeDTO) dto);
        } else if (dto instanceof RatingUrlDTO) {
            saveRatingUrl((RatingUrlDTO) dto);
        } else if (dto instanceof TranslationDTO) {
            saveTranslation((TranslationDTO) dto);
        } else {
            throw new RuntimeException("Failed to save " + dto.getClass().getSimpleName()
                    + ". Reason: table is not registered as a codetable in AdministratorUseCase::saveCodeTable");
        }
    }
}
