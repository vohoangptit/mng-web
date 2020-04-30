package com.nera.nms.repositories;

import com.nera.nms.dto.JobApprovalRequestDTO;
import com.nera.nms.models.JobOperatorExecutionApproval;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApprovalRepository extends JpaRepository<JobOperatorExecutionApproval, Long> {

        @Query(value = "select new com.nera.nms.dto.JobApprovalRequestDTO(joea.id ,job.description, job.name, ja.assignee, job.workflowName, ja.planner, joea.requestAt)" +
            "from JobOperatorExecutionApproval joea\n" +
            "inner join JobExecution je on je.id = joea.jobExeId\n" +
            "inner join JobManagement job on je.job = job.id\n" +
            "inner join JobAssignment ja on ja.jobManagement = job.id \n" +
            "where joea.approveBy = :email and ja.status = 'Executing' and (job.name LIKE %:searchString% or ja.assignee.fullName LIKE %:searchString%)",
                countQuery = "select count(joea.id)" +
            "from JobOperatorExecutionApproval joea\n" +
            "inner join JobExecution je on je.id = joea.jobExeId\n" +
            "inner join JobManagement job on je.job = job.id\n" +
            "inner join JobAssignment ja on ja.jobManagement = job.id \n" +
            "where joea.approveBy = :email and ja.status = 'Executing'")
        Page<JobApprovalRequestDTO> getJobApprovalRequestListWithConditions(@Param("searchString") String searchString, Pageable pageable, @Param("email") String email);

        @Query("select new com.nera.nms.dto.JobApprovalRequestDTO(joea.id , job, ja.assignee, ja.planner, joea.requestAt, ja.id)" +
                "from JobOperatorExecutionApproval joea\n" +
                "inner join JobExecution je on je.id = joea.jobExeId\n" +
                "inner join JobManagement job on je.job = job.id\n" +
                "inner join JobAssignment ja on ja.jobManagement = job.id \n" +
                "where ja.status = 'Executing' and joea.id = :id")
        JobApprovalRequestDTO getByJobApprovalId(long id);

        @Query(value = "select joea.id , jb.name, joea.request_at, joea.approve_by " +
                "from job_operator_execution_approval joea " +
                "inner join job_execution je on je.id = joea.jobExeId " +
                "inner join job jb on je.job_id = jb.id " +
                "inner join job_assignment ja on ja.job_id = jb.id " +
                "where DATEDIFF(CURDATE(),joea.request_at) = 0 and joea.approve_by like %:email% and ja.status = 'Executing'", nativeQuery = true)
        List<Object[]> getJobApprovalToday(@Param("email") String email);

        @Query(value = "select joea.id , jb.name, joea.request_at, joea.approve_by " +
                "from job_operator_execution_approval joea " +
                "inner join job_execution je on je.id = joea.jobExeId " +
                "inner join job jb on je.job_id = jb.id " +
                "inner join job_assignment ja on ja.job_id = jb.id " +
                "where YEARWEEK(joea.request_at, 1) = YEARWEEK(CURDATE(), 1) and joea.approve_by like %:email% and ja.status = 'Executing'", nativeQuery = true)
        List<Object[]> getJobApprovalOnWeek(@Param("email") String email);

        @Query(value = "select joea.id , jb.name, joea.request_at, joea.approve_by " +
                "from job_operator_execution_approval joea " +
                "inner join job_execution je on je.id = joea.jobExeId " +
                "inner join job jb on je.job_id = jb.id " +
                "inner join job_assignment ja on ja.job_id = jb.id " +
                "where MONTH(joea.request_at) = MONTH(CURDATE()) and YEAR(joea.request_at) = YEAR(CURDATE()) and joea.approve_by like %:email% and ja.status = 'Executing'", nativeQuery = true)
        List<Object[]> getJobApprovalOnMonth(@Param("email") String email);

}
