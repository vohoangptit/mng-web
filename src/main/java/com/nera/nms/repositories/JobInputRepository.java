package com.nera.nms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import com.nera.nms.models.JobInput;
import java.util.List;

public interface JobInputRepository extends JpaRepository<JobInput, Long>{
	@Query("Select i from JobInput i")
	 List<JobInput> findAll();
    //get detail by id
	@Query("Select a from JobInput a where a.id =:id")
	JobInput getById(@Param("id") long id);
	@Modifying
    @Transactional
    @Query("delete from JobInput u where u.id = :id")
    void deletebyId(@Param("id") long id);

	@Modifying
	@Transactional
	@Query(value = "delete from nera.job_input where workflow_id = :wfId", nativeQuery = true)
	void deleteByWorkflowId(long wfId);

	@Query(value = "Select * from nera.job_input where workflow_id = :id", nativeQuery = true)
	List<JobInput> findByWorkflowId(@Param("id") long workflowId);

	@Query(value = "Select j.variable from nera.job_input j where j.id = :jobInputId", nativeQuery = true)
	String findVariableById(long jobInputId);

	@Query(value = "Select jp.value from nera.job_input ji  inner join nera.job_payload jp on ji.id = jp.job_input_id where ji.workflow_id = :workflowId and ji.variable = :variable", nativeQuery = true)
	String findValueByVariableAndId(long workflowId, String variable);
}
