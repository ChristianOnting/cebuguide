package com.cebuguide.backend.repository;

import com.cebuguide.backend.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpotRepository extends JpaRepository<Spot, Long> {

    long countByCategoryId(Long categoryId);

    /** Administrator search: keyword in name or description, and status ALL, ACTIVE, or INACTIVE. */
    @Query("""
            select s from Spot s
            where (lower(s.name) like lower(concat('%', :keyword, '%'))
                   or lower(s.description) like lower(concat('%', :keyword, '%')))
              and (:status = 'ALL'
                   or (:status = 'ACTIVE' and s.active = true)
                   or (:status = 'INACTIVE' and s.active = false))
            order by s.name asc
            """)
    List<Spot> adminSearch(@Param("keyword") String keyword, @Param("status") String status);
}
