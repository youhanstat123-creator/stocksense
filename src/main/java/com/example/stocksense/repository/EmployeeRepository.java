package com.example.stocksense.repository;

import com.example.stocksense.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {

    // ✅ JPA 파생쿼리
    List<EmployeeEntity> findByStatus(String status);
}
