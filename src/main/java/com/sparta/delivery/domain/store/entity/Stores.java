package com.sparta.delivery.domain.store.entity;
import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.enums.Category;
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
@Table(name="p_stores")
public class Stores extends Timestamped {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID storeid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "stores")
    private List<Region> regionList = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    private Category category;
}
