package com.zerobase.reservation.repository.kiosk;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.zerobase.reservation.domain.kiosk.Kiosk;
import com.zerobase.reservation.dto.kiosk.SearchConditionKioskDto;
import com.zerobase.reservation.repository.QueryDsl4RepositorySupport;
import com.zerobase.reservation.type.InstallationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import static com.zerobase.reservation.domain.kiosk.QKiosk.kiosk;

public class KioskRepositoryQueryDslImpl extends QueryDsl4RepositorySupport implements KioskRepositoryQueryDsl {

    public KioskRepositoryQueryDslImpl() {
        super(Kiosk.class);
    }

    @Override
    public Page<Kiosk> findAllBySearchConditions(SearchConditionKioskDto condition, Pageable pageable) {
        return applyPagination(pageable, query -> query
                        .selectFrom(kiosk)
                        .leftJoin(kiosk.shop).fetchJoin()
                        .where(
                                shopCodeEq(condition.getShopCode()),
                                installationStatusEq(condition.getInstallationStatus())
                        ),
                countQuery -> countQuery
                        .selectFrom(kiosk)
                        .where(
                                shopCodeEq(condition.getShopCode()),
                                installationStatusEq(condition.getInstallationStatus())
                        )
        );
    }


    /**
     * 상점별 검색조건
     */
    private BooleanExpression shopCodeEq(String shopCode) {
        return !StringUtils.hasText(shopCode) ? null : kiosk.shop.shopCode.eq(shopCode);
    }

    /**
     * 설치여부 검색조건
     */
    private BooleanExpression installationStatusEq(InstallationStatus installationStatus) {
        return ObjectUtils.isEmpty(installationStatus) ? null : kiosk.installationStatus.eq(installationStatus);
    }

}
