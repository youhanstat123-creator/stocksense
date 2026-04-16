package com.example.stocksense.controller;

import com.example.stocksense.repository.EmployeeRepository;
import com.example.stocksense.service.intf.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class HomeController {

    private final ProductService productService;
    private final EmployeeRepository employeeRepository;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("company", "2Way Smart Work Company");
        model.addAttribute("dangerList", productService.dangerTop());
        model.addAttribute("totalProducts", productService.totalCount());
        model.addAttribute("dangerCount", productService.dangerCount());
        model.addAttribute("warnCount", productService.warnCount());
        model.addAttribute("okCount", productService.okCount());
        model.addAttribute("employeeCount", employeeRepository.count());
        return "dashboard/home";
    }
}
