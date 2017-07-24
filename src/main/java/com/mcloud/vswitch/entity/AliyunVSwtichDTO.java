package com.mcloud.vswitch.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mcloud.core.constant.ActiveEnum;
import com.mcloud.core.constant.PlatformEnum;
import com.mcloud.vswitch.constant.AliyunVSwitchStatusEnum;

import lombok.Data;

@Data
@Entity
@Table(name = "aliyun_vswitch")
public class AliyunVSwtichDTO {

	/**
	 * UUID主键.
	 */
	@Id
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@GeneratedValue(generator = "system-uuid")
	private String id;

	/**
	 * 数据状态,默认"A" {@link ActiveEnum}.
	 */
	@JsonIgnore
	@Column(name = "active")
	private String active = ActiveEnum.A.name();

	/**
	 * 平台ID. {@link PlatformEnum}
	 */
	@Column(name = "platform_id")
	private String platformId;

	/**
	 * 区域.
	 */
	@Column(name = "region_id")
	private String regionId;

	/**
	 * task对象,不持久化.
	 */
	@Transient
	private String taskId = "";

	/**
	 * 用户名,唯一.
	 */
	@Column(name = "user_name")
	private String username;

	/**
	 * 平台资源的唯一标识符.
	 */
	@Column(name = "uuid")
	private String uuid = "";

	/**
	 * 指定VSwitch的网段.
	 */
	@Column(name = "cidr_block")
	private String cidrBlock;

	/**
	 * 说明.
	 */
	@Column(name = "description")
	private String description = "";

	/**
	 * 服务/资源状态.{@link AliyunVSwitchStatusEnum}
	 */
	@Column(name = "status")
	private String status = AliyunVSwitchStatusEnum.Pending.name();

	/**
	 * vSwitch名称.
	 */
	@Column(name = "vswitch_name")
	private String vswitchName = "";

	/**
	 * 可用区 Id.
	 */
	@Column(name = "zone_id")
	private String zoneId;

	/**
	 * VPC聚合服务主键.
	 */
	@Column(name = "vpc_id")
	private String vpcId = "";

	/**
	 * VPC聚合服务的UUID.
	 */
	@Column(name = "vpc_uuid")
	private String vpcUuid = "";

	/**
	 * Router聚合服务的主键.
	 */
	@Column(name = "router_id")
	private String routerId = "";

	/**
	 * Router聚合服务的UUID.
	 */
	@Column(name = "router_uuid")
	private String routerUuid = "";

	/**
	 * RouterTable的主键.
	 */
	@Column(name = "router_table_id")
	private String routerTableId;

	/**
	 * RouterTable的UUID.
	 */
	@Column(name = "router_table_uuid")
	private String routerTableUuid;

	/**
	 * vSwitch的主键.
	 */
	@Column(name = "vswitch_id")
	private String vswitchId;

	/**
	 * 创建时间.
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "create_time")
	private Date createTime;

	/**
	 * 修改时间.
	 */
	@JsonIgnore
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "modify_time")
	private Date modifyTime;

}
