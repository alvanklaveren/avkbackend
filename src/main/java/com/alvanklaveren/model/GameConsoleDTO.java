package com.alvanklaveren.model;

import java.util.List;
import java.util.stream.Collectors;

public class GameConsoleDTO {

    public Integer code;
    public String description;
    public int sortorder;
    public int version;

    public CompanyDTO company;

    public GameConsoleDTO(){ }

    public GameConsoleDTO(Integer code, String description, int sortorder){
        this.code = code;
        this.description = description;
        this.sortorder = sortorder;
        version = 0;
        company = null;
    }

    public static List<GameConsoleDTO> toDto(List<GameConsole> gameConsoles, int level){
        return gameConsoles.stream().map(g -> GameConsoleDTO.toDto(g, level)).collect(Collectors.toList());
    }

    public static GameConsoleDTO toDto(GameConsole gameConsole, int level) {
        if (gameConsole == null) {
            return null;
        }

        GameConsoleDTO dto = new GameConsoleDTO();
        dto.code = gameConsole.getCode();
        dto.description = gameConsole.getDescription();
        dto.sortorder = gameConsole.getSortorder();
        dto.version = gameConsole.getVersion();

        if(--level >= 0) {
            dto.company = CompanyDTO.toDto(gameConsole.getCompany(), level);
        }

        return dto;
    }

}
