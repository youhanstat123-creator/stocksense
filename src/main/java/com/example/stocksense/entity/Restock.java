package com.example.stocksense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name="RESTOCK_SS")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@SequenceGenerator(name="restock_seq", sequenceName="RESTOCK_SEQ", allocationSize=1)
public class Restock {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restock_seq")
    @Column(name="RESTOCK_ID")
    private Long restockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PROD_ID", nullable=false)
    private Product product;

    @Column(name="ETA_DATE", nullable=false)
    private LocalDate etaDate;

    @Column(name="QTY", nullable=false)
    private int qty;

    @Column(name="NOTE", length=200)
    private String note;
}
