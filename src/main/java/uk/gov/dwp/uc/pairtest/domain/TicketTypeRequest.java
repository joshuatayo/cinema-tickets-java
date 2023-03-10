package uk.gov.dwp.uc.pairtest.domain;

import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

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
        int totalAmount = 0;
        for (TicketTypeRequest t : ticketTypeRequests) {
            // check if Type is not INFANT
            if(!t.getTicketType().equals(TicketTypeRequest.Type.INFANT)) {
                int ticketPrice  = getTicketPrice(t.getTicketType().toString());
                int amount = ticketPrice * t.getNoOfTickets();

                totalAmount += amount;
            }
        }
        return totalAmount;
    }

    public static int getNoOfSeatToReserve(TicketTypeRequest... ticketTypeRequests) {
        int noOfReserveSeat = 0;
        for (TicketTypeRequest t : ticketTypeRequests) {
            // check if Type is not INFANT
            if(!t.getTicketType().equals(TicketTypeRequest.Type.INFANT)) {
                noOfReserveSeat += t.getNoOfTickets();
            }
        }
        return noOfReserveSeat;
    }

    public static void validAccountId(Long accountId) {
        // check if accountId is <= 0
        if(accountId <= 0) {
            throw new InvalidPurchaseException("requested account not found with id " + accountId);
        }
    }

    public static void adultExist(String value, TicketTypeRequest... ticketTypeRequests) {
        // return true if Adult ticket type exist in purchased request.
        boolean isExist = Arrays.stream(ticketTypeRequests)
                .anyMatch(str -> Objects.equals(str.getTicketType().toString(), value));

        // check isExist is false
        if(!isExist) {
            throw new InvalidPurchaseException("Child and Infant tickets cannot be purchased without " +
                    "purchasing an "+ TicketTypeRequest.Type.ADULT.toString() +" ticket");
        }
    }

    public static void checkNoOfTicketPurchasedAtATime(TicketTypeRequest... ticketTypeRequests) {
        // sum all numbers of ticket purchased at a time
        int noOfTickets =  Arrays.stream(ticketTypeRequests)
                .mapToInt(t -> t.getNoOfTickets())
                .sum();

        // check if numbers of purchased ticket at a time is > 20
        if(noOfTickets > 20) {
            throw new InvalidPurchaseException("Only a maximum of 20 tickets that can be purchased at a time");
        }
    }

    public static int getTicketPrice(String ticketType) {
        // setting price to each Type using HashMap
        HashMap<String, Integer> addPrice = new HashMap<String, Integer>();

        // Mapping price values to Ticket Type
        addPrice.put(TicketTypeRequest.Type.INFANT.toString(),  0);
        addPrice.put(TicketTypeRequest.Type.CHILD.toString(),  10);
        addPrice.put(TicketTypeRequest.Type.ADULT.toString(),  20);

        // get ticket type price
        int price = addPrice.get(ticketType);

        // return ticket type price
        return price;
    }
}
