package com.zerobase.reservation.global.resolver.shop;

import com.zerobase.reservation.global.common.ColumnNamesConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.zerobase.reservation.dto.Sort.Direction;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageDefault {

    int pageNum() default 1;

    int pageSize() default 10;

    ColumnNamesConstants[] sort() default {};

    Direction direction() default Direction.ASC;
}
