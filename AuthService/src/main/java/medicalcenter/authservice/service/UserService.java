package medicalcenter.authservice.service;

import lombok.RequiredArgsConstructor;
import medicalcenter.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Username is the phone per User.getUsername()
        return userRepository
                .findByPhone(username)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user with phone: " + username));
    }
}
