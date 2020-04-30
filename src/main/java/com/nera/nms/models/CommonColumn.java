package com.nera.nms.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public class CommonColumn {

    @Column(name="create_date")
    @Getter @Setter
    private Date createdDate;

    @Column(name="created_by")
    @Getter @Setter
    private String createdBy;

    @Column(name="modified_date")
    @Getter @Setter
    private Date modifiedDate;

    @Column(name="modified_by")
    @Getter @Setter
    private String modifiedBy;

}
