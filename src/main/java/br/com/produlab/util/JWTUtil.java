package br.com.produlab.util;


import br.com.produlab.entity.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.JWSHeader.Builder;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.eclipse.microprofile.jwt.Claims;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Calendar;

public class JWTUtil {
    public static String generateTokenString(User user) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, ParseException, JOSEException {
        JSONObject jwtContent = new JSONObject();


        long currentTimeInSecs = (Calendar.getInstance().getTimeInMillis()/1000);
        long exp = currentTimeInSecs + 3600L;
        jwtContent.put(Claims.iat.name(), currentTimeInSecs);
        jwtContent.put(Claims.auth_time.name(), currentTimeInSecs);

        jwtContent.put(Claims.iat.name(), currentTimeInSecs);
        jwtContent.put(Claims.auth_time.name(), currentTimeInSecs);
        jwtContent.put(Claims.exp.name(), exp);

        jwtContent.put(Claims.sub.name(),user.id.toString());
        jwtContent.put(Claims.iss.name(),"br.com.prodlab");
        jwtContent.put(Claims.email.name(),user.email);


        JSONArray groups = new JSONArray();
        groups.add(user.userProfile.name().toLowerCase());

        jwtContent.put(Claims.groups.name(),groups);
        PrivateKey pk = TokenUtils.readPrivateKey("/private.pem");

        JWSSigner signer = new RSASSASigner(pk);
        JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwtContent);
        JWSAlgorithm alg = JWSAlgorithm.RS256;


        JWSHeader jwtHeader = (new Builder(alg)).keyID("/private.pem").type(JOSEObjectType.JWT).build();
        SignedJWT signedJWT = new SignedJWT(jwtHeader, claimsSet);
        signedJWT.sign((JWSSigner) signer);
        String jwt = signedJWT.serialize();
        return jwt;
    }
}
