package com.mcloud.vswitch.business;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.ecs.model.v20140526.CreateVSwitchRequest;
import com.aliyuncs.ecs.model.v20140526.CreateVSwitchResponse;
import com.aliyuncs.ecs.model.v20140526.DeleteVSwitchRequest;
import com.aliyuncs.ecs.model.v20140526.DeleteVSwitchResponse;
import com.aliyuncs.ecs.model.v20140526.ModifyVSwitchAttributeRequest;
import com.aliyuncs.ecs.model.v20140526.ModifyVSwitchAttributeResponse;
import com.aliyuncs.exceptions.ClientException;
import com.mcloud.core.constant.ActiveEnum;
import com.mcloud.core.constant.AggTypeEnum;
import com.mcloud.core.constant.mq.MQConstant;
import com.mcloud.core.constant.result.ResultDTO;
import com.mcloud.core.constant.result.ResultEnum;
import com.mcloud.core.constant.task.TaskDTO;
import com.mcloud.core.constant.task.TaskStatusEnum;
import com.mcloud.core.mapper.BeanMapper;
import com.mcloud.vswitch.client.AccesskeyDTO;
import com.mcloud.vswitch.client.VSwitchServiceDTO;
import com.mcloud.vswitch.constant.AliyunVSwitchStatusEnum;
import com.mcloud.vswitch.entity.AliyunVSwtichDTO;
import com.mcloud.vswitch.service.AliyunVSwtichService;

@Component
public class AliyunVSwtichBusiness extends AbstractAliyunCommon {

	@Autowired
	protected AliyunVSwtichService service;

	/**
	 * 根据阿里云的Id获得AliyunVpcDTO对象.
	 * 
	 * @param uuid
	 * @return
	 */
	private AliyunVSwtichDTO getAliyunVSwtichDTOByUUID(String uuid) {
		Map<String, Object> map = new HashMap<>();
		map.put("EQ_uuid", uuid);
		return service.find(map);
	}

	public void removeVSwtich(VSwitchServiceDTO vSwitchServiceDTO) {

		// Step.1 创建Task对象.
		TaskDTO taskDTO = taskClient.getTask(vSwitchServiceDTO.getTaskId());

		// Step.2 获得AliyunRouterDTO对象,并更新状态.
		AccesskeyDTO accesskeyDTO = accountClient
				.getAccesskey(vSwitchServiceDTO.getUsername(), vSwitchServiceDTO.getPlatformId()).getData();

		// Step.3 获得AliyunVSwtichDTO.
		AliyunVSwtichDTO aliyunVSwtichDTO = getAliyunVSwtichDTOByUUID(vSwitchServiceDTO.getVswitchUuid());

		// Step.4 调用阿里云SDK执行操作.
		DeleteVSwitchRequest request = new DeleteVSwitchRequest();
		request.setVSwitchId(aliyunVSwtichDTO.getUuid());

		IAcsClient client = getServiceInstance(vSwitchServiceDTO.getRegionId(), accesskeyDTO);

		DeleteVSwitchResponse response = null;

		try {
			response = (DeleteVSwitchResponse) client.getAcsResponse(request);

			taskDTO.setRequestId(response.getRequestId());

		} catch (ClientException e) {

			taskDTO.setStatus(TaskStatusEnum.执行失败.name());
			taskDTO.setResponseCode(e.getErrCode());
			taskDTO.setResponseData(e.getErrMsg());
			taskDTO = taskClient.updateTask(taskDTO.getId(), taskDTO);

			ResultDTO resultDTO = new ResultDTO(vSwitchServiceDTO.getVswitchId(), AggTypeEnum.vSwitch.name(),
					ResultEnum.ERROR.name(), taskDTO.getId(), vSwitchServiceDTO.getUsername(),
					vSwitchServiceDTO.getVswitchUuid());

			rabbitTemplate.convertAndSend(MQConstant.MQ_EXCHANGE_NAME, MQConstant.ROUTINGKEY_RESULT_REMOVE,
					binder.toJson(resultDTO));
			return;
		}

		taskDTO.setStatus(TaskStatusEnum.执行成功.name());
		taskDTO = taskClient.updateTask(taskDTO.getId(), taskDTO);

		aliyunVSwtichDTO.setActive(ActiveEnum.N.name());
		aliyunVSwtichDTO = service.saveAndFlush(aliyunVSwtichDTO);

		ResultDTO resultDTO = new ResultDTO(vSwitchServiceDTO.getVswitchId(), AggTypeEnum.vSwitch.name(),
				ResultEnum.SUCCESS.name(), taskDTO.getId(), vSwitchServiceDTO.getUsername(),
				vSwitchServiceDTO.getVswitchUuid());

		rabbitTemplate.convertAndSend(MQConstant.MQ_EXCHANGE_NAME, MQConstant.ROUTINGKEY_RESULT_REMOVE,
				binder.toJson(resultDTO));
	}

