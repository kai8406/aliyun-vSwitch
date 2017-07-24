package com.mcloud.vswitch.mq;

import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mcloud.core.constant.PlatformEnum;
import com.mcloud.core.constant.mq.MQConstant;
import com.mcloud.core.mapper.JsonMapper;
import com.mcloud.core.util.EncodeUtils;
import com.mcloud.vswitch.business.AliyunVSwtichBusiness;
import com.mcloud.vswitch.client.VSwitchServiceDTO;

@Component
public class AliyunVSwitchMQServiceImpl implements AliyunVSwitchMQService {

	private static JsonMapper binder = JsonMapper.nonEmptyMapper();

	@Autowired
	private AliyunVSwtichBusiness business;

	@Override
	public void aliyunVSwitchAgg(Message message) {

		String receivedRoutingKey = message.getMessageProperties().getReceivedRoutingKey();

		String receiveString = EncodeUtils.EncodeMessage(message.getBody());

		VSwitchServiceDTO vSwitchServiceDTO = (VSwitchServiceDTO) binder.fromJson(receiveString,
				VSwitchServiceDTO.class);
		if (!PlatformEnum.aliyun.name().equalsIgnoreCase(vSwitchServiceDTO.getPlatformId())) {
			return;
		}

		if (MQConstant.ROUTINGKEY_AGG_VSWITCH_SAVE.equalsIgnoreCase(receivedRoutingKey)) {

			business.saveVSwtich(vSwitchServiceDTO);

		} else if (MQConstant.ROUTINGKEY_AGG_VSWITCH_UPDATE.equalsIgnoreCase(receivedRoutingKey)) {

			business.updateVSwtich(vSwitchServiceDTO);

		} else if (MQConstant.ROUTINGKEY_AGG_VSWITCH_REMOVE.equalsIgnoreCase(receivedRoutingKey)) {

			business.removeVSwtich(vSwitchServiceDTO);
		}
	}

}
