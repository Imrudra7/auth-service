package in.maithilart.auth.service;

import java.util.List;
import java.util.UUID;

import in.maithilart.auth.dto.AdminUserCreateRequest;
import in.maithilart.auth.dto.AdminUserResponse;
import in.maithilart.auth.dto.AdminUserStatusRequest;

public interface AdminUserService {

	List<AdminUserResponse> getAdmins();

	AdminUserResponse createAdmin(AdminUserCreateRequest request);

	AdminUserResponse updateAdminStatus(UUID userId, AdminUserStatusRequest request, String actorUserId);

	void deleteAdmin(UUID userId, String actorUserId);
}
