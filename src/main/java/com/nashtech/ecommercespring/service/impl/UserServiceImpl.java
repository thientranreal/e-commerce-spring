package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.request.UserCreateDTO;
import com.nashtech.ecommercespring.dto.request.UserUpdateDTO;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import com.nashtech.ecommercespring.enums.RoleName;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.UserInfoMapper;
import com.nashtech.ecommercespring.mapper.UserMapper;
import com.nashtech.ecommercespring.model.Role;
import com.nashtech.ecommercespring.model.User;
import com.nashtech.ecommercespring.repository.RoleRepository;
import com.nashtech.ecommercespring.repository.UserInfoRepository;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserInfoRepository userInfoRepository;

    private final UserMapper userMapper;

    private final UserInfoMapper userInfoMapper;

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
    public UserSignUpResDTO signUp(UserSignUpDTO userSignUpDTO) {
        if (userRepository.findByEmail(userSignUpDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        if (userInfoRepository.findByPhone(userSignUpDTO.getPhone()).isPresent()) {
            throw new BadRequestException("Phone already exists");
        }

        User user = userMapper.toEntity(userSignUpDTO);
        user.setPassword(encoder.encode(userSignUpDTO.getPassword()));

        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new NotFoundException("ROLE_USER not found"));

        user.setRoles(Set.of(role));

        user.setUserInfos(
                List.of(userInfoRepository.save(userInfoMapper.toEntity(userSignUpDTO)))
        );

        return userMapper.toDtoForSignUp(userRepository.save(user));
    }

    @Override
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.findByEmail(userCreateDTO.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        User user = userMapper.toEntity(userCreateDTO);
        user.setPassword(encoder.encode(userCreateDTO.getPassword()));
//        Set role for the user
        Set<Role> roles = userCreateDTO.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId)))
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        return userMapper.toDto(user);
    }

    @Override
    public UserDTO updateUser(UUID id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

//        Check if role exist and get corresponding roles
        Set<Role> roles = userUpdateDTO.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new NotFoundException("Role not found with id: " + roleId)))
                .collect(Collectors.toSet());

        userMapper.updateUserFromDto(userUpdateDTO, user);

        //        Update encoded password
        user.setPassword(encoder.encode(userUpdateDTO.getPassword()));

//        Update role
        user.setRoles(roles);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserDTO deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        user.setDeleted(true);
        return userMapper.toDto(userRepository.save(user));
    }
}
