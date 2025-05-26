package pk.edu.pl.PASiR_Sosin_Jakub.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.LoginDto;
import pk.edu.pl.PASiR_Sosin_Jakub.dto.UserDto;
import pk.edu.pl.PASiR_Sosin_Jakub.model.User;
import pk.edu.pl.PASiR_Sosin_Jakub.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService=userService;
    }
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserDto dto){
        return ResponseEntity.ok(userService.register(dto));
    }
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginDto dto){
        try{
            String token=userService.login(dto);
            return ResponseEntity.ok(Map.of("token",token));
        }catch (UsernameNotFoundException | BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }
}


