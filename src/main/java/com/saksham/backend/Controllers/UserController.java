package com.saksham.backend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.backend.Mappers.UsersMapper;
import com.saksham.backend.Models.Users;

@RestController
@RequestMapping("/api/user")
public class UserController {
  @Autowired
  private UsersMapper usersMapper;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody Users user) {
    try {
      Users foundUser = usersMapper.findByEmail(user.getEmail());
      if (foundUser != null) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatches = passwordEncoder.matches(user.getPassword(), foundUser.getPassword());

        if (passwordMatches) {
          foundUser.setPassword(null);
          return ResponseEntity.ok(foundUser);
        }
      }

      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user not found");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

  }

  @PostMapping("/register")
  public ResponseEntity<Users> addUsers(@RequestBody Users user) {
    try {
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      usersMapper.addUser(user);
      user.setPassword(null);
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}