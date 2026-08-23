package in.maithilart.auth.dto;

public class AdminUserStatusRequest {

	private Boolean enabled;
	private Boolean accountLocked;

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getAccountLocked() {
		return accountLocked;
	}

	public void setAccountLocked(Boolean accountLocked) {
		this.accountLocked = accountLocked;
	}
}
