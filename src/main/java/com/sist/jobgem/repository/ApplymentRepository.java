package com.sist.jobgem.repository;

import com.sist.jobgem.dto.ApplymentDto;
import com.sist.jobgem.entity.Applyment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

@Repository
public interface ApplymentRepository extends JpaRepository<Applyment, Integer> {
    int countByPoIdx(int poIdx);

    int countByJoIdxAndApState(int joIdx, int apState);

    int countByJoIdxAndApReadAndApState(int joIdx, int apRead, int apState);

    // 공통 조건을 처리하는 메서드
    @Query("SELECT new com.sist.jobgem.dto.ApplymentDto(a) " +
            "FROM Applyment a " +
            "WHERE a.joIdx = :joIdx " +
            "AND a.apState = 1 " +
            "AND (:apRead IS NULL OR a.apRead = :apRead) " +
            "AND (:startDate IS NULL OR a.apDate >= :startDate) " +
            "AND (:endDate IS NULL OR a.apDate <= :endDate)")
    Page<ApplymentDto> searchApplyments(
            @Param("joIdx") int joIdx,
            @Param("apRead") Integer apRead, // int -> Integer로 수정하여 null 처리 가능
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    Page<Applyment> findByJoIdxAndApState(int idx, int apState, Pageable pageable);

    @Query("SELECT a FROM Applyment a WHERE a.poIdx = :poIdx AND a.apState = :apState")
    Page<Applyment> findByPoIdxAndApState(@Param("poIdx") int poIdx, @Param("apState") int apState, Pageable pageable);

}
