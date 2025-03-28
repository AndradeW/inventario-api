package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.PermissionDTO;
import com.inventario.inventario_api.config.TestSecurityConfig;
import com.inventario.inventario_api.exceptions.ErrorResponse;
import com.inventario.inventario_api.model.Permission;
import com.inventario.inventario_api.repository.PermissionRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class PermissionControllerE2ETest {

    private static final String PERMISSION_URL = "/api/permissions";

    private static final String TEST_PERMISSION_READ = "READ";
    private static final String TEST_PERMISSION_TEST = "TEST";

    private final TestRestTemplate restTemplate;
    private final PermissionRepository permissionRepository;

    @Autowired
    PermissionControllerE2ETest(TestRestTemplate restTemplate, PermissionRepository permissionRepository) {
        this.restTemplate = restTemplate;
        this.permissionRepository = permissionRepository;
    }

    @BeforeEach
    void setUp() {
        Permission permission = Permission.builder().name(TEST_PERMISSION_TEST).build();
        this.permissionRepository.save(permission);
    }

    @AfterEach
    void tearDown() {
        this.permissionRepository.deleteAll();
    }

    @Test
    public void testCreatePermissionOk() {
        // Given
        PermissionDTO permissionDTO = PermissionDTO.builder()
                .name(TEST_PERMISSION_READ)
                .build();

        // When
        ResponseEntity<PermissionDTO> response = this.restTemplate.postForEntity(PERMISSION_URL, permissionDTO, PermissionDTO.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        PermissionDTO createdPermission = response.getBody();
        assertNotNull(createdPermission);
        assertEquals(TEST_PERMISSION_READ, createdPermission.name());
    }

    @Test
    public void testCreatePermissionAlreadyExists() {
        // Given
        PermissionDTO permissionDTO = PermissionDTO.builder()
                .name(TEST_PERMISSION_TEST)
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(PERMISSION_URL, permissionDTO, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("El campo ya existe en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Permission TEST already exists", apiError.getDetails().get("Error"));
    }


    @Test
    public void testUpdatePermissionOk() {
        // Given
        PermissionDTO permissionDTO = PermissionDTO.builder()
                .name(TEST_PERMISSION_READ)
                .build();
        // When
        ResponseEntity<PermissionDTO> response = this.restTemplate.exchange(
                PERMISSION_URL + "/name/" + TEST_PERMISSION_TEST,
                HttpMethod.PUT,
                new HttpEntity<>(permissionDTO),
                PermissionDTO.class);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        PermissionDTO updatedPermission = response.getBody();
        assertNotNull(updatedPermission);
        assertEquals(TEST_PERMISSION_READ, updatedPermission.name());
    }

    @Test
    public void testUpdatePermissionNotFound() {
        // Given
        PermissionDTO permissionDTO = PermissionDTO.builder()
                .name(TEST_PERMISSION_TEST)
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.exchange(
                PERMISSION_URL + "/name/" + TEST_PERMISSION_READ,
                HttpMethod.PUT,
                new HttpEntity<>(permissionDTO),
                ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("El campo no fue encontrado en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Permission with name READ not found", apiError.getDetails().get("Error"));
    }

    @Test
    public void testDeletePermissionOk() {
        // Given

        // When
        ResponseEntity<Void> response = this.restTemplate.exchange(
                PERMISSION_URL + "/name/" + TEST_PERMISSION_TEST,
                HttpMethod.DELETE,
                null,
                Void.class);
        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testDeletePermissionNotFound() {
        // Given

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.exchange(
                PERMISSION_URL + "/name/" + TEST_PERMISSION_READ,
                HttpMethod.DELETE,
                null,
                ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("El campo no fue encontrado en la DB", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Permission with name READ not found", apiError.getDetails().get("Error"));
    }
}
