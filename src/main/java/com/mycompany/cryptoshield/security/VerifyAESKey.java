/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package encrypt;

/**
 *
 * @author Abdur
 */
import java.util.Base64;
import java.util.Scanner;

public class VerifyAESKey {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter the Base64-encoded AES key: ");
        String encodedKey = scanner.nextLine();
        
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

        if (decodedKey.length == 32) {
            System.out.println("The key is a valid 256-bit AES key.");
        } else {
            System.out.println("The key is not a valid 256-bit AES key. It should be 32 bytes in length.");
        }
        
        scanner.close();
    }
}

