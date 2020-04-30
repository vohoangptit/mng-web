/**
 * 
 */
package com.nera.nms.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * @author Martin Do
 *
 */

@Entity
@Table(name = "LOGINS_PERSISTENT")
@Data
public class PersistentLogin  {

	  @Id
	  @Column(name = "SERIES")
	  private String series;

	  @Column(name = "USERNAME", nullable = false)
	  private String username;

	  @Column(name = "TOKEN", nullable = false)
	  private String token;

	  @Column(name = "LAST_USED", nullable = false)
	  private Date lastUsed;
}