package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class GameConsoleDTO {

    public Integer code;
    public String description;
    public int sortorder;
    public int version;

    public CompanyDTO company;

    public static List<GameConsoleDTO> toDto(List<GameConsole> gameConsoles){
        return gameConsoles.stream().map(GameConsoleDTO::toDto).collect(Collectors.toList());
    }

    public static GameConsoleDTO toDto(GameConsole gameConsole) {
        if (gameConsole == null) {
            return null;
        }

        GameConsoleDTO dto = new GameConsoleDTO();
        dto.code = gameConsole.getCode();
        dto.description = gameConsole.getDescription();
        dto.sortorder = gameConsole.getSortorder();
        dto.version = gameConsole.getVersion();

        dto.company = CompanyDTO.toDto(gameConsole.getCompany());

        return dto;
    }

}
