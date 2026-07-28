package in.maithilart.auth.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class InternalSecurityFilter extends OncePerRequestFilter {

	@Value("${gateway.secret}")
	private String gatewaySecret;

	@Value("${internal.secret}")
	private String internalSecret;


	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();

		// Infra / swagger ko skip
		return path.startsWith("/swagger") || path.startsWith("/v3/api-docs") || path.startsWith("/actuator");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();
		System.out.println("Request Recieved in internal filter on path: " + path);
		String expectedSecret = path.startsWith("/auth/api/internal/") ? internalSecret : gatewaySecret;

		String headerName = path.startsWith("/auth/api/internal/") ? "X-Internal-Secret" : "X-Gateway-Secret";

		String incomingSecret = request.getHeader(headerName);

		if (expectedSecret.equals(incomingSecret)) {
			System.out.println("Secrets are verified!!");
			System.out.println("Moving from internal security filter to next filter chain");
			filterChain.doFilter(request, response);

		} else {

			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");

			response.getWriter().write("{\"error\":\"Unauthorized Request\"}");
		}
	}

}
