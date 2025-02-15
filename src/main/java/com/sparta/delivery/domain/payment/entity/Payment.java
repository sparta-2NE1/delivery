package com.sparta.delivery.domain.payment.entity;


import com.sparta.delivery.domain.card.entity.Card;
import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "p_payment")
public class Payment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID payment_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

//    @OneToOne
//    @JoinColumn(name="order_id")
//    private Order order;

    private Integer amount;

    private LocalDateTime payment_time;


}
