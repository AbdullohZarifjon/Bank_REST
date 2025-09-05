package com.example.bank_rest.security.userdetails;

import com.example.bank_rest.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        if (login.matches("^\\+?\\d{9,15}$")) {
            return userRepository.findByPhoneNumber(normalize(login))
                    .orElseThrow(() -> new UsernameNotFoundException(login));
        } else {
            return userRepository.findByUsername(login)
                    .orElseThrow(() -> new UsernameNotFoundException(login));
        }
    }

    public static String normalize(String phoneNumber) {
        if (phoneNumber == null) return null;
        return phoneNumber.replaceAll("[^+0-9]", "");
    }


}
