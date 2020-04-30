package com.nera.nms.repositories;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nera.nms.models.Playbook;


public interface PlaybookRepository extends JpaRepository<Playbook, Long> {

	//get detail by id
	@Query("Select a from Playbook a where a.isDeleted = false and a.id =:id")
	Playbook getById(@Param("id") long id);
	
	//get list create person
	@Query("Select a.createdBy from Playbook a where a.isDeleted = false group by a.createdBy")
	List<String> getListCreatedPerson();
	//get list create person to approve
	@Query("Select a.createdBy from Playbook a where a.isDeleted = false and a.status = 1 group by a.createdBy")
	List<String> getListCreatedPersonToApprove();
	//get list approved person
	@Query("Select a.approvedBy from Playbook a where a.isDeleted = false group by a.approvedBy")
	List<String> getListApprovedPerson();
	
	// without condition
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.sendToApproved = false")
	Page<Playbook> findLikeSearchString(
			@Param("searchString") String searchString, 
			Pageable pageable);
	
	// all
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.createdBy in (:createdBy) "
			+ "and s.approvedBy in (:approvedBy) "
			+ "and s.createdDate BETWEEN :startDate AND :endDate "
			+ "and s.sendToApproved = false")
    Page<Playbook> findLikeSearchStringAndCondition(
    		@Param("searchString") String searchString,
    		@Param("createdBy") List<String> createdBy, 
    		@Param("approvedBy") List<String> approvedBy,
    		@Param("startDate") Date startDate,
    		@Param("endDate") Date endDate,
    		Pageable pageable);
	
	// find by created by 
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% and s.createdBy in (:createdBy) "
			+ "and s.sendToApproved = false")
    Page<Playbook> findLikeSearchStringAndCreatedBy(
    		@Param("searchString") String searchString, 
    		@Param("createdBy") List<String> createdBy, 
    		Pageable pageable);
	
	// find by approved by
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% and s.approvedBy in (:approvedBy) "
			+ "and s.sendToApproved = false")
    Page<Playbook> findLikeSearchStringAndApprovedBy(
    		@Param("searchString") String searchString, 
    		@Param("approvedBy") List<String> approvedBy, 
    		Pageable pageable);
	
	// find by created date
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% and s.createdDate BETWEEN :startDate AND :endDate "
			+ "and s.sendToApproved = false")
    Page<Playbook> findLikeSearchStringAndCreatedDate(
    		@Param("searchString") String searchString, 
    		@Param("startDate") Date startDate,
    		@Param("endDate") Date endDate,
    		Pageable pageable);
	
	
	// find by create and approved person
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.createdBy in (:createdBy) "
			+ "and s.approvedBy in (:approvedBy) "
			+ "and s.sendToApproved = false")
    Page<Playbook> findLikeSearchStringAndCreateAndApproved(
    		@Param("searchString") String searchString, 
    		@Param("createdBy") List<String> createdBy, 
    		@Param("approvedBy") List<String> approvedBy,
    		Pageable pageable);
	
	// find by created by and date
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.createdBy in (:createdBy) "
			+ "and s.createdDate BETWEEN :startDate AND :endDate "
			+ "and s.sendToApproved = false")
    Page<Playbook> findLikeSearchStringAndCreateByAndDate(
    		@Param("searchString") String searchString,
    		@Param("createdBy") List<String> createdBy, 
    		@Param("startDate") Date startDate,
    		@Param("endDate") Date endDate,
    		Pageable pageable);
	
	// find by created by and date
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.approvedBy in (:approvedBy) "
			+ "and s.createdDate BETWEEN :startDate AND :endDate "
			+ "and s.sendToApproved = false")
    Page<Playbook> findLikeSearchStringAndApprovedAndCreatedDate(
    		@Param("searchString") String searchString,
    		@Param("approvedBy") List<String> approvedBy,
    		@Param("startDate") Date startDate,
    		@Param("endDate") Date endDate,
    		Pageable pageable);
	
	// get by status new to approved and created person
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.createdBy in (:createdBy) "
			+ "and s.status = 1 "
			+ "and s.sendToApproved = true")
    Page<Playbook> findByStatusNewAndCreadtedBy(@Param("searchString") String searchString, @Param("createdBy") List<String> createdBy, 
			Pageable pageable);	
	//find by status active and approved
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.approvedBy in (:approvedBy) "
			+ "and s.status = 2")
	Page<Playbook> findByStatusActiveAndAprroved(@Param("searchString") String searchString, @Param("approvedBy") List<String> approvedBy, 
			Pageable pageable);	
	// get by status new to approved
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.status = 1 "
			+ "and s.sendToApproved = true")
    Page<Playbook> findByStatusNew(@Param("searchString") String searchString, Pageable pageable);
	

	// get by status approved to workflow
	@Query("Select s from Playbook s where s.isDeleted = false and s.searchString like %:searchString% "
			+ "and s.status = 2")
    Page<Playbook> findByStatusApproved(@Param("searchString") String searchString, Pageable pageable);
	
	@Query("Select COUNT(s.id) from Playbook s where s.isDeleted = false and s.status = 2")
    int countByStatusApprove();
	@Query("Select COUNT(s.id) from Playbook s where s.isDeleted = false and s.status = 1 "
			+ "and s.sendToApproved = true")
    int countByStatusNew();
	
	//get delete output for update
	@Modifying
	@Transactional
	@Query(value ="delete from playbook_output where playbook_id =:playbookId",
			nativeQuery = true)
	void deleteOutputForUpdate(@Param("playbookId")long playbookId);

	@Modifying
	@Transactional
	@Query(value ="delete from playbook_input where playbook_id =:playbookId",
			nativeQuery = true)
	void deleteInputForRestore(@Param("playbookId")long playbookId);

	@Query("Select s from Playbook s where s.isDeleted = false and s.name like %:searchString% "
			+ "and s.status = 2")
	List<Playbook> findByPlaybookApproved(@Param("searchString") String searchString);

	@Query(value = "Select status, count(*) as num from playbook a where is_deleted = false group by status", nativeQuery = true)
	List<Object[]> countByStatus();

	@Query(value = "Select count(*) as num from playbook a where is_deleted = false and send_to_approved = 1", nativeQuery = true)
	int countBySendToApproved();

	@Query(value = "Select status, count(*) as num from playbook a where is_deleted = false and YEARWEEK(IFNULL(a.modified_date, a.create_date), 1) = YEARWEEK(CURDATE(), 1) - 1 group by status", nativeQuery = true)
	List<Object[]> countByStatusLastWeek();

	@Query(value = "Select count(*) as num from playbook a where is_deleted = false and send_to_approved = 1 and YEARWEEK(IFNULL(a.modified_date, a.create_date), 1) = YEARWEEK(CURDATE(), 1) - 1", nativeQuery = true)
	int countBySendToApprovedLastWeek();

	@Query(value = "Select status, count(*) as num from playbook a where is_deleted = false" +
			" and YEAR(IFNULL(a.modified_date, a.create_date)) = YEAR(CURDATE() - INTERVAL 1 MONTH)" +
			" AND MONTH(IFNULL(a.modified_date, a.create_date)) = MONTH(CURDATE() - INTERVAL 1 MONTH)" +
			" group by status", nativeQuery = true)
	List<Object[]> countByStatusLastMonth();

	@Query(value = "Select count(*) as num from playbook a where is_deleted = false and send_to_approved = 1" +
			" and YEAR(IFNULL(a.modified_date, a.create_date)) = YEAR(CURDATE() - INTERVAL 1 MONTH)" +
			" AND MONTH(IFNULL(a.modified_date, a.create_date)) = MONTH(CURDATE() - INTERVAL 1 MONTH)",nativeQuery = true)
	int countBySendToApprovedLastMonth();

	@Query(value = "Select status, count(*) as num from playbook a where is_deleted = false" +
			" and YEAR(IFNULL(a.modified_date, a.create_date)) = YEAR(CURDATE())" +
			" group by status", nativeQuery = true)
	List<Object[]> countByStatusThisYear();

	@Query(value = "Select count(*) as num from playbook a where is_deleted = false and send_to_approved = 1" +
			" and YEAR(IFNULL(a.modified_date, a.create_date)) = YEAR(CURDATE())",nativeQuery = true)
	int countBySendToApprovedThisYear();
}
