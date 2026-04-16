package com.example.stocksense.controller;

import com.example.stocksense.dto.ProductDTO;
import com.example.stocksense.entity.Product;
import com.example.stocksense.entity.Restock;
import com.example.stocksense.service.intf.EmployeeService;
import com.example.stocksense.service.intf.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final EmployeeService employeeService;

    @GetMapping("/list")
    public String list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String kw,
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false) String managerId,
            @RequestParam(required = false) String msg,
            @RequestParam(required = false) String error,
            Model model
    ) {
        long total = productService.totalCountFiltered(kw, status, managerId);
        long totalPages = (total + size - 1) / size;

        model.addAttribute("company", "2Way Smart Work Company");

        var pageList = productService.listPage(page, size, kw, status, managerId);
        model.addAttribute("list", pageList);

        // ✅ 추가 1) 다음 입고예정 맵(productId -> Restock)
        model.addAttribute("restockMap", productService.nextRestockMap(pageList));

        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("kw", kw);
        model.addAttribute("status", status);
        model.addAttribute("managerId", managerId);
        model.addAttribute("emps", employeeService.activeEmployees());

        model.addAttribute("msg", msg);
        model.addAttribute("error", error);

        return "product/list";
    }


    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("company", "2Way Smart Work Company");
        model.addAttribute("dto", new ProductDTO());
        model.addAttribute("emps", employeeService.activeEmployees());
        return "product/form";
    }

    @PostMapping("/new")
    public String create(
            @ModelAttribute ProductDTO dto,
            @RequestParam(name="imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes ra
    ) {
        try {
            productService.create(dto, imageFile);
            ra.addAttribute("msg", "상품등록완료");
            return "redirect:/product/list";
        } catch (Exception e) {
            ra.addAttribute("error", "등록실패: " + (e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
            return "redirect:/product/list";
        }
    }

    @GetMapping("/detail")
    public String detail(@RequestParam("id") Long id, Model model) {
        model.addAttribute("company", "2Way Smart Work Company");
        Product product = productService.findByIdOrThrow(id);
        model.addAttribute("product", product);
        return "product/detail";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam("id") Long id, Model model) {
        model.addAttribute("company", "2Way Smart Work Company");

        Product p = productService.findByIdOrThrow(id);
        ProductDTO dto = new ProductDTO();
        dto.setProdName(p.getProdName());
        dto.setAreaType(p.getAreaType());
        dto.setCategory(p.getCategory());
        dto.setPrice(p.getPrice());
        dto.setStock(p.getStock());
        dto.setMinStock(p.getMinStock());
        dto.setManagerId(p.getManager().getEmpId());
        dto.setImageName(p.getImageName());
        dto.setAreaType(p.getAreaType());

        model.addAttribute("id", id);
        model.addAttribute("dto", dto);
        model.addAttribute("product", p);
        model.addAttribute("emps", employeeService.activeEmployees());

        return "product/edit";
    }

    @PostMapping("/edit")
    public String edit(
            @RequestParam("id") Long id,
            @ModelAttribute ProductDTO dto,
            @RequestParam(name="imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes ra
    ) {
        try {
            productService.update(id, dto, imageFile);
            ra.addAttribute("msg", "수정완료");
            return "redirect:/product/detail?id=" + id;
        } catch (Exception e) {
            ra.addAttribute("error", "수정실패: " + (e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
            return "redirect:/product/edit?id=" + id;
        }
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Long id, RedirectAttributes ra) {
        try {
            productService.delete(id);
            ra.addAttribute("msg", "삭제완료");
            return "redirect:/product/list";
        } catch (Exception e) {
            ra.addAttribute("error", "삭제실패: " + (e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
            return "redirect:/product/list";
        }

    }
    @PostMapping("/adjust")
    public String adjustStock(
            @RequestParam("id") Long id,
            @RequestParam("delta") int delta,
            RedirectAttributes ra
    ) {
        try {
            productService.adjustStock(id, delta);
            ra.addAttribute("msg", "재고가 " + (delta >= 0 ? "+" : "") + delta + " 조정되었습니다.");
        } catch (Exception e) {
            ra.addAttribute("error", "재고 조정 실패: " + (e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
        }
        return "redirect:/product/list";
    }

}
