DELETE FROM user_access_permission_user_group where user_group_id = 99999;

DELETE FROM user_group_user WHERE user_group_id = 99999;	

DELETE FROM user_group WHERE id = 99999;

DELETE FROM user_user WHERE id = 99999;

SET 	@id = 99999,
		@email = 'superadmin@super.com',
		@fullname = 'Super Admin',
		@is_active = 'Y',
		@is_delete = 'N',
		@password = '$2a$12$.VPQeKYLcJ./gI7OrdjuPOtQ1q0jqw7MgTvOFtAT354xVry46ylOi',
		@login_count = 0;
		
INSERT INTO user_user (id, email, fullname, is_active, is_delete, password, login_count) 
VALUES (@id, @email, @fullname, @is_active, @is_delete, @password, @login_count) 
ON DUPLICATE KEY UPDATE
	id = @id,
	email = @email,
	fullname = @full_name,
	is_active = @is_active,
	is_delete = @is_delete,
	password = @password,
	login_count = @login_count;

SET 	@id = 99999,
		@description = 'super admin desc',
		@group_name = 'super admin',
		@is_active = 'Y',
		@is_deleted = 'N';
		
INSERT INTO user_group (id, description, group_name, is_active, is_deleted)
VALUES (@id, @description, @group_name, @is_active, @is_deleted)
ON DUPLICATE KEY UPDATE
	id = @id,
	description = @description,
	group_name = @group_name,
	is_active = @is_active,
	is_deleted = @is_deleted;
	
	
REPLACE INTO user_group_user (user_group_id, user_id)
VALUE (99999, 99999);	
	



REPLACE INTO user_access_permission_user_group (user_group_id, access_permission_name)
VALUES 
(99999,	"VIEW_LISTING_USERS"),
(99999,	"CREATE_USER"),
(99999,	"UPDATE_USER"),
(99999,	"DELETE_USER"),
(99999,	"VIEW_LISTING_USER_GROUP"),
(99999,	"CREATE_GROUP"),
(99999,	"UPDATE_GROUP"),
(99999,	"DELETE_GROUP"),
(99999,	"VIEW_ACCESS_CONTROL"),
(99999,	"UPDATE_ACCESS_CONTROL"),
(99999,	"VIEW_ALL_SETTING"),
(99999,	"UPDATE_GENERAL"),
(99999,	"ADD_MAIL_TEMPLATE"),
(99999,	"UPDATE_MAIL_TEMPLATE"),
(99999,	"DETELE_MAIL_TEMPLATE"),
(99999,	"VIEW_AUDIT"),
(99999,	"EXPORT_AUDIT");
