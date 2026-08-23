package in.maithilart.auth.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.maithilart.auth.dto.AdminUserCreateRequest;
import in.maithilart.auth.dto.AdminUserResponse;
import in.maithilart.auth.dto.AdminUserStatusRequest;
import in.maithilart.auth.service.AdminUserService;
import in.maithilart.common.dto.MaithilResponse;
import in.maithilart.common.security.MaithilPrincipal;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	private final AdminUserService adminUserService;

	public AdminUserController(AdminUserService adminUserService) {
		this.adminUserService = adminUserService;
	}

	@GetMapping
	public ResponseEntity<MaithilResponse<List<AdminUserResponse>>> getAdmins() {
		return ResponseEntity.ok(MaithilResponse.success("ADMIN_USERS_FETCHED",
				"Admin users fetched successfully", adminUserService.getAdmins()));
	}

	@PostMapping
	public ResponseEntity<MaithilResponse<AdminUserResponse>> createAdmin(
			@Valid @RequestBody AdminUserCreateRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(MaithilResponse.success("ADMIN_USER_CREATED",
				"Admin user created successfully", adminUserService.createAdmin(request)));
	}

	@PatchMapping("/{userId}/status")
	public ResponseEntity<MaithilResponse<AdminUserResponse>> updateStatus(@PathVariable UUID userId,
			@RequestBody AdminUserStatusRequest request,
			Authentication authentication) {
		return ResponseEntity.ok(MaithilResponse.success("ADMIN_USER_STATUS_UPDATED",
				"Admin user status updated successfully",
				adminUserService.updateAdminStatus(userId, request, getActorUserId(authentication))));
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<MaithilResponse<Void>> deleteAdmin(@PathVariable UUID userId,
			Authentication authentication) {
		adminUserService.deleteAdmin(userId, getActorUserId(authentication));
		return ResponseEntity.ok(MaithilResponse.success("ADMIN_USER_DELETED",
				"Admin user deleted successfully", null));
	}

	private String getActorUserId(Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof MaithilPrincipal principal) {
			return principal.getUserId();
		}
		return null;
	}
}
