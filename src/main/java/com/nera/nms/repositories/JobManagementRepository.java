package com.nera.nms.repositories;

import com.nera.nms.models.JobManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface JobManagementRepository extends JpaRepository<JobManagement, Long> {

    JobManagement findOneById(Long id);

    JobManagement findOneByName(String name);

    JobManagement findJobManagementByIdIsNotLikeAndNameEquals(long jobId, String name);

    List<JobManagement> findAllByDeletedFalseAndWorkflowIDEquals(long workflowID);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM nera.job_host where job_id =?1 and host_id=?2", nativeQuery = true)
    void deleteJobHostDetail(Long jobId, Long hostId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM nera.job_host where job_id =?1", nativeQuery = true)
    void deleteJobHostDetailByJobID(Long jobId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM nera.job where id =?1", nativeQuery = true)
    void deleteJobCustom(Long id);

    @Modifying
    @Transactional
    @Query(value = "Select host_id FROM nera.job_host where job_id =?1", nativeQuery = true)
    List<Number> getListJobHost(Long jobId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO nera.job_host(job_id,host_id) values(?1,?2)", nativeQuery = true)
    void saveJobHostDetail(Long jobId, Long hostId);

    @Query("Select j.createdBy from JobManagement j where j.deleted = false")
    Set<String> findAllCreatedPerson();

    @Query(value="Select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% group by j.id",
            countQuery = "Select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% group by j.id")
    Page<JobManagement> findAllRecord(
            @Param("searchString") String searchString,
            @Param("createdBy") String createdBy,
            Pageable pageable);


    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate BETWEEN :startDate AND :endDate and j.isActive = :jobActive group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate BETWEEN :startDate AND :endDate and j.isActive = :jobActive group by j.id")
    Page<JobManagement> findByAllCondition(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("createdBy") String createdBy,
            @Param("jobActive") Boolean jobActive,
            @Param("searchString") String searchString,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate >= :startDate and j.isActive = :jobActive group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate >= :startDate and j.isActive = :jobActive group by j.id")
    Page<JobManagement> findByStartDateAndStatus(
            @Param("startDate") Date startDate,
            @Param("createdBy") String createdBy,
            @Param("jobActive") Boolean jobActive,
            @Param("searchString") String searchString,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate <= :endDate and j.isActive = :jobActive group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate <= :endDate and j.isActive = :jobActive group by j.id")
    Page<JobManagement> findByEndDateAndStatus(
            @Param("endDate") Date endDate,
            @Param("createdBy") String createdBy,
            @Param("jobActive") Boolean jobActive,
            @Param("searchString") String searchString,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate BETWEEN :startDate AND :endDate group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate BETWEEN :startDate AND :endDate group by j.id")
    Page<JobManagement> findByStartEndDate(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("createdBy") String createdBy,
            @Param("searchString") String searchString,
            Pageable pageable);

    @Query(value="Select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate >= :startDate group by j.id",
            countQuery = "Select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate >= :startDate group by j.id")
    Page<JobManagement> findByStartDate(
            @Param("startDate") Date startDate,
            @Param("createdBy") String createdBy,
            @Param("searchString") String searchString,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate <= :endDate group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate <= :endDate group by j.id")
    Page<JobManagement> findByEndDate(
            @Param("endDate") Date endDate,
            @Param("createdBy") String createdBy,
            @Param("searchString") String searchString,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.isActive = :jobActive group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.isActive = :jobActive group by j.id")
    Page<JobManagement> findByStatus(
            @Param("jobActive") Boolean jobActive,
            @Param("createdBy") String createdBy,
            @Param("searchString") String searchString,
            Pageable pageable);

    @Query("from JobManagement j where j.deleted = false AND j.isActive = true")
    List<JobManagement> findAllNoPaging();

    @Query("select count(job) " +
            "from JobManagement job " +
            "inner join JobAssignment ja on ja.jobManagement = job.id " +
            "where job.deleted = false and job.workflowID = :workflowId and (ja.status = 'Rejected' or ja.status = 'Accepted' or ja.status = 'Executing')")
    int countJobByWorkflowId(@Param("workflowId") long workflowId);

    @Query(value = "select jobM from JobManagement jobM inner join jobM.hosts host where host.isActive=:active GROUP BY jobM.id",
            countQuery = "select count(jobM.id) from JobManagement jobM inner join jobM.hosts host where host.isActive=:active GROUP BY jobM.id")
    Page<JobManagement> findJobByHostActive(Pageable pageable, boolean active);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.isActive = :jobActive and host.isActive=:active group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.isActive = :jobActive and host.isActive=:active group by j.id")
    Page<JobManagement> findByStatusDependency(
            @Param("jobActive") Boolean jobActive,
            @Param("createdBy") String createdBy,
            @Param("searchString") String searchString,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate <= :endDate and host.isActive=:active group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate <= :endDate and host.isActive=:active group by j.id")
    Page<JobManagement> findByEndDateDependency(
            @Param("endDate") Date endDate,
            @Param("createdBy") String createdBy,
            @Param("searchString") String searchString,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query(value="Select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate >= :startDate and host.isActive=:active group by j.id",
            countQuery = "Select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate >= :startDate and host.isActive=:active group by j.id")
    Page<JobManagement> findByStartDateDependency(
            @Param("startDate") Date startDate,
            @Param("createdBy") String createdBy,
            @Param("searchString") String searchString,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate BETWEEN :startDate AND :endDate and host.isActive=:active group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate BETWEEN :startDate AND :endDate and host.isActive=:active group by j.id")
    Page<JobManagement> findByStartEndDateDependency(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("createdBy") String createdBy,
            @Param("searchString") String searchString,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate <= :endDate and j.isActive = :jobActive and host.isActive=:active group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate <= :endDate and j.isActive = :jobActive and host.isActive=:active group by j.id")
    Page<JobManagement> findByEndDateAndStatusDependency(
            @Param("endDate") Date endDate,
            @Param("createdBy") String createdBy,
            @Param("jobActive") Boolean jobActive,
            @Param("searchString") String searchString,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate >= :startDate and j.isActive = :jobActive and host.isActive=:active group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate >= :startDate and j.isActive = :jobActive and host.isActive=:active group by j.id")
    Page<JobManagement> findByStartDateAndStatusDependency(
            @Param("startDate") Date startDate,
            @Param("createdBy") String createdBy,
            @Param("jobActive") Boolean jobActive,
            @Param("searchString") String searchString,
            @Param("active") Boolean active,
            Pageable pageable);

    @Query(value="select j from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate BETWEEN :startDate AND :endDate and j.isActive = :jobActive and host.isActive=:active group by j.id",
            countQuery = "select count(j.id) from JobManagement j inner join j.hosts host where j.deleted = false and j.searchString like %:searchString% and j.createdBy like %:createdBy% and j.createdDate BETWEEN :startDate AND :endDate and j.isActive = :jobActive and host.isActive=:active group by j.id")
    Page<JobManagement> findByAllConditionDependency(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("createdBy") String createdBy,
            @Param("jobActive") Boolean jobActive,
            @Param("searchString") String searchString,
            @Param("active") Boolean active,
            Pageable pageable);
}
