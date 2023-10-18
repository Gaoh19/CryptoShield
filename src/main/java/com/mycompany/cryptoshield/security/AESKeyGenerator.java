/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cryptoshield.security;

/**
 *
 * @author Abdur
 */

import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESKeyGenerator {
    public static SecretKey generateAES256Key() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, new SecureRandom());
        return keyGen.generateKey();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        SecretKey aesKey = generateAES256Key();
        byte[] keyBytes = aesKey.getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Generated AES-256 Key (Base64): " + base64Key);
    }
}





