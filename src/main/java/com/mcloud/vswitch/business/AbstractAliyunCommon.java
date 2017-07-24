package com.mcloud.vswitch.business;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.mcloud.core.mapper.JsonMapper;
import com.mcloud.vswitch.client.AccesskeyDTO;
import com.mcloud.vswitch.client.AccountClient;
import com.mcloud.vswitch.client.TaskClient;

/**
 * aliyun business common.
 * 
 * @author liukai
 *
 */
public abstract class AbstractAliyunCommon {

	protected static JsonMapper binder = JsonMapper.nonEmptyMapper();

	/**
	 * 阿里云初始化连接.
	 * 
	 * @param regionId
	 * @param accesskeyDTO
	 * @return
	 */
	protected static IAcsClient getServiceInstance(String regionId, AccesskeyDTO accesskeyDTO) {
		DefaultProfile profile = DefaultProfile.getProfile(regionId, accesskeyDTO.getAccesskeyId(),
				accesskeyDTO.getAccesskeySecret());
		IAcsClient client = new DefaultAcsClient(profile);
		return client;
	}

	@Autowired
	protected AccountClient accountClient;

	@Autowired
	protected RabbitTemplate rabbitTemplate;

	@Autowired
	protected TaskClient taskClient;

}
