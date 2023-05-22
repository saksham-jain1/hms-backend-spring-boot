package com.saksham.backend.Controllers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.backend.Mappers.UsersMapper;
import com.saksham.backend.Models.Otp;
import com.saksham.backend.Models.Users;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

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

  @DeleteMapping
  public ResponseEntity<?> removeUser(@RequestBody String email) {
    try {
      usersMapper.deleteUser(email);
      return ResponseEntity.ok("User Removed Successfully");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/staf")
  public ResponseEntity<?> getStaf(@RequestParam int hotelId) {
    try {
      return ResponseEntity.ok(usersMapper.getAllStaf(hotelId));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/verify")
  public ResponseEntity<?> sendOtp(@RequestParam String email, @RequestParam int type) {
    try {
      Users user = usersMapper.findByEmail(email);
      if (type == 1) {
        if (user == null)
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found! Plz register");
      }
      if (user != null && type == 0)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists! Plz login");

      if (sendOtp(email))
        return ResponseEntity.ok("Mail Sent Successfully");
      else
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/verify")
  public ResponseEntity<?> verifyUser(@RequestBody Otp data, @RequestParam int type) {
    try {
      LocalDateTime time = LocalDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      Otp otp = usersMapper.getOtp(data.getEmail());
      long d = Duration.between(LocalDateTime.parse(otp.getCreate_time(), format), time).toMinutes();
      if (d <= 5 && otp.getOtp().equals(data.getOtp())) {
        if (type == 1) {
          BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
          usersMapper.changePassword(passwordEncoder.encode(data.getPassword()), data.getEmail());
        }
        return ResponseEntity.ok("Successful");
      } else if (!otp.getOtp().equals(data.getOtp())) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong OTP");
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong OTP");
      }
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PutMapping("/verify")
  public ResponseEntity<?> changePassword(@RequestBody Map<String, String> m) {
    try {
      Users foundUser = usersMapper.findByEmail(m.get("email"));
      if (foundUser != null) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean passwordMatches = passwordEncoder.matches(m.get("password"), foundUser.getPassword());

        if (passwordMatches) {
          usersMapper.changePassword(passwordEncoder.encode(m.get("newPassword")), m.get("email"));
          return ResponseEntity.ok("Password Changed Successfully!");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong Old Password!");
      }

      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found!");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

  }

  @PostMapping("/forgot")
  public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> m) {
    try {
      BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      usersMapper.changePassword(passwordEncoder.encode(m.get("password")), m.get("email"));
      return ResponseEntity.ok("Password Changed Successfully");
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/")
  public ResponseEntity<?> updateUser(@RequestBody Map<String, String> m) {
    try {
      usersMapper.update(m.get("email"), m.get("field"), m.get("value"));
      Users user = usersMapper.findByEmail(m.get("email"));
      user.setPassword("");
      return ResponseEntity.ok(user);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  public boolean mailer(String to, String messageString) {
    try {
      Properties properties = new Properties();
      properties.put("mail.smtp.auth", true);
      properties.put("mail.smtp.starttls.enable", true);
      properties.put("mail.smtp.port", "587");
      properties.put("mail.smtp.host", "smtp.gmail.com");

      Session session = Session.getInstance(properties, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {

          String password = "ptkftxovbvylffqo";
          String username = "sj20011002";
          return new PasswordAuthentication(username, password);
        }
      });

      Message message = new MimeMessage(session);

      message.setFrom(new InternetAddress("phamacy.management.syatem@gmail.com"));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
      message.setSubject("Verification Mail");
      message.setText(messageString);

      Transport.send(message);

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean sendOtp(String email) {
    try {
      LocalDateTime time = LocalDateTime.now();
      DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      Otp otp = usersMapper.getOtp(email);
      String otp1 = ((int) (Math.random() * 9000) + 1000) + "";
      if (otp == null)
        usersMapper.setOtp(email, otp1, time.format(format));
      else
        usersMapper.updateOtp(email, otp1, time.format(format));
      if (mailer(email, "Otp form namaste stays is: " + otp1))
        return true;
      else
        return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

}