package com.example.stocksense.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "EMPLOYEE2") // 🔥 반드시 DB와 동일
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEntity {

    @Id
    @Column(name = "EMP_ID", length = 20)
    private String empId;

    @Column(name = "EMP_NAME", nullable = false)
    private String empName;

    @Column(name = "BIRTH")
    private LocalDate birth;

    @Column(name = "DEPT")
    private String dept;

    @Column(name = "PHONE")
    private String phone;

    @Column(name = "STATUS")
    private String status;
    // ✅ 추가
    @Column(name = "IMAGE_NAME")
    private String imageName;

}
