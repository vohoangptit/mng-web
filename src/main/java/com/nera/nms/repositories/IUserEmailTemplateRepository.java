package com.nera.nms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nera.nms.models.UserEmailTemplate;

public interface IUserEmailTemplateRepository extends JpaRepository<UserEmailTemplate, Long> {
	
	@Query("Select a from UserEmailTemplate a where a.isDeleted = false and a.id =:id")
	UserEmailTemplate getById(@Param("id") long id);
	
	UserEmailTemplate findOneById( long id);
	
	UserEmailTemplate findOneByTemplateName(String templateName);

	@Query("Select s from UserEmailTemplate s where s.isDeleted = false and s.searchString like %:searchString%")
	List<UserEmailTemplate> findLikeSearchString(@Param("searchString") String searchString);
}
