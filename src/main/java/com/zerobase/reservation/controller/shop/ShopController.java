package com.zerobase.reservation.controller.shop;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zerobase.reservation.dto.shop.*;
import com.zerobase.reservation.global.resolver.shop.PageDefault;
import com.zerobase.reservation.global.resolver.shop.PageRequest;
import com.zerobase.reservation.service.shop.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.zerobase.reservation.global.common.ColumnNamesConstants.NAME;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    @PreAuthorize("isAuthenticated() and (hasRole('SELLER'))")
    public ResponseEntity<?> create(@Valid @RequestBody CreateShopDto.Request request) {
        shopService.createShop(request.getEmail(), request.getName(), request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{shopCode}")
    public ResponseEntity<ShopInfoDetailDto.Response> read(@PathVariable String shopCode) {
        ShopDto shopDto = shopService.getShop(shopCode);
        return ResponseEntity.ok(
                ShopInfoDetailDto.Response.of(shopDto)
        );
    }

    @GetMapping
    public ResponseEntity<PageInfo<ShopInfoDto.Response>> readAll(@PageDefault(sort = NAME) PageRequest pageRequest,
                                                                  SearchConditionShopDto searchConditionShopDto) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        return ResponseEntity.ok(
                PageInfo.of(shopService.getShops(searchConditionShopDto, pageRequest))
        );
    }


}
