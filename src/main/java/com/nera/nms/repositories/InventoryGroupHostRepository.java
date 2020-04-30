package com.nera.nms.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nera.nms.models.InventoryGroupHost;


public interface InventoryGroupHostRepository extends JpaRepository<InventoryGroupHost, Long> {

	@Query(value = "Select * from nera.inventory_group_host where is_deleted = 0 and name =:Name", nativeQuery = true)
	InventoryGroupHost findByName(@Param("Name")String name);

	@Query(value = "Select * from nera.inventory_group_host where is_deleted = 0 and id <> :groupId and name =:Name", nativeQuery = true)
	InventoryGroupHost findByNameAndOtherId(@Param("Name") String name, @Param("groupId") long groupId);

	InventoryGroupHost findOneById(Long id);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO nera.host_group_host(group_host_id,host_id) values(?1,?2)", nativeQuery = true)
	void saveGroupHostDetail(Long groupHostId, Long hostId);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM nera.host_group_host where group_host_id =?1 and host_id=?2", nativeQuery = true)
	void deleteGroupHostDetail(Long groupHostId, Long hostId);

	@Modifying
	@Transactional
	@Query(value = "delete from nera.host_group_host where group_host_id =?1", nativeQuery = true)
	void deleteGroupHost(long groupHostId);
	
	@Query(value = "Select * from nera.inventory_group_host where is_deleted = 0 and search_string like %:searchString%",
			nativeQuery = true)
	List<InventoryGroupHost> findBySearchString(@Param("searchString")String searchString);

	// without condition
	@Query("from InventoryGroupHost s where s.isDeleted = false and s.searchString like %:searchString%")
	Page<InventoryGroupHost> findAllRecord(
			@Param("searchString") String searchString,
			Pageable pageable);

	@Query(value = "SELECT *, COUNT(*) AS hostCount FROM nera.inventory_group_host as groupHost INNER JOIN nera.host_group_host as keyGroupHost ON groupHost.id = keyGroupHost.group_host_id where groupHost.search_string like %:searchString% GROUP BY groupHost.id ORDER BY hostCount DESC", nativeQuery = true)
	List<InventoryGroupHost> sortByHostDesc(Pageable pageable, @Param("searchString") String searchString);

    @Query(value = "SELECT *, COUNT(*) AS hostCount FROM nera.inventory_group_host as groupHost INNER JOIN nera.host_group_host as keyGroupHost ON groupHost.id = keyGroupHost.group_host_id where groupHost.search_string like %:searchString% GROUP BY groupHost.id ORDER BY hostCount ASC", nativeQuery = true)
    List<InventoryGroupHost> sortByHostAsc(Pageable pageable, @Param("searchString") String searchString);
}
