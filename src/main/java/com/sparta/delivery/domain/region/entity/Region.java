package com.sparta.delivery.domain.region.entity;

import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.store.entity.Stores;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="p_reigion")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    Long regionid;

    @Column(nullable = false)//(단위 : 도)
    private String province;
    @Column(nullable = false)//(단위 : 시)
    private String city;
    @Column(nullable = false)//(단위 : 동)
    private String locality;

    @OneToOne
    @JoinColumn(name = "store_id")
    private Stores stores;
}
