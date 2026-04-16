package com.example.stocksense.config;

import com.example.stocksense.entity.EmployeeEntity;
import com.example.stocksense.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DataInit implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {

        if (employeeRepository.count() > 0) return;

        employeeRepository.save(
                new EmployeeEntity(
                        "A1001",
                        "김총무",
                        LocalDate.of(2000, 1, 1),
                        "총무",
                        "010-1111-2222",
                        "ACTIVE",
                        null // ✅ imageName
                )
        );

        employeeRepository.save(
                new EmployeeEntity(
                        "B2001",
                        "이영업",
                        LocalDate.of(1990, 5, 10),
                        "영업",
                        "010-3333-4444",
                        "ACTIVE",
                        null // ✅ imageName
                )
        );

        System.out.println("✅ EMPLOYEE2 초기 데이터 생성 완료");
    }
}
