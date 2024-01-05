package hcmute.kltn.Backend.model.account.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import hcmute.kltn.Backend.exception.CustomException;
import hcmute.kltn.Backend.model.account.dto.entity.Account;
import hcmute.kltn.Backend.model.account.repository.AccountRepository;
import hcmute.kltn.Backend.model.account.service.IAccountDetailService;

@Service
public class AccountDetailsService implements UserDetailsService, IAccountDetailService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		boolean existsEmail = accountRepository.existsByEmail(username);
		if (!existsEmail) {
			throw new CustomException("Cannot find account");
		}
		
		Optional<Account> findAccount = accountRepository.findByEmail(username);
		if (findAccount.get().getStatus() == false) {
			throw new CustomException("Your account has been banned");
		}
		
		Account account = findAccount.get();

		List<GrantedAuthority> authorities = new ArrayList<>();
//      for (ERole role : user.get().getRoles()) {
//          authorities.add(new SimpleGrantedAuthority(role.toString()));
//      }
		authorities.add(new SimpleGrantedAuthority("ROLE_" + account.getRole()));

		return new org.springframework.security.core.userdetails.User(account.getUsername(),
				account.getPassword(), authorities);
	}
    
    @Override
    public Account getCurrentAccount() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		if (email == null || email == "") {
			throw new CustomException("Unauthenticated");
		}

		Optional<Account> account = accountRepository.findByEmail(email);
		if (account.isEmpty() || account.get().getStatus() == false) {
			throw new CustomException("Cannot find account");
		} 
		
		return account.get();
	}
}

