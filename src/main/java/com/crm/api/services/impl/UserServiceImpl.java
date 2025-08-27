package com.crm.api.services.impl;

import com.crm.api.api.models.Deposit;
import com.crm.api.api.repository.DepositRepository;
import com.crm.api.controllers.AuthController;
import com.crm.api.crm.models.ERole;
import com.crm.api.crm.models.Role;
import com.crm.api.crm.models.Ticket;
import com.crm.api.crm.models.User;
import com.crm.api.crm.repository.RoleRepository;
import com.crm.api.crm.repository.TicketRepository;
import com.crm.api.crm.repository.UserRepository;
import com.crm.api.dtos.EditDTO;
import com.crm.api.dtos.UserDTO;
import com.crm.api.payload.requests.SignUpRequest;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.payload.response.MessageResponse;
import com.crm.api.services.UserService;
import com.crm.api.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserServiceImpl implements UserService {

     AppUtils appUtils = new AppUtils();

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepositRepository depositRepository;
    @Override
    public GlobalResponse editUser(EditDTO userDTO, int id) {
        //get user
       Optional<User> user = userRepository.findById((long) id);
        if(user.isPresent()){
            User edit = user.get();
            edit.setUsername(userDTO.getUsername());
            edit.setEmail(userDTO.getEmail());
            edit.setRoles(getAllRoles(userDTO.getRoles()));
            if(userDTO.getPassword() != null){
                edit.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }
            userRepository.save(edit);
            return new GlobalResponse(null, true, false, "user updated successful");
        }
        return new GlobalResponse(null, false, true, "user not found");
    }

    private Set<Role> getAllRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();
       try{
        strRoles.forEach(role ->{

            switch (role){
                case "admin":
                    Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(()-> new RuntimeException("Error: Role admin not found"));
                    roles.add(roleAdmin);
                    logger.info("searching role {} - {} - {}" , role, ERole.ROLE_ADMIN, roleAdmin);
                    break;
                case "bookie":
                    Role modRole = roleRepository.findByName(ERole.ROLE_BOOKIE)
                            .orElseThrow(() -> new RuntimeException("Error: Role Bookie is not found."));
                    roles.add(modRole);
                    break;

                case "customer_care":
                    Role csRole = roleRepository.findByName(ERole.ROLE_CUSTOMER_CARE)
                            .orElseThrow(() -> new RuntimeException("Error: Role Customer Care is not found."));
                    logger.info("Role cs is {}", csRole);
                    roles.add(csRole);
                    break;
                default:
                    Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER_CARE)
                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                    roles.add(userRole);
            }
        });
       }catch (Exception e){
           logger.info("Roles not found {}", e.getMessage());
       }
        return roles;

    }

    @Override
    public GlobalResponse getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userList = users.stream().map(user -> UserDTO
                .builder()
                .userId(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .thumbnail(user.getThumbnail())
                .iso(user.getIso())
                .roles(user.getRoles().stream().map(role -> role.getName().toString()).collect(Collectors.toSet()))
                .build()).collect(Collectors.toList());
        return new GlobalResponse(userList,true,false,"Users list");
    }

    @Override
    public GlobalResponse deleteUser(long id) {
       Optional<User> user =  userRepository.findById(id);
       if(user.isPresent()){
           for (Role role: new HashSet<Role>(user.get().getRoles())){
               user.get().removeRole(role);
           }
           userRepository.delete(user.get());
           return new GlobalResponse(null,true,false,"User deleted successful");
       }

        return new GlobalResponse(null,false,true,"User not found");
    }

    @Override
    public GlobalResponse getUserDetails(int id, Pageable pageable) {
        Page<Deposit> deposits = depositRepository.getCrmDeposits(id, pageable);
        if(!deposits.isEmpty()){
            Map<String,Object> response = appUtils.dataFormatter(deposits.getContent(), deposits.getNumber(),deposits.getTotalElements(), deposits.getTotalPages());
            return new GlobalResponse(response,true,false,"user deposit");
        }
        return new GlobalResponse(null,false,true,"User deposits not found");
    }

    @Override
    public GlobalResponse getUserTickets(int id, Pageable pageable) {
        log.info("Searching tickets for {}", id);
       Page<Ticket> tickets = ticketRepository.findPagedUserTickets(id, pageable);
       if(!tickets.isEmpty()){
           log.info("tickets {}", tickets.getTotalElements());
           Map<String,Object> response = appUtils.dataFormatter(tickets.getContent(), tickets.getNumber(),tickets.getTotalElements(), tickets.getTotalPages());
           return new GlobalResponse(response,true,false,"user tickets");
       }
        return new GlobalResponse(null,false,true,"No user tickets");
    }

    @Override
    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest, MultipartFile file) {

        String thumbnail = file.getOriginalFilename();
        if(thumbnail != null){
            thumbnail = UUID.randomUUID() + "." + thumbnail.substring(thumbnail.lastIndexOf(".") + 1);
        }
        try{
            saveImage(file, thumbnail);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),passwordEncoder.encode(signUpRequest.getPassword()),signUpRequest.getIso());

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles =  new HashSet<>();
        if(strRoles == null){
            Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER_CARE)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found"));
            roles.add(userRole);
            System.out.println("Roles not create " + signUpRequest.toString());
        }else{
            logger.info("roles to create {}",signUpRequest.toString());
            roles = getUserRoles(strRoles);
        }
        user.setIso(signUpRequest.getIso());
        user.setRoles(roles);
        user.setThumbnail(thumbnail);
        user.setCreated_at(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully"));
    }


        private String saveImage(MultipartFile file, String name) throws IOException {
            Path uploadPath = Paths.get(uploadDir);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(name);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath.toString();
        }


    private Set<Role> getUserRoles(Set<String> strRoles) {
            Set<Role> roles = new HashSet<>();
            logger.info("Roles are {}" , strRoles.size());
            strRoles.forEach(System.out::println);
            strRoles.forEach(role ->{
                logger.info("searching role {}" , role);
                switch (role){

                    case "admin":
                        Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(()-> new RuntimeException("Error: Role admin not found"));
                        roles.add(roleAdmin);
                        break;
                    case "bookie":
                        Role modRole = roleRepository.findByName(ERole.ROLE_BOOKIE)
                                .orElseThrow(() -> new RuntimeException("Error: Role Bookie is not found."));
                        roles.add(modRole);
                        break;

                    case "customer_care":
                        Role csRole = roleRepository.findByName(ERole.ROLE_CUSTOMER_CARE)
                                .orElseThrow(() -> new RuntimeException("Error: Role Bookie is not found."));
                        roles.add(csRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER_CARE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });

            return roles;
        }


}
