package com.example.bank_rest.service.impl;

import com.example.bank_rest.entity.Role;
import com.example.bank_rest.entity.User;
import com.example.bank_rest.entity.enums.UserRole;
import com.example.bank_rest.entity.enums.UserStatus;
import com.example.bank_rest.exps.*;
import com.example.bank_rest.mapper.UserMapper;
import com.example.bank_rest.payload.dto.request.*;
import com.example.bank_rest.payload.dto.response.LoginResponseDto;
import com.example.bank_rest.payload.dto.response.UserCreateResponseDto;
import com.example.bank_rest.payload.dto.response.UserResponseDto;
import com.example.bank_rest.repository.RoleRepository;
import com.example.bank_rest.repository.UserRepository;
import com.example.bank_rest.security.jwt.JwtService;
import com.example.bank_rest.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.bank_rest.security.userdetails.CustomUserDetailsService.normalize;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserCreateResponseDto register(UserRequestDto userRequestDto) {
        checkUserByUsername(userRequestDto.username());
        checkUserByPhoneNumber(userRequestDto.phoneNumber());

        Role defaultRole = getRoleUser();

        User user = User.builder()
                .firstName(userRequestDto.firstName())
                .lastName(userRequestDto.lastName())
                .username(userRequestDto.username())
                .phoneNumber(normalize(userRequestDto.phoneNumber()))
                .userStatus(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(userRequestDto.password()))
                .roles(List.of(defaultRole))
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toCreateDto(savedUser);
    }

    @Override
    public LoginResponseDto signIn(LoginRequestDto loginDTO) throws JsonProcessingException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDTO.login(),
                        loginDTO.password()
                )
        );

        User user = (User) authentication.getPrincipal();

        validateUserStatus(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDto(accessToken, refreshToken);
    }

    @Override
    public LoginResponseDto refreshToken(RefreshTokenDto dto) throws JsonProcessingException {
        jwtService.validateRefreshToken(dto.getRefreshToken());

        String username = jwtService.refreshTokenClaims(dto.getRefreshToken()).getSubject();

        User user = getUserByUsername(username);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDto(newAccessToken, newRefreshToken);
    }

    @Override
    public UserCreateResponseDto createUser(CreateUserForAdminRequestDto dto) {
        checkUserByUsername(dto.username());
        checkUserByPhoneNumber(dto.phoneNumber());

        List<Role> roles = getValidRoles(dto.roleIds());

        User user = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .username(dto.username())
                .phoneNumber(normalize(dto.phoneNumber()))
                .userStatus(UserStatus.ACTIVE)
                .password(passwordEncoder.encode(dto.password()))
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);

        return userMapper.toCreateDto(savedUser);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        User user = getUserOrThrow(userId);

        return userMapper.toDto(user);
    }

    @Override
    public Page<UserCreateResponseDto> getAllUsers(int page, int size, Long id, String firstName) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<User> users;

        if (id != null || (firstName != null && !firstName.isBlank())) {
            users = userRepository.findByIdOrFirstNameContainingIgnoreCase(id, firstName, pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(userMapper::toCreateDto);
    }

    @Override
    public UserResponseDto update(Long id, UpdateUserRequestDto dto) {
        User user = getUserOrThrow(id);

        checkPassword(dto, user);

        if (!user.getUsername().equals(dto.username())) {
            checkUserByUsername(dto.username());
            user.setUsername(dto.username());
        }

        if (!user.getPhoneNumber().equals(dto.phoneNumber())) {
            checkUserByPhoneNumber(dto.phoneNumber());
            user.setPhoneNumber(dto.phoneNumber());
        }

        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());

        if (dto.newPassword() != null && !dto.newPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.newPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }


    @Override
    public void deactivateOwnAccount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = getUserByUsername(username);

        currentUser.setUserStatus(UserStatus.INACTIVE);

        userRepository.save(currentUser);
    }

    @Override
    public UserResponseDto updateUserStatus(Long userId, UserStatus status) {
        User user = getUserOrThrow(userId);

        user.setUserStatus(status);

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Override
    public User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RecordNotFoundException("Unauthorized: No authenticated user found");
        }

        return (User) auth.getPrincipal();
    }

    @Override
    public void validateUserStatus(User user) {
        if (user.getUserStatus() == UserStatus.INACTIVE) {
            throw new InactiveAccountException();
        }
        if (user.getUserStatus() == UserStatus.BLOCKED) {
            throw new BlockedAccountException();
        }
    }

    private List<Role> getValidRoles(List<Long> roleIds) {
        List<Role> roles = roleRepository.findAllById(roleIds);

        if (roles.size() != roleIds.size()) {
            throw new RecordNotFoundException("One or more roles not found");
        }
        return roles;
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RecordNotFoundException("User not found with username: " + username));
    }

    private Role getRoleUser() {
        return roleRepository.findByRole(UserRole.ROLE_USER)
                .orElseThrow(() -> new RecordNotFoundException("Role not found: " + UserRole.ROLE_USER));
    }

    private void checkUserByUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new RecordAlreadyException("Username is already registered");
        }
    }

    private void checkUserByPhoneNumber(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new RecordAlreadyException("Phone number is already registered");
        }
    }

    private void checkPassword(UpdateUserRequestDto dto, User user) {
        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new InvalidOldPasswordException("Password is incorrect");
        }
    }

}