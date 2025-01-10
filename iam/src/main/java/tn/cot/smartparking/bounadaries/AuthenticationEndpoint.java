package tn.cot.smartparking.bounadaries;


import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smartparking.entities.Identity;
import tn.cot.smartparking.services.IdentityServices;
import tn.cot.smartparking.utils.Oauth2Pkce;



@Path("/authenticate")
public class AuthenticationEndpoint {


    @Inject
    IdentityServices identityServices;
    @Inject
    Oauth2Pkce oauth2Pkce;

    @POST
    public Response authenticate(@FormParam("username") String username, @FormParam("password") String password, @CookieParam("XSS-Cookie") Cookie xssCookie ) {
        if(username == null || password == null || xssCookie == null){
            return Response.status(Response.Status.NOT_ACCEPTABLE)
                    .entity("{\"message\":\"Invalid Credentials!\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
        try {
            Identity attemptedIdentity = identityServices.authenticateIdentity(username,password);
            var state = xssCookie.getValue();
            return Response.status(Response.Status.FOUND)
                    .entity("{\"AuthorizationCode\":\"" + oauth2Pkce.generateAuthorizationCode(state, attemptedIdentity) + "\", \"state\":\"" + state + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (EJBException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\""+e.getMessage()+"\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}