package com.project.shopapp.authentication;

import com.project.shopapp.entity.UserEntity;
import com.project.shopapp.repository.UserRepostiory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepostiory userRepostiory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepostiory.findByPhoneNumber(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User doesn't exist!");
        }

        return new MyUserDetails(user.get());
    }
}
