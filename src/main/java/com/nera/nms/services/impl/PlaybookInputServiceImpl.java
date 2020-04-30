package com.nera.nms.services.impl;

import com.nera.nms.repositories.PlaybookInputRepository;
import com.nera.nms.services.PlaybookInputService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlaybookInputServiceImpl implements PlaybookInputService {

    @Autowired
    private PlaybookInputRepository playbookInputRepository;

    @Override
    public long count(long fileId) {
        return playbookInputRepository.countAllByFileManagementId(fileId);
    }
}
