package com.zerobase.reservation.scheduler;

import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.repository.review.ReviewRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@SpringBootTest
@Transactional
class ShopRatingSchedulerTest {


    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ShopRatingScheduler scheduler;

    @Test
    @DisplayName("정해진 시간에 상점에 대한 리뷰 평점들의 평균을 상점 평점에 업데이트한다")
    public void schedulerSuccess() throws Exception {
        //given
        Shop shop = Shop.builder().build();
        shopRepository.save(shop);

        IntStream.range(1, 6).mapToObj(value ->
                Review.builder()
                        .shop(shop)
                        .rating(value).build()
        ).forEach(review -> reviewRepository.save(review));

        //when
        scheduler.updateShopRating();

        Shop findShop = shopRepository.findByShopCode(shop.getShopCode())
                .orElse(null);

        //then
        Assertions.assertThat(findShop.getRating()).isEqualTo(3.0);
    }


}