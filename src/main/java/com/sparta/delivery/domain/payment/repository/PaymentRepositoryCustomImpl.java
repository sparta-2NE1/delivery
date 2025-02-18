package com.sparta.delivery.domain.payment.repository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.domain.payment.dto.SearchDto;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.payment.entity.QPayment;
import com.sparta.delivery.domain.order.entity.QOrder;
import com.sparta.delivery.domain.card.entity.QCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Payment> searchPayments(SearchDto searchDto, String username) {
        QPayment payment = QPayment.payment;
        QOrder order = QOrder.order;
        QCard card = QCard.card;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(payment.user.username.eq(username));

        // 최소 금액 설정
        if (searchDto.getMinAmount() != null) {
            builder.and(payment.amount.goe(searchDto.getMinAmount()));
        }

        // 최대 금액 설정
        if (searchDto.getMaxAmount() != null) {
            builder.and(payment.amount.loe(searchDto.getMaxAmount()));
        }

        // 주문 상태 설정
        if (searchDto.getOrderStatus() != null) {
            builder.and(payment.order.orderStatus.eq(searchDto.getOrderStatus()));
        }

        // 주문 타입 설정
        if (searchDto.getOrderType() != null) {
            builder.and(payment.order.orderType.eq(searchDto.getOrderType()));
        }

        // 결제 시간 설정
        if (searchDto.getPaymentTime() != null) {
            builder.and(payment.createdAt.after(searchDto.getPaymentTime()));
        }

        // 카드사 설정
        if (searchDto.getCardCompany() != null && !searchDto.getCardCompany().isEmpty()) {
            builder.and(payment.card.cardCompany.eq(searchDto.getCardCompany()));
        }

        return queryFactory
                .selectFrom(payment)
                .leftJoin(payment.order, order).fetchJoin()
                .leftJoin(payment.card, card).fetchJoin()
                .where(builder)
                .fetch();
    }
}
