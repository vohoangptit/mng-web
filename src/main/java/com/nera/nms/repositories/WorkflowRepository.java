package com.nera.nms.repositories;


import com.nera.nms.models.Workflow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {

    Workflow findOneById(Long id);

    @Query("from Workflow s where s.isDeleted = false Order by s.createdDate desc")
    List<Workflow> findAllWorkflow();

    @Query("from Workflow s where s.isDeleted = false and s.searchString like %:searchString%")
    Page<Workflow> findLikeSearchString(
            @Param("searchString") String searchString,
            Pageable pageable);

    @Query("from Workflow s where s.isDeleted = false and s.searchString like %:searchString% and s.createdDate BETWEEN :startDate AND :endDate")
    Page<Workflow> findLikeSearchStringAndDate(
            Pageable pageable,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("searchString") String searchString);

    @Query("from Workflow s where s.isDeleted = false and s.searchString like %:searchString% and s.createdDate >= :startDate")
    Page<Workflow> findLikeSearchStringAndStartDate(
            Pageable pageable,
            @Param("startDate") Date startDate,
            @Param("searchString") String searchString);

    @Query("from Workflow s where s.isDeleted = false and s.searchString like %:searchString% and s.createdDate <= :endDate")
    Page<Workflow> findLikeSearchStringAndEndDate(
            Pageable pageable,
            @Param("endDate") Date endDate,
            @Param("searchString") String searchString);

    @Query("from Workflow s where s.isDeleted = false and s.searchString like %:searchString% and s.createdDate >= :startDate and s.createdBy in (:createdBy)")
    Page<Workflow> findLikeCreatedByAndStartDate(
            Pageable pageable,
            @Param("startDate") Date startDate,
            @Param("searchString") String searchString,
            @Param("createdBy") List<String> createdBy);

    @Query("from Workflow s where s.isDeleted = false and s.searchString like %:searchString% and s.createdDate <= :endDate and s.createdBy in (:createdBy)")
    Page<Workflow> findLikeCreatedByAndEndDate(
            Pageable pageable,
            @Param("endDate") Date endDate,
            @Param("searchString") String searchString,
            @Param("createdBy") List<String> createdBy);

    @Query("from Workflow s where s.isDeleted = false and s.searchString like %:searchString% " +
            "and s.createdDate BETWEEN :startDate AND :endDate " +
            "and s.createdBy in (:createdBy)")
    Page<Workflow> findByAllConditions(
            Pageable pageable,
            @Param("searchString") String searchString,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("createdBy") List<String> createdBy);

    @Query("from Workflow s where s.isDeleted = false and s.searchString like %:searchString% and s.createdBy in (:createdBy)")
    Page<Workflow> findByCreatedBy(
            Pageable pageable,
            @Param("searchString") String searchString,
            @Param("createdBy") List<String> createdBy);

    @Query("Select s.createdBy from Workflow s where s.isDeleted = false")
    Set<String> findAllCreatedPerson();
    
    @Query("from Workflow s where s.isDeleted = false and s.name = :name")
    Workflow findByName(@Param("name") String nameWorkflow);

    @Query("from Workflow s where s.isDeleted = false and s.id <> :id and s.name = :name")
    Workflow findByName(@Param("name") String nameWorkflow, @Param("id") Long id);
}
