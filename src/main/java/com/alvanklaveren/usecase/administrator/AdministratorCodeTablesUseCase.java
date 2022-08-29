package com.alvanklaveren.usecase.administrator;

import com.alvanklaveren.enums.ECodeTable;
import com.alvanklaveren.model.*;
import com.alvanklaveren.repository.*;
import com.mysql.cj.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.alvanklaveren.enums.ECodeTable.*;

@Service("AdministratorCodeTablesUseCase")
@Slf4j
@AllArgsConstructor
public class AdministratorCodeTablesUseCase {

    @Autowired private final CompanyRepository companyRepository;
    @Autowired private final RatingUrlRepository ratingUrlRepository;
    @Autowired private final TranslationRepository translationRepository;
    @Autowired private final GameConsoleRepository gameConsoleRepository;
    @Autowired private final ProductTypeRepository productTypeRepository;

    private static final Map<ECodeTable, Class<? extends AbstractDTO>> codeTableMap = new HashMap<>();
    static {
        codeTableMap.put(GameConsole, GameConsoleDTO.class);
        codeTableMap.put(ProductType, ProductTypeDTO.class);
        codeTableMap.put(Translation, TranslationDTO.class);
        codeTableMap.put(RatingUrl, RatingUrlDTO.class);
        codeTableMap.put(Companies, CompanyDTO.class);
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
            translation = translationRepository.findByCode(translationDTO.code)
                    .orElseThrow(() -> new RuntimeException("Could not find translation with code: " + translationDTO.code));

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

            Translation translation = translationRepository.findByCode(code)
                    .orElseThrow(() -> new RuntimeException("Could not find and delete translation with code: " + code));

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

        AbstractDTO abstractDTO = AbstractDTO.mapToDTO(codeTableMap.get(eCodeTable), codeTableRow);
        if (abstractDTO instanceof CompanyDTO dto) {
            saveCompany(dto);
        } else if (abstractDTO instanceof GameConsoleDTO dto) {
            saveGameConsole(dto);
        } else if (abstractDTO instanceof ProductTypeDTO dto) {
            saveProductType(dto);
        } else if (abstractDTO instanceof RatingUrlDTO dto) {
            saveRatingUrl(dto);
        } else if (abstractDTO instanceof TranslationDTO dto) {
            saveTranslation(dto);
        } else {
            throw new RuntimeException("Failed to save " + abstractDTO.getClass().getSimpleName()
                    + ". Reason: table is not registered as a codetable in AdministratorUseCase::saveCodeTable");
        }
    }
}
