package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.AuthRequest;
import com.nashtech.ecommercespring.dto.UserDTO;
import com.nashtech.ecommercespring.dto.UserSignUpDTO;
import com.nashtech.ecommercespring.enums.Role;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.model.User;
import com.nashtech.ecommercespring.repository.UserRepository;
import com.nashtech.ecommercespring.security.JwtTokenProvider;
import com.nashtech.ecommercespring.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtTokenProvider.generateToken(authentication);
    }

    @Override
    public UserDTO signUp(UserSignUpDTO userSignUpDTO) {
        if (userRepository.findByEmail(userSignUpDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.findByPhone(userSignUpDTO.getPhone()).isPresent()) {
            throw new BadRequestException("Phone already exists");
        }

        User user = new User();
        user.setEmail(userSignUpDTO.getEmail());
        user.setPassword(encoder.encode(userSignUpDTO.getPassword()));
        user.setFirstName(userSignUpDTO.getFirstName());
        user.setLastName(userSignUpDTO.getLastName());
        user.setPhone(userSignUpDTO.getPhone());
        user.setAddress(userSignUpDTO.getAddress());
        user.setRole(Role.ROLE_USER);

        return new UserDTO(userRepository.save(user));

    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        if (userDTO.getPhone() != null
                && userRepository.findByPhone(userDTO.getPhone()).isPresent()) {
            throw new BadRequestException("Phone already exists");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(encoder.encode(userDTO.getPassword()));
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());

        if (userDTO.getPhone() != null) {
            user.setPhone(userDTO.getPhone());
        }
        if (userDTO.getAddress() != null) {
            user.setAddress(userDTO.getAddress());
        }

        user.setRole(userDTO.getRole());

        return new UserDTO(userRepository.save(user));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        return new UserDTO(user);
    }

    @Override
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(encoder.encode(userDTO.getPassword()));
        }

        user.setRole(userDTO.getRole());

        return new UserDTO(userRepository.save(user));
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        userRepository.delete(user);
    }
}
