package com.example.stocksense.service.impl;

import com.example.stocksense.dto.ProductDTO;
import com.example.stocksense.entity.EmployeeEntity;
import com.example.stocksense.entity.Product;
import com.example.stocksense.entity.Restock;
import com.example.stocksense.repository.EmployeeRepository;
import com.example.stocksense.repository.ProductRepository;
import com.example.stocksense.repository.RestockRepository;
import com.example.stocksense.service.intf.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;
    private final RestockRepository restockRepository;
    private final FileStore fileStore;

    @Override
    public List<Product> dangerTop() {
        return productRepository.dangerTop10();
    }

    @Override
    public long totalCount() {
        return productRepository.countAll11g();
    }

    @Override
    public long dangerCount() {
        return productRepository.countDanger11g();
    }

    @Override
    public long warnCount() {
        return productRepository.countWarn11g();
    }

    @Override
    public long okCount() {
        return productRepository.countOk11g();
    }

    @Override
    public List<Product> listPage(int page, int size, String kw, String status, String managerId) {
        if (page < 1) page = 1;
        int startRow = (page - 1) * size;
        int endRow = page * size;
        return productRepository.findPage11gFiltered(startRow, endRow, kw, status, managerId);
    }

    @Override
    public long totalCountFiltered(String kw, String status, String managerId) {
        return productRepository.countFiltered11g(kw, status, managerId);
    }

    @Override
    public Map<Long, Restock> nextRestockMap(List<Product> pageList) {
        if (pageList == null || pageList.isEmpty()) return Collections.emptyMap();

        List<Long> ids = pageList.stream()
                .map(Product::getProdId)
                .collect(Collectors.toList());

        List<Restock> restocks = restockRepository.findNextRestocks(ids);

        Map<Long, Restock> map = new HashMap<>();
        for (Restock r : restocks) {
            map.put(r.getProduct().getProdId(), r);
        }
        return map;
    }

    @Override
    public void create(ProductDTO dto, MultipartFile imageFile) {

        EmployeeEntity manager = employeeRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new IllegalArgumentException("담당 사원 없음"));

        String savedImage = fileStore.save(imageFile);

        Product p = new Product();
        p.setProdName(dto.getProdName());
        p.setCategory(dto.getCategory());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setMinStock(dto.getMinStock());
        p.setManager(manager);
        p.setImageName(savedImage);
        p.setAreaType((dto.getAreaType() == null || dto.getAreaType().isBlank()) ? "사무실" : dto.getAreaType());

        productRepository.save(p);

        saveRestockIfPresent(dto, p);
    }

    @Override
    public Product findByIdOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다. id=" + id));
    }

    @Override
    @Transactional
    public void update(Long id, ProductDTO dto, MultipartFile imageFile) {

        Product p = findByIdOrThrow(id);

        EmployeeEntity manager = employeeRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new IllegalArgumentException("담당 사원 없음"));

        String savedImage = fileStore.save(imageFile);
        if (savedImage != null) p.setImageName(savedImage);

        p.setProdName(dto.getProdName());
        p.setCategory(dto.getCategory());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setMinStock(dto.getMinStock());
        p.setManager(manager);
        p.setAreaType((dto.getAreaType() == null || dto.getAreaType().isBlank()) ? "사무실" : dto.getAreaType());

        productRepository.save(p);

        // ✅ 핵심: 수정 시에는 기존 입고예정(메모) 삭제 후 최신 1건만 저장
        restockRepository.deleteByProdId(id);
        saveRestockIfPresent(dto, p);
    }


    private void saveRestockIfPresent(ProductDTO dto, Product p) {
        try {
            if (dto.getEtaDate() == null || dto.getEtaDate().isBlank()) return;
            if (dto.getEtaQty() == null) return;

            LocalDate date = LocalDate.parse(dto.getEtaDate());

            Restock r = new Restock();
            r.setProduct(p);
            r.setEtaDate(date);
            r.setQty(dto.getEtaQty());
            r.setNote(dto.getEtaNote());

            restockRepository.save(r);
        } catch (Exception ignored) {
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // ✅ 1) 자식(입고예정) 먼저 삭제
        restockRepository.deleteByProdId(id);

        // ✅ 2) 부모(상품) 삭제
        productRepository.deleteById(id);
    }


    @Override
    @Transactional
    public void adjustStock(Long id, int delta) {
        Product p = findByIdOrThrow(id);

        int next = p.getStock() + delta;
        if (next < 0) next = 0;

        p.setStock(next);
        productRepository.save(p);
    }
}
