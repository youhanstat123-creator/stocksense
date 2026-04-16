package com.example.stocksense.repository;

import com.example.stocksense.entity.Restock;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RestockRepository extends JpaRepository<Restock, Long> {

    @Query(value = """
    SELECT r.*
    FROM RESTOCK_SS r
    WHERE r.RESTOCK_ID IN (
        SELECT MIN(r2.RESTOCK_ID)
        FROM RESTOCK_SS r2
        WHERE r2.PROD_ID IN (:prodIds)
          AND r2.ETA_DATE = (
              SELECT MIN(r3.ETA_DATE)
              FROM RESTOCK_SS r3
              WHERE r3.PROD_ID = r2.PROD_ID
                AND r3.ETA_DATE >= TRUNC(SYSDATE)
          )
        GROUP BY r2.PROD_ID
    )
    """, nativeQuery = true)
    List<Restock> findNextRestocks(@Param("prodIds") List<Long> prodIds);

    // ✅ 추가: 상품 삭제 전에 해당 상품의 입고예정 먼저 삭제
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM RESTOCK_SS WHERE PROD_ID = :prodId", nativeQuery = true)
    int deleteByProdId(@Param("prodId") Long prodId);
}
