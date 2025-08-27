package com.crm.api.controllers;

import com.crm.api.crm.models.User;
import com.crm.api.dtos.EditDTO;
import com.crm.api.dtos.UserDTO;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/edit/{id}")
    public GlobalResponse editUser(@PathVariable(name = "id") int id, @RequestBody EditDTO editDTO){
        return userService.editUser(editDTO, id);
    }

    @GetMapping("/list")
    public GlobalResponse users(){
        return userService.getUsers();
    }

    @DeleteMapping("/{id}")
    public GlobalResponse deleteUser(@PathVariable long id){
        return userService.deleteUser(id);
    }


    @GetMapping("/deposits/{id}")
    public GlobalResponse userDetails(@PathVariable int id, @RequestParam(value = "page",defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "15") int size){
        Pageable pageable = PageRequest.of(page,size);
        return userService.getUserDetails(id,pageable);
    }
    @GetMapping("/tickets/{id}")
    public GlobalResponse userTickets(@PathVariable int id, @RequestParam(value = "page",defaultValue = "0") int page,
                                      @RequestParam(value = "size", defaultValue = "15") int size){
        Pageable pageable = PageRequest.of(page,size);
        return userService.getUserTickets(id,pageable);
    }

}
