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

import static com.inventario.inventario_api.model.Role.ROLE_ADMIN;
import static com.inventario.inventario_api.model.Role.ROLE_CUSTOMER;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

    private static final String REGISTER_URL = "/auth/register";
    private static final String LOGIN_URL = "/auth/login";

    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "testuser@email.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String[] TEST_ADMIN_ROLE = {ROLE_ADMIN};
    private static final String[] TEST_CUSTOMER_ROLE = {ROLE_CUSTOMER};

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
        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, new String[]{});

        // When
        ResponseEntity<UserDTO> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(newUser.getName(), responseBody.getName());
        assertEquals(newUser.getUsername(), responseBody.getUsername());
        assertEquals(newUser.getEmail(), responseBody.getEmail());
        assertEquals(ROLE_CUSTOMER, responseBody.getRole()[0]);
        assertEquals(newUser.getAddress(), responseBody.getAddress());
        assertEquals(newUser.getPhone(), responseBody.getPhone());
    }

    @Test
    public void testRegisterUser_Ok_sending_rol_ADMIN() {
        // Given
        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ADMIN_ROLE);

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
        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, new String[]{ROLE_ADMIN, ROLE_CUSTOMER});

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
        UserInputDTO newUser = this.createUser(TEST_USERNAME, "invalid-email", TEST_PASSWORD, TEST_ADMIN_ROLE);

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

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
        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_CUSTOMER_ROLE);

        this.rolesRepository.deleteAll();

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals(HttpStatus.BAD_REQUEST, apiError.getStatusCode());
        assertEquals("Bad credentials", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("Error"));
        assertEquals("Roles do not match: " + ROLE_CUSTOMER, apiError.getDetails().get("Error"));
    }

    @Test
    public void testRegisterUser_UsernameAlreadyExist() {
        // Given
        this.userRepository.save(
                User.builder()
                        .username(TEST_USERNAME).build()
        );

        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ADMIN_ROLE);

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
        this.userRepository.save(
                User.builder()
                        .email(TEST_EMAIL).build()
        );

        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ADMIN_ROLE);

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
        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, "123", TEST_ADMIN_ROLE);

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
        UserInputDTO newUser = this.createUser(null, null, null, TEST_ADMIN_ROLE);

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("Validations error", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("username"));
        assertEquals("El nombre de usuario no puede estar vacío", apiError.getDetails().get("username"));
        assertTrue(apiError.getDetails().containsKey("email"));
        assertEquals("El correo electrónico no puede estar vacío", apiError.getDetails().get("email"));
        assertTrue(apiError.getDetails().containsKey("password"));
        assertEquals("La contraseña no puede estar vacía", apiError.getDetails().get("password"));
    }

    @Test
    public void testRegisterUser_EmptyFields() {
        // Given
        UserInputDTO newUser = this.createUser("", "", "", TEST_ADMIN_ROLE);

        // When
        ResponseEntity<ErrorResponse> response = this.restTemplate.postForEntity(REGISTER_URL, newUser, ErrorResponse.class);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse apiError = response.getBody();
        assertNotNull(apiError);
        assertEquals("Validations error", apiError.getMessage());
        assertTrue(apiError.getDetails().containsKey("username"));
        assertEquals("El nombre de usuario debe tener entre 3 y 20 caracteres", apiError.getDetails().get("username"));
        assertTrue(apiError.getDetails().containsKey("email"));
        assertEquals("El correo electrónico no puede estar vacío", apiError.getDetails().get("email"));
        assertTrue(apiError.getDetails().containsKey("password"));
        assertEquals("La contraseña debe tener al menos 8 caracteres", apiError.getDetails().get("password"));
    }


    @Test
    public void testRegisterUser_InvalidUsername() {
        // Given
        UserInputDTO newUser = this.createUser("invalid!&%username", TEST_EMAIL, TEST_PASSWORD, TEST_ADMIN_ROLE);

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
        this.createTestUserOk();

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build();

        // When
        ResponseEntity<AuthResponse> response = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, AuthResponse.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse responseBody = response.getBody();
        assertNotNull(responseBody);

        assertEquals(TEST_USERNAME, responseBody.username());
        assertEquals("", responseBody.message());
        assertNotNull(responseBody.token());
        assertTrue(responseBody.status());
    }

    @Test
    public void testLoginUser_UserNotFound() {
        // Given
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username("nonexistentUser")
                .password(TEST_PASSWORD)
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
        this.createTestUserOk();

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .password(TEST_PASSWORD)
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
        this.createTestUserOk();

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(TEST_USERNAME)
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
        this.createTestUserOk();

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(TEST_USERNAME)
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
        String token = this.getToken();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);


        // When
        ResponseEntity<String> resp = this.restTemplate.exchange("/api/users", HttpMethod.GET, entity, String.class);

        // Then
        assertEquals(HttpStatus.OK, resp.getStatusCode());
    }



    @Test
    public void testAccessProtectedRouteWithInvalidJWT() {
        // Given
        String token = this.getToken();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token + "invalid");
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
        String token = this.getToken();
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
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

    private UserInputDTO createUser(String username, String email, String password, String[] role) {
        return UserInputDTO.builder()
                .username(username)
                .email(email)
                .password(password)
                .role(role)
                .name("name")
                .address("address")
                .phone("phone")
                .build();
    }

    private void createTestUserOk(){
        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ADMIN_ROLE);

        ResponseEntity<UserDTO> userCreateResponse = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);
        assertEquals(HttpStatus.CREATED, userCreateResponse.getStatusCode());
    }

    private String getToken() {
        UserInputDTO newUser = this.createUser(TEST_USERNAME, TEST_EMAIL, TEST_PASSWORD, TEST_ADMIN_ROLE);

        ResponseEntity<UserDTO> userCreateResponse = this.restTemplate.postForEntity(REGISTER_URL, newUser, UserDTO.class);
        assertEquals(HttpStatus.CREATED, userCreateResponse.getStatusCode());

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(TEST_USERNAME)
                .password(TEST_PASSWORD)
                .build();

        ResponseEntity<AuthResponse> loginResponse = this.restTemplate.postForEntity(LOGIN_URL, userLoginDTO, AuthResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        AuthResponse responseBody = loginResponse.getBody();
        assertNotNull(responseBody);

        return responseBody.token();
    }
}
