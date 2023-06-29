package com.zerobase.reservation.scheduler;

import com.zerobase.reservation.repository.review.ReviewRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShopRatingScheduler {

    private final ReviewRepository reviewRepository;
    private final ShopRepository shopRepository;

    @Scheduled(cron = "${schedules.cron.rating}")
    public void updateShopRating() {

        //샵을 전부 찾아온 후 샵에 해당된 리뷰의 평점을 갖고와서 평균값을 구한 후 샵 평점을 업데이트
        shopRepository.findAll()
                .stream().forEach(shop ->
                        shop.updateRating(
                                (reviewRepository.findAllRatingByShopId(shop.getId())
                                        .stream().mapToDouble(Integer::doubleValue).average().orElse(0.0))
                        ));
        log.info("[{}] 상점 평점 업데이트 완료.", LocalDateTime.now());
    }
}
