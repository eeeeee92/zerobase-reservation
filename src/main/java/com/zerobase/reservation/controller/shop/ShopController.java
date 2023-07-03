package com.zerobase.reservation.controller.shop;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zerobase.reservation.dto.shop.*;
import com.zerobase.reservation.global.annotation.PageDefault;
import com.zerobase.reservation.dto.PageRequest;
import com.zerobase.reservation.global.annotation.Trace;
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

    /**
     * 상점 등록
     */
    @Trace
    @PostMapping
    @PreAuthorize("isAuthenticated() and (hasRole('SELLER'))")
    public ResponseEntity<?> create(@Valid @RequestBody CreateShopDto.Request request) {
        shopService.createShop(request.getEmail(), request.getName(), request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok().build();
    }

    /**
     * 상점 단건조회
     */
    @Trace
    @GetMapping("/{shopCode}")
    public ResponseEntity<ShopInfoDetailDto.Response> read(@PathVariable String shopCode) {
        ShopDto shopDto = shopService.getShop(shopCode);
        return ResponseEntity.ok(
                ShopInfoDetailDto.Response.of(shopDto)
        );
    }

    /**
     * 상점 전체조회 (검색조건별)
     */
    @Trace
    @GetMapping
    public ResponseEntity<PageInfo<ShopInfoDto.Response>> readAll(@PageDefault(sort = NAME) PageRequest pageRequest,
                                                                  SearchConditionShopDto searchConditionShopDto) {
        PageHelper.startPage(pageRequest.getPageNum(), pageRequest.getPageSize());
        return ResponseEntity.ok(
                PageInfo.of(shopService.getShops(searchConditionShopDto, pageRequest))
        );
    }

    /**
     * 상점 수정
     */
    @Trace
    @PutMapping("/{shopCode}")
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) and (hasRole('SELLER')))")
    public ResponseEntity<?> update(@PathVariable String shopCode,
            @RequestBody UpdateShopDto.Request request){
        shopService.update(shopCode, request.getName(), request.getLongitude(), request.getLatitude());
        return ResponseEntity.ok().build();
    }

    /**
     * 상점 삭제
     */
    @Trace
    @DeleteMapping("/{shopCode}")
    @PreAuthorize("isAuthenticated() and ((#request.email == principal.username) and (hasRole('SELLER')))")
    public ResponseEntity<?> delete(@PathVariable String shopCode,
                                    @RequestBody DeleteShopDto.Request request) {
        shopService.delete(shopCode);
        return ResponseEntity.ok().build();
    }

}
