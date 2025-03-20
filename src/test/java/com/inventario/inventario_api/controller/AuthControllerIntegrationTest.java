package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.AuthResponse;
import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
import com.inventario.inventario_api.DTO.UserLoginDTO;
import com.inventario.inventario_api.exceptions.ErrorResponse;
import com.inventario.inventario_api.model.Role;
import com.inventario.inventario_api.model.User;
import com.inventario.inventario_api.repository.RolesRepository;
import com.inventario.inventario_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

    private static final String REGISTER_URL = "/auth/register";
    private static final String LOGIN_URL = "/auth/login";


    @Autowired
    private RolesRepository rolesRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void init() {

    }

    @BeforeEach
    public void setUp() {
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        this.rolesRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setName("CUSTOMER");
        this.rolesRepository.save(userRole);
    }

    @AfterEach
    void tearDown() {
        this.userRepository.deleteAll();
        this.rolesRepository.deleteAll();
    }

    @Test
    public void testRegisterUser_Ok_no_sending_rol() throws Exception {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("username")
                .email("email@email.com")
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        List<Role> roles = this.rolesRepository.findAll();
        System.out.println("Role in DB: " + roles.size());

        // When
        ResponseEntity<UserDTO> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(newUser.getName(), responseBody.getName());
        assertEquals(newUser.getUsername(), responseBody.getUsername());
        assertEquals(newUser.getEmail(), responseBody.getEmail());
        assertEquals("CUSTOMER", responseBody.getRole()[0]);
        assertEquals(newUser.getAddress(), responseBody.getAddress());
        assertEquals(newUser.getPhone(), responseBody.getPhone());
    }

    @Test
    public void testRegisterUser_Ok_sending_rol_ADMIN() throws Exception {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("username")
                .email("email@email.com")
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
                .role(new String[]{"ADMIN"})
                .build();

        // When
        ResponseEntity<UserDTO> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(newUser.getName(), responseBody.getName());
        assertEquals(newUser.getUsername(), responseBody.getUsername());
        assertEquals(newUser.getEmail(), responseBody.getEmail());
        assertArrayEquals(newUser.getRole(), responseBody.getRole());
        assertEquals(newUser.getAddress(), responseBody.getAddress());
        assertEquals(newUser.getPhone(), responseBody.getPhone());
    }

    @Test
    public void testRegisterUser_RoleList() throws Exception {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("username")
                .email("email@email.com")
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
                .role(new String[]{"ADMIN", "CUSTOMER"})
                .build();

        // When
        ResponseEntity<UserDTO> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(newUser.getName(), responseBody.getName());
        assertEquals(newUser.getUsername(), responseBody.getUsername());
        assertEquals(newUser.getEmail(), responseBody.getEmail());
        assertArrayEquals(newUser.getRole(), responseBody.getRole());
        assertEquals(newUser.getAddress(), responseBody.getAddress());
        assertEquals(newUser.getPhone(), responseBody.getPhone());
    }

    @Test
    public void testRegisterUser_withInvalidEmail() throws Exception {
        // Given
        UserInputDTO invalidUser = UserInputDTO.builder()
                .username("newUser")
                .email("invalid-email")
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, invalidUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.BAD_REQUEST, apiError.getStatusCode());
        assertEquals("Validations error", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("email"));
        assertEquals("debe ser una dirección de correo electrónico con formato correcto", apiError.getDetails().get("email"));
    }

    @Test
    public void testRegisterUser_RoleUserNotFoundInDB() throws Exception {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("username")
                .email("email@email.com")
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        this.rolesRepository.deleteAll();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.NOT_FOUND, apiError.getStatusCode());
        assertEquals("User not found", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Role 'User' not found in the database", apiError.getDetails().get("Error"));
    }

    @Test
    public void testRegisterUser_UsernameAlreadyExist() throws Exception {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("username")
                .email("email@email.com")
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        this.userRepository.save(
                User.builder()
                        .username("username").build()
        );

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.BAD_REQUEST, apiError.getStatusCode());
        assertEquals("Bad credentials", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Username already exists", apiError.getDetails().get("Error"));
    }

    @Test
    public void testLoginUser_OK() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("testuser")
                .email("testuser@email.com")
                .password("password123").build();

        ResponseEntity<UserDTO> userCreateResponse = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);
        assertEquals(HttpStatus.CREATED, userCreateResponse.getStatusCode());

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username("testuser")
                .password("password123")
                .build();

        // When
        ResponseEntity<AuthResponse> response = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, AuthResponse.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse responseBody = response.getBody();
        assertNotNull(responseBody);

        assertEquals(newUser.getUsername(), responseBody.username());
        assertEquals("", responseBody.message());
        assertNotNull(responseBody.token());
        assertTrue(responseBody.status());
    }

    @Test
    public void testLoginUser_noUsername() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("testuser")
                .email("testuser@email.com")
                .password("password123").build();

        ResponseEntity<UserDTO> userCreateResponse = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);
        assertEquals(HttpStatus.CREATED, userCreateResponse.getStatusCode());

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .password("password123")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse responseBody = response.getBody();
        assertNotNull(responseBody);

        assertEquals("Validations error", responseBody.getMessage());
        assertTrue(responseBody.getDetails().containsKey("username"));
        assertEquals("no debe estar vacío", responseBody.getDetails().get("username"));
    }

    @Test
    public void testLoginUser_noPassword() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("testuser")
                .email("testuser@email.com")
                .password("password123").build();

        ResponseEntity<UserDTO> userCreateResponse = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);
        assertEquals(HttpStatus.CREATED, userCreateResponse.getStatusCode());

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                        .username("testuser")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse responseBody = response.getBody();
        assertNotNull(responseBody);

        assertEquals("Validations error", responseBody.getMessage());
        assertTrue(responseBody.getDetails().containsKey("password"));
        assertEquals("no debe estar vacío", responseBody.getDetails().get("password"));
    }

    @Test
    public void testLoginUser_wrongPassword() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("testuser")
                .email("testuser@email.com")
                .password("password123").build();

        ResponseEntity<UserDTO> userCreateResponse = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);
        assertEquals(HttpStatus.CREATED, userCreateResponse.getStatusCode());

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                        .username("testuser")
                .password("wrongpassword")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse responseBody = response.getBody();
        assertNotNull(responseBody);

        assertEquals("Bad credentials", responseBody.getMessage());
        assertTrue(responseBody.getDetails().containsKey("Error"));
        assertEquals("Contraseña incorrecta", responseBody.getDetails().get("Error"));
    }
}