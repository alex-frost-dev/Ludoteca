package com.ccsw.tutorialgame.client;

import com.ccsw.tutorialgame.client.model.Client;
import com.ccsw.tutorialgame.client.model.ClientDto;
import com.ccsw.tutorialgame.dto.StatusResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientLoanHelperService helper;

    @InjectMocks
    private ClientServiceImpl clientService;

    public final String CLIENT_NAME = "CLINAME_TEST";
    public final long EXISTENT_CLIENT_ID = 1L;
    public final long NON_EXISTENT_CLIENT_ID = 90L;

    /**
     * {@code findAll()} debería devolver todos los {@code Client} en la BBDD.
     */
    @Test
    public void findAllShouldReturnAllClients() {
        List<Client> list = new ArrayList<>();
        list.add(mock(Client.class));

        when(clientRepository.findAll()).thenReturn(list);

        List<Client> clients = clientService.findAll();

        assertNotNull(clients);
        assertEquals(1, clients.size());
    }

    /**
     * {@code save()} un {@code Client} con nombre único ha de guardarlo correctamente.
     */
    @Test
    public void saveWithUniqueNameShouldSave() {
        ClientDto clientDto = new ClientDto();
        clientDto.setName(CLIENT_NAME);

        ArgumentCaptor<Client> client = ArgumentCaptor.forClass(Client.class);

        clientService.save(null, clientDto);

        verify(clientRepository).save(client.capture());

        assertEquals(CLIENT_NAME, client.getValue().getName());
    }

    /**
     * {@code save()} un {@code Client} con un {@code name} ya existente en la BBDD ha de devolver un {@code ClientException.NAME_ALREADY_EXISTS}.
     */
    @Test
    public void saveWhenSameNameExistsShouldReturnClientNameError() {
        ClientDto firstClientDto = new ClientDto();
        firstClientDto.setName(CLIENT_NAME);

        ClientDto existentClientDto = new ClientDto();
        existentClientDto.setName(CLIENT_NAME);

        // Configura para que el primer 'save()' se comporte distinto al segundo.
        when(clientRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList())  // Para el primer 'save()' ('name' no existe) devuelve una empty list.
                .thenReturn(List.of(new Client() {{ // Para el primer 'save()' ('name' ahora existe) devuelve una 'List' de 'Client's.
                    setId(EXISTENT_CLIENT_ID + 1);
                }}));

        // El primero debería haber tenido éxito
        StatusResponse result1 = clientService.save(null, firstClientDto);
        // Captura el resultado del segundo 'save()', que debería fallar
        StatusResponse result2 = clientService.save(null, existentClientDto);

        assertEquals(result1.getMessage(), StatusResponse.OK_REQUEST_MSG);

        assertEquals(result2.getMessage(), ClientException.NAME_ALREADY_EXISTS);
    }

    /**
     * {@code save()} un {@code Client} con un {@code name} ya existente en la BBDD pero son el mismo (mismo {@code id})
     * ha de devolver un {@code StatusResponse.OK_REQUEST_MSG}.
     */
    @Test
    public void saveWhenSameNameExistsShouldContinue() {
        ClientDto firstClientDto = new ClientDto();
        firstClientDto.setId(EXISTENT_CLIENT_ID);
        firstClientDto.setName(CLIENT_NAME);

        ClientDto existentClientDto = new ClientDto();
        existentClientDto.setId(EXISTENT_CLIENT_ID);
        existentClientDto.setName(CLIENT_NAME);

        // Configura para que el primer 'save()' se comporte distinto al segundo.
        when(clientRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList())  // Para el primer 'save()' ('name' no existe) devuelve una empty list.
                .thenReturn(List.of(new Client() {{ // Para el primer 'save()' ('name' ahora existe) devuelve una 'List' de 'Client's.
                    setId(EXISTENT_CLIENT_ID);
                }}));

        // El primero debería haber tenido éxito
        StatusResponse result1 = clientService.save(null, firstClientDto);
        // Captura el resultado del segundo 'save()', que debería fallar
        StatusResponse result2 = clientService.save(null, existentClientDto);

        assertEquals(result1.getMessage(), StatusResponse.OK_REQUEST_MSG);

        assertEquals(result2.getMessage(), StatusResponse.OK_REQUEST_MSG);
    }

    /**
     * {@code delete()} un {@code Client} que existe en la BBDD debería eliminarlo con éxito.
     */
    @Test
    public void deleteExistsShouldDelete() throws Exception {
        Client client = mock(Client.class);
        when(clientRepository.findById(EXISTENT_CLIENT_ID)).thenReturn(Optional.ofNullable(client));

        clientService.delete(EXISTENT_CLIENT_ID);
        verify(clientRepository).deleteById(EXISTENT_CLIENT_ID);
    }

    /**
     * {@code delete()} un {@code Client} que no existe ha de devolver un {@code ClientException.CLIENT_ID_NOT_FOUND}.
     */
    @Test
    public void deleteClienteNotExistsShouldReturnClientNotFound() {
        StatusResponse response = clientService.delete(NON_EXISTENT_CLIENT_ID);
        assertEquals(response.getMessage(), ClientException.CLIENT_ID_NOT_FOUND);
    }
}