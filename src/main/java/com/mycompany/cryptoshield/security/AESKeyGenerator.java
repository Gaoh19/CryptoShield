/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package encrypt;

/**
 *
 * @author Abdur
 */

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AESKeyGenerator {
    public static SecretKey generateAES256Key() {
        byte[] keyBytes = new byte[32]; // 256 bits (32 bytes)

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(keyBytes);

        return new SecretKeySpec(keyBytes, "AES");
    }

    public static void main(String[] args) {
        SecretKey aesKey = generateAES256Key();
        byte[] keyBytes = aesKey.getEncoded();

        // Display the generated key
        System.out.println("Generated AES-256 Key (Base64): " + Base64.getEncoder().encodeToString(keyBytes));
    }
}






