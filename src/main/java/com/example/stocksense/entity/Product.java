package com.example.stocksense.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PRODUCT_SS")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_seq"
    )
    @SequenceGenerator(
            name = "product_seq",
            sequenceName = "PRODUCT_SS_SEQ",
            allocationSize = 1
    )
    @Column(name = "PROD_ID")
    private Long prodId;

    @Column(name = "PROD_NAME", nullable = false)
    private String prodName;

    @Column(name = "CATEGORY")
    private String category;

    @Column(name = "AREA_TYPE")
    private String areaType;

    // ✅ 추가된 필드 (이번 에러 해결 핵심)
    @Column(name = "IMAGE_NAME")
    private String imageName;

    @Column(name = "STOCK")
    private int stock;

    @Column(name = "MIN_STOCK")
    private int minStock;

    @Column(name = "PRICE")
    private int price;

    // 담당 사원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_ID")
    private EmployeeEntity manager;
}
