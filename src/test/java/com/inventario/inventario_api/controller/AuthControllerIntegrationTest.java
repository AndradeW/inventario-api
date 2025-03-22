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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

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
    public void testRegisterUser_Ok_no_sending_rol() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("username")
                .email("email@email.com")
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
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
        assertEquals("CUSTOMER", responseBody.getRole()[0]);
        assertEquals(newUser.getAddress(), responseBody.getAddress());
        assertEquals(newUser.getPhone(), responseBody.getPhone());
    }

    @Test
    public void testRegisterUser_Ok_sending_rol_ADMIN() {
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
    public void testRegisterUser_RoleList() {
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
    public void testRegisterUser_withInvalidEmail() {
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
        assertEquals("Debe ser una dirección de correo electrónico válida", apiError.getDetails().get("email"));
    }

    @Test
    public void testRegisterUser_RoleUserNotFoundInDB() {
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
    public void testRegisterUser_UsernameAlreadyExist() {
        // Given

        String existingUsername = "username";
        UserInputDTO newUser = UserInputDTO.builder()
                .username(existingUsername)
                .email("email@email.com")
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        this.userRepository.save(
                User.builder()
                        .username(existingUsername).build()
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
    public void testRegisterUser_EmailAlreadyExists() {
        // Given
        String existingEmail = "email@email.com";

        UserInputDTO newUser = UserInputDTO.builder()
                .username("existingUser")
                .email(existingEmail)
                .password("password")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        this.userRepository.save(
                User.builder()
                        .email(existingEmail).build()
        );

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("Bad credentials", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Email already exists", apiError.getDetails().get("Error"));
    }


    @Test
    public void testRegisterUser_ShortPassword() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("newUser")
                .email("email@email.com")
                .password("123")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertTrue(apiError.getDetails().containsKey("password"));
        assertEquals("La contraseña debe tener al menos 8 caracteres", apiError.getDetails().get("password"));
    }

    @Test
    public void testRegisterUser_MissingFields() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("username")
//                .email("")
//                .password("")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("Validations error", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("email"));
        assertEquals("El correo electrónico no puede estar vacío", apiError.getDetails().get("email"));
        assertTrue(apiError.getDetails().containsKey("password"));
        assertEquals("La contraseña no puede estar vacía", apiError.getDetails().get("password"));
    }


    @Test
    public void testRegisterUser_InvalidUsername() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("invalid!user")  // Nombre de usuario con caracteres no permitidos
                .email("validemail@email.com")
                .password("password123")
                .name("name")
                .address("address")
                .phone("phone")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertTrue(apiError.getDetails().containsKey("username"));
        assertEquals("El nombre de usuario solo puede contener letras, números, guiones y puntos", apiError.getDetails().get("username"));
    }

    //   ------------------- -------------------   Login  ------------------------------ -------------------------------
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
    public void testLoginUser_UserNotFound() {
        // Given
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username("nonexistentUser")
                .password("password123")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("User not found", responseBody.getMessage());
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

    /*TODO Revisar escenario
    @Test
    public void testLoginUser_UserBlocked() {
        // Given
        UserInputDTO newUser = UserInputDTO.builder()
                .username("blockedUser")
                .email("blocked@email.com")
                .password("password123")
                .build();
        // Crear usuario bloqueado
        User user = userRepository.save(newUser.toEntity());
        user.setBlocked(true);
        userRepository.save(user);

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username("blockedUser")
                .password("password123")
                .build();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ErrorResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("User is blocked", responseBody.getMessage());
    }*/

    // ------------------------------------- JWT ---------------------------------------------------------------------
    @Test
    public void testAccessProtectedRouteWithJWT() {
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

        ResponseEntity<AuthResponse> loginResponse = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, AuthResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        AuthResponse responseBody = loginResponse.getBody();
        assertNotNull(responseBody);

        String token = responseBody.token();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);


        // When
        ResponseEntity<String> resp = this.restTemplate.exchange("/api/users", HttpMethod.GET, entity, String.class);

        // Then
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }

    @Test
    public void testAccessProtectedRouteWithInvalidJWT() {
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

        ResponseEntity<AuthResponse> loginResponse = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, AuthResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        AuthResponse loginResponseBody = loginResponse.getBody();
        assertNotNull(loginResponseBody);

        String token = loginResponseBody.token();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token + "invalid");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // When
        ResponseEntity<ErrorResponse> resp = this.restTemplate.exchange("/api/users", HttpMethod.GET, entity, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        ErrorResponse responseBody = resp.getBody();
        assertNotNull(responseBody);

        assertEquals(HttpStatus.UNAUTHORIZED, responseBody.getStatusCode());
        assertEquals("Invalid token", responseBody.getMessage());
        assertTrue(responseBody.getDetails().containsKey("Error"));
        assertEquals("Token inválido", responseBody.getDetails().get("Error"));
    }

    @Test
    public void testTokenExpiration() throws InterruptedException {
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

        ResponseEntity<AuthResponse> loginResponse = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, AuthResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        AuthResponse loginResponseBody = loginResponse.getBody();
        assertNotNull(loginResponseBody);

        String token = loginResponseBody.token();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Thread.sleep(3000);

        // When
        ResponseEntity<ErrorResponse> resp = this.restTemplate.exchange("/api/users", HttpMethod.GET, entity, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        ErrorResponse responseBody = resp.getBody();
        assertNotNull(responseBody);

        assertEquals(HttpStatus.UNAUTHORIZED, responseBody.getStatusCode());
        assertEquals("Invalid token", responseBody.getMessage());
        assertTrue(responseBody.getDetails().containsKey("Error"));
        assertEquals("El token ha expirado", responseBody.getDetails().get("Error"));
    }

//    @Test
//    public void testTokenRefresh() {
//        String refreshToken = getValidRefreshToken(); // Obtener un token de refresco válido
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + refreshToken);
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        ResponseEntity<Map> response = this.restTemplate.exchange("/api/refresh-token", HttpMethod.POST, entity, Map.class);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertTrue(response.getBody().containsKey("newToken"));
//    }

}
