package com.nera.nms.repositories;

import com.nera.nms.models.JobAssignment;
import com.nera.nms.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface JobPlanRepository extends JpaRepository<JobAssignment, Long> {

    // without condition
    @Query("Select s from JobAssignment s where s.isDeleted = false and s.searchString like %:searchString% ")
    Page<JobAssignment> findLikeSearchString(@Param("searchString") String searchString, Pageable pageable);

    // all
    @Query("Select s from JobAssignment s where s.isDeleted = false and s.searchString like %:searchString% "
            + "and s.status =:status " + "and s.executionDate BETWEEN :startDate AND :endDate ")
    Page<JobAssignment> findLikeSearchStringAndCondition(@Param("searchString") String searchString,
                                                         @Param("status") String status, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                                         Pageable pageable);

    // find by status & search string
    @Query("Select s from JobAssignment s where s.isDeleted = false and s.searchString like %:searchString% "
            + "and s.status =:status")
    Page<JobAssignment> findLikeSearchStringAndStatus(@Param("searchString") String searchString,
                                                      @Param("status") String status, Pageable pageable);

    // find by date & search string
    @Query("Select s from JobAssignment s where s.isDeleted = false and s.searchString like %:searchString% "
            + "and s.executionDate BETWEEN :startDate AND :endDate ")
    Page<JobAssignment> findLikeSearchStringAndDate(@Param("searchString") String searchString,
                                                    @Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    // get detail by id
    @Query("Select a from JobAssignment a where a.isDeleted = false and a.id =:id")
    JobAssignment getById(@Param("id") long id);

    @Query("Select a from JobAssignment a where a.isDeleted = false and a.id = :id and a.status = :status")
    JobAssignment getByIdAndStatus(long id, String status);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM nera.job_assignment where id =?1", nativeQuery = true)
    void deleteJobAssignById(Long jobAssignId);

    // without condition
    @Query("Select s from JobAssignment s where s.isDeleted = false and s.assignee =:assignee and s.searchString like %:searchString% ")
    Page<JobAssignment> findMyJobLikeSearchString(@Param("searchString") String searchString,
                                                  @Param("assignee") User assignee, Pageable pageable);

    // all
    @Query("Select s from JobAssignment s where s.isDeleted = false and s.assignee =:assignee and s.searchString like %:searchString% "
            + "and s.status =:status " + "and s.executionDate BETWEEN :startDate AND :endDate ")
    Page<JobAssignment> findMyJobLikeSearchStringAndCondition(@Param("searchString") String searchString,
                                                              @Param("status") String status, @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                                              @Param("assignee") User assignee, Pageable pageable);

    // find by status & search string
    @Query("Select s from JobAssignment s where s.isDeleted = false and s.assignee =:assignee and s.searchString like %:searchString% "
            + "and s.status =:status")
    Page<JobAssignment> findMyJobLikeSearchStringAndStatus(@Param("searchString") String searchString,
                                                           @Param("status") String status,
                                                           @Param("assignee") User assignee, Pageable pageable);

    // find by date & search string
    @Query("Select s from JobAssignment s where s.isDeleted = false and s.assignee =:assignee and s.searchString like %:searchString% "
            + "and s.executionDate BETWEEN :startDate AND :endDate ")
    Page<JobAssignment> findMyJobLikeSearchStringAndDate(@Param("searchString") String searchString,
                                                         @Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                                         @Param("assignee") User assignee, Pageable pageable);

    List<JobAssignment> findByAssignee(User assignee);

	List<JobAssignment> findAllByStatusEquals(String status);

    @Query("Select j from JobAssignment j JOIN FETCH j.assignee JOIN FETCH  j.planner where j.isDeleted = false and j.status = :status")
    List<JobAssignment> findAllJobByStatus(String status);

    List<JobAssignment> findByAssigneeAndStatusEquals(User assignee, String status);

    @Query(value = "Select status, count(*) as num from job_assignment where is_deleted = false and assignee = :assignee group by status", nativeQuery = true)
    List<Object[]> countByStatusByUser(long assignee);

    @Query("Select s from JobAssignment s where s.isDeleted = false and s.jobManagement.id =?1")
    List<JobAssignment> findByJobId(long id);

    @Query(value = "Select * from nera.job_assignment where is_deleted = false and planner =?1", nativeQuery = true)
    List<JobAssignment> findJobByPlanner(long id);

    @Query("Select s from JobAssignment s where s.isDeleted = false and s.assignee.id =?1")
    List<JobAssignment> findByAssigneeId(long id);

    @Query("Select s from JobAssignment s where s.isDeleted = false and s.jobManagement.id=?1 and (s.status = 'Pending for Acceptance' or s.status = 'Rejected' or s.status = 'Accepted' or s.status = 'Executing')")
    List<JobAssignment> findJobAssigmentByJobIdAndStatus(long id);

    @Query(value = "Select status, count(*) as num from job_assignment where is_deleted = false group by status", nativeQuery = true)
    List<Object[]> countByStatus();

    List<JobAssignment> findAllByPlanner(User planner);

    @Query(value = "Select a.id, b.name as jobName, a.execution_date, a.start_time as dateExec, b.id as job_id, b.workflow_id " +
            "from job_assignment a " +
            "inner join job b on a.job_id = b.id " +
            "where a.is_deleted = false and a.status = 'Accepted' and DATEDIFF(CURRENT_DATE(),a.execution_date) = 0 and a.assignee = ?1", nativeQuery = true)
    List<Object[]> getJobToExecToday(@Param(value = "assignee") long assignee);

    @Query(value = "Select a.id, b.name as jobName, a.execution_date, a.start_time as dateExec, b.id as job_id, b.workflow_id " +
            "from job_assignment a " +
            "inner join job b on a.job_id = b.id " +
            "where a.is_deleted = false and a.status = 'Accepted' and YEARWEEK(a.execution_date, 1) = YEARWEEK(CURDATE(), 1) and a.assignee = ?1", nativeQuery = true)
    List<Object[]> getJobToExecOnWeek(@Param(value = "assignee") long assignee);

    @Query(value = "Select a.id, b.name as jobName, a.execution_date, a.start_time as dateExec, b.id as job_id, b.workflow_id " +
            "from job_assignment a " +
            "inner join job b on a.job_id = b.id " +
            "where a.is_deleted = false and a.status = 'Accepted' and MONTH(a.execution_date) = MONTH(CURDATE()) and YEAR(a.execution_date) = YEAR(CURDATE()) and a.assignee = ?1", nativeQuery = true)
    List<Object[]> getJobToExecOnMonth(@Param(value = "assignee") long assignee);

    @Query(value = "Select status, count(*) as num from job_assignment a where is_deleted = false and YEARWEEK(IFNULL(a.modified_date, a.create_date), 1) = YEARWEEK(CURDATE(), 1) - 1 group by status", nativeQuery = true)
    List<Object[]> countByStatusLastWeek();

    @Query(value = "Select status, count(*) as num from job_assignment a where is_deleted = false" +
            " and YEAR(IFNULL(a.modified_date, a.create_date)) = YEAR(CURDATE() - INTERVAL 1 MONTH)" +
            " AND MONTH(IFNULL(a.modified_date, a.create_date)) = MONTH(CURDATE() - INTERVAL 1 MONTH)" +
            " group by status", nativeQuery = true)
    List<Object[]> countByStatusLastMonth();

    @Query(value = "Select wf.id, wf.name, cast(sum(TIMESTAMPDIFF(Second,je.execute_start, je.execute_end))/count(wf.id) AS UNSIGNED) AS time, count(wf.id) from workflow wf" +
            " inner join job j on wf.id = j.workflow_id" +
            " inner join job_execution je on j.id = je.job_id" +
            " where wf.is_deleted = false group by wf.name order by count(wf.id) desc limit 10", nativeQuery = true)
    List<Object[]> statisticWorkflowTimeExec();

    @Query(value = "Select * from nera.job_assignment s where s.is_deleted = false and s.planner =?1 and MONTH(s.create_date) = MONTH(CURDATE()) and YEAR(s.create_date) = YEAR(CURDATE())", nativeQuery = true)
    List<JobAssignment> findAllJobByPlanner(long id);

    @Query(value = "Select status, count(*) as num from job_assignment a where is_deleted = false" +
            " and YEAR(IFNULL(a.modified_date, a.create_date)) = YEAR(CURDATE())" +
            " group by status", nativeQuery = true)
    List<Object[]> countByStatusThisYear();
}
