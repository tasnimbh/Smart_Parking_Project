package tn.cot.smartparking.services;

import jakarta.ejb.EJBException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import org.apache.commons.lang3.tuple.Pair;
import tn.cot.smartparking.entities.Identity;
import tn.cot.smartparking.repositories.IdentityRepository;
import tn.cot.smartparking.utils.Argon2Utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class IdentityServices {

    @Inject
    IdentityRepository identityRepository;
    @Inject
    Argon2Utils argon2Utils;
    @Inject
    EmailService emailService;

    private final Map<String, Pair<String, LocalDateTime>> activationCodes = new HashMap<>();

    public void registerUser(@Valid Identity identity)  {

        if(identityRepository.findByUsername(identity.getUsername()).isPresent()){
            throw new EJBException("Identity with username " + identity.getUsername() + " already exists");
        }
        identity.setCreationDate(LocalDateTime.now().toLocalDate().toString());
        identity.setRoles(2L);
        identity.setScopes("resource:read,resource:write");
        identity.hashPassword(identity.getPassword(), argon2Utils);
        identityRepository.save(identity);
        String activationCode = GenerateActivationCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5); // Set expiration time
        activationCodes.put(activationCode, Pair.of(identity.getEmail(),expirationTime));
        String message= "Welcome to Smart Hydro the best solution to monitor your Hydroponic Farm !! . Activate your account now and start your journey with Us .Here is your Activation Code: " + activationCode;
        emailService.sendEmail("smartparking.services@gmail.com", identity.getEmail(), "Activate Account", message);
    }

    public void activateUser(String code) {
        if (activationCodes.containsKey(code)) {
            Pair<String, LocalDateTime> codeDetails = activationCodes.get(code);
            LocalDateTime expirationTime = codeDetails.getRight();
            if (LocalDateTime.now().isAfter(expirationTime)) {
                activationCodes.remove(code);
                throw new EJBException("Activation code expired");
            }
            String email = codeDetails.getLeft();
            Identity identity = identityRepository.findByEmail(email).orElse(null);
            if (identity !=null) {
                identity.setAccountActivated(true);
                identityRepository.save(identity);
                activationCodes.remove(code);
            } else {
                throw new EJBException("Identity not found.");
            }
        }else {
            throw new EJBException("Activation code not found");
        }
    }

    public Identity authenticateIdentity(String username, String password) {
        final Identity identity = identityRepository.findByUsername(username).orElseThrow(() -> new EJBException("Identity not found"));
        if(identity != null && argon2Utils.check(identity.getPassword(), password.toCharArray()) && identity.getAccountActivated()==true){
            return identity;
        }
        throw new EJBException("Failed log in with username: " + username + " [Unknown username or wrong password]");
    }

    private String GenerateActivationCode() {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

}







