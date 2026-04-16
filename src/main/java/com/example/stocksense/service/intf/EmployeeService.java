package com.example.stocksense.service.intf;

import com.example.stocksense.entity.EmployeeEntity;
import java.util.List;

public interface EmployeeService {

    void save(EmployeeEntity employee);

    List<EmployeeEntity> findAll();          // ✅ 통일

    EmployeeEntity findById(String empId);   // ✅ 통일

    void deleteById(String empId);           // ✅ 통일

    void update(EmployeeEntity employee);

    List<EmployeeEntity> activeEmployees();  // ✅ 유지
}
