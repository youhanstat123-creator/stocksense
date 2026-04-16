package com.example.stocksense.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductDTO {

    private String prodName;
    private String areaType;   // "사무실" / "현장"
    private String category;

    private int price;
    private int stock;
    private int minStock;

    private String managerId;

    private String imageName;

    private String etaDate;   // yyyy-MM-dd
    private Integer etaQty;
    private String etaNote;
}
