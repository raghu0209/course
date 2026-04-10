package com.lms.course.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.lms.course.model.Account;
import com.lms.course.repository.AccountRepository;
import com.lms.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final AccountRepository accountRepository;
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    String path = request.getServletPath();
	    return path.startsWith("/v3/api-docs") 
	            || path.startsWith("/swagger-ui") 
	            || path.equals("/swagger-ui.html");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		log.info("Entered JwtFilter with header: {}", header);

		if (header == null || !header.startsWith("Bearer ")) {
			log.info("Entered if block: header is null or does not start with Bearer");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		log.info("Extracting token from header");
		String token = header.substring(7);
		boolean validateToken = jwtUtil.validateToken(token);
		if (!validateToken) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		Long userIdFromJWT = jwtUtil.getUserIdFromJWT(token);
		Optional<Account> userOpt = accountRepository.findById(userIdFromJWT);
		if (userOpt.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_" + userOpt.get().getRole().name()));
        
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                		userOpt.get().getUsername(),
                        null,
                        authorities
                );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
		
		request.setAttribute("AuthorisedUser", userOpt.get());
		request.setAttribute("userId", userIdFromJWT);
		request.setAttribute("role", userOpt.get().getRole());

		filterChain.doFilter(request, response);

	}

}
