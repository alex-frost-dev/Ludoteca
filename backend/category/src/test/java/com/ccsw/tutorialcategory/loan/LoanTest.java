package com.ccsw.ludoteca.loan;

import com.ccsw.ludoteca.client.model.Client;
import com.ccsw.ludoteca.dto.StatusResponse;
import com.ccsw.ludoteca.exception.CommonErrorResponse;
import com.ccsw.ludoteca.game.model.Game;
import com.ccsw.ludoteca.loan.model.Loan;
import com.ccsw.ludoteca.loan.model.LoanDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanTest {

    public static final Long EXISTING_LOAN_ID = 1L;

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanServiceImpl loanService;

    /**
     * Debería devolver un {@code List<Loan>} de los {@code Loan} guardados en la BBDD.
     */
    @Test
    public void findAllShouldReturnAllLoans() {
        List<Loan> list = new ArrayList<>();
        list.add(mock(Loan.class));

        when(loanRepository.findAll()).thenReturn(list);

        List<Loan> categories = loanService.findAll();

        assertNotNull(categories);
        assertEquals(1, categories.size());
    }

    /**
     * {@code save()} un {@code Loan} debería guardar.
     */
    @Test
    public void saveValidLoanShouldSave() throws LoanException {
        // Arrange
        Client mockClient = mock(Client.class);
        Game mockGame = mock(Game.class);
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(4);

        LoanDto loanDto = new LoanDto();
        loanDto.setClient(mockClient);
        loanDto.setGame(mockGame);
        loanDto.setStartDate(startDate);
        loanDto.setEndDate(endDate);

        Loan expectedLoan = new Loan();
        expectedLoan.setClient(mockClient);
        expectedLoan.setGame(mockGame);
        expectedLoan.setStartDate(startDate);
        expectedLoan.setEndDate(endDate);

        when(loanRepository.save(any(Loan.class))).thenReturn(expectedLoan);

        // Act
        StatusResponse response = loanService.save(loanDto);

        // Assert
        verify(loanRepository).save(any(Loan.class));
        assertNotNull(response);
        assertEquals(StatusResponse.OK_REQUEST_MSG, response.getMessage());
    }

    /**
     * {@code save()} un {@code Loan} sin alguno de los datos debería devolver error.
     */
    @Test
    public void saveInvalidLoanShouldReturnError() throws LoanException {
        // Arrange
        Client mockClient = mock(Client.class);
        Game mockGame = mock(Game.class);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(4);

        LoanDto loanDto1 = new LoanDto(); // Sin Client
        loanDto1.setGame(mockGame);
        loanDto1.setStartDate(startDate);
        loanDto1.setEndDate(endDate);

        LoanDto loanDto2 = new LoanDto(); // Sin Game
        loanDto2.setClient(mockClient);
        loanDto2.setStartDate(startDate);
        loanDto2.setEndDate(endDate);

        LoanDto loanDto3 = new LoanDto(); // Sin StartDate
        loanDto3.setClient(mockClient);
        loanDto3.setGame(mockGame);
        loanDto3.setEndDate(endDate);

        LoanDto loanDto4 = new LoanDto(); // Sin EndDate
        loanDto4.setClient(mockClient);
        loanDto4.setGame(mockGame);
        loanDto4.setEndDate(endDate);

        assertEquals(CommonErrorResponse.MISSING_REQUIRED_FIELDS, loanService.save(loanDto1).getMessage());
        assertEquals(CommonErrorResponse.MISSING_REQUIRED_FIELDS, loanService.save(loanDto2).getMessage());
        assertEquals(CommonErrorResponse.MISSING_REQUIRED_FIELDS, loanService.save(loanDto3).getMessage());
        assertEquals(CommonErrorResponse.MISSING_REQUIRED_FIELDS, loanService.save(loanDto4).getMessage());
    }
}