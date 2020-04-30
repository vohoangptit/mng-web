package com.nera.nms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.nera.nms.models.UserDepartment;

/**
 * @author Martin Do
 *
 */
public interface IUserDepartmentRepository extends JpaRepository<UserDepartment, Long> {
	List<UserDepartment> findAllByOrderByDepartmentName();

    UserDepartment findById(long id);
    
    @Query(value = "SELECT ud.* FROM user_department ud INNER JOIN user_user u"
    		+ " ON u.department_id = ud.id"
    		+ " WHERE u.is_delete = 'N' GROUP BY ud.department_name"
    		+ " ORDER BY ud.department_name ASC", nativeQuery = true)
    List<UserDepartment> findLinkedUserDepartment();
}
