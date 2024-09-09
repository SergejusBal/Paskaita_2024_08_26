package Renginys.Paskaita_2024_08_26.Services;

import Renginys.Paskaita_2024_08_26.Models.User;
import Renginys.Paskaita_2024_08_26.Repositories.UserRepository;
import Renginys.Paskaita_2024_08_26.Security.JwtDecoder;
import Renginys.Paskaita_2024_08_26.Security.JwtGenerator;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public String registerUser(User user){
        return userRepository.registerUser(user);
    }

    public HashMap<String,String> login(User user){

        HashMap<String,String> response = userRepository.login(user);
        if(response.get("response").equals("user authorize")){
            response.put("JWT", JwtGenerator.generateJwt(Integer.parseInt(response.get("id"))));
        }
        return response;
    }

    public boolean userAutoLogIn(String authorizationHeader){
        return autorize(authorizationHeader);
    }

    private boolean autorize(String authorizationHeader){

        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            return false;
        }
        try {
            JwtDecoder.decodeJwt(authorizationHeader);
        } catch (JwtException e){
            return false;
        }
        return true;
    }

    private int userIDFromJWT(String authorizationHeader){
        return Integer.parseInt((String) JwtDecoder.decodeJwt(authorizationHeader).get("UserId"));
    }



}
