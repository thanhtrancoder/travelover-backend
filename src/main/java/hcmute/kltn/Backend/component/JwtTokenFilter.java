package hcmute.kltn.Backend.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import hcmute.kltn.Backend.model.account.dto.entity.Account;
import hcmute.kltn.Backend.model.account.repository.AccountRepository;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
	@Autowired
    private JwtTokenUtil jwtUtil;
	@Autowired
	private AccountRepository accountRepository;
 
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
 
        if (!hasAuthorizationBearer(request)) {
            filterChain.doFilter(request, response);
            return;
        }
 
        String token = getAccessToken(request);
 
        if (!jwtUtil.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        setAuthenticationContext(token, request);

        filterChain.doFilter(request, response);

    }
 
    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (ObjectUtils.isEmpty(header) || !header.startsWith("Bearer")) {
            return false;
        }
 
        return true;
    }
 
    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.split(" ")[1].trim();
        return token;
    }
 
    private void setAuthenticationContext(String token, HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(token);
        
        List<GrantedAuthority> authorities = getAuthorities(userDetails.getUsername());
 
        UsernamePasswordAuthenticationToken
            authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
 
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
 
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
 
    private UserDetails getUserDetails(String token) {
        Account userDetails = new Account();
        String email = jwtUtil.getSubject(token);
 
        userDetails.setEmail(email);

        return userDetails;
    }
    
    private List<GrantedAuthority> getAuthorities(String username) {
    	Optional<Account> user = accountRepository.findByEmail(username);
    	if (user.isPresent()) {
    		List<GrantedAuthority> authorities = new ArrayList<>();
//			for (ERole role : user.get().getRoles()) {
//			    authorities.add(new SimpleGrantedAuthority(role.toString()));
//			}
    		if (user.get().getRole().equals("SUPER_ADMIN")) {
    			authorities.add(new SimpleGrantedAuthority("ROLE_ENTERPRISE"));
    			
    			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    			authorities.add(new SimpleGrantedAuthority("ROLE_STAFF"));
    			authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    		} else if (user.get().getRole().equals("ADMIN")) {
    			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    			authorities.add(new SimpleGrantedAuthority("ROLE_STAFF"));
    			authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    		} else if (user.get().getRole().equals("STAFF")) {
    			authorities.add(new SimpleGrantedAuthority("ROLE_STAFF"));
    			authorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
    		} else {
    			authorities.add(new SimpleGrantedAuthority("ROLE_" + user.get().getRole()));
    		}
            
            return authorities;
    	} else {
    		return null;
    	}
    }
}
