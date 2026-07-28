package in.maithilart.auth.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import in.maithilart.common.security.MaithilPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GatewayVerificationFilter extends OncePerRequestFilter {


	 @Override
	    protected boolean shouldNotFilter(HttpServletRequest request) {
	        String path = request.getRequestURI();

	        // Infra / swagger ko skip
	        return path.startsWith("/swagger")
	            || path.startsWith("/v3/api-docs")
	            || path.startsWith("/actuator");
	    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        
        String path = request.getRequestURI();

        // 1. Auth Open Endpoints ko skip karo (Login/Register/Public)
        if (path.startsWith("/auth/login")|| path.startsWith("/auth/admin/login") || path.startsWith("/auth/refresh") || path.startsWith("/auth/admin/refresh") || path.startsWith("/auth/register") || path.startsWith("/public/")) {
            try {
				filterChain.doFilter(request, response);
			} catch (java.io.IOException | ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return;
        }
        
     // 2. Gateway Verification check
        String gatewayAuth = request.getHeader("X-Gateway-Auth");
        if (!"verified".equals(gatewayAuth)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 if not via gateway
            return;
        }
        
     // 3. Role aur User Data Extraction (Identity Logic)
        String userId = request.getHeader("X-User-Id");
        String rolesHeader = request.getHeader("X-Roles");
        String userEmail = request.getHeader("X-User-Email");
        String fullName = request.getHeader("X-User-Full-Name");
		MaithilPrincipal principal = new MaithilPrincipal(userId, userEmail, fullName);
        
        if (userId != null && rolesHeader != null) {
            // Roles ko authorities mein badlo (Adding ROLE_ prefix for hasRole support)
        	// 1. Pehle header ko split karke array banao
        	String[] roles = rolesHeader.split(",");

        	// 2. Ek khaali list taiyar karo
        	List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        	// 3. Loop chala kar ek-ek role ko add karo
        	for (String role : roles) {
        	    String formattedRole = "ROLE_" + role.trim().toUpperCase();
        	    authorities.add(new SimpleGrantedAuthority(formattedRole));
        	}

            // Authentication object banao
            UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(principal, userEmail, authorities);
            
            // SecurityContext mein set karo
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        try {
			filterChain.doFilter(request, response);
		} catch (java.io.IOException | ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

