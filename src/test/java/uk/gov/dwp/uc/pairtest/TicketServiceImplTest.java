package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TicketServiceImplTest {

    private TicketPaymentService paymentService;
    private SeatReservationService seatReservationService;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        paymentService = mock(TicketPaymentService.class);
        seatReservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, seatReservationService);
    }

    @Test
    void shouldCalculatePriceAndSeatsForMixedPurchase() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2));

        verify(paymentService).makePayment(1L, 80);
        verify(seatReservationService).reserveSeat(1L, 4);
    }

    @Test
    void shouldRejectInfantOnlyPurchase() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldRejectChildOnlyPurchase() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldRejectMoreThan25Tickets() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 26)));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldRejectMoreInfantsThanAdults() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L,
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                        new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldRejectZeroOrNegativeQuantities() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0)));
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1)));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldRejectEmptyRequests() {
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(1L));
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, (TicketTypeRequest[]) null));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldRejectInvalidAccountIds() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(0L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)));
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(-1L, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)));
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(null, new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldNotChargeOrReserveSeatsForInfantTickets() {
        ticketService.purchaseTickets(1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1));

        verify(paymentService).makePayment(1L, 25);
        verify(seatReservationService).reserveSeat(1L, 1);
    }

    @Test
    void shouldRejectTicketQuantitiesThatExceedTheLimitWithoutOverflow() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(
                        1L,
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20),
                        new TicketTypeRequest(
                                TicketTypeRequest.Type.ADULT,
                                Integer.MAX_VALUE
                        )
                )
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldAcceptExactly25AdultTickets() {
        ticketService.purchaseTickets(
                1L,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25)
        );

        verify(paymentService).makePayment(1L, 625);
        verify(seatReservationService).reserveSeat(1L, 25);
    }

    @Test
    void shouldRejectInfantsThatCauseTheTotalToExceed25() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(
                        1L,
                        new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 13),
                        new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 13)
                )
        );

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldRejectNullRequestEntry() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(1L, (TicketTypeRequest) null));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldRejectNullTicketType() {
        assertThrows(InvalidPurchaseException.class, () ->
                ticketService.purchaseTickets(
                        1L,
                        new TicketTypeRequest(null, 1)
                ));

        verifyNoInteractions(paymentService, seatReservationService);
    }

    @Test
    void shouldStoreTicketTypeAndQuantity() {
        TicketTypeRequest request =
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2);

        assertEquals(TicketTypeRequest.Type.ADULT, request.getTicketType());
        assertEquals(2, request.getNoOfTickets());
    }

    @Test
    void shouldSupportEachTicketType() {
        assertEquals(
                TicketTypeRequest.Type.ADULT,
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1).getTicketType());

        assertEquals(
                TicketTypeRequest.Type.CHILD,
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1).getTicketType());

        assertEquals(
                TicketTypeRequest.Type.INFANT,
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1).getTicketType());
    }
}

