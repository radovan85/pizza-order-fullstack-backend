package com.radovan.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.radovan.spring.service.impl.UserDetailsImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	private CorsHandler corsHandler;

	private String[] unSecuredPaths = new String[] { "/login", "/register"};
	private String[] adminPaths = new String[] { "/api/admin/**" };
	private String[] userPaths = new String[] { "/api/cart/**", "/api/order/**" };

	private AntPathRequestMatcher[] getAntPathRequestMatchers() {
		AntPathRequestMatcher[] requestMatchers = new AntPathRequestMatcher[unSecuredPaths.length];
		for (int i = 0; i < unSecuredPaths.length; i++) {
			requestMatchers[i] = new AntPathRequestMatcher(unSecuredPaths[i]);
		}
		return requestMatchers;
	}

	private AntPathRequestMatcher[] getAdminPathRequestMatchers() {
		AntPathRequestMatcher[] returnValue = new AntPathRequestMatcher[adminPaths.length];
		for (int x = 0; x < adminPaths.length; x++) {
			returnValue[x] = new AntPathRequestMatcher(adminPaths[x]);
		}

		return returnValue;
	}

	private AntPathRequestMatcher[] getUserPathRequestMatchers() {
		AntPathRequestMatcher[] returnValue = new AntPathRequestMatcher[userPaths.length];
		for (int x = 0; x < userPaths.length; x++) {
			returnValue[x] = new AntPathRequestMatcher(userPaths[x]);
		}

		return returnValue;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		AntPathRequestMatcher[] requestMatchers = getAntPathRequestMatchers();
		AntPathRequestMatcher[] requestAdminMatchers = getAdminPathRequestMatchers();
		AntPathRequestMatcher[] requestUserMatchers = getUserPathRequestMatchers();
		return httpSecurity
				.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling((exception) -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(requestMatchers).anonymous()
						.requestMatchers(requestAdminMatchers).hasAuthority("ADMIN")
						.requestMatchers(requestUserMatchers).hasAuthority("ROLE_USER")
						.requestMatchers("/api/users/currentUser").permitAll()
						.anyRequest().authenticated()

				).addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return new ProviderManager(authProvider);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new UserDetailsImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}