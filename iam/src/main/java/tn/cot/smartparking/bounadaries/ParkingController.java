package tn.cot.smartparking.bounadaries;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smartparking.services.ParkingService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Path("/parking")
public class ParkingController {

    @Inject
    private ParkingService parkingService;

    @GET
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableSpots() {
        var spots = parkingService.findAvailableSpots();
        return Response.ok(spots).build();
    }

    @POST
    @Path("/reserve")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reserveSpot(@QueryParam("spotId") String spotId, @QueryParam("userId") String userId,
                                @QueryParam("startTime") String startTimeStr, @QueryParam("endTime") String endTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
        var reservation = parkingService.reserveSpot(spotId, userId, startTime, endTime);
        if (reservation.isPresent()) {
            return Response.status(Response.Status.CREATED).entity(reservation.get()).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Unable to reserve spot").build();
        }
    }
}
