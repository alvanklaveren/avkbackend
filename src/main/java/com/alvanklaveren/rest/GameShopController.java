package com.alvanklaveren.rest;

import com.alvanklaveren.enums.EProductSort;
import com.alvanklaveren.model.GameConsoleDTO;
import com.alvanklaveren.model.ProductDTO;
import com.alvanklaveren.usecase.GameShopUseCase;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/backend/gameshop")
public class GameShopController {

    @Autowired
    private GameShopUseCase gameShopUseCase;

    @CrossOrigin
    @RequestMapping(value = "/getProductList", method = RequestMethod.POST, produces="application/json")
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

    @CrossOrigin
    @RequestMapping(value = "/getGameConsoleList", method = RequestMethod.POST, produces="application/json")
    public ResponseEntity<List<GameConsoleDTO>> getGameConsoleList() {

        List<GameConsoleDTO> gameConsoleDTOs = gameShopUseCase.getGameConsoles();

        return new ResponseEntity<>(gameConsoleDTOs, HttpStatus.OK);
    }

}
