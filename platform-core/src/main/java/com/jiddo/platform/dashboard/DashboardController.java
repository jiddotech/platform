package com.jiddo.platform.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.jiddo.platform.dashboard.GenericDashBoardService.IFetchDashboardRequest;
import com.jiddo.platform.security.SecurityUtils;
import com.jiddo.platform.security.UserDTO;

import lombok.Data;

@RestController
public class DashboardController {

	@Lazy
	@Autowired
	private GenericDashBoardService filterService;

	@GetMapping("/secure/dashboard/app/{key}")
	public DashboardCardDTO getDashBoardCardDTOForAPP(@PathVariable("key") String key) {
		UserDTO user = SecurityUtils.getLoggedInUser();
		DHAppDashboardRequest request = new DHAppDashboardRequest();
		request.setKey(key);
		request.setUserId(user.getUserId());
		return filterService.getDashBoardCardDTO(request);
	}

	@Data
	public static class DHAppDashboardRequest implements IFetchDashboardRequest {
		private String key;
		private String userId;
	}

	@GetMapping("/secure/dashboard/czo/{key}")
	public DashboardCardDTO getDashBoardCardDTOForCZO(@PathVariable("key") String key) {
		CZODashboardRequest request = new CZODashboardRequest();
		request.setKey(key);
		return filterService.getDashBoardCardDTO(request);
	}

	@Data
	public static class CZODashboardRequest implements IFetchDashboardRequest {
		private String key;
	}

	@GetMapping("/secure/dashboard/group/{groupId}/{key}")
	public DashboardCardDTO getDashBoardCardDTOForUserGroup(@PathVariable("key") String key,
			@PathVariable("groupId") String groupId) {
		UserGroupDashboardRequest request = new UserGroupDashboardRequest();
		request.setKey(key);
		request.setGroupId(groupId);
		return filterService.getDashBoardCardDTO(request);
	}

	@Data
	public static class UserGroupDashboardRequest implements IFetchDashboardRequest {
		private String key;
		private String groupId;
	}

	@GetMapping("/secure/dashboard/fleet/{fleetId}/{key}")
	public DashboardCardDTO getDashBoardCardDTOForFleet(@PathVariable("key") String key,
			@PathVariable("fleetId") String fleetId) {
		FleetDashboardRequest request = new FleetDashboardRequest();
		request.setKey(key);
		request.setFleetId(fleetId);
		return filterService.getDashBoardCardDTO(request);
	}

	@Data
	public static class FleetDashboardRequest implements IFetchDashboardRequest {
		private String key;
		private String fleetId;
	}

}
