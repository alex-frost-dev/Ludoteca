package com.ccsw.tutorialcategory.client;

import com.ccsw.tutorialcategory.client.model.ClientDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClientIT {
    public static final Long NEW_CLIENT_ID = 8L;
    public static final String NEW_CLIENT_NAME = "Cliente 8";

    public static final Long MODIFY_CLIENT_ID = 1L;

    public static final Long DELETE_CLIENT_ID = 7L;
    public static final Long WITH_LOANS_CLIENT_ID = 1L;

    public static final String LOCALHOST = "http://localhost:";
    public static final String SERVICE_PATH = "/client";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    ParameterizedTypeReference<List<ClientDto>> responseType = new ParameterizedTypeReference<List<ClientDto>>() {
    };

    /**
     * {@code findAll()} debería devolver todos los {@code Client} de la base de datos.
     */
    @Test
    public void findAllShouldReturnAllClients() {

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);

        assertNotNull(response);
        assertEquals(7, response.getBody().size());
    }

    /**
     * Guardar un {@code Client} que no exista aún en la BBDD debería crear uno nuevo con éxito.
     */
    @Test
    public void saveWithoutIdShouldCreateNewClient() {

        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(8, response.getBody().size());

        ClientDto clientSearch = response.getBody().stream().filter(item -> item.getId().equals(NEW_CLIENT_ID)).findFirst().orElse(null);
        assertNotNull(clientSearch);
        assertEquals(NEW_CLIENT_NAME, clientSearch.getName());
    }

    /**
     * Modificar un {@code Client} que exista debería modificarlo con éxito.
     */
    @Test
    public void modifyWithExistIdShouldModifyClient() {

        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + MODIFY_CLIENT_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        ResponseEntity<List<ClientDto>> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);
        assertNotNull(response);
        assertEquals(7, response.getBody().size());

        ClientDto clientSearch = response.getBody().stream().filter(item -> item.getId().equals(MODIFY_CLIENT_ID)).findFirst().orElse(null);
        assertNotNull(clientSearch);
        assertEquals(NEW_CLIENT_NAME, clientSearch.getName());
    }

    /**
     * Modificar un {@code Client} que no exista debería devolver un {@code HttpStatus.BAD_REQUEST}.
     */
    @Test
    public void modifyWithIdNotExistShouldReturnBadRequestError() {

        ClientDto dto = new ClientDto();
        dto.setName(NEW_CLIENT_NAME);

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CLIENT_ID, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Borrar un {@code Client} que no tenga ningún {@code Loan} a su nombre debería borrarlo con éxito.
     */
    @Test
    public void deleteWhenIdExistsAndDoNotHaveLoanShouldDelete() {
        // Borra el 'Client' 7, que no tiene ningún 'Loan' a su nombre.
        ResponseEntity<?> delResponse = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + DELETE_CLIENT_ID, HttpMethod.DELETE, null, Void.class);

        ResponseEntity<List<ClientDto>> getResponse = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType);

        assertNotNull(getResponse);
        assertEquals(6, getResponse.getBody().size());
    }

    /**
     * Borrar un {@code Client} que tenga algún {@code Loan} a su nombre debería devolver {@code HttpStatus.BAD_REQUEST}.
     */
    @Test
    public void deleteWhenIdExistsButHaveLoansShouldReturnError() {
        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + WITH_LOANS_CLIENT_ID, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Borrar un {@code Client} que no exista debería devolver {@code HttpStatus.BAD_REQUEST}.
     */
    @Test
    public void deleteWithIdNotExistsShouldReturnNotFoundError() {

        ResponseEntity<?> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/" + NEW_CLIENT_ID, HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
