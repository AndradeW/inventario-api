package com.inventario.inventario_api.controller;

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

    public static final String ROLES_URL = "/roles";

    private static final String TEST_ROLE = "TEST";
    private static final String TEST_DESCRIPTION_ROLE = "Test Role";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @BeforeEach
    public void setUp() {
        Permission permission = Permission.builder().name("READ").build();
        this.permissionRepository.save(permission);

        Role role = Role.builder().name(ROLE_ADMIN).description("Admin Role").permissionsList(setOf(permission)).build();
        this.roleRepository.save(role);
    }

    @AfterEach
    public void tearDown() {
        this.roleRepository.deleteAll();
        this.permissionRepository.deleteAll();
    }
/*
    @Test
    public void testCreateRoleWithRoleOk() {
        // Given
        Role newRole = Role.builder()
                .name(TEST_ROLE)
                .description(TEST_DESCRIPTION_ROLE)
                .permissionsList(setOf(Permission.builder().name("READ").build()))
                .build();

        // When
        ResponseEntity<Role> response = this.restTemplate.postForEntity(ROLES_URL, newRole, Role.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Role updatedRole = response.getBody();
        assertNotNull(updatedRole);
        assertEquals(TEST_ROLE, updatedRole.getName());
        assertEquals(TEST_DESCRIPTION_ROLE, updatedRole.getDescription());
//        assertArrayEquals(newRole.getPermissionsList(), updatedRole.getPermissionsList().stream()
//                .map(Permission::getName)
//                .toArray(String[]::new)); //TODO revisar si se retorna un DTO o un Array de String
    }
*/

    @Test
    public void testCreateRoleRepeated() {
        // Given
        Role newRole = Role.builder()
                .name("ADMIN")
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
        assertEquals("Role with name ADMIN ya existe", apiError.getDetails().get("Error"));
    }

/*
    @Test
    public void testUpdateRoleOk() {
        // Given
        Role newRole = Role.builder()
                .name(TEST_ROLE)
                .description(TEST_DESCRIPTION_ROLE)
                .build();

        // When
        ResponseEntity<Role> updatedRoleResponse = this.restTemplate.exchange(
                ROLES_URL + "/1",
                HttpMethod.PUT,
                new HttpEntity<>(newRole),
                Role.class);

        // Then
        assertEquals(HttpStatus.OK, updatedRoleResponse.getStatusCode());
        Role roleResponseBody = updatedRoleResponse.getBody();
        assertNotNull(roleResponseBody);
        assertEquals(newRole.getId(), roleResponseBody.getId());
        assertEquals(newRole.getName(), roleResponseBody.getName());
        assertEquals(newRole.getDescription(), roleResponseBody.getDescription());
        assertFalse(roleResponseBody.getPermissionsList().isEmpty());
    }
*/

    @Test
    public void testUpdateRoleNotFound() {
        // Given
        Role newRole = Role.builder()
                .name("ADMIN")
                .description("Admin Role")
                .build();

        // When
        ResponseEntity<ErrorResponse> updatedRoleResponse = this.restTemplate.exchange(
                ROLES_URL + "/99",
                HttpMethod.PUT,
                new HttpEntity<>(newRole),
                ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, updatedRoleResponse.getStatusCode());
        ErrorResponse apiError = updatedRoleResponse.getBody();
        assertNotNull(apiError);
        assertEquals("El campo no fue encontrado en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Role with id 99 not found", apiError.getDetails().get("Error"));
    }

    // Test para crear un rol y asignar permisos
    @Test
    public void testCreateRoleAndAssignPermissions() {
        // Given
        Role newRole = Role.builder()
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
        ResponseEntity<Role> updatedRoleResponse = this.restTemplate.postForEntity("/roles/TEST/permissions", permission, Role.class);

        // Then
        assertEquals(HttpStatus.OK, updatedRoleResponse.getStatusCode());
        Role updatedRole = updatedRoleResponse.getBody();
        assertNotNull(updatedRole);
        assertEquals(TEST_ROLE, updatedRole.getName());
        assertEquals(TEST_DESCRIPTION_ROLE, updatedRole.getDescription());
        assertArrayEquals(permission, updatedRole.getPermissionsList().stream()
                .map(Permission::getName)
                .toArray(String[]::new)); //TODO revisar si se retorna un DTO o un Array de String
    }

    // Test para manejar rol no encontrado
    @Test
    public void testRoleNotFound() {
        // Given
        String[] permission = new String[]{"READ"};

        // When
        ResponseEntity<ErrorResponse> updatedRoleResponse = this.restTemplate.postForEntity("/roles/NON_EXISTING_ROLE/permissions", permission, ErrorResponse.class);

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
        ResponseEntity<ErrorResponse> updatedRoleResponse = this.restTemplate.postForEntity("/roles/ADMIN/permissions", permission, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, updatedRoleResponse.getStatusCode());
        ErrorResponse apiError = updatedRoleResponse.getBody();
        assertNotNull(apiError);
        assertEquals("El campo no fue encontrado en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Permission with name NON_EXISTING_PERMISSION not found", apiError.getDetails().get("Error"));
    }
}
