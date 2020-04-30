package com.nera.nms.repositories;

import com.nera.nms.models.InventoryHost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;


public interface InventoryHostRepository extends JpaRepository<InventoryHost, Long> {

    @Query(value = "SELECT h.* FROM nera.inventory_host h LEFT JOIN nera.host_group_host g ON h.id = g.host_id where g.group_host_id = :groupId and g.host_id = :hostId and h.is_active = 1", nativeQuery = true)
    List<InventoryHost> findByGroupHostId(@Param("groupId") long groupId, @Param("hostId") long hostId);

	@Query(value = "SELECT h.* FROM nera.inventory_host h LEFT JOIN nera.host_group_host g ON h.id = g.host_id " +
			"LEFT JOIN nera.inventory_group_host i ON i.id = g.group_host_id Where (i.name like %:name% or h.name like %:name%) and h.is_active = 1 and h.is_deleted = 0", nativeQuery = true)
	List<InventoryHost> findHostByName(String name);


	@Query(value = "SELECT h.* FROM nera.inventory_host h LEFT JOIN nera.host_group_host g ON h.id = g.host_id where g.group_host_id = :groupId and h.is_active = 1", nativeQuery = true)
	List<InventoryHost> findByGroupId(@Param("groupId") long groupId);

	@Query(value = "SELECT h.* FROM nera.inventory_host h where h.hostId = :hostId and h.is_active = 1", nativeQuery = true)
	List<InventoryHost> findByHostId(@Param("hostId") long hostId);

	@Query(value = "Select * from nera.inventory_host where is_deleted = 0 and name =:Name or (ip_address = :ipAddress and port = :port)", nativeQuery = true)
	List<InventoryHost> findByNameAndInventoryId(@Param("Name")String name, @Param("ipAddress") String ipAddress, @Param("port") int port);

	@Query(value = "Select * from nera.inventory_host where is_deleted = 0  and id <> :hostId and (name =:Name or (ip_address = :ipAddress and port = :port))", nativeQuery = true)
	List<InventoryHost> findByNameIpAndOtherID(@Param("Name")String name, @Param("ipAddress") String ipAddress, @Param("port") int port, @Param("hostId") long hostId);

	InventoryHost findOneById(Long id);
	
	@Query(value ="Select * from nera.inventory_host where is_deleted = 0 and search_string like %:searchString% or description like %:searchString%",
			nativeQuery = true)
	List<InventoryHost> findBySearchString(@Param("searchString")String searchString);

	@Query(value ="Select * from nera.inventory_host where is_deleted = 0 and is_active = 1 and search_string like %:searchString%",
			nativeQuery = true)
	List<InventoryHost> findBySearchStringAndActive(@Param("searchString")String searchString);

	// without condition
	@Query("from InventoryHost s where s.isDeleted = false and s.searchString like %:searchString% or s.description like %:searchString%")
	Page<InventoryHost> findAllRecord(
			@Param("searchString") String searchString,
			Pageable pageable);

	@Modifying
	@Transactional
	@Query(value = "delete from nera.host_group_host where host_id =?1", nativeQuery = true)
	void deleteHostDetail(long hostId);
}
