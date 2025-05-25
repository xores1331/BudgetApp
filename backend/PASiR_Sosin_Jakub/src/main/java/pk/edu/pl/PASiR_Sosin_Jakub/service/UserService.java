package pk.edu.pl.PASiR_Sosin_Jakub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.LoginDto;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.UserDto;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;
import pk.edu.pl.PASiR_Sosin_Jakub.repository.UserRepository;
import pk.edu.pl.PASiR_Sosin_Jakub.security.JwtUtil;

import java.util.ArrayList;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public User register(UserDto dto){
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public String login(LoginDto dto){
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Nie znaleziono uzytkownika"));
        if(!encoder.matches(dto.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Nieprawidlowe dane logowania");
        }
        return jwtUtil.generateToken(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("Nie znaleziono uzytkownika "+ email));
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                new ArrayList<>()
        );
    }
}
