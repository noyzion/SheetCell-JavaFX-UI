# Sheet Cell - Spreadsheet System

A Java project implementing a spreadsheet system with support for cells, versions, and cell dependencies.

## Project Structure

The project is divided into two main modules:

### Engine
The core module containing the business logic:
- **Cell** - Interface for a cell in the spreadsheet
- **CellImp** - Implementation of a spreadsheet cell
- **Sheet** - Interface for a spreadsheet
- **SheetImp** - Implementation of a spreadsheet

### UI
The user interface module:
- **Main** - Entry point of the application
- **UIManager** - User interface manager and menu handler

## Features

- **Cells**: Each cell contains an original value and an effective value
- **Versioning**: Support for tracking versions of the spreadsheet and cells
- **Dependencies**: Cells can be related to other cells (related cells) and affect other cells (affected cells)
- **Dynamic Spreadsheet**: Create a spreadsheet with custom size (rows and columns)

## Requirements

- Java JDK (version 8 or higher)
- Java development environment (optional - IntelliJ IDEA recommended)

## Usage

Currently, the program creates a 20x20 spreadsheet named "FirstSheet", sets a value for the cell at position (5,5), and displays:
- The cell details (position, values, version, related cells)
- The complete spreadsheet in table format

### Menu

The program supports a menu with the following options:
1. Read File
2. Display Spreadsheet
3. Display Single Cell
4. Update Single Cell
5. Display Versions
6. Exit

## Cell Structure

Each cell contains:
- `rowNum` - Row number
- `colNum` - Column number
- `originalValue` - Original value
- `effectiveValue` - Effective value
- `lastVersionUpdate` - Last version in which it was updated
- `relatedCells` - List of related cells
- `affectedCells` - List of affected cells

## Spreadsheet Structure

The spreadsheet contains:
- Spreadsheet name
- Version number
- Size (number of rows and columns)
- Two-dimensional array of cells


