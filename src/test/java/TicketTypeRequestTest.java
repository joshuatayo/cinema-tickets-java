import org.junit.Test;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TicketTypeRequestTest {

    private TicketTypeRequest[] ticketTypeRequest;

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
        int actualAmount = TicketTypeRequest.getTotalAmountToPay(ticketTypeRequest);
        // then
        assertEquals(40, actualAmount);
    }

    @Test
    public void calculateNoOfSeatsToReserveRest() {
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
    public void testAccountLessThanOrEqualZero() {
        // given
        long accountId = 0L;
        // when
        TicketTypeRequest.validAccountId(accountId);
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
    public void testWhenMoreThan20TicketIsPurchasedAtATime() {
        // given
        List<TicketTypeRequest> ticketTypeRequests = new ArrayList<TicketTypeRequest>();
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 19));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2));
        ticketTypeRequests.add(new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));

        ticketTypeRequest = new TicketTypeRequest[ticketTypeRequests.size()];
        ticketTypeRequests.toArray(ticketTypeRequest);
        // when
        TicketTypeRequest.checkNoOfTicketPurchasedAtATime(ticketTypeRequest);
    }

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

        TicketServiceImpl ticketServiceImpl = new TicketServiceImpl();
        ticketServiceImpl.purchaseTickets(1L, ticketTypeRequest);
    }
}