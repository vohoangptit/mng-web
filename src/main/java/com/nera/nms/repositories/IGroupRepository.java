package com.nera.nms.repositories;

import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nera.nms.models.Group;

public interface IGroupRepository extends JpaRepository<Group, Long> {

	@Modifying
	@Transactional
	@Query(value = "update Group set isDeleted =:status where id=:id")
	void updateActiveGroup(@Param("status") boolean status,@Param("id") long id);
	
	public Page<Group> findAllByIsDeleted(Pageable pageable, boolean isActive);
	
	@Query(value = "select s from Group s where s.isActive =:active and s.isDeleted =:delete")
	public List<Group> findAllByIsActive(@Param("active") boolean isActive, @Param("delete") boolean isDeleted);

	@Query(value = "select s from Group s where s.isActive =:active and s.isDeleted =:delete ORDER BY group_name ASC")
	List<Group> findAllByOrderByGroupName(@Param("active") boolean isActive, @Param("delete") boolean isDeleted);
	
	Group findOneById(Long id);
	
	Group findOneByGroupName(String groupName);

	@Query(value = "select c.access_permission_name from user_group b"
			+ " inner join user_access_permission_user_group c on c.user_group_id = b.id"
			+ " where b.group_name =:group_name", nativeQuery = true)
	Collection<String> getPermissionByGroup(@Param("group_name")String groupName);

	@Query("select g.userAccessPermissions from Group g where g.groupName = :name")
	Group findByGroupNameEquals(String name);

	@Query("Select s from Group s where s.isDeleted = false and s.searchString like %:searchString%")
    Page<Group> findLikeSearchString(@Param("searchString") String searchString, Pageable pageable);
	
	@Query("Select s from Group s where s.isDeleted = false and s.searchString LIKE :searchString")
    Page<Group> findActiveSearchString(@Param("searchString") String searchString, Pageable pageable);
	
	@Query(value = "SELECT ug.* from user_user u INNER JOIN user_group_user ugu ON u.id = ugu.user_id " + 
			"INNER JOIN user_group ug ON ugu.user_group_id = ug.id WHERE u.is_delete = 'N' AND ug.is_deleted = 'N' GROUP BY " + 
			"ug.group_name ORDER BY ug.group_name ASC", nativeQuery = true)
    List<Group> findLinkedUserGroups();
	
	@Query(value ="SELECT COUNT(uu.id) FROM user_group_user ugu  INNER JOIN user_user uu ON uu.id = ugu.user_id"
			+ " where ugu.user_group_id = :groupId AND uu.is_active = 'Y'"
			, nativeQuery = true)
	int checkActiveUserGroupUsers(@Param("groupId") long groupId);
}