	public void saveVSwtich(VSwitchServiceDTO vSwitchServiceDTO) {

		// Step.1 创建Task对象.
		TaskDTO taskDTO = taskClient.getTask(vSwitchServiceDTO.getTaskId());

		// Step.2 获得AliyunRouterDTO对象,并更新状态.
		AccesskeyDTO accesskeyDTO = accountClient
				.getAccesskey(vSwitchServiceDTO.getUsername(), vSwitchServiceDTO.getPlatformId()).getData();

		// Step.3 持久化AliyunVSwtichDTO.
		AliyunVSwtichDTO aliyunVSwtichDTO = BeanMapper.map(vSwitchServiceDTO, AliyunVSwtichDTO.class);
		aliyunVSwtichDTO.setVswitchId(vSwitchServiceDTO.getVswitchId());
		aliyunVSwtichDTO.setCreateTime(new Date());

		aliyunVSwtichDTO = service.saveAndFlush(aliyunVSwtichDTO);

		// Step.4 调用阿里云SDK执行操作
		CreateVSwitchRequest request = new CreateVSwitchRequest();
		request.setVSwitchName(aliyunVSwtichDTO.getVswitchName());
		request.setDescription(aliyunVSwtichDTO.getDescription());
		request.setCidrBlock(aliyunVSwtichDTO.getCidrBlock());
		request.setRegionId(aliyunVSwtichDTO.getRegionId());
		request.setZoneId(aliyunVSwtichDTO.getZoneId());
		request.setVpcId(aliyunVSwtichDTO.getVpcUuid());

		IAcsClient client = getServiceInstance(aliyunVSwtichDTO.getRegionId(), accesskeyDTO);

		CreateVSwitchResponse response = null;
		try {
			response = client.getAcsResponse(request);

			taskDTO.setRequestId(response.getRequestId());

		} catch (ClientException e) {

			aliyunVSwtichDTO.setStatus(AliyunVSwitchStatusEnum.Error.name());
			aliyunVSwtichDTO = service.saveAndFlush(aliyunVSwtichDTO);

			// 修改Task对象执行状态.
			taskDTO.setStatus(TaskStatusEnum.执行失败.name());
			taskDTO.setResponseCode(e.getErrCode());
			taskDTO.setResponseData(e.getErrMsg());
			taskDTO = taskClient.updateTask(taskDTO.getId(), taskDTO);

			ResultDTO resultDTO = new ResultDTO(vSwitchServiceDTO.getVswitchId(), AggTypeEnum.vSwitch.name(),
					ResultEnum.ERROR.name(), taskDTO.getId(), vSwitchServiceDTO.getUsername(), "");

			// 将执行的结果进行广播.
			rabbitTemplate.convertAndSend(MQConstant.MQ_EXCHANGE_NAME, MQConstant.ROUTINGKEY_RESULT_SAVE,
					binder.toJson(resultDTO));
			return;
		}
		aliyunVSwtichDTO.setStatus(AliyunVSwitchStatusEnum.Available.name());
		aliyunVSwtichDTO.setUuid(response.getVSwitchId());
		aliyunVSwtichDTO = service.saveAndFlush(aliyunVSwtichDTO);

		taskDTO.setStatus(TaskStatusEnum.执行成功.name());
		taskDTO = taskClient.updateTask(taskDTO.getId(), taskDTO);

		ResultDTO resultDTO = new ResultDTO(vSwitchServiceDTO.getVswitchId(), AggTypeEnum.vSwitch.name(),
				ResultEnum.SUCCESS.name(), taskDTO.getId(), vSwitchServiceDTO.getUsername(),
				aliyunVSwtichDTO.getUuid());

		// Step.7 将执行的结果进行广播.
		rabbitTemplate.convertAndSend(MQConstant.MQ_EXCHANGE_NAME, MQConstant.ROUTINGKEY_RESULT_SAVE,
				binder.toJson(resultDTO));
	}

