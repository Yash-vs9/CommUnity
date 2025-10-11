package com.Flow.Backend.model;

import com.Flow.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> user= userRepository.findByUsername(username);
        if(user.isPresent()){
            UserModel userObj=user.get();
            String password = userObj.getPassword();
            if(password == null) {
                password = "{noop}dummy"; // {noop} tells Spring Security this is plaintext
            }
            return User.builder()
                    .username(userObj.getUsername())
                    .password(password)
                    .roles(userObj.getRole())
                    .build();

        }
        else{
            throw new UsernameNotFoundException(username);
        }
    }
}
