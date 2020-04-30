package com.nera.nms.repositories;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.nera.nms.models.UserAccessPermission;

public interface IUserAccessPermissionRepository extends JpaRepository<UserAccessPermission, Long> {

	@Modifying
	@Transactional
	@Query(value = "Delete from nera.user_access_permission_user_group where user_group_id =?1 ", nativeQuery = true)
	public void deleteRole(long groupId);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO nera.user_access_permission_user_group (user_group_id, access_permission_name) "
    		+ "values(?1,?2)", nativeQuery = true)
	public void reCreateRole(long groupId, String roleName);

	@Modifying
	@Transactional
	@Query(value = "REPLACE  INTO user_group_user(user_group_id, user_id) "
			+ "values(?1,?2)", nativeQuery = true)
	public void updateUserGrUser(long groupId, long userId);
}
