package com.zerobase.reservation.global.common;

import com.zerobase.reservation.dto.Sort;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ParamToSortConverter implements Converter<String, Sort> {

    private static final List<String> columnNames;
    private static final String DIRECTION_DESC = "DESC";

    static {
        columnNames = Arrays.stream(ColumnNamesConstants.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public Sort convert(String paramNameSort) {
        String[] columnAndDirection = paramNameSort.split(",");

        String columnName = columnAndDirection[0].toUpperCase();
        String direction = columnAndDirection[1].toUpperCase();

        if (!columnNames.stream().anyMatch(columnName::equals)) {
            throw new ArgumentException(ErrorCode.INVALID_REQUEST, columnName);
        }

        Sort sort;
        if (DIRECTION_DESC.equals(direction)) {
            sort = getSort(columnName, Sort.Direction.DESC);
        } else {
            sort = getSort(columnName, Sort.Direction.ASC);
        }
        log.info("{}", sort);
        return sort;

    }

    private static Sort getSort(String property, Sort.Direction direction) {
        return Sort.builder()
                .property(property)
                .direction(direction)
                .build();
    }
}
