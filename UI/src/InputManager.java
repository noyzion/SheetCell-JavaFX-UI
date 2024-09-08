
import sheet.coordinate.CoordinateFactory;
import sheet.coordinate.ParseException;

import java.util.Scanner;

public class InputManager {

    private final Scanner scanner = new Scanner(System.in);

    public int getUserChoice(int range) {
        int userChoice;

        while (true) {
            try {
                System.out.println("Please enter a number between 1 and " + range + ":");
                String userInput = scanner.nextLine();
                userChoice = Integer.parseInt(userInput);

                if (userChoice >= 1 && userChoice <= range) {
                    break;
                } else {
                    System.out.println("Input out of range. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        return userChoice;
    }

  

    public String getNewValueForCell() {
        System.out.print("Enter the new value: ");
        return scanner.nextLine();
    }

    public String getFilePath(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int getVersionNumber(int numVersions) {
        while (true) {
            System.out.print("Please enter the version number to view: ");
            try {
                int versionNumber = Integer.parseInt(scanner.nextLine());
                if (versionNumber >= 1 && versionNumber <= numVersions) {
                    return versionNumber;
                } else {
                    System.out.printf("Invalid version number. Please enter a number between 1 and %d.%n", numVersions);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    public boolean isValidFileName(String fileName) {
        String invalidChars = "[\\\\/:*?\"<>|]";
        return !fileName.matches(".*" + invalidChars + ".*");
    }
}