	public void updateVSwtich(VSwitchServiceDTO vSwitchServiceDTO) {
		// Step.1 获得Task对象.
		TaskDTO taskDTO = taskClient.getTask(vSwitchServiceDTO.getTaskId());

		// Step.2 根据username获得阿里云accesskeyId和accesskeySecret
		AccesskeyDTO accesskeyDTO = accountClient
				.getAccesskey(vSwitchServiceDTO.getUsername(), vSwitchServiceDTO.getPlatformId()).getData();

		// Step.3 查询AliyunVpcDTO.
		AliyunVSwtichDTO aliyunVSwtichDTO = getAliyunVSwtichDTOByUUID(vSwitchServiceDTO.getVswitchUuid());

		// Step.4 调用阿里云SDK执行操作.
		ModifyVSwitchAttributeRequest request = new ModifyVSwitchAttributeRequest();
		request.setDescription(vSwitchServiceDTO.getDescription());
		request.setVSwitchName(vSwitchServiceDTO.getVswitchName());
		request.setVSwitchId(vSwitchServiceDTO.getVswitchUuid());

		IAcsClient client = getServiceInstance(aliyunVSwtichDTO.getRegionId(), accesskeyDTO);

		ModifyVSwitchAttributeResponse response = null;
		try {
			response = (ModifyVSwitchAttributeResponse) client.getAcsResponse(request);

			taskDTO.setRequestId(response.getRequestId());
		} catch (ClientException e) {
			/// 修改Task对象执行状态.
			taskDTO.setStatus(TaskStatusEnum.执行失败.name());
			taskDTO.setResponseCode(e.getErrCode());
			taskDTO.setResponseData(e.getErrMsg());
			taskDTO = taskClient.updateTask(taskDTO.getId(), taskDTO);

			ResultDTO resultDTO = new ResultDTO(vSwitchServiceDTO.getVswitchId(), AggTypeEnum.vSwitch.name(),
					ResultEnum.ERROR.name(), taskDTO.getId(), vSwitchServiceDTO.getUsername(),
					aliyunVSwtichDTO.getUuid());

			// 将执行的结果进行广播.
			rabbitTemplate.convertAndSend(MQConstant.MQ_EXCHANGE_NAME, MQConstant.ROUTINGKEY_RESULT_UPDATE,
					binder.toJson(resultDTO));
			return;
		}

		// Step.5 更新Task和服务对象.
		aliyunVSwtichDTO.setDescription(vSwitchServiceDTO.getDescription());
		aliyunVSwtichDTO.setVswitchName(vSwitchServiceDTO.getVswitchName());
		aliyunVSwtichDTO.setModifyTime(new Date());
		aliyunVSwtichDTO = service.saveAndFlush(aliyunVSwtichDTO);

		taskDTO.setStatus(TaskStatusEnum.执行成功.name());
		taskDTO = taskClient.updateTask(taskDTO.getId(), taskDTO);

		ResultDTO resultDTO = new ResultDTO(vSwitchServiceDTO.getVswitchId(), AggTypeEnum.vSwitch.name(),
				ResultEnum.SUCCESS.name(), taskDTO.getId(), vSwitchServiceDTO.getUsername(),
				aliyunVSwtichDTO.getUuid());

		// Step.6 将执行的结果进行广播.
		rabbitTemplate.convertAndSend(MQConstant.MQ_EXCHANGE_NAME, MQConstant.ROUTINGKEY_RESULT_UPDATE,
				binder.toJson(resultDTO));

	}
}
