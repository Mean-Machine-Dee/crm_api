package com.crm.api.controllers;


import com.crm.api.crm.models.ERole;
import com.crm.api.crm.models.Role;
import com.crm.api.crm.models.User;
import com.crm.api.crm.repository.RoleRepository;
import com.crm.api.crm.repository.UserRepository;
import com.crm.api.payload.requests.LoginRequest;
import com.crm.api.payload.requests.SignUpPayload;
import com.crm.api.payload.requests.SignUpRequest;
import com.crm.api.payload.requests.SlideRequest;
import com.crm.api.payload.response.JwtResponse;
import com.crm.api.payload.response.MessageResponse;
import com.crm.api.security.jwt.JwtUtils;
import com.crm.api.security.services.UserDetailsImpl;
import com.crm.api.services.UserService;
import com.crm.api.utils.AppUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

@Autowired
    private UserService userService;
     AppUtils appUtils = new AppUtils();

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Value("${crm.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        String thumbnail = "";
        Authentication authentication =authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
       String jwt = jwtUtils.generateJwtToken(authentication);

        logger.info("JWT IS {}", jwt);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Long expiry = appUtils.addHoursToJavaUtilDate(new Date(),5).getTime();
        logger.info("Expire {}",expiry);
        Optional<User> user = userRepository.findByEmail(userDetails.getEmail());
        if(user.isPresent()){
            logger.info("User is present {}",user.get().getThumbnail());
            thumbnail =  user.get().getThumbnail();
        }
        return ResponseEntity.ok(
                new JwtResponse(jwt,userDetails.getId(),userDetails.getUsername(),userDetails.getEmail(),roles,expiry, thumbnail));
    }


    @PostMapping(value = "/signup")
    public ResponseEntity<?> registerUser(@RequestParam("file") MultipartFile file, @RequestParam("data") SignUpRequest signUpRequest){
      logger.info("request {}",signUpRequest);


      //TODO:: move this checks to service layer
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error:Username is already taken!"));
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error:Email is already taken!"));
        }



      return userService.registerUser(signUpRequest, file);
    }

}
