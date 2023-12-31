package com.zerobase.reservation.service.review;

import com.zerobase.reservation.config.AcceptanceTest;
import com.zerobase.reservation.domain.member.Member;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.domain.review.Review;
import com.zerobase.reservation.domain.shop.Shop;
import com.zerobase.reservation.dto.review.ReviewDto;
import com.zerobase.reservation.dto.review.SearchConditionReviewDto;
import com.zerobase.reservation.global.exception.ArgumentException;
import com.zerobase.reservation.global.exception.ConflictException;
import com.zerobase.reservation.global.exception.ErrorCode;
import com.zerobase.reservation.repository.member.MemberRepository;
import com.zerobase.reservation.repository.reservation.ReservationRepository;
import com.zerobase.reservation.repository.review.ReviewRepository;
import com.zerobase.reservation.repository.shop.ShopRepository;
import com.zerobase.reservation.type.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

@AcceptanceTest
@Transactional
class ReviewServiceTest {
    @Autowired
    ReviewService reviewService;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ShopRepository shopRepository;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("리뷰 등록")
    public void create() throws Exception {
        //given
        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();
        reservation.updateArrivalStatus();
        reservationRepository.save(reservation);

        int rating = 5;
        String content = "zerobase";
        String imageUrl = "imageUrl";

        //when
        ReviewDto reviewDto = reviewService.create(email, shop.getShopCode(), reservation.getReservationCode(),
                rating, content, imageUrl);


        //then
        Assertions.assertThat(reviewDto).extracting("rating", "rating", "content", "imageUrl")
                .contains(rating, content, imageUrl);
    }

    @Test
    @DisplayName("리뷰 등록시 예약이 존재하지 않으면 예외가 발생한다")
    public void create_reservationNotFound() throws Exception {
        //given
        String email = "zerobase@naver.com";
        String shopCode = UUID.randomUUID().toString();
        String reservationCode = UUID.randomUUID().toString();
        ArgumentException argumentException = new ArgumentException(ErrorCode.RESERVATION_NOT_FOUND, reservationCode);
        //when //then

        ArgumentException exception = assertThrows(ArgumentException.class,
                () -> reviewService.create(email, shopCode, reservationCode, 1, "content", "imageUrl"));

        Assertions.assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("리뷰 등록시 상점을 방문하지 않았으면 예외가 발생한다")
    public void create_shopNotVisited() throws Exception {
        //given
        Member member = Member.builder()
                .email("zerobase@naver.com")
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();
        reservationRepository.save(reservation);
        String email = "zerobase@naver.com";
        String shopCode = UUID.randomUUID().toString();
        //when
        ConflictException exception = assertThrows(ConflictException.class, () ->
                reviewService.create(email, shopCode, reservation.getReservationCode(), 1, "content", "imageUrl"));
        //then
        Assertions.assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SHOP_NOT_VISITED);
    }

    @Test
    @DisplayName("리뷰 등록시 이미 예약에 대해 리뷰가 존재하면 예외가 발생한다")
    public void create_alreadyExistReservation() throws Exception {
        //given

        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();

        reservation.updateArrivalStatus();
        reservationRepository.save(reservation);



        Review review = Review.builder()
                .reservation(reservation)
                .member(member)
                .shop(shop)
                .rating(1)
                .content("콘텐트")
                .build();
        reviewRepository.save(review);

        //when //then
        ConflictException exception = assertThrows(ConflictException.class, () ->
                reviewService.create(email, shop.getShopCode(), reservation.getReservationCode(), 1, "content", "imageUrl"));
        Assertions.assertThat(exception.getErrorCode())
                .isEqualTo(ErrorCode.ALREADY_EXIST_REVIEW);

    }

