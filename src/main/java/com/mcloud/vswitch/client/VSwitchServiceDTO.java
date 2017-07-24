package com.mcloud.vswitch.client;

import javax.persistence.Column;

import com.mcloud.core.constant.PlatformEnum;

import lombok.Data;

/**
 * VPC聚合服务持久化对象.
 * 
 * @author liukai
 *
 */
@Data
public class VSwitchServiceDTO {

	/**
	 * UUID主键.
	 */
	private String vswitchId;

	/**
	 * 平台ID. {@link PlatformEnum}
	 */
	private String platformId;

	/**
	 * 区域.
	 */
	private String regionId;

	/**
	 * task对象,不持久化.
	 */
	private String taskId = "";

	/**
	 * 用户名,唯一.
	 */
	private String username;

	/**
	 * 平台资源的唯一标识符.
	 */
	private String vswitchUuid = "";

	/**
	 * 指定VSwitch的网段.
	 */
	private String cidrBlock;

	/**
	 * 说明.
	 */
	private String description = "";

	/**
	 * 服务/资源状态.{@link VSwitchStatusEnum}
	 */
	@Column(name = "status")
	private String status;

	/**
	 * vSwitch名称.
	 */
	private String vswitchName = "";

	/**
	 * 可用区 Id.
	 */
	private String zoneId;

	/**
	 * VPC聚合服务主键.
	 */
	private String vpcId = "";

	/**
	 * VPC聚合服务的UUID.
	 */
	private String vpcUuid = "";

	/**
	 * Router聚合服务的主键.
	 */
	private String routerId = "";

	/**
	 * Router聚合服务的UUID.
	 */
	private String routerUuid = "";

	/**
	 * RouterTable的主键.
	 */
	private String routerTableId;

	/**
	 * RouterTable的UUID.
	 */
	private String routerTableUuid;

}
