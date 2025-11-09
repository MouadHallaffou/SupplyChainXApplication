package com.supplychainx.service.service_user;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_user.dto.Request.UserRequestDTO;
import com.supplychainx.service_user.dto.Response.UserResponseDTO;
import com.supplychainx.service_user.mapper.UserMapper;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.model.User;
import com.supplychainx.service_user.repository.RoleRepository;
import com.supplychainx.service_user.repository.UserRepository;
import com.supplychainx.service_user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRoleId(1L);
        role.setName("ADMIN");

        user = new User();
        user.setUserId(1L);
        user.setFirstName("mouad");
        user.setLastName("hallaffou");
        user.setEmail("mouadhallaff@gmail.com");
        user.setPassword("hashedPassword");
        user.setRole(role);
        user.setIsActive(true);
        user.setIsDeleted(false);

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("mouad");
        userRequestDTO.setLastName("hallaffou");
        userRequestDTO.setEmail("mouadhallaff@gmail.com");
        userRequestDTO.setPassword("password123");
        userRequestDTO.setRoleId(1L);

        userResponseDTO = new UserResponseDTO(
                1L, "mouad", "hallaffou", "mouadhallaff@gmail.com",
                true, false, "ADMIN", LocalDateTime.now(), null
        );
    }

    // CREATE Tests
    @Test
    void createUser_ShouldReturnUserResponseDTO_WhenValidInput() {
        // Given
        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        // When
        UserResponseDTO result = userService.create(userRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(userResponseDTO, result);
        verify(userMapper, times(1)).toEntity(userRequestDTO);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toResponseDTO(user);
    }

    @Test
    void createUser_ShouldHashPassword_WhenCreatingUser() {
        // Given
        String rawPassword = "password123";
        userRequestDTO.setPassword(rawPassword);
        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        // When
        userService.create(userRequestDTO);

        // Then
        verify(userRepository).save(argThat(savedUser ->
                savedUser.getPassword().startsWith("$2a$") // Vérifie que le mot de passe est hashé
        ));
    }

    // GET BY ID Tests
    @Test
    void getById_ShouldReturnUserResponseDTO_WhenUserExists() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        // When
        UserResponseDTO result = userService.getById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userResponseDTO, result);
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toResponseDTO(user);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getById(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, never()).toResponseDTO(any());
    }

    // UPDATE Tests
    @Test
    void updateUser_ShouldReturnUpdatedUserResponseDTO_WhenValidInput() {
        // Given
        Long userId = 1L;
        UserRequestDTO updateRequest = new UserRequestDTO();
        updateRequest.setFirstName("updatedFirstName");
        updateRequest.setLastName("updatedLastName");
        updateRequest.setEmail("updated@email.com");
        updateRequest.setPassword("newPassword123");
        updateRequest.setRoleId(1L);

        User updatedUser = new User();
        updatedUser.setUserId(userId);
        updatedUser.setFirstName("updatedFirstName");
        updatedUser.setLastName("updatedLastName");
        updatedUser.setEmail("updated@email.com");

        UserResponseDTO updatedResponseDTO = new UserResponseDTO(
                userId, "updatedFirstName", "updatedLastName", "updated@email.com",
                 true, false,"ADMIN", LocalDateTime.now(), LocalDateTime.now()
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(updateRequest.getRoleId())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponseDTO(updatedUser)).thenReturn(updatedResponseDTO);

        // When
        UserResponseDTO result = userService.update(userId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updatedResponseDTO, result);
        verify(userRepository, times(1)).findById(userId);
        verify(roleRepository, times(1)).findById(updateRequest.getRoleId());
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toResponseDTO(updatedUser);
    }

    @Test
    void updateUser_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> userService.update(userId, userRequestDTO));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    // GET ALL Tests
    @Test
    void getAll_ShouldReturnListOfUserResponseDTO_WhenUsersExist() {
        // Given
        List<User> users = List.of(user);
        List<UserResponseDTO> userResponseDTOs = List.of(userResponseDTO);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        // When
        List<UserResponseDTO> result = userService.getAll();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(userResponseDTOs, result);
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toResponseDTO(user);
    }

    @Test
    void getAll_ShouldThrowResourceNotFoundException_WhenNoUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getAll());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, never()).toResponseDTO(any());
    }

    // SOFT DELETE Tests
    @Test
    void softDelete_ShouldSetIsDeletedToTrue_WhenUserExistsAndNotAlreadyDeleted() {
        // Given
        Long userId = 1L;
        user.setIsDeleted(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.softDelete(userId);

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(argThat(savedUser ->
                savedUser.getIsDeleted() && savedUser.getDeletedAt() != null
        ));
    }

    @Test
    void softDelete_ShouldNotSave_WhenUserAlreadyDeleted() {
        // Given
        Long userId = 1L;
        user.setIsDeleted(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.softDelete(userId);

        // Then
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void softDelete_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.softDelete(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }

    // Additional method tests
    @Test
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Given
        String email = "test@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailNotExists() {
        // Given
        String email = "nonexistent@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.findByEmail(email);

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByEmail(email);
    }
}