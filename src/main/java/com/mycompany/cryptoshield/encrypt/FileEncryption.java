/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Abdur
 */
package com.mycompany.cryptoshield.encrypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;

public class FileEncryption {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        Properties config = loadConfigProperties();

        String keyFilePath = config.getProperty("keyLocation");
        String inputFile = config.getProperty("fileLocation");

        // Read the Base64-encoded key from the file
        String base64Key = new String(readKeyFromFile(keyFilePath), StandardCharsets.UTF_8);

        // Decode the Base64 string into bytes
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);

        if (keyBytes == null || keyBytes.length != 32) { // For AES-256, key length should be 32 bytes
            System.err.println("Error: The AES key is invalid.");
            System.err.println("keyFilePath: " + keyFilePath);
            System.err.println("keyBytes: " + Arrays.toString(keyBytes));
            System.exit(1);
        }

        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

        // Get the name for the encrypted file from the user
        String encryptedFileName = getInputForEncryptedFileName(inputFile);

        String outputFilePath;
        outputFilePath = "src/main/resources/files/encryptFiles/" + encryptedFileName;

        try {
            encryptFile(inputFile, outputFilePath, aesKey);
            System.out.println("Encryption completed. Encrypted file stored in: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Encryption failed: " + e.getMessage());
        }
    }

    private static Properties loadConfigProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = FileEncryption.class.getResourceAsStream("/config/config.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration properties: " + e.getMessage());
        }
        return properties;
    }

    private static String getInputForEncryptedFileName(String inputFile) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name for the encrypted file (ends with .enc): ");
        String fileName = scanner.nextLine();
        scanner.close();

        // Ensure the file name ends with .enc extension
        if (!fileName.endsWith(".enc")) {
            fileName += ".enc";
        }

        return fileName;
    }

    private static byte[] readKeyFromFile(String keyFilePath) {
        try {
            Path path = Path.of(keyFilePath);
            if (!Files.exists(path)) {
                throw new FileNotFoundException("Key file not found: " + keyFilePath);
            }
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read the key file: " + e.getMessage());
        }
    }

    private static void encryptFile(String inputFile, String outputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = generateRandomIV();

        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        try (FileInputStream in = new FileInputStream(inputFile);
             FileOutputStream out = new FileOutputStream(outputFile)) {
            byte[] input = new byte[64];
            int bytesRead;

            while ((bytesRead = in.read(input)) != -1) {
                byte[] output = cipher.update(input, 0, bytesRead);
                if (output != null) {
                    out.write(output);
                }
            }

            byte[] output = cipher.doFinal();
            if (output != null) {
                out.write(output);
            }
        }

        // Save the IV for later use (e.g., decryption)
        saveIVToFile(outputFile + ".iv", iv);
    }

    private static byte[] generateRandomIV() {
        byte[] iv = new byte[16]; // IV for AES/CBC mode should be 16 bytes long
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    private static void saveIVToFile(String ivFilePath, byte[] iv) {
        try (FileOutputStream ivFile = new FileOutputStream(ivFilePath)) {
            ivFile.write(iv);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save IV to file: " + e.getMessage());
        }
    }
}