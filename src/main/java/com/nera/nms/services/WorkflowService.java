package com.nera.nms.services;

import com.nera.nms.dto.WorkflowDTO;
import com.nera.nms.models.JobInput;
import com.nera.nms.models.JobInputHistory;
import com.nera.nms.models.Playbook;
import com.nera.nms.models.Workflow;
import com.nera.nms.models.WorkflowHistory;
import com.nera.nms.models.WorkflowOperator;
import com.nera.nms.models.WorkflowOperatorHistory;
import com.nera.nms.repositories.JobInputRepository;
import com.nera.nms.repositories.PlaybookRepository;
import com.nera.nms.repositories.WorkflowHistoryRepository;
import com.nera.nms.repositories.WorkflowOperatorRepository;
import com.nera.nms.repositories.WorkflowRepository;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WorkflowService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final long serialVersionUID = 45L;

    private static final String COPY = "_copy";

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private WorkflowHistoryRepository workflowHistoryRepository;

    @Autowired
    private JobInputRepository jobInputRepository;

    @Autowired
    private WorkflowOperatorRepository workflowOperatorRepository;

    @Autowired
    private PlaybookRepository playbookRepository;

    public Page<Workflow> findAllWorkflow(Pageable pageable, String searchString) {
        return workflowRepository.findLikeSearchString(searchString, pageable);
    }

    public Page<Workflow> findByCreatedBy(Pageable pageable, String searchString, List<String> createdBy) {
        return workflowRepository.findByCreatedBy(pageable, searchString, createdBy);
    }

    public Page<Workflow> findByAllConditions(Pageable pageable, String searchString, Date startDate, Date endDate, List<String> createdBy) {
        return workflowRepository.findByAllConditions(pageable, searchString, startDate, endDate, createdBy);
    }

    public Page<Workflow> findAllByDate(Pageable pageable, Date startDate, Date endDate, String searchString) {
        return workflowRepository.findLikeSearchStringAndDate(pageable, startDate, endDate, searchString);
    }

    public Page<Workflow> findAllByStartDate(Pageable pageable, Date startDate, String searchString) {
        return workflowRepository.findLikeSearchStringAndStartDate(pageable, startDate, searchString);
    }

    public Page<Workflow> findAllByEndDate(Pageable pageable, Date endDate, String searchString) {
        return workflowRepository.findLikeSearchStringAndEndDate(pageable, endDate, searchString);
    }

    public Page<Workflow> findByStartDayAndCreatedBy(Pageable pageable, Date startDate, String searchString, List<String> createdBy) {
        return workflowRepository.findLikeCreatedByAndStartDate(pageable, startDate, searchString, createdBy);
    }

    public Page<Workflow> findByEndDayAndCreatedBy(Pageable pageable, Date endDate, String searchString, List<String> createdBy) {
        return workflowRepository.findLikeCreatedByAndEndDate(pageable, endDate, searchString, createdBy);
    }

    public List<Workflow> findAllWorkflow() {
        return workflowRepository.findAllWorkflow();
    }

    public Workflow findByIdWorkflow(long workflowID){
        return workflowRepository.findOneById(workflowID);
    }

    public String cloneWorkflow(long id, String createdBy) {
        Workflow wf = findByIdWorkflow(id);
        if(wf != null) {
            wf.setName(wf.getName()+COPY);
            wf.setCreatedDate(new Date());
            wf.setCreatedBy(createdBy);
            workflowRepository.save(wf);
            return wf.getName();
        }
        return StringUtils.EMPTY;
    }

    public String deleteWorkflow(long workflowID) {
        Workflow workflow = findByIdWorkflow(workflowID);
        if (workflow != null) {
            workflowRepository.delete(workflow);
            return workflow.getName();
        }
        return StringUtils.EMPTY;
    }

    public Set<String> findAllCreatedPerson() {
        return workflowRepository.findAllCreatedPerson();
    }

    public boolean findByName(String name, long id) {
        if (id > 0) {
            if(workflowRepository.findByName(name, id) != null) {
                return true;
            }
        } else {
            if(workflowRepository.findByName(name) != null) {
                return true;
            }
        }

        return false;
    }

    public boolean saveWorkflow(String nameCreatedBy, WorkflowDTO workflowDTO) {
        boolean check = true;
        try {
            workflowDTO.setCreatedBy(nameCreatedBy);
            Workflow entityWorkflow = workflowDTO.dtoToEntitySave();
            workflowRepository.save(entityWorkflow);
        } catch (Exception e) {
            logger.error("Exception : WorkflowService.saveWorkflow ", e);
            check = false;
        }
        return check;
    }

    public List<WorkflowHistory> getListWorkflowByWorkflowId(Long workflowId) {
        return workflowHistoryRepository.findByWorkflowId(workflowId);
    }

    public WorkflowHistory getWorkflowHistoryById(Long workflowId) {
        return workflowHistoryRepository.getById(workflowId);
    }

    public void deleteWorkflowOperator(List<WorkflowOperator> operators) {
        operators.forEach(i -> workflowOperatorRepository.deleteById(i.getId()));
    }


    public void restoreWorkflow(WorkflowHistory workflowHistory, String username) {
        Workflow wf = workflowRepository.findOneById(workflowHistory.getWorkflowId());
        jobInputRepository.deleteByWorkflowId(wf.getId());
        workflowOperatorRepository.deleteByWorkflowId(wf.getId());

        wf.setVersion(wf.getVersion()+1);
        wf.setName(wf.getName());
        wf.setDescription(wf.getDescription());
        wf.setModifiedDate(new Date());
        wf.setModifiedBy(username);
        wf.setActive(wf.isActive());

        List<WorkflowOperator> listOperator = new ArrayList<>();
        for(WorkflowOperatorHistory item : workflowHistory.getWorkflowOperatorHistories()) {
            WorkflowOperator workflowOperator = new WorkflowOperator();
            workflowOperator.setApproved(item.getApproved());
            workflowOperator.setContentXML(item.getContentXML());
            workflowOperator.setFrom(item.getFrom());
            workflowOperator.setTo(item.getTo());
            workflowOperator.setPlaybook(item.getPlaybook());
            workflowOperator.setProperties(item.getProperties());
            workflowOperator.setType(item.getType());
            workflowOperator.setWorkflow(wf);
            listOperator.add(workflowOperator);
        }
        wf.setWorkflowOperator(listOperator);

        List<JobInput> listJobInput = new ArrayList<>();
        for(JobInputHistory item : workflowHistory.getJobInputHistories()) {
            JobInput jobInput = new JobInput();
            jobInput.setType(item.getType());
            jobInput.setVariable(item.getVariable());
            jobInput.setValue(item.getValue());
            jobInput.setWorkflow(wf);
            listJobInput.add(jobInput);
        }
        wf.setJobInput(listJobInput);
        workflowRepository.save(wf);
    }

    public void saveWorkflowHistory(Workflow wf) {
        WorkflowHistory workflowHistory = new WorkflowHistory();
        workflowHistory.setName(wf.getName());
        workflowHistory.setDescription(wf.getDescription());
        workflowHistory.setCreatedBy(wf.getCreatedBy());
        workflowHistory.setCreatedDate(wf.getCreatedDate());
        workflowHistory.setActive(wf.isActive());
        workflowHistory.setVersion(wf.getVersion() == 0 ? 1 : wf.getVersion());
        workflowHistory.setWorkflowId(wf.getId());

        List<JobInputHistory> jobInputHistoryList = new ArrayList<>();
        for(JobInput item : wf.getJobInput()) {
            JobInputHistory jobInputHistory = new JobInputHistory();
            jobInputHistory.setType(item.getType());
            jobInputHistory.setVariable(item.getVariable());
            jobInputHistory.setValue(item.getValue());
            jobInputHistory.setWorkflowHistory(workflowHistory);
            jobInputHistoryList.add(jobInputHistory);
        }

        List<WorkflowOperatorHistory> workflowOperatorHistoryList = new ArrayList<>();
        for(WorkflowOperator item : wf.getWorkflowOperator()) {
            WorkflowOperatorHistory operatorHistory = new WorkflowOperatorHistory();
            operatorHistory.setApproved(item.getApproved());
            operatorHistory.setContentXML(item.getContentXML());
            operatorHistory.setFrom(item.getFrom());
            operatorHistory.setTo(item.getTo());
            operatorHistory.setPlaybook(item.getPlaybook());
            if(StringUtils.containsIgnoreCase(item.getContentXML(),"<Playbook ")) {
                Pattern pattern = Pattern.compile("pbid=\"(\\d{1,})\"");
                Matcher matcher = pattern.matcher(item.getContentXML());
                if(matcher.find()) {
                    Playbook pl = playbookRepository.getById(Long.parseLong(matcher.group(1)));
                    operatorHistory.setPlaybookVersion(pl != null ? pl.getVersion() : 1);
                }
            } else {
                operatorHistory.setPlaybookVersion(0);
            }
            operatorHistory.setProperties(item.getProperties());
            operatorHistory.setType(item.getType());
            operatorHistory.setWorkflowHistory(workflowHistory);
            workflowOperatorHistoryList.add(operatorHistory);
        }
        workflowHistory.setJobInputHistories(jobInputHistoryList);
        workflowHistory.setWorkflowOperatorHistories(workflowOperatorHistoryList);
        workflowHistoryRepository.save(workflowHistory);
    }

    public boolean checkListWorkflowOperator(Long playbookId) {
        String query = "%pbid=\"" + playbookId + "\"%";
        return CollectionUtils.isNotEmpty(workflowOperatorRepository.findByContentXMLLike(query));
    }

    public boolean checkVersionPlaybook(WorkflowHistory workflowHistory) {
        List<WorkflowOperatorHistory> workflowOperatorHistoryList = workflowHistory.getWorkflowOperatorHistories();
        for (WorkflowOperatorHistory operatorHistory : workflowOperatorHistoryList) {
            if(StringUtils.containsIgnoreCase(operatorHistory.getContentXML(),"<Playbook ")) {
                Pattern pattern = Pattern.compile("pbid=\"(\\d{1,})\"");
                Matcher matcher = pattern.matcher(operatorHistory.getContentXML());
                if(matcher.find()) {
                    Playbook pl = playbookRepository.getById(Long.parseLong(matcher.group(1)));
                    if(pl == null || pl.getVersion() > operatorHistory.getPlaybookVersion()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

