package com.zerobase.reservation.repository.shop.mybatis;

import com.zerobase.reservation.dto.shop.SearchConditionShopDto;
import com.zerobase.reservation.dto.shop.ShopInfoDto;
import com.zerobase.reservation.global.resolver.shop.PageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShopMapper {

    List<ShopInfoDto.Response> findAllBySearchConditions(@Param("condition") SearchConditionShopDto searchConditions, @Param("pageRequest") PageRequest pageRequest);
}
