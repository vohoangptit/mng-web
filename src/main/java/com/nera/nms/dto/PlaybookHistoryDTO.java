package com.nera.nms.dto;

import com.nera.nms.models.ENUM;
import com.nera.nms.models.PlaybookHistory;
import com.nera.nms.models.PlaybookInputHistory;
import com.nera.nms.models.PlaybookOutputHistory;
import com.nera.nms.utils.BEDateUtils;
import lombok.Data;

import java.util.List;

@Data
public class PlaybookHistoryDTO {

    private long id;
    private long no;
    private long playbookId;
    private int version;
    private String updateAt;
    private String updateBy;
    private String name;
    private String remark;
    private String note;
    private String fileName;
    private ENUM.StatusPlaybook status;
    private String sourceUrl;
    private boolean isActive;
    List<PlaybookInputHistory> playbookInput;
    List<PlaybookOutputHistory> playbookOutput;

    public PlaybookHistoryDTO(PlaybookHistory playbookHistory) {
        this.id = playbookHistory.getId();
        this.playbookId = playbookHistory.getPlaybookId();
        this.version = playbookHistory.getVersion() == 0 ? 1 : playbookHistory.getVersion();
        this.updateAt = BEDateUtils.convertDateToStringByFormatOn(playbookHistory.getCreatedDate());
        this.updateBy = playbookHistory.getCreatedBy();
        this.name = playbookHistory.getName();
        this.remark = playbookHistory.getRemark();
        this.note = playbookHistory.getNote();
        this.fileName = playbookHistory.getFileName();
        this.status = playbookHistory.getStatus();
        this.sourceUrl = playbookHistory.getSourceUrl();
        this.isActive = playbookHistory.isActive();
        this.playbookInput = playbookHistory.getPlaybookInput();
        this.playbookOutput = playbookHistory.getPlaybookOutput();
    }
}
