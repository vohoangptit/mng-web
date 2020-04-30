package com.nera.nms.repositories;


import com.nera.nms.models.PlaybookHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaybookHistoryRepository extends JpaRepository<PlaybookHistory, Long> {

    List<PlaybookHistory> findByPlaybookId(Long id);

    PlaybookHistory findPlaybookHistoryById(Long id);

}