    @Test
    @DisplayName("리뷰 등록시 회원이 존재하지 않으면 예외가 발생한다")
    public void create_memberNotFound() throws Exception {
        //given

        Member member = Member.builder()
                .email("zerobase@naver.com")
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();

        reservation.updateArrivalStatus();
        reservationRepository.save(reservation);


        String email = "email@nave.com";
        ArgumentException argumentException = new ArgumentException(ErrorCode.MEMBER_NOT_FOUND, email);
        //when
        ArgumentException exception = assertThrows(ArgumentException.class, () ->
                reviewService.create(email, shop.getShopCode(), reservation.getReservationCode(), 1, "content", "imageUrl"));


        //then
        Assertions.assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("리뷰 등록시 상점이 존재하지 않으면 예외가 발생한다")
    public void create_shopNotFound() throws Exception {
        //given

        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();

        reservation.updateArrivalStatus();
        reservationRepository.save(reservation);


        String shopCode = UUID.randomUUID().toString();

        ArgumentException argumentException = new ArgumentException(ErrorCode.SHOP_NOT_FOUND, shopCode);
        //when
        ArgumentException exception = assertThrows(ArgumentException.class, () ->
                reviewService.create(email, shopCode, reservation.getReservationCode(), 1, "content", "imageUrl"));


        //then
        Assertions.assertThat(exception)
                .extracting("errorCode", "errorMessage")
                .contains(argumentException.getErrorCode(), argumentException.getErrorMessage());
    }

    @Test
    @DisplayName("리뷰 상세조회")
    public void read() throws Exception {
        //given
        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();

        reservation.updateArrivalStatus();
        reservationRepository.save(reservation);

        Review review = Review.builder()
                .member(member)
                .shop(shop)
                .reservation(reservation)
                .rating(1)
                .content("content")
                .imageUrl("imageUrl")
                .build();

        Review saveReview = reviewRepository.save(review);

        //when
        Review findReview = reviewRepository.findByReviewCode(saveReview.getReviewCode())
                .orElse(null);

        //then
        Assertions.assertThat(findReview).isEqualTo(saveReview);
    }

    @Test
    @DisplayName("리뷰 삭제")
    public void delete() throws Exception {
        //given
        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();

        reservation.updateArrivalStatus();
        reservationRepository.save(reservation);

        Review review = Review.builder()
                .member(member)
                .shop(shop)
                .reservation(reservation)
                .rating(1)
                .content("content")
                .imageUrl("imageUrl")
                .build();

        reviewRepository.save(review);


        //when
        reviewService.delete(review.getReviewCode());

        //then
        Review findReview = reviewRepository.findByReviewCode(review.getReviewCode())
                .orElse(null);
        Assertions.assertThat(findReview)
                .isNull();;
    }


    @Test
    @DisplayName("리뷰 수정")
    public void update() throws Exception {
        //given
        //given
        String email = "zerobase@naver.com";
        Member member = Member.builder()
                .email(email)
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation = Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();

        reservation.updateArrivalStatus();
        reservationRepository.save(reservation);

        Review review = Review.builder()
                .member(member)
                .shop(shop)
                .reservation(reservation)
                .rating(1)
                .content("content")
                .imageUrl("imageUrl")
                .build();

        reviewRepository.save(review);

        //when
        ReviewDto reviewDto = reviewService.update(review.getReviewCode(), "수정콘텐츠", 1, "updateImageUrl");

        //쿼리 확인을 위한 영속성컨텍스트 초기화
        em.flush();
        em.clear();

        //then
        Assertions.assertThat(reviewDto)
                .extracting("content", "rating", "imageUrl")
                .contains("수정콘텐츠", 1, "updateImageUrl");
    }

    @Test
    @DisplayName("검색조건별 리뷰 전체조회")
    public void getReviewsBy() throws Exception {
        //given
        Member member = Member.builder()
                .email("zerobase@naver.com")
                .nickname("닉네임")
                .role(Role.USER)
                .build();

        Shop shop = Shop.builder()
                .name("shop1")
                .latitude(12.0)
                .longitude(13.0)
                .rating(1.0)
                .build();

        shopRepository.save(shop);
        memberRepository.save(member);

        Reservation reservation1 = getReservation(member, shop);
        reservation1.updateArrivalStatus();
        Reservation reservation2 = getReservation(member, shop);
        reservation2.updateArrivalStatus();
        Reservation reservation3 = getReservation(member, shop);
        reservation3.updateArrivalStatus();
        Reservation reservation4 = getReservation(member, shop);
        reservation4.updateArrivalStatus();
        Reservation reservation5 = getReservation(member, shop);
        reservation5.updateArrivalStatus();


        reservationRepository.saveAll(
                Arrays.asList(reservation1, reservation2, reservation3, reservation4, reservation5)
        );

        Review review1 = getReview(member, shop, reservation1, "imageUrl1", 1, "content1");
        Review review2 = getReview(member, shop, reservation2, "imageUrl2", 2, "content2");
        Review review3 = getReview(member, shop, reservation3, "imageUrl3", 3, "content3");
        Review review4 = getReview(member, shop, reservation4, "imageUrl4", 4, "content4");
        Review review5 = getReview(member, shop, reservation5, "imageUrl5", 5, "content5");
        reviewRepository.saveAll(
                Arrays.asList(review1, review2, review3, review4, review5)
        );

        SearchConditionReviewDto condition = SearchConditionReviewDto.builder()
                .email("zerobase@naver.com")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 5);


        //when
        Page<ReviewDto> reviews = reviewService.getReviewsBy(condition, pageRequest);

        //then
        Assertions.assertThat(reviews.getContent())
                .extracting("member", "shop", "reviewCode","rating", "content", "imageUrl")
                .containsExactlyInAnyOrder(
                        tuple(member, shop, review1.getReviewCode(), review1.getRating(), review1.getContent(), review1.getImageUrl()),
                        tuple(member, shop, review2.getReviewCode(), review2.getRating(), review2.getContent(), review2.getImageUrl()),
                        tuple(member, shop, review3.getReviewCode(), review3.getRating(), review3.getContent(), review3.getImageUrl()),
                        tuple(member, shop, review4.getReviewCode(), review4.getRating(), review4.getContent(), review4.getImageUrl()),
                        tuple(member, shop, review5.getReviewCode(), review5.getRating(), review5.getContent(), review5.getImageUrl())
                );
    }

    private static Review getReview(Member member, Shop shop, Reservation reservation, String imageUrl, int rating, String content) {
        return Review.builder()
                .member(member)
                .shop(shop)
                .reservation(reservation)
                .imageUrl(imageUrl)
                .rating(rating)
                .content(content)
                .build();
    }

    private static Reservation getReservation(Member member, Shop shop) {
        return Reservation.builder()
                .member(member)
                .shop(shop)
                .startDateTime(LocalDateTime.now())
                .endDateTime(LocalDateTime.now())
                .build();
    }
}
