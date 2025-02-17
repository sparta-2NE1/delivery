package com.sparta.delivery.domain.store.entity;
import com.sparta.delivery.domain.common.Timestamped;
import com.sparta.delivery.domain.region.entity.Region;
import com.sparta.delivery.domain.store.enums.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="p_stores")
public class Stores extends Timestamped {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long storeid;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private boolean status;

    @OneToOne(mappedBy = "stores")
    private Region region;


    @Enumerated(EnumType.STRING)
    private Category category;
}
