package com.phoenix.read.service.impl;

import com.phoenix.read.entity.Organizer;
import com.phoenix.read.mapper.OrganizerMapper;
import com.phoenix.read.service.OrganizerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizerServiceImpl implements OrganizerService {

    @Autowired
    private OrganizerMapper organizerMapper;

    @Override
    public List<Organizer> getOrganizerList() {
        return organizerMapper.getOrganizerList();
    }

    @Override
    public List<Organizer> getOrganizerListByName(String name) {
        return getOrganizerListByName(name);
    }
}
