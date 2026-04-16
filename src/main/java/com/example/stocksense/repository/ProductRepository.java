package com.example.stocksense.repository;

import com.example.stocksense.entity.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
        SELECT * FROM (
            SELECT * FROM product_ss
            ORDER BY stock ASC, prod_id DESC
        )
        WHERE ROWNUM <= 10
        """, nativeQuery = true)
    List<Product> dangerTop10();

    @Query(value = "SELECT COUNT(*) FROM product_ss", nativeQuery = true)
    long countAll11g();

    @Query(value = "SELECT COUNT(*) FROM product_ss WHERE stock <= min_stock", nativeQuery = true)
    long countDanger11g();

    @Query(value = "SELECT COUNT(*) FROM product_ss WHERE stock > min_stock AND stock <= (min_stock + 5)", nativeQuery = true)
    long countWarn11g();

    @Query(value = "SELECT COUNT(*) FROM product_ss WHERE stock > (min_stock + 5)", nativeQuery = true)
    long countOk11g();

    @Query(value = """
        SELECT * FROM (
            SELECT t.*, ROWNUM rn FROM (
                SELECT *
                FROM product_ss
                WHERE
                    ( :kw IS NULL OR :kw = '' OR
                      LOWER(prod_name) LIKE '%'||LOWER(:kw)||'%' OR
                      LOWER(category)  LIKE '%'||LOWER(:kw)||'%' OR
                      LOWER(area_type) LIKE '%'||LOWER(:kw)||'%'
                    )
                    AND ( :managerId IS NULL OR :managerId = '' OR manager_id = :managerId )
                    AND (
                        :status IS NULL OR :status = '' OR :status = 'ALL' OR
                        (:status = 'DANGER' AND stock <= min_stock) OR
                        (:status = 'WARN' AND stock > min_stock AND stock <= (min_stock + 5)) OR
                        (:status = 'OK' AND stock > (min_stock + 5))
                    )
                ORDER BY stock ASC, prod_id DESC
            ) t
            WHERE ROWNUM <= :endRow
        )
        WHERE rn > :startRow
        """, nativeQuery = true)
    List<Product> findPage11gFiltered(
            @Param("startRow") int startRow,
            @Param("endRow") int endRow,
            @Param("kw") String kw,
            @Param("status") String status,
            @Param("managerId") String managerId
    );

    @Query(value = """
        SELECT COUNT(*)
        FROM product_ss
        WHERE
            ( :kw IS NULL OR :kw = '' OR
              LOWER(prod_name) LIKE '%'||LOWER(:kw)||'%' OR
              LOWER(category)  LIKE '%'||LOWER(:kw)||'%' OR
              LOWER(area_type) LIKE '%'||LOWER(:kw)||'%'
            )
            AND ( :managerId IS NULL OR :managerId = '' OR manager_id = :managerId )
            AND (
                :status IS NULL OR :status = '' OR :status = 'ALL' OR
                (:status = 'DANGER' AND stock <= min_stock) OR
                (:status = 'WARN' AND stock > min_stock AND stock <= (min_stock + 5)) OR
                (:status = 'OK' AND stock > (min_stock + 5))
            )
        """, nativeQuery = true)
    long countFiltered11g(
            @Param("kw") String kw,
            @Param("status") String status,
            @Param("managerId") String managerId
    );
}
