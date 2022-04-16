package com.phoenix.read.service;

import com.phoenix.read.entity.Organizer;

import java.util.List;

public interface OrganizerService {
    List<Organizer> getOrganizerList();

    List<Organizer> getOrganizerListByName(String name);
}
