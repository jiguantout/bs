package com.community.toolsharing.service;

import com.community.toolsharing.dto.LoginRequest;
import com.community.toolsharing.dto.ProfileUpdateRequest;
import com.community.toolsharing.dto.RegisterRequest;
import com.community.toolsharing.enums.PointType;
import com.community.toolsharing.enums.UserRole;
import com.community.toolsharing.exception.BusinessException;
import com.community.toolsharing.exception.ResourceNotFoundException;
import com.community.toolsharing.model.User;
import com.community.toolsharing.repository.UserRepository;
import com.community.toolsharing.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PointService pointService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       PointService pointService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.pointService = pointService;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname() != null ? request.getNickname() : request.getUsername())
                .phone(request.getPhone())
                .role(UserRole.USER)
                .points(0)
                .status(1)
                .build();

        User savedUser = userRepository.save(user);

        pointService.addPoints(savedUser.getId(), 10, PointType.BONUS, "Registration bonus points");

        return savedUser;
    }

    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        return jwtTokenProvider.generateToken(request.getUsername());
    }

    public User getProfile(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Transactional
    public User updateProfile(String username, ProfileUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUserStatus(Long id, Integer status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setStatus(status);
        return userRepository.save(user);
    }

    public long getUserCount() {
        return userRepository.count();
    }
}
