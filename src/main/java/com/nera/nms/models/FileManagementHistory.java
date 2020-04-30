package com.nera.nms.models;

import com.nera.nms.constants.CommonConstants;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.thymeleaf.util.DateUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Table(name="file_management_history")
@Entity
public class FileManagementHistory {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @Column(name="name")
    private String name;

    @Column(name="filename")
    private String filename;

    @Column(name="description")
    private String description;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="is_active", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isActive;

    @Column(name = "SEARCH_STRING", length = 1000)
    private String searchString;

    @Column(name = "version", nullable = false)
    private int version = 1;

    @Column(name = "file_management_id", nullable = false)
    private Long fileManagementId;

    @PreUpdate
    @PrePersist
    void updateSearchString() {
        String status;
        if (isActive) {
            status = CommonConstants.ACTIVE;
        } else {
            status = CommonConstants.INACTIVE;
        }

        String createdDateFormatted = DateUtils.format(createdDate, "dd/MM/yyyy", new Locale("en"));

        final String fullSearchString = StringUtils.join(Arrays.asList(
                name,
                status,
                filename,
                createdBy,
                createdDateFormatted),
                " ");
        this.searchString = StringUtils.substring(fullSearchString, 0, 999);
    }
}
