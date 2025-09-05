package com.example.bank_rest.service;

import com.example.bank_rest.entity.Role;
import com.example.bank_rest.entity.User;
import com.example.bank_rest.entity.enums.UserRole;
import com.example.bank_rest.entity.enums.UserStatus;
import com.example.bank_rest.exps.InactiveAccountException;
import com.example.bank_rest.exps.RecordAlreadyException;
import com.example.bank_rest.exps.RecordNotFoundException;
import com.example.bank_rest.mapper.UserMapper;
import com.example.bank_rest.payload.dto.request.CreateUserForAdminRequestDto;
import com.example.bank_rest.payload.dto.request.LoginRequestDto;
import com.example.bank_rest.payload.dto.request.UpdateUserRequestDto;
import com.example.bank_rest.payload.dto.request.UserRequestDto;
import com.example.bank_rest.payload.dto.response.LoginResponseDto;
import com.example.bank_rest.payload.dto.response.UserCreateResponseDto;
import com.example.bank_rest.payload.dto.response.UserResponseDto;
import com.example.bank_rest.repository.RoleRepository;
import com.example.bank_rest.repository.UserRepository;
import com.example.bank_rest.security.jwt.JwtService;
import com.example.bank_rest.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;
    private UserRequestDto userRequestDto;
    private LoginRequestDto loginRequestDto;
    private CreateUserForAdminRequestDto adminRequestDto;
    private UpdateUserRequestDto updateRequestDto;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .role(UserRole.ROLE_USER)
                .build();

        user = User.builder()
                .id(1L)
                .firstName("Ali")
                .lastName("Valiyev")
                .username("ali")
                .phoneNumber("+998901234567")
                .userStatus(UserStatus.ACTIVE)
                .password("encodedPassword")
                .roles(List.of(role))
                .build();

        userRequestDto = UserRequestDto.builder()
                .firstName("Ali")
                .lastName("Valiyev")
                .username("ali")
                .phoneNumber("+998901234567")
                .password("password123")
                .build();

        loginRequestDto = LoginRequestDto.builder()
                .login("ali")
                .password("password123")
                .build();

        adminRequestDto = CreateUserForAdminRequestDto.builder()
                .firstName("Ali")
                .lastName("Valiyev")
                .username("ali")
                .phoneNumber("+998901234567")
                .password("password123")
                .roleIds(List.of(1L))
                .build();

        updateRequestDto = UpdateUserRequestDto.builder()
                .firstName("NewAli")
                .lastName("NewValiyev")
                .username("newali")
                .phoneNumber("+998901234568")
                .oldPassword("password123")
                .newPassword("newpassword123")
                .build();
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername("ali")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("+998901234567")).thenReturn(false);
        when(roleRepository.findByRole(UserRole.ROLE_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toCreateDto(user)).thenReturn(new UserCreateResponseDto(1L, "Ali", "Valiyev", "ali", "+998901234567"));

        UserCreateResponseDto result = userService.register(userRequestDto);

        assertNotNull(result);
        assertEquals("ali", result.getUsername());
        verify(userRepository).save(any(User.class));
        verify(userMapper).toCreateDto(user);
    }

    @Test
    void register_UsernameAlreadyExists_ThrowsException() {
        when(userRepository.existsByUsername("ali")).thenReturn(true);

        assertThrows(RecordAlreadyException.class, () -> userService.register(userRequestDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signIn_Success() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateAccessToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");

        LoginResponseDto result = userService.signIn(loginRequestDto);

        assertNotNull(result);
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void signIn_InactiveUser_ThrowsException() throws Exception {
        user.setUserStatus(UserStatus.INACTIVE);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        assertThrows(InactiveAccountException.class, () -> userService.signIn(loginRequestDto));
        verify(jwtService, never()).generateAccessToken(user);
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername("ali")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("+998901234567")).thenReturn(false);
        when(roleRepository.findAllById(List.of(1L))).thenReturn(List.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toCreateDto(user)).thenReturn(new UserCreateResponseDto(1L, "Ali", "Valiyev", "ali", "+998901234567"));

        UserCreateResponseDto result = userService.createUser(adminRequestDto);

        assertNotNull(result);
        assertEquals("ali", result.getUsername());
        verify(userRepository).save(any(User.class));
        verify(roleRepository).findAllById(List.of(1L));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(UserResponseDto.builder()
                .id(1L)
                .firstName("Ali")
                .lastName("Valiev")
                .username("ali")
                .phoneNumber("+998901234567")
                .userStatus(UserStatus.ACTIVE)
                .build());
        UserResponseDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("ali", result.username());
        verify(userMapper).toDto(user);
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void getAllUsers_Success() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(any(PageRequest.class))).thenReturn(userPage);
        when(userMapper.toCreateDto(user)).thenReturn(new UserCreateResponseDto(1L, "Ali", "Valiyev", "ali", "+998901234567"));

        Page<UserCreateResponseDto> result = userService.getAllUsers(0, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(any(PageRequest.class));
    }

    @Test
    void update_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(userRepository.existsByUsername("newali")).thenReturn(false);
        when(userRepository.existsByPhoneNumber("+998901234568")).thenReturn(false);
        when(passwordEncoder.encode("newpassword123")).thenReturn("newEncodedPassword");

        User updatedUser = User.builder()
                .id(1L)
                .firstName("NewAli")
                .lastName("NewValiyev")
                .username("newali")
                .phoneNumber("+998901234568")
                .userStatus(UserStatus.ACTIVE)
                .password("newEncodedPassword")
                .roles(List.of(role))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(UserResponseDto.builder()
                .id(1L)
                .firstName("NewAli")
                .lastName("NewValiyev")
                .username("newali")
                .phoneNumber("+998901234568")
                .userStatus(UserStatus.ACTIVE)
                .build());

        UserResponseDto result = userService.update(1L, updateRequestDto);

        assertNotNull(result);
        assertEquals("newali", result.username());
        assertEquals("NewAli", result.firstName());
        assertEquals("NewValiyev", result.lastName());
        assertEquals("+998901234568", result.phoneNumber());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deactivateOwnAccount_Success() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("ali");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsername("ali")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deactivateOwnAccount();

        assertEquals(UserStatus.INACTIVE, user.getUserStatus());
        verify(userRepository).save(user);
    }
}