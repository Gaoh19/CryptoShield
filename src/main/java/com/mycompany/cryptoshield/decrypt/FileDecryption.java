/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Abdur
 */

package com.mycompany.cryptoshield.decrypt;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;
import java.util.Scanner;

public class FileDecryption {
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());

        String encryptedFilePath = getInputForEncryptedFile();
        String ivFilePath = getInputForIVFile();
        String decryptedFileName = getDecryptedFilePath();

        try {
            decryptFile(encryptedFilePath, ivFilePath, decryptedFileName);
            System.out.println("Decryption completed. Decrypted file stored in: " + decryptedFileName);
        } catch (Exception e) {
            System.err.println("Decryption failed: " + e.getMessage());
        }
    }

    private static String getInputForEncryptedFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the encrypted file: ");
        return scanner.nextLine();
    }

    private static String getInputForIVFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path to the IV file: ");
        return scanner.nextLine();
    }

    private static String getDecryptedFilePath() {
        String decryptedDirectory = "src/main/resources/files/decryptFiles/";
        File directory = new File(decryptedDirectory);
        directory.mkdirs(); // Ensure the directory exists

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name for the decrypted file (ends with .dec): ");
        String fileName = scanner.nextLine();
        scanner.close();

        if (!fileName.endsWith(".dec")) {
            fileName += ".dec";
        }

        return decryptedDirectory + fileName;
    }

    private static void decryptFile(String encryptedFilePath, String ivFilePath, String decryptedFilePath) throws Exception {
        byte[] iv = readIVFromFile(ivFilePath);
        byte[] keyBytes = getKeyFromFile("src/main/resources/files/key.txt");
        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(iv));

        try (FileInputStream in = new FileInputStream(encryptedFilePath);
             FileOutputStream out = new FileOutputStream(decryptedFilePath)) {
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
    }

    private static byte[] readIVFromFile(String ivFilePath) throws IOException {
        try (FileInputStream ivFile = new FileInputStream(ivFilePath)) {
            byte[] iv = new byte[16];
            ivFile.read(iv);
            return iv;
        }
    }

    private static byte[] getKeyFromFile(String keyFilePath) throws IOException {
        try (FileInputStream keyFile = new FileInputStream(keyFilePath)) {
            ByteArrayOutputStream keyBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = keyFile.read(buffer)) != -1) {
                keyBuffer.write(buffer, 0, bytesRead);
            }
            return Base64.getDecoder().decode(keyBuffer.toByteArray());
        }
    }

}
