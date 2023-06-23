package com.zerobase.reservation.repository.reservation;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.zerobase.reservation.domain.reservation.Reservation;
import com.zerobase.reservation.dto.reservation.SearchConditionReservationDto;
import com.zerobase.reservation.repository.QueryDsl4RepositorySupport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.zerobase.reservation.domain.reservation.QReservation.reservation;

public class ReservationRepositoryQueryDslImpl extends QueryDsl4RepositorySupport implements ReservationRepositoryQueryDsl {
    public ReservationRepositoryQueryDslImpl() {
        super(Reservation.class);
    }

    @Override
    public Page<Reservation> findAllBySearchConditions(SearchConditionReservationDto searchCondition, Pageable pageable) {
        return applyPagination(pageable, (query) -> query
                        .selectFrom(reservation)
                        .join(reservation.member).fetchJoin()
                        .join(reservation.shop).fetchJoin()
                        .where(
                                shopCodeEq(searchCondition.getShopCode()),
                                emailEq(searchCondition.getEmail()),
                                startDateBetween(searchCondition.getDate())
                        ),
                countQuery -> countQuery
                        .select(reservation.count())
                        .from(reservation)
                        .where(
                                shopCodeEq(searchCondition.getShopCode()),
                                emailEq(searchCondition.getEmail()),
                                startDateBetween(searchCondition.getDate())
                        )
        );
    }

    /**
     * 날짜별 검색조건
     **/
    private BooleanExpression startDateBetween(LocalDate date) {
        if (date != null) {
            LocalDateTime startDateTime = date.atStartOfDay();
            LocalDateTime endDateTime = startDateTime.plusDays(1).minusSeconds(1);
            return reservation.startDateTime.between(startDateTime, endDateTime);
        }
        return null;
    }

    /**
     * 상점별 검색조건
     */
    private BooleanExpression shopCodeEq(String shopCode) {
        return !StringUtils.hasText(shopCode) ? null : reservation.shop.shopCode.eq(shopCode);
    }

    /**
     * 회원별 검색조건
     */
    private BooleanExpression emailEq(String email) {
        return !StringUtils.hasText(email) ? null : reservation.member.email.eq(email);
    }

}


