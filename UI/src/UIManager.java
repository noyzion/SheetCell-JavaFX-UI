
import DTO.CellDTO;
import DTO.SheetDTO;
import logic.Logic;
import menu.Menu;
import sheet.coordinate.ParseException;
import xmlParse.XmlSheetLoader;

import java.io.*;

public class UIManager implements Menu {

    private Logic logic = new Logic();
    private final InputManager inputManager = new InputManager();
    private boolean isSheetLoaded = false;

    private void printMenu() {
        System.out.println(" ".repeat(MenuOption.getMaxDescriptionLength() / 2) + "Menu:\n" + "=".repeat(MenuOption.getMaxDescriptionLength() + 6));
        for (MenuOption option : MenuOption.values()) {
            System.out.println("(" + option.getValue() + ") " + option.getDescription());
        }
    }

    @Override
    public void displaySpreadsheet() {
        checkSheetLoaded();
        SheetDTO sheetDTO = logic.getLatestSheet();
        System.out.println(sheetDTO.toString());
    }

    @Override
    public void displaySingleCell() {
        checkSheetLoaded();
        String coordinate = inputManager.getCellCoordinate(
                logic.getLatestSheet().getRowSize(),
                logic.getLatestSheet().getColumnSize()
        );
        if (coordinate != null) {
                CellDTO cellDTO = logic.getLatestSheet().getCell(coordinate);
                if (cellDTO != null) {
                    System.out.println(cellDTO);
                } else {
                    System.out.println("Cell " + coordinate + " is empty");
                }
    }

    @Override
    public void updateSingleCell() {
        checkSheetLoaded();
        String cellID = inputManager.getCellCoordinate(
                logic.getLatestSheet().getRowSize(),
                logic.getLatestSheet().getColumnSize()
        );
        try {
            if (logic.getLatestSheet().getCell(cellID) == null || logic.getLatestSheet().getCell(cellID).getOriginalValue() == null)
                System.out.println("Cell at: " + cellID + " is empty");
            else {
                System.out.println("Cell at: " + cellID);
                System.out.println("Original value is: " + logic.getLatestSheet().getCell(cellID).getOriginalValue());
                System.out.println("Effective value is: " + logic.getLatestSheet().getCell(cellID).getEffectiveValue().getValue());
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        boolean validCalc = false;
        while (!validCalc) {
            try {
                String newOriginalValue = inputManager.getNewValueForCell();
                if (newOriginalValue.isBlank()) {
                    logic.setCellValue(cellID, null);

                } else {
                    logic.setCellValue(cellID, newOriginalValue);
                    System.out.println("Cell updated successfully.");
                }
                validCalc = true;
            } catch (Exception e) {
                System.out.println("Error updating cell: " + e.getMessage());
            }
        }
        displaySpreadsheet();
    }

    private void checkSheetLoaded() {
        if (!isSheetLoaded) {
            throw new IllegalStateException("No sheet loaded. Please load a sheet first.");
        }
    }

    public void start() {
        boolean exit = false;

        while (!exit) {
            printMenu();
            int choice = inputManager.getUserChoice(MenuOption.values().length);

            try {
                MenuOption option = MenuOption.fromValue(choice);

                switch (option) {
                    case READ_FILE -> getXmlFile();
                    case DISPLAY_SPREADSHEET -> displaySpreadsheet();
                    case DISPLAY_SINGLE_CELL -> displaySingleCell();
                    case UPDATE_SINGLE_CELL -> updateSingleCell();
                    case DISPLAY_VERSIONS -> displayVersions();
                    case SAVE_SYSTEM_STATE -> saveSystemState();
                    case LOAD_SYSTEM_STATE -> loadSystemState();
                    case EXIT -> exit = true;
                }

                if (!exit) {
                    System.out.println("Returning to menu...\n");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid choice. Please try again.");
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Exiting program. Goodbye!");
    }

    @Override
    public void getXmlFile() {
        boolean validInput = false;

        while (!validInput) {
            String filePath = inputManager.getFilePath("Please enter the full path to the XML file: ");

            try {
                logic.addSheet(XmlSheetLoader.fromXmlFileToObject(filePath));
                isSheetLoaded = true;
                System.out.println("Sheet loaded successfully.");
                validInput = true;
            } catch (Exception e) {
                System.out.print("Error loading file: " + e.getMessage() + "\n Please try again.");
                System.out.println();
            }
        }
    }

    private void displayVersionsTable(int versions) {
        System.out.println("Version Number | Number of Changes");
        System.out.println("-------------------------------");
        for (int i = 0; i < versions; i++) {
            int versionNumber = i + 1;
            int changes = logic.getSheetByVersion(i + 1).getCounterChangedCells();
            System.out.printf("%-15d | %-17d%n", versionNumber, changes);
        }
    }

    @Override
    public void displayVersions() {
        checkSheetLoaded();
        displayVersionsTable(logic.getLatestSheet().getVersion());
        int version = inputManager.getVersionNumber(logic.getLatestSheet().getVersion());
        System.out.println("Displaying the state of the spreadsheet for version " + version + ":");
        System.out.println(logic.getSheetByVersion(version).toString());
    }

    @Override
    public void saveSystemState() {
        checkSheetLoaded();
        String filePath = inputManager.getFilePath("Please enter the full file path to save the system state (without extension): ");
        String fileName = inputManager.getFilePath("Please enter the desired file name to save the system state: ");

        while (!inputManager.isValidFileName(fileName)) {
            System.out.println("Invalid file name. Please avoid using special characters like \\ / : * ? \" < > | and try again.");
            fileName = inputManager.getFilePath("Please try again: ");
        }

        String fullPath = filePath + File.separator + fileName + ".dat";

        if (!saveSystemStateHelper(fullPath)) {
            System.out.println("Please try again: ");
            saveSystemState();
        }
        System.out.println("System state saved successfully to " + fullPath);
    }

    @Override
    public void loadSystemState() {
        String filePath = inputManager.getFilePath("Please enter the full file path to load the system state (without extension): ");
        String fullPath = filePath + ".dat";

        if (!loadSystemStateHelper(fullPath)) {
            System.out.println("Please try again: ");
            loadSystemState();
        }
        System.out.println("System state loaded successfully from " + fullPath);
    }

    private boolean saveSystemStateHelper(String filePath) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(logic);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving system state: " + e.getMessage());
            return false;
        }
    }

    private boolean loadSystemStateHelper(String filePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath))) {
            logic = (Logic) in.readObject();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading system state: " + e.getMessage());
            return false;
        }
    }
}
