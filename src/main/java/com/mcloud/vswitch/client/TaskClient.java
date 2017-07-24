package com.mcloud.vswitch.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.mcloud.core.constant.task.TaskDTO;

@FeignClient("task-service")
public interface TaskClient {

	@GetMapping("/task/{id}")
	TaskDTO getTask(@PathVariable(value = "id") String id);

	@PostMapping("/task/")
	TaskDTO saveTask(@RequestBody TaskDTO taskDTO);

	@PutMapping("/task/{id}")
	TaskDTO updateTask(@PathVariable(value = "id") String id, @RequestBody TaskDTO taskDTO);
}
