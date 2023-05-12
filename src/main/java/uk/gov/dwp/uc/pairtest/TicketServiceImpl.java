package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        // return true if accountId is greater than zero
        TicketTypeRequest.validAccountId(accountId);

        // return true if number of tickets purchase is greater than zero
        TicketTypeRequest.validNoOfTicket(ticketTypeRequests);

        // check if adult ticket is purchased
        TicketTypeRequest.adultExist(TicketTypeRequest.Type.ADULT.toString(), ticketTypeRequests);

        // check the number of ticket purchased at a time
        TicketTypeRequest.noOfTicketPurchasedAtATime(ticketTypeRequests);

        // make payment
        makePayment(accountId, TicketTypeRequest.getTotalAmountToPay(ticketTypeRequests));

        // reserve Seat
        reserveSeat(accountId, TicketTypeRequest.getNoOfSeatToReserve(ticketTypeRequests));
    }

    private void makePayment(Long accountId, int totalAmountToPay) {
        TicketPaymentServiceImpl ticketPaymentServiceImpl = new TicketPaymentServiceImpl();
        ticketPaymentServiceImpl.makePayment(accountId, totalAmountToPay);
    }

    private void reserveSeat(Long accountId, int noOfReserveSeat) {
        SeatReservationServiceImpl seatReservationServiceImpl =  new SeatReservationServiceImpl();
        seatReservationServiceImpl.reserveSeat(accountId, noOfReserveSeat);
    }


}
