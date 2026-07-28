package in.maithilart.auth.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import in.maithilart.common.context.provider.RequestContextProvider;
import jakarta.servlet.http.HttpServletRequest;

@Component
@RequestScope
public class ServletRequestContextProvider implements RequestContextProvider {

	private final HttpServletRequest request;

	public ServletRequestContextProvider(HttpServletRequest request) {
		this.request = request;
	}

	public String getHeader(String name) {
		return request.getHeader(name);
	}

	@Override
	public String getRequestUri() {
		return request.getRequestURI();
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}

	@Override
	public String getClientIp() {

		String forwarded = request.getHeader("X-Forwarded-For");

		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}

		return request.getRemoteAddr();
	}

	@Override
	public String getQueryString() {
		return request.getQueryString();
	}

	@Override
	public String getUserAgent() {
		return request.getHeader("User-Agent");
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	public String getRequestId() {
		return request.getHeader("X-Request-Id");
	}
}