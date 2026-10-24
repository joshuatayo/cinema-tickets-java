# Ticket Booking Service

## Overview

This project implements a ticket purchasing service in Java. It validates ticket purchase requests, calculates the total payment, and reserves seats through the provided payment and seat reservation services.

## Business Requirements

The service supports three ticket types:

| Ticket Type | Price | Seat Required |
|---|---:|---|
| Adult | £25 | Yes |
| Child | £15 | Yes |
| Infant | £0 | No |

The following rules apply:

- A maximum of **25 tickets** can be purchased in a single transaction.
- Adult tickets cost £25 each.
- Child tickets cost £15 each.
- Infant tickets are free and do not require a reserved seat.
- Child and infant tickets can only be purchased when at least one adult ticket is included.
- The account ID must be greater than zero.
- Ticket requests must not be null or empty.
- Ticket quantities must be greater than zero.
- Invalid requests must be rejected before payment or seat reservation is attempted.

## Implementation

The `TicketServiceImpl` class implements the existing `TicketService` interface.

Its main responsibilities are:

1. Validate the account ID and ticket requests.
2. Calculate the total number of tickets and enforce the purchase limit.
3. Calculate the total payment.
4. Calculate the number of seats to reserve, excluding infants.
5. Submit payment through `TicketPaymentService`.
6. Reserve seats through `SeatReservationService`.

The implementation uses constructor injection for the payment and seat reservation services, allowing them to be mocked during unit testing.

### Calculation Examples

**Example 1: Mixed purchase**

- 2 adult tickets: £50
- 2 child tickets: £30
- 2 infant tickets: £0
- **Total payment: £80**
- **Seats to reserve: 4**

**Example 2: Adult and infant**

- 1 adult ticket: £25
- 1 infant ticket: £0
- **Total payment: £25**
- **Seats to reserve: 1**

## Validation and Error Handling

The service throws `InvalidPurchaseException` when a purchase violates the business rules, including:

- Invalid account IDs
- Null or empty ticket requests
- Null request entries or ticket types
- Zero or negative ticket quantities
- Purchases exceeding 25 tickets
- Child or infant tickets purchased without an adult ticket

Validation takes place before external service calls, preventing invalid purchases from triggering payment or seat reservation.

## Testing

The unit tests use **JUnit 5** and **Mockito** to verify business rules and interactions with the payment and seat reservation services.

The test suite covers:

- Correct payment and seat calculations
- Infant tickets being free and requiring no seats
- Purchases without adult tickets
- The 25-ticket purchase limit
- Invalid account IDs and ticket quantities
- Null and empty requests
- Boundary cases and integer-overflow-safe ticket limit validation
- Verification that payment and seat reservation are called with the expected arguments

## Running the Tests

Run the unit tests using the project's build tool.

### Maven

```bash
mvn test
```

Use the command matching the build tool configured in the project.

## Assumptions

- All positive account IDs represent valid accounts.
- Sufficient funds are available for valid purchases.
- Payment succeeds when requested.
- Seat reservation succeeds when requested.
- The supplied `TicketService` interface and `thirdparty.*` packages are not modified.

## Design Considerations

- **Single responsibility:** Validation, ticket counting, and calculations are separated into private methods.
- **Dependency injection:** External services are injected through the constructor.
- **Testability:** Mockito allows service interactions to be verified without relying on real payment or booking systems.
- **Defensive validation:** Invalid requests are rejected before side effects occur.
- **Immutable request model:** `TicketTypeRequest` should remain immutable as required by the exercise.
