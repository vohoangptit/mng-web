package com.nera.nms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nera.nms.models.UserJobTitle;

/**
 * @author Martin Do
 *
 */
public interface IUserJobTitleRepository extends JpaRepository<UserJobTitle, Long> {

	List<UserJobTitle> findAllByOrderByJobTitleName();

    UserJobTitle findById(long id);
    
    @Query(value = "SELECT ujt.* FROM user_job_title ujt INNER JOIN user_user u"
    		+ " ON u.job_title_id = ujt.id"
    		+ " WHERE u.is_delete = 'N' GROUP BY ujt.job_title_name"
    		+ " ORDER BY ujt.job_title_name ASC", nativeQuery = true)
    List<UserJobTitle> findLinkedUserJobTitles();
}
