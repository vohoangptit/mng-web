package com.nera.nms.services;

import com.google.common.base.Strings;
import com.nera.nms.dto.ApproveAndRejectDTO;
import com.nera.nms.dto.PlaybookDTO;
import com.nera.nms.dto.StatisticPlaybookDTO;
import com.nera.nms.models.ENUM;
import com.nera.nms.models.FileManagement;
import com.nera.nms.models.Playbook;
import com.nera.nms.models.PlaybookHistory;
import com.nera.nms.models.PlaybookInput;
import com.nera.nms.models.PlaybookInputHistory;
import com.nera.nms.models.PlaybookOutput;
import com.nera.nms.models.PlaybookOutputHistory;
import com.nera.nms.repositories.FileManagementRepository;
import com.nera.nms.repositories.PlaybookHistoryRepository;
import com.nera.nms.repositories.PlaybookRepository;
import com.nera.nms.utils.BeanConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlaybookService {

    private SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    private final PlaybookRepository playbookRepository;

    @Autowired
    private PlaybookHistoryRepository playbookHistoryRepository;

    @Autowired
    private FileManagementRepository fileManagementRepository;

    public PlaybookService(PlaybookRepository playbookRepository) {
        this.playbookRepository = playbookRepository;
    }

    public Page<Playbook> findAllWithSearchString(Pageable pageable, String searchString, List<String> createdBy,
                                                  List<String> approvedBy, String startDate, String endDate) throws ParseException {
        if (!CollectionUtils.isEmpty(createdBy) && !CollectionUtils.isEmpty(approvedBy) && !Strings.isNullOrEmpty(startDate)
                && !Strings.isNullOrEmpty(endDate)) {
            Date start = formatter.parse(startDate);
            Date end = DateUtils.addDays(formatter.parse(endDate), 1);
            return playbookRepository.findLikeSearchStringAndCondition(searchString, createdBy, approvedBy, start, end, pageable);
        } else if (!CollectionUtils.isEmpty(createdBy) && !CollectionUtils.isEmpty(approvedBy)) {
            return playbookRepository.findLikeSearchStringAndCreateAndApproved(searchString, createdBy, approvedBy,
                    pageable);
        } else if (!CollectionUtils.isEmpty(createdBy) && !Strings.isNullOrEmpty(startDate)
                && !Strings.isNullOrEmpty(endDate)) {
            Date start = formatter.parse(startDate);
            Date end = DateUtils.addDays(formatter.parse(endDate), 1);
            return playbookRepository.findLikeSearchStringAndCreateByAndDate(searchString, createdBy, start, end, pageable);
        } else if (!CollectionUtils.isEmpty(approvedBy) && !Strings.isNullOrEmpty(startDate)
                && !Strings.isNullOrEmpty(endDate)) {
            Date start = formatter.parse(startDate);
            Date end = DateUtils.addDays(formatter.parse(endDate), 1);
            return playbookRepository.findLikeSearchStringAndApprovedAndCreatedDate(searchString, approvedBy, start, end, pageable);
        } else if (!CollectionUtils.isEmpty(createdBy)) {
            return playbookRepository.findLikeSearchStringAndCreatedBy(searchString, createdBy, pageable);
        } else if (!CollectionUtils.isEmpty(approvedBy)) {
            return playbookRepository.findLikeSearchStringAndApprovedBy(searchString, approvedBy, pageable);
        } else if (!Strings.isNullOrEmpty(startDate) && !Strings.isNullOrEmpty(endDate)) {
            Date start = formatter.parse(startDate);
            Date end = DateUtils.addDays(formatter.parse(endDate), 1);
            return playbookRepository.findLikeSearchStringAndCreatedDate(searchString, start, end, pageable);
        } else {
            return playbookRepository.findLikeSearchString(searchString, pageable);
        }
    }

    public Page<Playbook> findByStatusNew(Pageable pageable, String searchString, List<String> createdBy) {
        if (!CollectionUtils.isEmpty(createdBy)) {
            return playbookRepository.findByStatusNewAndCreadtedBy(searchString, createdBy, pageable);
        } else {
            return playbookRepository.findByStatusNew(searchString, pageable);
        }
    }

    public Page<Playbook> findByStatusActiveAndApproved(Pageable pageable, String searchString, List<String> approvedBy) {
        if (!CollectionUtils.isEmpty(approvedBy)) {
            return playbookRepository.findByStatusActiveAndAprroved(searchString, approvedBy, pageable);
        } else {
            return playbookRepository.findByStatusApproved(searchString, pageable);
        }
    }

    public List<Playbook> findByPlaybookApproved(String searchString) {
        return playbookRepository.findByPlaybookApproved(searchString);
    }

    public void savePlaybook(PlaybookDTO dto) {
        Playbook entity = dto.dtoToEntitySave(dto);
        playbookRepository.save(entity);
    }

    public void updatePlaybook(PlaybookDTO dto, String username) {
        Playbook entityOle = playbookRepository.getById(dto.getId());
        savePlaybookHistory(entityOle, username);
        Playbook entity = dto.dtoToEntitySave(dto);
        entity.setCreatedBy(entityOle.getCreatedBy());
        entity.setCreatedDate(entityOle.getCreatedDate());
        entity.setApprovedBy(entityOle.getApprovedBy() == null ? null : entityOle.getApprovedBy());
        entity.setApprovedDate(entityOle.getApprovedDate() == null ? null : entityOle.getApprovedDate());
        entity.setVersion(entityOle.getVersion()+1);
        playbookRepository.deleteOutputForUpdate(entity.getId());
        playbookRepository.save(entity);
    }

    public void savePlaybookHistory(Playbook entityOle, String username) {
        PlaybookHistory playbookHistory = new PlaybookHistory();
        playbookHistory.setVersion(entityOle.getVersion() == 0 ? 1 : entityOle.getVersion());
        playbookHistory.setApprovedDate(playbookHistory.getApprovedDate());
        playbookHistory.setApprovedBy(entityOle.getApprovedBy());
        playbookHistory.setActive(entityOle.isActive());
        playbookHistory.setCreatedBy(username);
        playbookHistory.setCreatedDate(new Date());
        playbookHistory.setDeleted(entityOle.isDeleted());
        playbookHistory.setNote(entityOle.getNote());
        playbookHistory.setRemark(entityOle.getRemark());
        playbookHistory.setSendToApproved(false);
        playbookHistory.setStatus(entityOle.getStatus());
        playbookHistory.setPlaybookId(entityOle.getId());
        playbookHistory.setName(entityOle.getName());
        playbookHistory.setSourceUrl(entityOle.getSourceUrl());

        List<PlaybookInputHistory> playbookInputHistories = new ArrayList<>();
        for(PlaybookInput item : entityOle.getPlaybookInput()) {
            PlaybookInputHistory playbookInputHistory = new PlaybookInputHistory();
            playbookInputHistory.setVariable(item.getVariable());
            playbookInputHistory.setType(item.getType());
            playbookInputHistory.setDeleted(item.isDeleted());
            playbookInputHistory.setValue(item.getValue());
            playbookInputHistory.setMandatory(item.isMandatory());
            playbookInputHistory.setFileManagementId(item.getFileManagementId());
            if(item.getType() == ENUM.PlaybookInputType.FILE) {
                FileManagement fileManagement = fileManagementRepository.findOneById(item.getFileManagementId());
                playbookInputHistory.setFileVersion(fileManagement.getVersion() == 0 ? 1 : fileManagement.getVersion());
            }
            playbookInputHistory.setPlaybookHistory(playbookHistory);
            playbookInputHistories.add(playbookInputHistory);
        }
        playbookHistory.setPlaybookInput(playbookInputHistories);
        List<PlaybookOutputHistory> playbookOutputHistories = new ArrayList<>();
        for(PlaybookOutput item : entityOle.getPlaybookOutput()) {
            PlaybookOutputHistory playbookOutputHistory = new PlaybookOutputHistory();
            playbookOutputHistory.setDeleted(item.isDeleted());
            playbookOutputHistory.setType(item.getType());
            playbookOutputHistory.setValue(item.getValue());
            playbookOutputHistory.setVariable(item.getVariable());
            playbookOutputHistory.setPlaybookHistory(playbookHistory);
            playbookOutputHistories.add(playbookOutputHistory);
        }
        playbookHistory.setPlaybookOutput(playbookOutputHistories);
        playbookHistoryRepository.save(playbookHistory);
    }

    public void deleteById(long id) {
        Playbook entity = playbookRepository.getById(id);
        entity.setDeleted(true);
        List<PlaybookInput> input = entity.getPlaybookInput();
        input.forEach(e -> e.setDeleted(true));
        entity.setPlaybookInput(input);
        playbookRepository.save(entity);
    }

    public boolean deletePlaybookById(long id) {
        Playbook entity = playbookRepository.getById(id);
        playbookRepository.delete(entity);
        return true;
    }

    public PlaybookDTO getById(long id) {
        Playbook entity = playbookRepository.getById(id);
        PlaybookDTO dto = new PlaybookDTO();
        BeanConvertUtils.copy(entity, dto);
        return dto;
    }

    public List<String> getListCreatedPerson() {
        List<String> datas = playbookRepository.getListCreatedPerson();
        java.util.Iterator<String> itr = datas.iterator();
        String str;
        while (itr.hasNext()) {
            str = itr.next();
            if (Strings.isNullOrEmpty(str))
                itr.remove();
        }
        return datas;
    }

    public List<String> getListCreatedPersonToApprove() {
        List<String> datas = playbookRepository.getListCreatedPersonToApprove();
        java.util.Iterator<String> itr = datas.iterator();
        String str;
        while (itr.hasNext()) {
            str = itr.next();
            if (Strings.isNullOrEmpty(str))
                itr.remove();
        }
        return datas;
    }

    public List<String> getListApprovedPerson() {
        List<String> datas = playbookRepository.getListApprovedPerson();
        java.util.Iterator<String> itr = datas.iterator();
        String str;
        while (itr.hasNext()) {
            str = itr.next();
            if (Strings.isNullOrEmpty(str))
                itr.remove();
        }
        return datas;
    }

    public void approveOrRejectPlaybook(ApproveAndRejectDTO dto, String approvedPerson) {
        Playbook entity = playbookRepository.getById(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setApprovedBy(approvedPerson);
        entity.setApprovedDate(new Date());
        entity.setSendToApproved(false);
        if (!Strings.isNullOrEmpty(dto.getRemark())) {
            entity.setRemark(dto.getRemark());
        }
        playbookRepository.save(entity);
    }

    public int countPlaybookAprroved() {
        return playbookRepository.countByStatusNew();
    }

    public Playbook sendApprovalPlaybook(long id) {
        Playbook entity = playbookRepository.getById(id);
        entity.setSendToApproved(true);
        playbookRepository.save(entity);
        return entity;
    }

    public Map<Integer, Integer> countStatusPlaybook() {
        List<Object[]> data = playbookRepository.countByStatus();
        Set<StatisticPlaybookDTO> result = new HashSet<>();
        int pendingCount = playbookRepository.countBySendToApproved();
        StatisticPlaybookDTO pending = new StatisticPlaybookDTO();
        pending.setStatus(4);
        pending.setNum(pendingCount);
        result.add(pending);
        for (int i = 1; i < 4; i++) {
            StatisticPlaybookDTO record = new StatisticPlaybookDTO();
            record.setStatus(i);
            record.setNum(0);
            result.add(record);
        }
        for (Object[] datum : data) {
            StatisticPlaybookDTO record = new StatisticPlaybookDTO();
            int x = (int) datum[0];
            int y = ((Number) datum[1]).intValue();
            if(x==1){
                y = y - pendingCount;
            }
            record.setStatus(x);
            record.setNum(y);
            result.add(record);
        }
        return result
                .stream()
                .collect(Collectors.groupingBy(StatisticPlaybookDTO::getStatus,
                        Collectors.summingInt(StatisticPlaybookDTO::getNum)));
    }

    public List<PlaybookHistory> getListPlayBookHistoryById(Long id) {
        return playbookHistoryRepository.findByPlaybookId(id);
    }

    public PlaybookHistory getPlaybookHistoryById(Long id) {
        return playbookHistoryRepository.findPlaybookHistoryById(id);
    }

    public Playbook getPlaybookById(Long id) {
        return playbookRepository.getById(id);
    }

    public void restorePlaybook(PlaybookHistory playbookHistory, String userName, Playbook pl) {
        savePlaybookHistory(pl, userName);
        playbookRepository.deleteInputForRestore(playbookHistory.getPlaybookId());
        playbookRepository.deleteOutputForUpdate(playbookHistory.getPlaybookId());
        Playbook playbook = getPlaybookById(playbookHistory.getPlaybookId());
        playbook.setVersion(playbook.getVersion()+1);
        playbook.setApprovedDate(playbookHistory.getApprovedDate());
        playbook.setApprovedBy(playbookHistory.getApprovedBy());
        playbook.setActive(playbookHistory.isActive());
        playbook.setCreatedBy(playbookHistory.getCreatedBy());
        playbook.setCreatedDate(playbookHistory.getCreatedDate());
        playbook.setDeleted(playbookHistory.isDeleted());
        playbook.setNote(playbookHistory.getNote());
        playbook.setRemark(playbookHistory.getRemark());
        playbook.setSendToApproved(playbookHistory.isSendToApproved());
        playbook.setStatus(ENUM.StatusPlaybook.NEW);
        playbook.setName(playbookHistory.getName());
        playbook.setSourceUrl(playbookHistory.getSourceUrl());
        playbook.setModifiedDate(new Date());
        playbook.setModifiedBy(userName);

        List<PlaybookInput> playbookInputs = new ArrayList<>();
        for(PlaybookInputHistory item : playbookHistory.getPlaybookInput()) {
            PlaybookInput playbookInput = new PlaybookInput();
            playbookInput.setVariable(item.getVariable());
            playbookInput.setType(item.getType());
            playbookInput.setDeleted(item.isDeleted());
            playbookInput.setValue(item.getValue());
            playbookInput.setMandatory(item.isMandatory());
            playbookInput.setFileManagementId(item.getFileManagementId());
            playbookInput.setPlaybook(playbook);
            playbookInputs.add(playbookInput);
        }
        playbook.setPlaybookInput(playbookInputs);
        List<PlaybookOutput> playbookOutputs = new ArrayList<>();
        for(PlaybookOutputHistory item : playbookHistory.getPlaybookOutput()) {
            PlaybookOutput playbookOutput = new PlaybookOutput();
            playbookOutput.setDeleted(item.isDeleted());
            playbookOutput.setType(item.getType());
            playbookOutput.setValue(item.getValue());
            playbookOutput.setVariable(item.getVariable());
            playbookOutput.setPlaybook(playbook);
            playbookOutputs.add(playbookOutput);
        }
        playbook.setPlaybookOutput(playbookOutputs);
        playbookRepository.save(playbook);
    }

    public Map<Integer, Integer> countStatusPlaybookPie(int type) {
        List<Object[]> data;
        int pendingCount;
        if(type==1){
            data = playbookRepository.countByStatusLastWeek();
            pendingCount = playbookRepository.countBySendToApprovedLastWeek();
        } else if(type==2){
            data = playbookRepository.countByStatusLastMonth();
            pendingCount = playbookRepository.countBySendToApprovedLastMonth();
        } else{
            data = playbookRepository.countByStatusThisYear();
            pendingCount = playbookRepository.countBySendToApprovedThisYear();
        }
        Set<StatisticPlaybookDTO> result = new HashSet<>();

        StatisticPlaybookDTO pending = new StatisticPlaybookDTO();
        pending.setStatus(4);
        pending.setNum(pendingCount);
        result.add(pending);
        for (int i = 1; i < 4; i++) {
            StatisticPlaybookDTO record = new StatisticPlaybookDTO();
            record.setStatus(i);
            record.setNum(0);
            result.add(record);
        }
        for (Object[] datum : data) {
            StatisticPlaybookDTO record = new StatisticPlaybookDTO();
            int x = (int) datum[0];
            int y = ((Number) datum[1]).intValue();
            if(x==1){
                y = y - pendingCount;
            }
            record.setStatus(x);
            record.setNum(y);
            result.add(record);
        }
        return result
                .stream()
                .collect(Collectors.groupingBy(StatisticPlaybookDTO::getStatus,
                        Collectors.summingInt(StatisticPlaybookDTO::getNum)));
    }
}
