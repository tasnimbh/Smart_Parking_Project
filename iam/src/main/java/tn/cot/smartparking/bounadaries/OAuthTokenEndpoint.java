package tn.cot.smartparking.bounadaries;


import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tn.cot.smartparking.enums.Role;
import tn.cot.smartparking.security.JwtManager;
import tn.cot.smartparking.utils.Oauth2Pkce;
import java.util.Map;
import java.util.Set;


@Path("/oauth/token")
public class OAuthTokenEndpoint {

    @Inject
    JwtManager jwtManager;
    @Inject
    Oauth2Pkce oauth2Pkce;
    @GET
    public Response generateToken(@QueryParam("authorization_code") String authorizationCode,
                                  @QueryParam("code_verifier") String codeVerifier) {

        try {
            if (authorizationCode == null || codeVerifier == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"authorization_code or code_verifier missing\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            Map<String, Object> cred = oauth2Pkce.CheckChallenge(authorizationCode, codeVerifier);
            if (cred == null) {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity("{\"error\": \"Invalid authorization_code or code_verifier\"}")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }

            String tenantId = (String) cred.get("tenantId");
            String subject = (String) cred.get("subject");
            String approvedScopes = (String) cred.get("approvedScopes");
            String[] roles = (String[]) cred.get("roles");

            var token = jwtManager.generateToken(tenantId, subject, approvedScopes, roles);
            return Response
                    .ok(Json.createObjectBuilder()
                            .add("accessToken", token)
                            .add("tokenType", "Bearer")
                            .add("expiresIn", 1020)
                            .build())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

}
