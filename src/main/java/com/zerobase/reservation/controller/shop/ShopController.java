package com.zerobase.reservation.controller.shop;

import com.zerobase.reservation.dto.shop.CreateShopDto;
import com.zerobase.reservation.service.shop.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shops")
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    @PreAuthorize("isAuthenticated() and (hasRole('SELLER'))")
    public ResponseEntity<?> create(@Valid @RequestBody CreateShopDto.Request request){
        log.info("{}",request);
        shopService.createShop(request.getEmail(), request.getName(), request.getLatitude(), request.getLongitude());
        return ResponseEntity.ok().build();
    }


}
