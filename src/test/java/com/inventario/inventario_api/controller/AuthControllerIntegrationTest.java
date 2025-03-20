package com.inventario.inventario_api.controller;

import com.inventario.inventario_api.DTO.UserDTO;
import com.inventario.inventario_api.DTO.UserInputDTO;
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

}

//    @Test
//    public void testLoginUser() {
//        // Crear un usuario previamente registrado
//        User user = new User("testuser", "password123");
//
//        // Registrar al usuario (puedes hacerlo directamente o usando un mock de base de datos)
//        restTemplate.postForEntity(registerUrl, user, User.class);
//
//        // Crear una solicitud de autenticación (por ejemplo, usando un body con username y password)
//        AuthRequest authRequest = new AuthRequest("testuser", "password123");
//
//        // Enviar la solicitud POST a /auth/login
//        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(loginUrl, authRequest, AuthResponse.class);
//
//        // Verificar que el estado de la respuesta sea 200 OK
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//
//        // Verificar que el token JWT esté presente
//        assertThat(response.getBody().getToken()).isNotEmpty();
//    }
//}