package com.crm.api.services;

import com.crm.api.dtos.EditDTO;
import com.crm.api.payload.requests.SignUpRequest;
import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

public interface UserService {
    GlobalResponse editUser(EditDTO editDTO, int id);

    GlobalResponse getUsers();

    GlobalResponse deleteUser(long id);

    GlobalResponse getUserDetails(int id, Pageable pageable);

    GlobalResponse getUserTickets(int id, Pageable pageable);

    ResponseEntity<?> registerUser(@Valid SignUpRequest signUpRequest, MultipartFile file);
}
