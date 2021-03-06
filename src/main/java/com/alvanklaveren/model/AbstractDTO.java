package com.alvanklaveren.model;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class AbstractDTO {

    public static <T> AbstractDTO mapToDTO(Class<T> dtoClass, String content) {

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(dtoClass);
        try {

            MappingIterator<? extends AbstractDTO> mappingIterator = reader.readValues(content);
            return mappingIterator.next();
        } catch (IOException e) {

            throw new RuntimeException("AbstractDTO::mapToDTO", e);
        }
    }
}
