package com.nashtech.ecommercespring.service.impl;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.request.UserReqDTO;
import com.nashtech.ecommercespring.dto.response.JwtAuthResponse;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import com.nashtech.ecommercespring.enums.RoleName;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
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
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    private final CacheManager cacheManager;

    @Override
    public JwtAuthResponse login(AuthRequest request, HttpServletResponse httpServletResponse) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        ResponseCookie cookie = createCookie(token, 24 * 60 * 60);

        // Set cookie in response header
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setAccessToken(token);

        return jwtAuthResponse;
    }

    @Override
    public void logout(HttpServletResponse httpServletResponse) {
        // Create an expired cookie
        ResponseCookie cookie = createCookie("", 0);

        // Add the cookie to the response
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    public UserSignUpResDTO signUp(UserSignUpDTO userSignUpDTO) {
        if (userRepository.findByEmail(userSignUpDTO.getEmail()).isPresent()) {
            throw new BadRequestException(
                    String.format(ExceptionMessages.ALREADY_EXISTS, userSignUpDTO.getEmail())
            );
        }
        if (userInfoRepository.findByPhone(userSignUpDTO.getPhone()).isPresent()) {
            throw new BadRequestException(
                    String.format(ExceptionMessages.ALREADY_EXISTS, userSignUpDTO.getPhone())
            );
        }

        Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, RoleName.ROLE_USER.name()))
                );

        User user = userMapper.toEntity(userSignUpDTO);
        user.setPassword(encoder.encode(userSignUpDTO.getPassword()));

        user.setRoles(Set.of(role));

        user.setUserInfos(
                List.of(userInfoRepository.save(userInfoMapper.toEntity(userSignUpDTO)))
        );

        return userMapper.toDtoForSignUp(userRepository.save(user));
    }

    @Override
    public UserDTO createUser(UserReqDTO userCreateDTO) {
        if (userRepository.findByEmail(userCreateDTO.getEmail()).isPresent()) {
            throw new BadRequestException(
                    String.format(ExceptionMessages.ALREADY_EXISTS, userCreateDTO.getEmail())
            );
        }

        User user = userMapper.toEntity(userCreateDTO);
        user.setPassword(encoder.encode(userCreateDTO.getPassword()));
//        Set role for the user
        Set<Role> roles = userCreateDTO.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new NotFoundException(
                                String.format(ExceptionMessages.NOT_FOUND, roleId)))
                )
                .collect(Collectors.toSet());

        user.setRoles(roles);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository
                .findAll(pageable)
                .map(userMapper::toDto);
    }

    @Override
    public UserDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        return userMapper.toDto(user);
    }

    @Override
    public UserDTO updateUser(UUID id, UserReqDTO userReqDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        if (userRepository.findByEmail(userReqDTO.getEmail()).isPresent()) {
            throw new BadRequestException(String.format(ExceptionMessages.ALREADY_EXISTS, userReqDTO.getEmail()));
        }

//        Check if role exist and get corresponding roles
        Set<Role> roles = userReqDTO.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new NotFoundException(
                                String.format(ExceptionMessages.NOT_FOUND, roleId)))
                )
                .collect(Collectors.toSet());

        // Evict cache entry manually
        removeUserFromCache(user.getEmail());

        userMapper.updateUserFromDto(userReqDTO, user);

        //        Update encoded password
        if (userReqDTO.getPassword() != null) {
            user.setPassword(encoder.encode(userReqDTO.getPassword()));
        }

//        Update role
        user.setRoles(roles);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, id))
                );

        // Evict cache entry manually
        removeUserFromCache(user.getEmail());

        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public UserDTO getCurrentUser(UserDetails userDetails) {
        String email = userDetails.getUsername();

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new NotFoundException(
                        String.format(ExceptionMessages.NOT_FOUND, email))
                );

        return userMapper.toDto(user);
    }

    // ======================================= Helper Method ====================================

    private void removeUserFromCache(String email) {
        Cache usersCache = cacheManager.getCache("users");
        if (usersCache != null) {
            usersCache.evict(email);
        }
    }

    private ResponseCookie createCookie(String token, long maxAge) {
        return ResponseCookie.from("spring_token", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }
}
