package com.example.stocksense.service.intf;

import com.example.stocksense.dto.ProductDTO;
import com.example.stocksense.entity.Product;
import com.example.stocksense.entity.Restock;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService {

    List<Product> dangerTop();
    long totalCount();
    long dangerCount();
    long warnCount();
    long okCount();

    List<Product> listPage(int page, int size, String kw, String status, String managerId);
    long totalCountFiltered(String kw, String status, String managerId);

    Map<Long, Restock> nextRestockMap(List<Product> pageList);

    void create(ProductDTO dto, MultipartFile imageFile);
    Product findByIdOrThrow(Long id);
    void update(Long id, ProductDTO dto, MultipartFile imageFile);
    void delete(Long id);
    void adjustStock(Long id, int delta);
}
