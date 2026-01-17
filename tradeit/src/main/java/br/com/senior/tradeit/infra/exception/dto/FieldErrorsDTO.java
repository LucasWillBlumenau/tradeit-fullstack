package br.com.senior.tradeit.infra.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class FieldErrorsDTO {
    private List<Field> fields;
}
