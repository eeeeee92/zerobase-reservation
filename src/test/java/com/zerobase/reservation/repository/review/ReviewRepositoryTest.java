package com.zerobase.reservation.repository.review;

import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.repository.shop.ShopRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ReviewRepositoryTest {


    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Test
    public void findAllRatingByShopId() throws Exception {

        Shop shop = Shop.builder().build();
        Shop saveShop = shopRepository.save(shop);

        //given
        IntStream.range(1, 6).mapToObj(rating -> Review.builder().shop(saveShop).rating(rating).build())
                .collect(Collectors.toList()).forEach(review -> reviewRepository.save(review));


        //when
        List<Integer> ratings = reviewRepository.findAllRatingByShopId(saveShop.getId());

        //then
        Assertions.assertThat(ratings)
                .containsExactlyInAnyOrder(1, 2, 3, 4, 5)
                        .hasSize(5);

    }
}