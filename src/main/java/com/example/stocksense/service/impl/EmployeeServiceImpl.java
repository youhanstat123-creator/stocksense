package com.example.stocksense.service.impl;

import com.example.stocksense.entity.EmployeeEntity;
import com.example.stocksense.repository.EmployeeRepository;
import com.example.stocksense.service.intf.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public void save(EmployeeEntity employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeEntity> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public EmployeeEntity findById(String empId) {
        return employeeRepository.findById(empId).orElse(null);
    }

    @Override
    public void deleteById(String empId) {
        employeeRepository.deleteById(empId);
    }

    @Override
    public void update(EmployeeEntity employee) {
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeEntity> activeEmployees() {
        return employeeRepository.findByStatus("ACTIVE");
    }
}
