package com.supplychainx.service.service_user;

import com.supplychainx.exception.ResourceNotFoundException;
import com.supplychainx.service_user.dto.Request.RoleRequestDTO;
import com.supplychainx.service_user.dto.Response.RoleResponseDTO;
import com.supplychainx.service_user.mapper.RoleMapper;
import com.supplychainx.service_user.model.Role;
import com.supplychainx.service_user.repository.RoleRepository;
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
public class TestRoleService {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private Role role;
    private RoleRequestDTO roleRequestDTO;
    private RoleResponseDTO roleResponseDTO;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRoleId(1L);
        role.setName("ADMIN");

        roleRequestDTO = new RoleRequestDTO();
        roleRequestDTO.setName("ADMIN");

        roleResponseDTO = new RoleResponseDTO(1L, "ADMIN", LocalDateTime.now(), LocalDateTime.now());
    }

    // CREATE Tests
    @Test
    void createRole_ShouldReturnRoleResponseDTO_WhenValidInput() {
        // Given
        when(roleMapper.toEntity(roleRequestDTO)).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toDTO(role)).thenReturn(roleResponseDTO);

        // When
        RoleResponseDTO result = roleService.create(roleRequestDTO);

        // Then
        assertNotNull(result);
        assertEquals(roleResponseDTO, result);
        verify(roleMapper, times(1)).toEntity(roleRequestDTO);
        verify(roleRepository, times(1)).save(role);
        verify(roleMapper, times(1)).toDTO(role);
    }

    // GET BY ID Tests
    @Test
    void getById_ShouldReturnRoleResponseDTO_WhenRoleExists() {
        // Given
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleMapper.toDTO(role)).thenReturn(roleResponseDTO);

        // When
        RoleResponseDTO result = roleService.getById(roleId);

        // Then
        assertNotNull(result);
        assertEquals(roleResponseDTO, result);
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleMapper, times(1)).toDTO(role);
    }

    @Test
    void getById_ShouldThrowResourceNotFoundException_WhenRoleNotFound() {
        // Given
        Long roleId = 999L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> roleService.getById(roleId));
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleMapper, never()).toDTO(any());
    }

    // UPDATE Tests
    @Test
    void updateRole_ShouldReturnUpdatedRoleResponseDTO_WhenValidInput() {
        // Given
        Long roleId = 1L;
        RoleRequestDTO updateRequest = new RoleRequestDTO();
        updateRequest.setName("UPDATED_ADMIN");

        Role updatedRole = new Role();
        updatedRole.setRoleId(roleId);
        updatedRole.setName("UPDATED_ADMIN");

        RoleResponseDTO updatedResponseDTO = new RoleResponseDTO(
                roleId, "UPDATED_ADMIN", LocalDateTime.now(), LocalDateTime.now()
        );

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);
        when(roleMapper.toDTO(updatedRole)).thenReturn(updatedResponseDTO);

        // When
        RoleResponseDTO result = roleService.update(roleId, updateRequest);

        // Then
        assertNotNull(result);
        assertEquals(updatedResponseDTO, result);
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).save(role);
        verify(roleMapper, times(1)).toDTO(updatedRole);
        verify(roleMapper, times(1)).updateEntityFromDTO(updateRequest, role);
    }

    @Test
    void updateRole_ShouldThrowResourceNotFoundException_WhenRoleNotFound() {
        // Given
        Long roleId = 999L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> roleService.update(roleId, roleRequestDTO));
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).save(any());
    }

    // DELETE Tests
    @Test
    void deleteRole_ShouldDeleteRole_WhenRoleExists() {
        // Given
        Long roleId = 1L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        // When
        roleService.delete(roleId);

        // Then
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void deleteRole_ShouldThrowResourceNotFoundException_WhenRoleNotFound() {
        // Given
        Long roleId = 999L;
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> roleService.delete(roleId));
        verify(roleRepository, times(1)).findById(roleId);
        verify(roleRepository, never()).deleteById(any());
    }

    // GET ALL Tests
    @Test
    void getAll_ShouldReturnListOfRoleResponseDTO_WhenRolesExist() {
        // Given
        List<Role> roles = List.of(role);
        List<RoleResponseDTO> roleResponseDTOs = List.of(roleResponseDTO);

        when(roleRepository.findAll()).thenReturn(roles);
        when(roleMapper.toDTO(role)).thenReturn(roleResponseDTO);

        // When
        List<RoleResponseDTO> result = roleService.getAll();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(roleResponseDTOs, result);
        verify(roleRepository, times(1)).findAll();
        verify(roleMapper, times(1)).toDTO(role);
    }

    @Test
    void getAll_ShouldReturnEmptyList_WhenNoRolesExist() {
        // Given
        when(roleRepository.findAll()).thenReturn(List.of());

        // When
        List<RoleResponseDTO> result = roleService.getAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(roleRepository, times(1)).findAll();
        verify(roleMapper, never()).toDTO(any());
    }
}