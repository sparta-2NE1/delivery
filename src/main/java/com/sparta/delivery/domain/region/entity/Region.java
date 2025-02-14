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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long region_id;

    @Column
    private String province;
    @Column
    private String city;
    @Column
    private String localty;

    @OneToOne
    @JoinColumn(name = "store_id")
    private Stores stores;
}
