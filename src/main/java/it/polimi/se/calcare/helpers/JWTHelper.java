/*
 * Copyright (C) 2015 Germano Gabbianelli
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.polimi.se.calcare.helpers;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import it.polimi.se.calcare.entities.User;
import it.polimi.se.calcare.service.AuthREST;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author Germano Gabbianelli
 */
public class JWTHelper {

    protected static final String SECRET = "4553414d4544494d455244414a4156414d4d45524441";
    private static final JWTVerifier verifier = new JWTVerifier(SECRET);
    private static final JWTSigner signer = new JWTSigner(SECRET);

    public static String encode(Map<String, Object> map) {
        return signer.sign(map);
    }

    public static String encode(String key, Object value) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);
        return encode(map);
    }

    public static String encode(String key, User user) {
        return encode(key, user.getId());
    }

    public static Map<String, Object> decode(String token, int status) {
        try {
            return verifier.verify(token);
        } catch (NoSuchAlgorithmException | InvalidKeyException |
                IllegalStateException | IOException | SignatureException |
                JWTVerifyException ex) {
            Logger.getLogger(AuthREST.class.getName()).log(Level.SEVERE, null, ex);
            throw new WebApplicationException(status);
        }
    }

    public static Map<String, Object> decode(String token) {
        return decode(token, 400);
    }

    public static <T> T decode(String token, String key, int status) {
        Map<String, Object> payload = decode(token, status);
        try {
            return (T) payload.get(key);
        } catch (ClassCastException | NullPointerException ex) {
            throw new WebApplicationException(ex, 400);
        }
    }

    public static <T> T decode(String token, String key) {
        return decode(token, key, 400);
    }

}
