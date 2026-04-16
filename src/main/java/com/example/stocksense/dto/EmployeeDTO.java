package com.example.stocksense.dto;

import com.example.stocksense.entity.EmployeeEntity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    @NotBlank(message = "사원번호는 필수입니다.")
    @Pattern(regexp="^[A-Za-z0-9]{2,10}$", message="사원번호는 영문/숫자 2~10자리로 입력하세요.")
    private String empId;

    @NotBlank(message="이름은 필수입니다.")
    @Size(min=2, max=5, message="이름은 2~5글자로 입력하세요.")
    private String empName;

    @NotNull(message="생년월일은 필수입니다.")
    @Past(message="생년월일은 과거 날짜여야 합니다.")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate birth;

    @NotBlank(message="부서는 필수 선택사항입니다.")
    private String dept;

    @NotBlank(message="연락처는 필수입니다.")
    @Pattern(
            regexp="^(010-?\\d{4}-?\\d{4}$)",
            message="전화번호 형식이 올바르지 않습니다 (010-1234-5678)")
    private String phone;

    @Pattern(regexp="^(ACTIVE|INACTIVE|VACATION|LEAVE|CONTRACT|RESIGNED)?$", message="상태값이 올바르지 않습니다.")
    private String status;

    // ✅ 추가: 업로드 파일
    private MultipartFile imageFile;

    // ✅ 추가: 저장된 파일명(DB 저장용)
    private String imageName;

    public EmployeeEntity toEntityForCreate() {
        return new EmployeeEntity(empId, empName, birth, dept, phone, "ACTIVE", imageName);
    }

    public EmployeeEntity toEntityForUpdate() {
        EmployeeEntity ep = new EmployeeEntity();
        ep.setEmpId(empId);
        ep.setEmpName(empName);
        ep.setBirth(birth);
        ep.setDept(dept);
        ep.setPhone(phone);
        ep.setStatus(status);
        ep.setImageName(imageName);
        return ep;
    }
}
