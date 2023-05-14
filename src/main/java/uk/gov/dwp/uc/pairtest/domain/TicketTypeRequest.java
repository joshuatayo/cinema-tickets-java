package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.HashMap;

import static java.lang.String.format;

/**
 * Immutable Object
 */

public class TicketTypeRequest {

    private int noOfTickets;
    private Type type;

    public TicketTypeRequest(Type type, int noOfTickets) {
        this.type = type;
        this.noOfTickets = noOfTickets;
    }

    public int getNoOfTickets() {
        return noOfTickets;
    }

    public Type getTicketType() {
        return type;
    }

    public enum Type {
        ADULT, CHILD , INFANT
    }

    public static int getTotalAmountToPay(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(t -> !t.getTicketType().equals(Type.INFANT))
                .mapToInt(t -> (int) (ticketPrice(t.getTicketType().toString()) * t.getNoOfTickets()))
                .sum();
    }

    public static int getNoOfSeatToReserve(TicketTypeRequest... ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(t -> !t.getTicketType().equals(Type.INFANT))
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    public static void noOfTicketPurchasedAtATime(TicketTypeRequest... ticketTypeRequests) {
        // sum all numbers of ticket purchased at a time
        int noOfTickets =  Arrays.stream(ticketTypeRequests)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();

        // check if numbers of purchased ticket at a time is > 20
        if(noOfTickets > 20) {
            throw new InvalidPurchaseException("Only a maximum of 20 tickets that can be purchased at a time");
        }
    }

    public static void adultExist(String value, TicketTypeRequest... ticketTypeRequests) {
        Arrays.stream(ticketTypeRequests)
                .forEach((t) -> {
                    if(!t.getTicketType().toString().equals(value)) {
                        throw new InvalidPurchaseException(
                                format("Child and Infant tickets cannot be purchased without purchasing an %s ticket",
                                        TicketTypeRequest.Type.ADULT)
                        );
                    }
                });
    }

    public static void validNoOfTicket(TicketTypeRequest... ticketTypeRequests) {
        Arrays.stream(ticketTypeRequests)
                .forEach((t) -> {
                    if(t.getNoOfTickets() <= 0) {
                        throw new InvalidPurchaseException(
                                format("Invalid number of ticket for %s", t.getTicketType())
                        );
                    }
                });
    }

    public static void validAccountId(Long accountId) {
        // check if accountId is <= 0
        if(accountId <= 0 ) {
            throw new InvalidPurchaseException("requested account not found");
        }
    }

    public static double ticketPrice(String ticketType) {
        // setting price to each Type using HashMap
        HashMap<String, Double> addPrice = new HashMap<>();

        // Mapping price values to Ticket Type
        addPrice.put(TicketTypeRequest.Type.INFANT.toString(),  0.0);
        addPrice.put(TicketTypeRequest.Type.CHILD.toString(),  10.0);
        addPrice.put(TicketTypeRequest.Type.ADULT.toString(),  20.0);

        // return ticket type price
        return addPrice.get(ticketType);
    }
}
