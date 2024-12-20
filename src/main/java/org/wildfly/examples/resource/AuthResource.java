package org.wildfly.examples.resource;

import org.wildfly.examples.service.UserService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
public class AuthResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signUp(UserDTO userDTO) {
        boolean success = userService.registerUser(userDTO.getEmail(), userDTO.getPassword());
        return success ? Response.ok("{\"message\": \"User registered successfully\"}").build()
                : Response.status(Response.Status.CONFLICT)
                .entity("{\"message\": \"Email already exists\"}").build();
    }

    @POST
    @Path("/signin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response signIn(UserDTO userDTO) {
        boolean authenticated = userService.authenticateUser(userDTO.getEmail(), userDTO.getPassword());
        return authenticated ? Response.ok("{\"message\": \"Login successful\"}").build()
                : Response.status(Response.Status.UNAUTHORIZED)
                .entity("{\"message\": \"Invalid credentials\"}").build();
    }
//    @GET
//    @Path("/test")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String testEndpoint() {
//        return "AuthResource is working!";
//    }
}
