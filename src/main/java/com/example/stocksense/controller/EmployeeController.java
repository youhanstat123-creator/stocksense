package com.example.stocksense.controller;

import com.example.stocksense.dto.EmployeeDTO;
import com.example.stocksense.entity.EmployeeEntity;
import com.example.stocksense.service.intf.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/emp")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Value("${app.upload.dir}")
    private String uploadDir;

    /** 사원등록 폼 */
    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("dto", new EmployeeDTO());
        model.addAttribute("company", "2Way Smart Work Company");
        return "employee/empinput";
    }

    /** 사원등록 저장 (이미지도 받기) */
    @PostMapping("/new")
    public String save(@Valid @ModelAttribute("dto") EmployeeDTO dto,
                       BindingResult br,
                       Model mo,
                       @RequestParam(required = false) MultipartFile imageFile,
                       RedirectAttributes ra) {

        if (br.hasErrors()) {
            mo.addAttribute("company", "2Way Smart Work Company");
            return "employee/empinput";
        }

        try {
            EmployeeEntity ep = dto.toEntityForCreate();

            // ✅ 이미지 저장 (선택)
            if (imageFile != null && !imageFile.isEmpty()) {
                String savedName = saveFile(imageFile, dto.getEmpId());
                ep.setImageName(savedName);
            }

            employeeService.save(ep);
            ra.addAttribute("msg", "사원 등록 완료");
        } catch (Exception e) {
            ra.addAttribute("error", "사원 등록 실패: " + (e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
        }
        return "redirect:/emp/list";
    }

    /** 사원목록 */
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String msg,
                       @RequestParam(required = false) String error,
                       Model model) {

        List<EmployeeEntity> list = employeeService.findAll();
        model.addAttribute("list", list);

        model.addAttribute("company", "2Way Smart Work Company");
        model.addAttribute("msg", msg);
        model.addAttribute("error", error);

        return "employee/empout";
    }

    /** 삭제 */
    @GetMapping("/delete")
    public String delete(@RequestParam String empId, RedirectAttributes ra) {
        try {
            employeeService.deleteById(empId);
            ra.addAttribute("msg", "삭제 완료");
        } catch (Exception e) {
            ra.addAttribute("error", "삭제 실패: " + (e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
        }
        return "redirect:/emp/list";
    }

    /** 수정 폼 */
    @GetMapping("/edit")
    public String editForm(@RequestParam String empId, Model model, RedirectAttributes ra) {
        EmployeeEntity emp = employeeService.findById(empId);

        if (emp == null) {
            ra.addAttribute("error", "해당 사원이 없습니다. (empId=" + empId + ")");
            return "redirect:/emp/list";
        }

        // 지금처럼 엔티티를 dto로 넘겨도 OK (템플릿에서 dto.xxx로 쓰고 있으니까)
        model.addAttribute("dto", emp);
        model.addAttribute("company", "2Way Smart Work Company");
        return "employee/empupdateview";
    }

    /** 수정 저장 (핵심: 기존 이미지 유지 + 새 파일이면 교체) */
    @PostMapping("/edit")
    public String editSave(@RequestParam String empId,
                           @RequestParam String empName,
                           @RequestParam(required = false) @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate birth,
                           @RequestParam String dept,
                           @RequestParam String phone,
                           @RequestParam(required = false) String status,
                           @RequestParam(required = false) MultipartFile imageFile,
                           RedirectAttributes ra) {

        try {
            EmployeeEntity old = employeeService.findById(empId);
            if (old == null) {
                ra.addAttribute("error", "수정 실패: 해당 사원이 없습니다. (empId=" + empId + ")");
                return "redirect:/emp/list";
            }

            old.setEmpName(empName);
            old.setBirth(birth);
            old.setDept(dept);
            old.setPhone(phone);
            old.setStatus(status);

            // ✅ 새 이미지 선택했으면 저장 후 교체
            if (imageFile != null && !imageFile.isEmpty()) {
                String savedName = saveFile(imageFile, empId);
                old.setImageName(savedName);
            }
            // ✅ 선택 안 하면 기존 imageName 유지

            employeeService.update(old);
            ra.addAttribute("msg", "수정 완료");
        } catch (Exception e) {
            ra.addAttribute("error", "수정 실패: " + (e.getMessage() == null ? "알 수 없는 오류" : e.getMessage()));
        }
        return "redirect:/emp/list";
    }

    private String saveFile(MultipartFile file, String empId) throws Exception {
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) ext = original.substring(dot);

        String savedName = "emp_" + empId + "_" + UUID.randomUUID() + ext;
        Path target = Paths.get(uploadDir).resolve(savedName).normalize();
        Files.copy(file.getInputStream(), target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return savedName;
    }
}
