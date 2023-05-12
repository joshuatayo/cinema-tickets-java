import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.dwp.uc.pairtest.TicketService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TicketTypeRequestTest {

    private TicketTypeRequest[] ticketTypeRequest;
    @Mock
    private TicketServiceImpl ticketServiceImpl;

    @Test
    public void testPurchaseTickets() {
        // given
        List<TicketTypeRequest> ticketTypeRequests = new ArrayList<TicketTypeRequest>();
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        ticketTypeRequest = new TicketTypeRequest[ticketTypeRequests.size()];
        ticketTypeRequests.toArray(ticketTypeRequest);

        // when
        ticketServiceImpl.purchaseTickets(1L, ticketTypeRequest);

        // then
        verify(ticketServiceImpl).purchaseTickets(1L, ticketTypeRequest);


    }

    @Test
    public void testCalculateTotalAmountToPay() {
        // given
        List<TicketTypeRequest> ticketTypeRequests = new ArrayList<TicketTypeRequest>();
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        ticketTypeRequest = new TicketTypeRequest[ticketTypeRequests.size()];
        ticketTypeRequests.toArray(ticketTypeRequest);

        // when
        double actualAmount = TicketTypeRequest.getTotalAmountToPay(ticketTypeRequest);

        // then
        assertEquals(40.0, actualAmount, 0.0);
    }

    @Test
    public void testCalculateNoOfSeatsToReserveRest() {
        // given
        List<TicketTypeRequest> ticketTypeRequests = new ArrayList<TicketTypeRequest>();
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        ticketTypeRequest = new TicketTypeRequest[ticketTypeRequests.size()];
        ticketTypeRequests.toArray(ticketTypeRequest);

        // when
        int actualNoOfSeatToReserve = TicketTypeRequest.getNoOfSeatToReserve(ticketTypeRequest);

        // then
        assertEquals(5, actualNoOfSeatToReserve);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testWhenMoreThan20TicketIsPurchasedAtATime() {
        // given
        List<TicketTypeRequest> ticketTypeRequests = new ArrayList<TicketTypeRequest>();
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 19));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        ticketTypeRequest = new TicketTypeRequest[ticketTypeRequests.size()];
        ticketTypeRequests.toArray(ticketTypeRequest);

        // when
        TicketTypeRequest.noOfTicketPurchasedAtATime(ticketTypeRequest);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testWhenAdultTicketIsNotPurchased() {
        // given
        String ticketType = TicketTypeRequest.Type.ADULT.toString();
        List<TicketTypeRequest> ticketTypeRequests = new ArrayList<TicketTypeRequest>();
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        ticketTypeRequest = new TicketTypeRequest[ticketTypeRequests.size()];
        ticketTypeRequests.toArray(ticketTypeRequest);

        // when
        TicketTypeRequest.adultExist(ticketType, ticketTypeRequest);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testNoOfTicketLessThanOrEqualZero() {
        // given
        List<TicketTypeRequest> ticketTypeRequests = new ArrayList<TicketTypeRequest>();
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));
        ticketTypeRequest = new TicketTypeRequest[ticketTypeRequests.size()];
        ticketTypeRequests.toArray(ticketTypeRequest);

        // when
        TicketTypeRequest.validNoOfTicket(ticketTypeRequest);
    }

    @Test(expected = InvalidPurchaseException.class)
    public void testAccountIdLessThanOrEqualZero() {
        // given
        long accountId = 0L;

        // when
        TicketTypeRequest.validAccountId(accountId);
    }

    @Test
    public void  testTicketPrice() {
        // given
        String ticketType = String.valueOf(TicketTypeRequest.Type.ADULT);

        // when
        double price = TicketTypeRequest.getTicketPrice(ticketType);

        // then
        assertEquals(20.0, price, 0.0);
    }
}