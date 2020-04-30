package com.nera.nms.services;

import com.nera.nms.models.JobInput;
import com.nera.nms.repositories.JobInputRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class JobInputService {
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 45L;

	@Autowired
	private JobInputRepository jobInputRepository;

	public boolean createJobInput(String variable,Integer type) {
		JobInput jobInput = new JobInput();
		jobInput.setVariable(variable);
		jobInput.setType(type);
		jobInputRepository.save(jobInput);
		return true;
	}


	public List<JobInput> findJobByWorkflowId(long workflowId) {
		List<JobInput> listJobInput = Collections.emptyList();
		try {
			listJobInput = jobInputRepository.findByWorkflowId(workflowId);
		} catch(Exception e) {
			logger.error("Exception : JobInputService.findJobByWorkflowId ", e);
		}
		return listJobInput;
	}

	public String findVariableById(long jobInputId) {
		return jobInputRepository.findVariableById(jobInputId);
	}

	public void deleteJobById(long jobId) {
		try {
			jobInputRepository.deletebyId(jobId);
		} catch(Exception e) {
			logger.error("Exception : JobInputService.deleteJobById ", e);
		}
	}

	public String findValueByVariableAndWorkflowId(long workflowId, String variable) {
		return jobInputRepository.findValueByVariableAndId(workflowId, variable);
	}
}
