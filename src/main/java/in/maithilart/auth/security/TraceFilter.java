package in.maithilart.auth.security;

import java.io.IOException;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import in.maithilart.common.constants.MaithilConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class TraceFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {

		String correlationId = request.getHeader(MaithilConstants.CORRELATION_ID_HEADER);

		if (correlationId == null || correlationId.isBlank()) {
			correlationId = "N/A";
		}

		MDC.put(MaithilConstants.MDC_KEY, correlationId);
		String caller = request.getHeader(MaithilConstants.CALLER_SERVICE_HEADER);
		if (caller == null || caller.isBlank()) {
		    caller = "gateway";
		}

		MDC.put("caller", caller);
		MDC.put("method", request.getMethod());

		MDC.put("uri", request.getRequestURI());

		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.remove(MaithilConstants.MDC_KEY); // Memory leak se bachne ke liye clear karna zaroori hai
			MDC.remove("caller");

			MDC.remove("method");

			MDC.remove("uri");
		}
	}
}
