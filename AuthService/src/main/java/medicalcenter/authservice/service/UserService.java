package medicalcenter.authservice.service;

import lombok.RequiredArgsConstructor;
import medicalcenter.authservice.model.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDetails loadUserByPhone(String phone) throws UsernameNotFoundException {
        return userRepository
                .findByPhone(phone)
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user with phone: " + phone));
    }
}
