package com.mcloud.vswitch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mcloud.core.persistence.BaseEntityCrudServiceImpl;
import com.mcloud.vswitch.entity.AliyunVSwtichDTO;
import com.mcloud.vswitch.repository.AliyunVSwitchRepository;

@Service
@Transactional
public class AliyunVSwtichService extends BaseEntityCrudServiceImpl<AliyunVSwtichDTO, AliyunVSwitchRepository> {

	@Autowired
	private AliyunVSwitchRepository repository;

	@Override
	protected AliyunVSwitchRepository getRepository() {
		return repository;
	}

}
