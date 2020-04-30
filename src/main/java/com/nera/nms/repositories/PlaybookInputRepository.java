package com.nera.nms.repositories;

import com.nera.nms.models.PlaybookInput;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlaybookInputRepository extends JpaRepository<PlaybookInput, Long> {
	long countAllByFileManagementId(long id);
}
