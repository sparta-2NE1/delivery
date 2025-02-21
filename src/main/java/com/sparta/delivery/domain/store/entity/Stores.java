package com.sparta.delivery.domain.store.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.product.entity.Product;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.enums.Category;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "p_stores")
public class Stores extends Timestamped {
//올리는용

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID storeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "stores")
    private List<Region> regionList = new ArrayList<>();

    @OneToMany(mappedBy = "store")
    private List<Product> productList = new ArrayList<>();

    @OneToMany(mappedBy = "stores")
    private List<Order> orderList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column(nullable = false)
    private int starSum;//초기값

    @Column(nullable = false)
    private int reviewSum;//초기값

    @Enumerated(EnumType.STRING)
    private Category category;


}
