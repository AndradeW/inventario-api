package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.RoleDTO;
import com.inventario.inventario_api.config.TestSecurityConfig;
import com.inventario.inventario_api.exceptions.ErrorResponse;
import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.repository.PermissionRepository;
import com.inventario.inventario_api.repository.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static com.inventario.inventario_api.model.Role.ROLE_ADMIN;
import static org.hibernate.internal.util.collections.CollectionHelper.setOf;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
public class RoleControllerE2ETest {

    public static final String ROLES_URL = "/api/roles";

    private static final String TEST_ROLE = "TEST";
    private static final String TEST_DESCRIPTION_ROLE = "Test Role";

    private final TestRestTemplate restTemplate;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleControllerE2ETest(TestRestTemplate restTemplate, RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.restTemplate = restTemplate;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @BeforeEach
    public void setUp() {
        Permission permission = Permission.builder().name("READ").build();
        this.permissionRepository.save(permission);

        Role role = Role.builder().name(ROLE_ADMIN).description("Admin Role").permissions(setOf(permission)).build();
        this.roleRepository.save(role);
    }

    @AfterEach
    public void tearDown() {
        this.roleRepository.deleteAll();
        this.permissionRepository.deleteAll();
    }

    @Test
    public void testCreateRoleWithRoleOk() {
        // Given
        RoleDTO newRole = RoleDTO.builder()
                .name(TEST_ROLE)
                .description(TEST_DESCRIPTION_ROLE)
                .permissions(new String[]{"READ"})
                .build();

        // When
        ResponseEntity<Role> response = this.restTemplate.postForEntity(ROLES_URL, newRole, Role.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Role updatedRole = response.getBody();
        assertNotNull(updatedRole);
        assertEquals(TEST_ROLE, updatedRole.getName());
        assertEquals(TEST_DESCRIPTION_ROLE, updatedRole.getDescription());
        assertArrayEquals(newRole.permissions(), updatedRole.getPermissions().stream()
                .map(Permission::getName)
                .toArray(String[]::new)); //TODO revisar si se retorna un DTO o un Array de String
    }

    @Test
    public void testCreateRoleRepeated() {
        // Given
        RoleDTO newRole = RoleDTO.builder()
                .name(ROLE_ADMIN)
                .description("Admin Role")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(ROLES_URL, newRole, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("El campo ya existe en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Role with name ADMIN already exists", apiError.getDetails().get("Error"));
    }

    @Test
    public void testUpdateRoleOk() {
        // Given
        RoleDTO newRole = RoleDTO.builder()
                .name(TEST_ROLE)
                .description(TEST_DESCRIPTION_ROLE)
                .build();

        // When
        ResponseEntity<Role> updatedRoleResponse = this.restTemplate.exchange(
                ROLES_URL + "/name/" + ROLE_ADMIN,
                HttpMethod.PUT,
                new HttpEntity<>(newRole),
                Role.class);

        // Then
        assertEquals(HttpStatus.OK, updatedRoleResponse.getStatusCode());
        Role roleResponseBody = updatedRoleResponse.getBody();
        assertNotNull(roleResponseBody);
        //assertEquals(1, roleResponseBody.getId());
        assertEquals(newRole.name(), roleResponseBody.getName());
        assertEquals(newRole.description(), roleResponseBody.getDescription());
        assertFalse(roleResponseBody.getPermissions().isEmpty());
    }

    @Test
    public void testUpdateRoleNotFound() {
        // Given
        Role newRole = Role.builder()
                .name(ROLE_ADMIN)
                .description("Admin Role")
                .build();

        // When
        ResponseEntity<ErrorResponse> updatedRoleResponse = this.restTemplate.exchange(
                ROLES_URL + "/name/"+ "ABC",
                HttpMethod.PUT,
                new HttpEntity<>(newRole),
                ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, updatedRoleResponse.getStatusCode());
        ErrorResponse apiError = updatedRoleResponse.getBody();
        assertNotNull(apiError);
        assertEquals("El campo no fue encontrado en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Role with name ABC not found", apiError.getDetails().get("Error"));
    }

    // Test para crear un rol y asignar permisos
    @Test
    public void testCreateRoleAndAssignPermissions() {
        // Given
        RoleDTO newRole = RoleDTO.builder()
                .name(TEST_ROLE)
                .description(TEST_DESCRIPTION_ROLE)
                .build();

        // When
        ResponseEntity<Role> response = this.restTemplate.postForEntity(ROLES_URL, newRole, Role.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Role role = response.getBody();
        assertNotNull(role);
        assertEquals(TEST_ROLE, role.getName());
        assertEquals(TEST_DESCRIPTION_ROLE, role.getDescription());

        // Given
        String[] permission = new String[]{"READ"};

        // When
        ResponseEntity<Role> updatedRoleResponse = this.restTemplate.postForEntity(ROLES_URL+"/TEST/permissions", permission, Role.class);

        // Then
        assertEquals(HttpStatus.OK, updatedRoleResponse.getStatusCode());
        Role updatedRole = updatedRoleResponse.getBody();
        assertNotNull(updatedRole);
        assertEquals(TEST_ROLE, updatedRole.getName());
        assertEquals(TEST_DESCRIPTION_ROLE, updatedRole.getDescription());
        assertArrayEquals(permission, updatedRole.getPermissions().stream()
                .map(Permission::getName)
                .toArray(String[]::new)); //TODO revisar si se retorna un DTO o un Array de String
    }

    // Test para manejar rol no encontrado
    @Test
    public void testRoleNotFound() {
        // Given
        String[] permission = new String[]{"READ"};

        // When
        ResponseEntity<ErrorResponse> updatedRoleResponse = this.restTemplate.postForEntity(ROLES_URL+"/NON_EXISTING_ROLE/permissions", permission, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, updatedRoleResponse.getStatusCode());
        ErrorResponse apiError = updatedRoleResponse.getBody();
        assertNotNull(apiError);
        assertEquals("El campo no fue encontrado en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Role with name NON_EXISTING_ROLE not found", apiError.getDetails().get("Error"));
    }

    // Test para manejar permiso no encontrado
    @Test
    public void testPermissionNotFound() {
        // Given
        String[] permission = new String[]{"NON_EXISTING_PERMISSION"};

        // When
        ResponseEntity<ErrorResponse> updatedRoleResponse = this.restTemplate.postForEntity(ROLES_URL+"/ADMIN/permissions", permission, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, updatedRoleResponse.getStatusCode());
        ErrorResponse apiError = updatedRoleResponse.getBody();
        assertNotNull(apiError);
        assertEquals("El campo no fue encontrado en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Permission with name NON_EXISTING_PERMISSION not found", apiError.getDetails().get("Error"));
    }
}
