package com.nashtech.ecommercespring.service;

import com.nashtech.ecommercespring.dto.request.AuthRequest;
import com.nashtech.ecommercespring.dto.request.UserReqDTO;
import com.nashtech.ecommercespring.dto.request.UserSignUpDTO;
import com.nashtech.ecommercespring.dto.response.JwtAuthResponse;
import com.nashtech.ecommercespring.dto.response.UserDTO;
import com.nashtech.ecommercespring.dto.response.UserSignUpResDTO;
import com.nashtech.ecommercespring.enums.RoleName;
import com.nashtech.ecommercespring.exception.BadRequestException;
import com.nashtech.ecommercespring.exception.ExceptionMessages;
import com.nashtech.ecommercespring.exception.NotFoundException;
import com.nashtech.ecommercespring.mapper.UserInfoMapper;
import com.nashtech.ecommercespring.mapper.UserMapper;
import com.nashtech.ecommercespring.model.Role;
import com.nashtech.ecommercespring.model.User;
import com.nashtech.ecommercespring.model.UserInfo;
import com.nashtech.ecommercespring.repository.RoleRepository;
import com.nashtech.ecommercespring.repository.UserInfoRepository;
import com.nashtech.ecommercespring.repository.UserRepository;
import com.nashtech.ecommercespring.security.JwtTokenProvider;
import com.nashtech.ecommercespring.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserInfoMapper userInfoMapper;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache usersCache;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void login_ShouldReturnJwtAuthResponseAndSetCookie_WhenCredentialsAreValid() {
        // Arrange
        AuthRequest request = new AuthRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        Authentication authentication = mock(Authentication.class);
        String token = "mock-jwt-token";

        HttpServletResponse httpResponse = mock(HttpServletResponse.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn(token);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        JwtAuthResponse response = userService.login(request, httpResponse);

        // Assert
        assertNotNull(response);
        assertEquals(token, response.getAccessToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtTokenProvider).generateToken(authentication);

        verify(httpResponse).addHeader(headerCaptor.capture(), valueCaptor.capture());

        assertEquals(HttpHeaders.SET_COOKIE, headerCaptor.getValue());
        String cookieValue = valueCaptor.getValue();
        assertTrue(cookieValue.contains("spring_token=" + token));
        assertTrue(cookieValue.contains("HttpOnly"));
        assertTrue(cookieValue.contains("Path=/"));
        assertTrue(cookieValue.contains("Max-Age=86400"));
    }

    @Test
    void logout_ShouldSetExpiredCookie() {
        // Arrange
        HttpServletResponse httpResponse = mock(HttpServletResponse.class);
        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);

        // Act
        userService.logout(httpResponse);

        // Assert
        verify(httpResponse).addHeader(headerCaptor.capture(), valueCaptor.capture());

        assertEquals(HttpHeaders.SET_COOKIE, headerCaptor.getValue());

        String cookie = valueCaptor.getValue();
        assertTrue(cookie.contains("spring_token="));
        assertTrue(cookie.contains("Max-Age=0"));
        assertTrue(cookie.contains("Path=/"));
        assertTrue(cookie.contains("HttpOnly"));
        assertTrue(cookie.contains("SameSite=Strict"));
    }

    @Test
    void signUp_ShouldReturnUserSignUpResDTO_WhenAllConditionsAreMet() {
        // Arrange
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setEmail("test@example.com");
        userSignUpDTO.setPhone("1234567890");
        userSignUpDTO.setPassword("password123");

        Role role = new Role();
        role.setRoleName(RoleName.ROLE_USER);

        User user = new User();
        user.setEmail(userSignUpDTO.getEmail());

        UserInfo userInfo = new UserInfo();
        userInfo.setPhone(userSignUpDTO.getPhone());
        user.setUserInfos(List.of(userInfo));

        UserSignUpResDTO expectedResponse = new UserSignUpResDTO();
        expectedResponse.setEmail(user.getEmail());
        expectedResponse.setUserInfos(List.of(userInfo));

        when(userRepository.findByEmail(userSignUpDTO.getEmail())).thenReturn(Optional.empty());
        when(userInfoRepository.findByPhone(userSignUpDTO.getPhone())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(userMapper.toEntity(userSignUpDTO)).thenReturn(user);
        when(encoder.encode(userSignUpDTO.getPassword())).thenReturn("encodedPassword");
        when(userInfoMapper.toEntity(userSignUpDTO)).thenReturn(userInfo);
        when(userInfoRepository.save(userInfo)).thenReturn(userInfo);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDtoForSignUp(user)).thenReturn(expectedResponse);

        // Act
        UserSignUpResDTO result = userService.signUp(userSignUpDTO);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse.getEmail(), result.getEmail());
        assertEquals(expectedResponse.getUserInfos().get(0).getPhone(), result.getUserInfos().get(0).getPhone());
        verify(userRepository).findByEmail(userSignUpDTO.getEmail());
        verify(userInfoRepository).findByPhone(userSignUpDTO.getPhone());
        verify(roleRepository).findByRoleName(RoleName.ROLE_USER);
        verify(userRepository).save(user);
        verify(userInfoRepository).save(any(UserInfo.class));
    }

    @Test
    void signUp_ShouldThrowBadRequestException_WhenEmailAlreadyExists() {
        // Arrange
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setEmail("test@example.com");

        when(userRepository.findByEmail(userSignUpDTO.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> userService.signUp(userSignUpDTO));
        assertEquals(String.format(ExceptionMessages.ALREADY_EXISTS, userSignUpDTO.getEmail()), ex.getMessage());
        verify(userRepository).findByEmail(userSignUpDTO.getEmail());
        verifyNoMoreInteractions(userInfoRepository, roleRepository, userMapper, encoder);
    }

    @Test
    void signUp_ShouldThrowBadRequestException_WhenPhoneAlreadyExists() {
        // Arrange
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setPhone("1234567890");

        when(userRepository.findByEmail(userSignUpDTO.getEmail())).thenReturn(Optional.empty());
        when(userInfoRepository.findByPhone(userSignUpDTO.getPhone())).thenReturn(Optional.of(new UserInfo()));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> userService.signUp(userSignUpDTO));
        assertEquals(String.format(ExceptionMessages.ALREADY_EXISTS, userSignUpDTO.getPhone()), ex.getMessage());
        verify(userRepository).findByEmail(userSignUpDTO.getEmail());
        verify(userInfoRepository).findByPhone(userSignUpDTO.getPhone());
        verifyNoMoreInteractions(roleRepository, userMapper, encoder);
    }

    @Test
    void signUp_ShouldThrowNotFoundException_WhenRoleNotFound() {
        // Arrange
        UserSignUpDTO userSignUpDTO = new UserSignUpDTO();
        userSignUpDTO.setEmail("test@example.com");
        userSignUpDTO.setPhone("1234567890");

        when(userRepository.findByEmail(userSignUpDTO.getEmail())).thenReturn(Optional.empty());
        when(userInfoRepository.findByPhone(userSignUpDTO.getPhone())).thenReturn(Optional.empty());
        when(roleRepository.findByRoleName(RoleName.ROLE_USER)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.signUp(userSignUpDTO));
        assertEquals(String.format(ExceptionMessages.NOT_FOUND, RoleName.ROLE_USER.name()), ex.getMessage());
        verify(userRepository).findByEmail(userSignUpDTO.getEmail());
        verify(userInfoRepository).findByPhone(userSignUpDTO.getPhone());
        verify(roleRepository).findByRoleName(RoleName.ROLE_USER);
        verifyNoMoreInteractions(userMapper, encoder);
    }

    @Test
    void createUser_shouldReturnUserDTO_whenValidInput() {
        // Arrange
        UUID roleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UserReqDTO userReqDTO = new UserReqDTO();
        userReqDTO.setEmail("new@example.com");
        userReqDTO.setPassword("password123");
        userReqDTO.setRoleIds(Set.of(roleId));

        Role role = new Role();
        role.setId(roleId);

        User user = new User();
        user.setId(userId);
        user.setEmail(userReqDTO.getEmail());
        user.setRoles(Set.of(role));

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setEmail(user.getEmail());
        expectedDTO.setRoles(user.getRoles());

        when(userRepository.findByEmail(userReqDTO.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(userReqDTO)).thenReturn(user);
        when(encoder.encode(userReqDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedDTO);

        // Act
        UserDTO result = userService.createUser(userReqDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userReqDTO.getEmail(), result.getEmail());
        assertEquals(
                userReqDTO.getRoleIds(),
                result
                        .getRoles()
                        .stream()
                        .map(Role::getId)
                        .collect(Collectors.toSet()));

        verify(userRepository).findByEmail(userReqDTO.getEmail());
        verify(roleRepository).findById(roleId);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void createUser_shouldThrowBadRequestException_whenEmailExists() {
        // Arrange
        UserReqDTO userReqDTO = new UserReqDTO();
        userReqDTO.setEmail("existing@example.com");

        when(userRepository.findByEmail(userReqDTO.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> userService.createUser(userReqDTO));
        assertEquals(String.format(ExceptionMessages.ALREADY_EXISTS, userReqDTO.getEmail()), ex.getMessage());

        verify(userRepository).findByEmail(userReqDTO.getEmail());
        verifyNoMoreInteractions(roleRepository, userRepository, userMapper, encoder);
    }

    @Test
    void createUser_shouldThrowNotFoundException_whenRoleNotFound() {
        // Arrange
        UUID invalidRoleId = UUID.randomUUID();
        UserReqDTO userReqDTO = new UserReqDTO();
        userReqDTO.setEmail("new@example.com");
        userReqDTO.setPassword("pass");
        userReqDTO.setRoleIds(Set.of(invalidRoleId));

        when(userRepository.findByEmail(userReqDTO.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(userReqDTO)).thenReturn(new User());
        when(encoder.encode(userReqDTO.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findById(invalidRoleId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.createUser(userReqDTO));
        assertEquals(String.format(ExceptionMessages.NOT_FOUND, invalidRoleId), ex.getMessage());

        verify(userRepository).findByEmail(userReqDTO.getEmail());
        verify(roleRepository).findById(invalidRoleId);
    }

    @Test
    void updateUser_shouldReturnUpdatedUserDTO_whenValidInput() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        UserReqDTO userReqDTO = new UserReqDTO();
        userReqDTO.setEmail("updated@example.com");
        userReqDTO.setPassword("newPassword");
        userReqDTO.setRoleIds(Set.of(roleId));

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setEmail("old@example.com");

        Role role = new Role();
        role.setId(roleId);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setEmail(userReqDTO.getEmail());
        updatedUser.setRoles(Set.of(role));

        UserDTO expectedDTO = new UserDTO();
        expectedDTO.setEmail(updatedUser.getEmail());
        expectedDTO.setRoles(updatedUser.getRoles());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userReqDTO.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(cacheManager.getCache("users")).thenReturn(usersCache);
        doNothing().when(userMapper).updateUserFromDto(userReqDTO, existingUser);
        when(encoder.encode(userReqDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(expectedDTO);

        // Act
        UserDTO result = userService.updateUser(userId, userReqDTO);

        // Assert
        assertNotNull(result);
        assertEquals(userReqDTO.getEmail(), result.getEmail());

        Set<UUID> actualRoleIds = result.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
        assertEquals(Set.of(roleId), actualRoleIds);

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(userReqDTO.getEmail());
        verify(roleRepository).findById(roleId);
        verify(cacheManager).getCache("users");
        verify(encoder).encode(userReqDTO.getPassword());
        verify(userRepository).save(existingUser);
        verify(userMapper).toDto(updatedUser);
    }

    @Test
    void updateUser_shouldThrowBadRequestException_whenEmailExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserReqDTO userReqDTO = new UserReqDTO();
        userReqDTO.setEmail("existing@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(userReqDTO.getEmail())).thenReturn(Optional.of(new User()));

        // Act & Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () -> userService.updateUser(userId, userReqDTO));
        assertEquals(String.format(ExceptionMessages.ALREADY_EXISTS, userReqDTO.getEmail()), ex.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(userReqDTO.getEmail());
    }

    @Test
    void updateUser_shouldThrowNotFoundException_whenUserNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.updateUser(userId, new UserReqDTO()));
        assertEquals(String.format(ExceptionMessages.NOT_FOUND, userId), ex.getMessage());

        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_shouldThrowNotFoundException_whenRoleNotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        UserReqDTO userReqDTO = new UserReqDTO();
        userReqDTO.setEmail("new@example.com");
        userReqDTO.setRoleIds(Set.of(roleId));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(userRepository.findByEmail(userReqDTO.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.updateUser(userId, userReqDTO));
        assertEquals(String.format(ExceptionMessages.NOT_FOUND, roleId), ex.getMessage());

        verify(roleRepository).findById(roleId);
    }

    @Test
    void deleteUser_shouldSetDeletedTrue_whenUserExists() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setDeleted(false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cacheManager.getCache("users")).thenReturn(usersCache);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        userService.deleteUser(userId);

        // Assert
        assertTrue(user.isDeleted());
        verify(userRepository).findById(userId);
        verify(cacheManager).getCache("users");
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_shouldThrowNotFoundException_whenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
        assertEquals(String.format(ExceptionMessages.NOT_FOUND, userId), exception.getMessage());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void getCurrentUser_shouldReturnUserDTO_whenUserExists() {
        // Arrange
        String email = "test@example.com";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);

        User user = new User();
        user.setEmail(email);

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);

        when(userRepository.findByEmailAndDeletedFalse(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDTO);

        // Act
        UserDTO result = userService.getCurrentUser(userDetails);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());

        verify(userRepository).findByEmailAndDeletedFalse(email);
        verify(userMapper).toDto(user);
    }

    @Test
    void getCurrentUser_shouldThrowNotFoundException_whenUserNotFound() {
        // Arrange
        String email = "missing@example.com";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(email);

        when(userRepository.findByEmailAndDeletedFalse(email)).thenReturn(Optional.empty());

        // Act & Assert
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getCurrentUser(userDetails));

        assertEquals(String.format(ExceptionMessages.NOT_FOUND, email), exception.getMessage());

        verify(userRepository).findByEmailAndDeletedFalse(email);
        verify(userMapper, never()).toDto(any());
    }
}
