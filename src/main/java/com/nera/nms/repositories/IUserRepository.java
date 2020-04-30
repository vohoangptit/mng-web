package com.nera.nms.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nera.nms.models.User;

public interface IUserRepository extends JpaRepository<User, Long> {

	User findById(long id);

	@Query(value = "SELECT u.fullname, u.email,u.mobile_number, GROUP_CONCAT(g.group_name SEPARATOR ', ') as group_name, d.department_name, j.job_title_name, u.image  FROM user_user u"
			+ " left join user_group_user ugu on u.id = ugu.user_id"
			+ " left join user_group g on g.id = ugu.user_group_id and g.is_active ='Y' and g.is_deleted = 'N'"
			+ " left join user_department d on u.department_id = d.id and d.is_enabled ='Y'"
			+ " left join user_job_title j on u.job_title_id = j.id and  j.is_enabled ='Y' WHERE u.email =:email AND u.is_delete = 'N' AND u.is_active = 'Y'" +
			"group by u.fullname, u.email, u.mobile_number, d.department_name, j.job_title_name, u.image", nativeQuery = true)
	Object findUserDetail(@Param("email") String email);

	@Query(value = "select * from user_user u Where u.email = :email and u.is_delete = 'N'", nativeQuery = true)
	User selectUserByEmail(String email);

	@Query(value = "select c.access_permission_name from user_user u"
			+ " inner join user_group_user a on a.user_id = u.id"
			+ " inner join user_group b on a.user_group_id = b.id"
			+ " inner join user_access_permission_user_group c on c.user_group_id = b.id"
			+ " where u.email =:email AND u.is_active = 'Y'", nativeQuery = true)
	Collection<String> selectPermissionByEmail(@Param("email") String email);

	@Query(value = "select b.group_name from user_user u"
			+ " inner join user_group_user a on a.user_id = u.id inner join user_group b on a.user_group_id = b.id"
			+ " inner join user_access_permission_user_group c on c.user_group_id = b.id"
			+ " where  u.is_active = 'Y' and c.access_permission_name = 'APPROVED_WORKFLOW_PROCESS'" + " union"
			+ " select u.email from user_user u"
			+ " inner join user_group_user a on a.user_id = u.id inner join user_group b on a.user_group_id = b.id"
			+ " inner join user_access_permission_user_group c on c.user_group_id = b.id"
			+ " where  u.is_active = 'Y' and c.access_permission_name = 'APPROVED_WORKFLOW_PROCESS'", nativeQuery = true)
	List<Object> findAllGroupandUserApprovePLaybook();

	@Query(value = "select * from user_group b"
			+ " inner join user_access_permission_user_group c on c.user_group_id = b.id"
			+ " where c.access_permission_name = 'APPROVED_WORKFLOW_PROCESS'", nativeQuery = true)
	List<Object[]> findAllGroupApprovedPlaybook();

	@Query(value = "select u.email from user_user u" + " inner join user_group_user a on a.user_id = u.id"
			+ " Where a.user_group_id = ?1 and u.is_active = 'Y' and u.email like %?2%", nativeQuery = true)
	List<String> findAllUserApprovedPlaybook(long groupID, String searchString);

	@SuppressWarnings("unchecked")
	@Transactional
	User save(User user);

	@Transactional
	@Modifying
	@Query("Update User u set u.searchString = :searchString, u.isActive = :isActive WHERE u.id = :id")
	void saveStatus(@Param("searchString") String searchString, @Param("isActive") boolean isActive,
			@Param("id") long id);

	@Query("SELECT count(id) FROM User u WHERE u.searchString LIKE '%' || :text || '%'")
	long countWithSearch(@Param("text") String searchText);

	@Query("SELECT u FROM User u WHERE u.searchString LIKE '%' || :text || '%'")
	List<User> findWithSortingAndPaginationAndSearch(Pageable pageable, @Param("text") String searchText);

	@Query(value = "Select COUNT(*) from nera.playbook where is_deleted = false and status = true and send_to_approved = true", nativeQuery = true)
	int countPlaybookByStatusNew();

	@Query(value = "SELECT u.* FROM user_user u" 
			+ " inner join user_group_user a on a.user_id = u.id" 
			+ " inner join user_group b on a.user_group_id = b.id"
			+ " inner join user_access_permission_user_group c on c.user_group_id = b.id"
			+ " WHERE c.access_permission_name = 'ASSIGN_JOB' AND u.is_delete = 'N' AND u.is_active = 'Y'"
			+ " group by u.id", nativeQuery = true)
	List<User> getPlanner();
	
	@Query(value = "SELECT u.* FROM user_user u" 
			+ " inner join user_group_user a on a.user_id = u.id" 
			+ " inner join user_group b on a.user_group_id = b.id"
			+ " inner join user_access_permission_user_group c on c.user_group_id = b.id"
			+ " WHERE c.access_permission_name = 'EXECUTE_JOB' AND u.is_delete = 'N' AND u.is_active = 'Y'"
			+ " group by u.id", nativeQuery = true)
	List<User> getAssignee();

	@Query(value = "SELECT u.fullname FROM user_user u"
			+ " inner join user_group_user a on a.user_id = u.id"
			+ " inner join user_group b on a.user_group_id = b.id"
			+ " inner join user_access_permission_user_group c on c.user_group_id = b.id"
			+ " WHERE (c.access_permission_name = 'CREATE_JOB' OR c.access_permission_name = 'UPDATE_JOB') AND u.is_delete = 'N' AND u.is_active = 'Y'"
			+ " group by u.id", nativeQuery = true)
	Set<String> getCreatedByJobManagement();
}
