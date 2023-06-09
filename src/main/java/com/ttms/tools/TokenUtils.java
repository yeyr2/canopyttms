package com.ttms.tools;

import com.ttms.pojo.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtils {

    //设置过期时间
    private static final long EXPIRE_DATE= 6*60*60*1000;
    //普通用户token秘钥
    private static final String TOKEN_SECRET = "yeyr220230210TianMu";
    // 管理员token密钥
    private static final String ADMIN_TOKEN_SECRET = "yeyr2GETTIMETianMuADMIN";

    /**
     *
     * @param user 用户信息
     * @return
     */
    public static String getToken (User user){

        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis()+EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(user.getAdmin() ? ADMIN_TOKEN_SECRET : TOKEN_SECRET);
            //设置头部信息
            Map<String,Object> header = new HashMap<>();
            header.put("typ","JWT");
            header.put("alg","HS256");
            //携带username，password信息，生成签名
            token = JWT.create()
                    .withHeader(header)
                    .withClaim("id",String.valueOf(user.getId()))
                    .withClaim("username",user.getUsername())
                    .withClaim("password",user.getPassword())
                    .withExpiresAt(date)
                    .sign(algorithm);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return token;
    }

    /**
     * @desc   验证token，通过返回true
     * @param token [token]需要校验的串
     * @param status   是否是管理员
     * @return
     */
    public static DecodedJWT verify(String token,boolean status){
        try {
            Algorithm algorithm = Algorithm.HMAC256(status ? ADMIN_TOKEN_SECRET : TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        }catch (Exception e){
            return  null;
        }
    }

    /**
     *
     * @param token
     * @return 0: admin , 1 : user ,2 : 未知token
     */
    public static Integer IsAdmin(String token) {
        try{
            Algorithm admin = Algorithm.HMAC256(ADMIN_TOKEN_SECRET);
            JWTVerifier adminVerifier = JWT.require(admin).build();
            adminVerifier.verify(token);
            return 0;
        }catch (Exception e) {
            try{
                Algorithm user = Algorithm.HMAC256(TOKEN_SECRET);
                JWTVerifier userVerifier = JWT.require(user).build();
                userVerifier.verify(token);
                return 1;
            }catch (Exception ex){
                return 2;
            }
        }
    }

    public static DecodedJWT NoLevelVerify(String token) {
        JWTVerifier verifier;
        Algorithm algorithm;
        switch (IsAdmin(token)) {
            case 0:
                algorithm = Algorithm.HMAC256(ADMIN_TOKEN_SECRET);
                verifier = JWT.require(algorithm).build();
                break;
            case 1:
                algorithm = Algorithm.HMAC256(TOKEN_SECRET);
                verifier = JWT.require(algorithm).build();
                break;
            default:
                return null;
        }

        return verifier.verify(token);
    }

    public static String getData(String token, String key) {
        try {
            // 私钥和加密算法
            Integer integer = IsAdmin(token);
            Algorithm algorithm = Algorithm.HMAC256(integer == 0 ? ADMIN_TOKEN_SECRET : TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            // 验证签名
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim(key).asString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Long decodeGetIdByToken(String token) {
        String id = getData(token, "id");
        if (id == null){
            return -1L;
        }
        return Long.valueOf(id);
    }

//    public static void main(String[] args) {
//        User user = new User();
//        user.setPassword("123");
//        user.setUsername("yeyr2");
//        user.setId(1L);
//        user.setAdmin(true);
//        String token = getToken(user);
//        System.out.println(token);
//        DecodedJWT b = NoLevelVerify(token);
//        System.out.println(b);
//        String data =
//        System.out.println(data);
//    }
}
