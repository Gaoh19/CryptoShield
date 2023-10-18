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
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Properties;
import java.util.Scanner;

public class FileEncryption {
    public static void main(String[] args) throws Exception {
        // Add the Bouncy Castle provider
        Security.addProvider(new BouncyCastleProvider());

        Properties config = loadConfigProperties();

        String keyFilePath = config.getProperty("keyLocation");
        String inputFile = config.getProperty("fileLocation");

        byte[] keyBytes = readKeyFromFile(keyFilePath);

        if (keyBytes == null || keyBytes.length != 32) {
            System.err.println("Error: The AES key is invalid.");
            System.exit(1);
        }

        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

        // Get the name for the encrypted file from the user
        String encryptedFileName = getInputForEncryptedFileName(inputFile);

        encryptFile(inputFile, encryptedFileName, aesKey);

        System.out.println("Encryption completed. Encrypted file stored in the same directory as the original file: " + encryptedFileName);
    }

    private static Properties loadConfigProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = FileEncryption.class.getResourceAsStream("/config/config.properties")) {
            properties.load(inputStream);
        }
        return properties;
    }

    private static String getInputForEncryptedFileName(String inputFile) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name for the encrypted file: ");
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
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void encryptFile(String inputFile, String outputFile, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = generateRandomIV();

        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        FileInputStream in = new FileInputStream(inputFile);
        FileOutputStream out = new FileOutputStream(outputFile);

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

        in.close();
        out.close();
    }

    private static byte[] generateRandomIV() {
        byte[] iv = new byte[16]; // IV for AES/CBC mode should be 16 bytes long
        new SecureRandom().nextBytes(iv);
        return iv;
    }
}
