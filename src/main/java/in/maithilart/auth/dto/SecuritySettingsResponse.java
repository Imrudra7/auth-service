package in.maithilart.auth.dto;

public class SecuritySettingsResponse {

	private boolean cookieHttpOnly;
	private boolean cookieSecure;
	private String cookieSameSite;
	private String cookiePath;
	private long accessTokenMaxAgeSeconds;
	private long refreshTokenMaxAgeSeconds;
	private int maxFailedLoginAttempts;

	public boolean isCookieHttpOnly() {
		return cookieHttpOnly;
	}

	public void setCookieHttpOnly(boolean cookieHttpOnly) {
		this.cookieHttpOnly = cookieHttpOnly;
	}

	public boolean isCookieSecure() {
		return cookieSecure;
	}

	public void setCookieSecure(boolean cookieSecure) {
		this.cookieSecure = cookieSecure;
	}

	public String getCookieSameSite() {
		return cookieSameSite;
	}

	public void setCookieSameSite(String cookieSameSite) {
		this.cookieSameSite = cookieSameSite;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}

	public long getAccessTokenMaxAgeSeconds() {
		return accessTokenMaxAgeSeconds;
	}

	public void setAccessTokenMaxAgeSeconds(long accessTokenMaxAgeSeconds) {
		this.accessTokenMaxAgeSeconds = accessTokenMaxAgeSeconds;
	}

	public long getRefreshTokenMaxAgeSeconds() {
		return refreshTokenMaxAgeSeconds;
	}

	public void setRefreshTokenMaxAgeSeconds(long refreshTokenMaxAgeSeconds) {
		this.refreshTokenMaxAgeSeconds = refreshTokenMaxAgeSeconds;
	}

	public int getMaxFailedLoginAttempts() {
		return maxFailedLoginAttempts;
	}

	public void setMaxFailedLoginAttempts(int maxFailedLoginAttempts) {
		this.maxFailedLoginAttempts = maxFailedLoginAttempts;
	}
}
