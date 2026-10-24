package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Objects;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private static final int MAX_TICKETS_PER_PURCHASE = 25;
    private static final int INFANT_PRICE = 0;
    private static final int CHILD_PRICE = 15;
    private static final int ADULT_PRICE = 25;

    private final TicketPaymentService paymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl() {
        this(new TicketPaymentServiceImpl(), new SeatReservationServiceImpl());
    }

    public TicketServiceImpl(
            TicketPaymentService paymentService,
            SeatReservationService seatReservationService) {
        this.paymentService = Objects.requireNonNull(paymentService, "paymentService must not be null");
        this.seatReservationService = Objects.requireNonNull(
                seatReservationService, "seatReservationService must not be null");
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        validateAccount(accountId);
        PurchaseSummary summary = validateAndSummarise(ticketTypeRequests);

        paymentService.makePayment(accountId, summary.totalPrice());
        seatReservationService.reserveSeat(accountId, summary.seatsToReserve());
    }

    private static void validateAccount(Long accountId) {
        if (accountId == null || accountId <= 0) {
            throw new InvalidPurchaseException("Account ID must be greater than zero");
        }
    }

    private static PurchaseSummary validateAndSummarise(TicketTypeRequest[] requests) {

        validateRequests(requests);

        TicketCounts counts = countTickets(requests);

        validateTicketCounts(counts);

        return createPurchaseSummary(counts);
    }

    private static void validateRequests(TicketTypeRequest[] requests) {
        if (requests == null || requests.length == 0) {
            throw new InvalidPurchaseException(
                    "At least one ticket request is required.");
        }

        for (TicketTypeRequest request : requests) {
            validateRequest(request);
        }
    }

    private static void validateRequest(TicketTypeRequest request) {
        if (request == null) {
            throw new InvalidPurchaseException(
                    "Ticket requests must not contain null entries.");
        }

        if (request.getNoOfTickets() <= 0) {
            throw new InvalidPurchaseException(
                    "Ticket quantity must be greater than zero.");
        }

        if (request.getTicketType() == null) {
            throw new InvalidPurchaseException(
                    "Ticket type must not be null.");
        }
    }

    private static TicketCounts countTickets(TicketTypeRequest[] requests) {
        int adults = 0;
        int children = 0;
        int infants = 0;
        int totalTickets = 0;

        for (TicketTypeRequest request : requests) {
            int quantity = request.getNoOfTickets();

            if (quantity > MAX_TICKETS_PER_PURCHASE - totalTickets) {
                throw new InvalidPurchaseException(
                        "A maximum of " + MAX_TICKETS_PER_PURCHASE + " tickets may be purchased at a time.");
            }
            totalTickets += quantity;

            switch (request.getTicketType()) {
                case ADULT -> adults += quantity;
                case CHILD -> children += quantity;
                case INFANT -> infants += quantity;
                default -> throw new InvalidPurchaseException(
                        "Unsupported ticket type: " + request.getTicketType());
            }
        }

        return new TicketCounts(adults, children, infants);
    }

    private static void validateTicketCounts(TicketCounts counts) {
        if ((counts.children() > 0 || counts.infants() > 0)
                && counts.adults() == 0) {
            throw new InvalidPurchaseException(
                    "Child and infant tickets require at least one adult ticket.");
        }

        if (counts.infants() > counts.adults()) {
            throw new InvalidPurchaseException(
                    "Each infant must be accompanied by an adult for a lap seat.");
        }
    }

    private static PurchaseSummary createPurchaseSummary(
            TicketCounts counts) {

        return new PurchaseSummary(
                getTotalPrice(counts),
                getTotalSeats(counts)
        );
    }

    private static int getTotalPrice(TicketCounts counts) {
        return counts.adults() * ADULT_PRICE
                + counts.children() * CHILD_PRICE
                + counts.infants() * INFANT_PRICE;
    }

    private static int getTotalSeats(TicketCounts counts) {
        return counts.adults() + counts.children();
    }

    private record PurchaseSummary(int totalPrice, int seatsToReserve) {}

    private record TicketCounts(int adults, int children, int infants) {}
}
