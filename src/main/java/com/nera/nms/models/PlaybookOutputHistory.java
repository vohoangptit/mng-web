package com.nera.nms.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper=false)
@Table(name="playbook_output_history")
@Entity
public class PlaybookOutputHistory {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    
    @Column(name="type")
    private ENUM.PlaybookOutputType type;
    
    @Enumerated(EnumType.STRING)
    public ENUM.PlaybookOutputType getType() {
        return type;
    }
    
    @Column(name="variable")
    private String variable;
    
    @Column(name="value")
    private String value;
    
    @Column(name="is_deleted", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isDeleted;
    
    @ManyToOne
	@JoinColumn(name = "playbook_history_id", insertable = false, updatable = false)
    @JsonBackReference
	private PlaybookHistory playbookHistory;
}
