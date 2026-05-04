/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.realcoderz.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 *
 * @author mohit
 */
@Service
public class CommonExcelData {

    private static final Logger logger = LoggerFactory.getLogger(CommonExcelData.class);

    public ResponseEntity<byte[]> excelData(List<LinkedCaseInsensitiveMap> excelData, String[] headerData, String[] rowData, String sheetName, String fileName) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        try {

            String excelFileName = fileName.concat(".xlsx");
            List<LinkedCaseInsensitiveMap> allData = excelData;
            if (!allData.isEmpty()) {
                Sheet sheet = workbook.createSheet(sheetName);
                // Create header row
                Row headerRow = sheet.createRow(0);
                for (int i = 0; i < headerData.length; i++) {
                    headerRow.createCell(i).setCellValue(headerData[i]);
                }
                int rowNum = 1;
                for (LinkedCaseInsensitiveMap data : allData) {
                    // Your logic here using the 'data'
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rowNum - 1);
                    for (int i = 1; i <= rowData.length; i++) {

                        Object cellValue = data.get(rowData[i - 1]);
                        if (cellValue instanceof Number) {
                            // If the cell contains a number, set the numeric value directly
                            row.createCell(i).setCellValue(((Number) cellValue).doubleValue());
                        } else if (cellValue != null) {
                            // If the cell contains text, set the cell value as a string
                            row.createCell(i).setCellValue(cellValue.toString());
                        } else {
                            // If the cell value is null, set the cell value to an empty string
                            row.createCell(i).setCellValue("");
                        }

                    }

                }
                workbook.write(out);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(excelFileName).build()); //file name in .xlsx 
                return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Internal Server Error").getBytes());

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in Excell Data generation Class " + e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        } finally {
            try {
                workbook.close();
            } catch (IOException ex) {
                logger.error("Error in Excell Data generation Class " + ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.error("Error in Excell Data generation Class " + ex);
            }
        }
    }

//      public ResponseEntity<byte[]> excelDataForEsic(List<LinkedCaseInsensitiveMap> excelData, String[] headerData, String[] rowData, String sheetName, String fileName) {
//
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        Workbook workbook = new XSSFWorkbook();
//        try {
//            
//            
//            String excelFileName = fileName.concat(".xlsx");
//            List<LinkedCaseInsensitiveMap> allData = excelData;
//            if (!allData.isEmpty()) {
//                Sheet sheet = workbook.createSheet(sheetName);
//                int fixedCellWidth = 6000; // Set your desired fixed cell width here
//                // Create header row
//                Row headerRow = sheet.createRow(0);
//                // Create a cell style with wrap text property enabled
//                CellStyle cellStyle = workbook.createCellStyle();
//                cellStyle.setWrapText(true);
//                
//                for (int i = 0; i < headerData.length; i++) {
//                    // headerRow.createCell(i).setCellValue(headerData[i]);
//                    sheet.setColumnWidth(i, fixedCellWidth); // Set column width
//
//                    // Create the cell
//                    Cell cell = headerRow.createCell(i);
//                    cell.setCellValue(headerData[i]);
//                    cell.setCellStyle(cellStyle); // Apply cell style with wrap text property
//                }
//                int rowNum = 1;
//                for (LinkedCaseInsensitiveMap data : allData) {
//                    // Your logic here using the 'data'
//                    Row row = sheet.createRow(rowNum++);
////                    row.createCell(0).setCellValue(rowNum - 1);
//                    for (int i = 0; i < rowData.length; i++) {
//                        
//                     		Object cellValue = data.get(rowData[i]);
//                    if (cellValue instanceof Number) {
//                        // If the cell contains a number, set the numeric value directly
//                        row.createCell(i).setCellValue(((Number) cellValue).doubleValue());
//                    } else if (cellValue != null) {
//                        // If the cell contains text, set the cell value as a string
//                        row.createCell(i).setCellValue(cellValue.toString());
//                    } else {
//                        // If the cell value is null, set the cell value to an empty string
//                        row.createCell(i).setCellValue("-");
//                    }
//                    
//                    }
//
//                }
//                workbook.write(out);
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(excelFileName).build()); //file name in .xlsx 
//                return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
//            } else {
//                return ResponseEntity
//                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body(("Internal Server Error").getBytes());
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Exception in Excell Data generation Class " + e);
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(("Internal Server Error").getBytes());
//        } finally {
//            try {
//                workbook.close();
//            } catch (IOException ex) {
//                logger.error("Error in Excell Data generation Class " + ex);
//            }
//            try {
//                out.close();
//            } catch (IOException ex) {
//                logger.error("Error in Excell Data generation Class " + ex);
//            }
//        }
//    }
    public ResponseEntity<byte[]> excelDataForEsic(List<LinkedCaseInsensitiveMap> excelData, String[] headerData, String[] rowData, String sheetName, String fileName) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();

        String[] headerDataSheet1 = {"Reason", "Code", "Note"};
        String[] rowDataSheet1 = {"reason", "code", "note"};

        try {

            String excelFileName = fileName.concat(".xlsx");
            List<LinkedCaseInsensitiveMap> allData = excelData;

            List<LinkedCaseInsensitiveMap> allData1 = this.getDummyData();

            if (!allData.isEmpty()) {
                // First sheet
                Sheet sheet = workbook.createSheet("sheet1");
                createSheetContent(workbook, sheet, headerData, rowData, allData);

                // Second sheet (Sheet1)
                Sheet sheet1 = workbook.createSheet("Instructions & Reason Codes");
                createSheetContent1(workbook, sheet1, headerDataSheet1, rowDataSheet1, allData1);

                workbook.write(out);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(excelFileName).build());
                return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Internal Server Error").getBytes());

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in Excel Data generation Class " + e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        } finally {
            try {
                workbook.close();
            } catch (IOException ex) {
                logger.error("Error in Excel Data generation Class " + ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.error("Error in Excel Data generation Class " + ex);
            }
        }
    }

    private void createSheetContent(Workbook workbook, Sheet sheet, String[] headerData, String[] rowData, List<LinkedCaseInsensitiveMap> allData) {
        int fixedCellWidth = 6000; // Set your desired fixed cell width here
        // Create header row
        Row headerRow = sheet.createRow(0);
        // Create a cell style with wrap text property enabled
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        for (int i = 0; i < headerData.length; i++) {
            sheet.setColumnWidth(i, fixedCellWidth); // Set column width

            // Create the cell
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerData[i]);
            cell.setCellStyle(cellStyle); // Apply cell style with wrap text property
        }

        int rowNum = 1;
        for (LinkedCaseInsensitiveMap data : allData) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.length; i++) {
                Object cellValue = data.get(rowData[i]);
                if (cellValue instanceof Number) {
                    row.createCell(i).setCellValue(((Number) cellValue).doubleValue());
                } else if (cellValue != null) {
                    row.createCell(i).setCellValue(cellValue.toString());
                } else {
                    row.createCell(i).setCellValue("-");
                }
            }
        }
    }

    private void createSheetContent1(Workbook workbook, Sheet sheet, String[] headerData, String[] rowData, List<LinkedCaseInsensitiveMap> allData) {
        int fixedCellWidth = 9000; // Set your desired fixed cell width here
        // Create header row
        Row headerRow = sheet.createRow(0);
        // Create a cell style with wrap text property enabled
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        // Insert instruction rows
        String[] instructions = {
            "",
            "Instructions to fill in the excel file",
            "1. Enter the IP number, IP name, No. of Days, Total Monthly Wages, Reason for 0 wages (If Wages ‘0’) & Last Working Day (only if employee has left service, Retired, Out of coverage, Expired, Non-Implemented area or Retrenchment. For other reasons, last working day must be left BLANK).",
            "2. Number of days must be a whole number. Fractions should be rounded up to the next higher whole number/integer.",
            "3. Excel sheet upload will lead to successful transaction only when all the Employees’ (who are currently mapped in the system) details "
            + " are entered perfectly in the excel sheet",
            "4. Reasons are to be assigned numeric code  and date has to be provided as mentioned in the table above",
            "5. Once  0 wages given and last working day is mentioned as in reason codes (2,3,4,5,10)  IP will be removed from the employer’s record. Subsequent months will not have this IP listed under the employer. Last working day should be mentioned only if 'Number of days wages paid/payable' is '0'.",
            "6. In case IP has worked for part of the month(i.e. atleast 1 day wage is paid/payable) and left in between of the month, then last working day shouldn’t be mentioned.",
            "7. Calculations – IP Contribution and Employer contribution calculation will be automatically done by the system",
            "8. Date  column format is  dd/mm/yyyy or dd-mm-yyyy.  Pad single digit dates with 0.  Eg:- 2/5/2010  or  2-May-2010 is NOT acceptable.  Correct format  is 02/05/2010  "
            + " or 02-05-2010",
            "9. Excel file should be saved in .xls format (Excel 97-2003)",
            "10. Note that all the column including date column should be in ‘Text’ format",
            "10a. To convert  all columns to text,",
            "a.  Select column A; Click Data in Menu Bar on top;  Select Text to Columns ; Click Next (keep default selection of Delimited);  Click"
            + " Next (keep default selection of Tab); Select  TEXT;  Click FINISH.  Excel 97 – 2003 as well have TEXT to COLUMN  conversion "
            + "facility",
            "b.  Repeat the above step for each of the 6 columns. (Columns A – F )",
            "10b.   Another method that can be used to text conversion is – copy the column with data and paste it in NOTEPAD.  Select the column (in "
            + " excel) and convert to text. Copy the data back from notepad to excel",
            "11.   If problem continues while upload,  download a fresh template by clicking 'Sample MC Excel Template'. Then copy the data area from "
            + " Step 8a.a – eg:  copy Cell A2 to F8 (if there is data in 8 rows); Paste it in cell A2 in the fresh template. Upload it",
            "",
            "Note :   Kindly turn  OFF   ‘POP UP BLOCKER’  if it is ON in your  browser.  Follow the steps given to turn off  pop up blocker ."
            + "           his  is required to  upload Monthly contribution,  view or print  Challan /  TIC after uploading the excel",
            "         1.Mozilla Firefox  3.5.11 :  From Menu Bar, select   Tools -> Options -> Content -> Uncheck (remove tick mark)"
            + "         ‘Block Popup Windows’.   Click OK",
            "          2.  IE 7.0  :     From Menu Bar, select  Tools -> Pop up Blocker -> Turn Off Pop up Blocker"
        };

        for (int i = 0; i < headerData.length; i++) {
            sheet.setColumnWidth(i, fixedCellWidth); // Set column width

            // Create the cell
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headerData[i]);
            cell.setCellStyle(cellStyle); // Apply cell style with wrap text property
        }

        int rowNum = 1;
        for (LinkedCaseInsensitiveMap data : allData) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < rowData.length; i++) {
                Object cellValue = data.get(rowData[i]);
                if (cellValue instanceof Number) {
                    row.createCell(i).setCellValue(((Number) cellValue).doubleValue());
                } else if (cellValue != null) {
                    row.createCell(i).setCellValue(cellValue.toString());
                } else {
                    row.createCell(i).setCellValue("-");
                }

            }
        }

        for (String instruction : instructions) {
            Row instructionRow = sheet.createRow(rowNum++);
            Cell instructionCell = instructionRow.createCell(0);
            instructionCell.setCellValue(instruction);
            instructionCell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, headerData.length - 1));

            // Set row height to accommodate wrapped text
            instructionRow.setHeightInPoints((instruction.length() / 90 + 1) * sheet.getDefaultRowHeightInPoints());

        }

    }

    public List<LinkedCaseInsensitiveMap> getDummyData() {

        List<LinkedCaseInsensitiveMap> allData = new ArrayList<>();

        LinkedCaseInsensitiveMap data1 = new LinkedCaseInsensitiveMap<>();
        data1.put("reason", "Without Reason");
        data1.put("code", 0);
        data1.put("note", "Leave last working day as blank");

        allData.add(data1);

        LinkedCaseInsensitiveMap data2 = new LinkedCaseInsensitiveMap<>();
        data2.put("reason", "On Leave");
        data2.put("code", 1);
        data2.put("note", "Leave last working day as blank");

        allData.add(data2);

        LinkedCaseInsensitiveMap data3 = new LinkedCaseInsensitiveMap<>();
        data3.put("reason", "Left Service");
        data3.put("code", 2);
        data3.put("note", "Please provide last working day (dd/mm/yyyy). IP will not appear from next wage period");

        allData.add(data3);

        LinkedCaseInsensitiveMap data4 = new LinkedCaseInsensitiveMap<>();
        data4.put("reason", "Retired");
        data4.put("code", 3);
        data4.put("note", "Please provide last working day (dd/mm/yyyy). IP will not appear from next wage period");

        allData.add(data4);

        LinkedCaseInsensitiveMap data5 = new LinkedCaseInsensitiveMap<>();
        data5.put("reason", "Out of Coverage");
        data5.put("code", 4);
        data5.put("note", "Please provide last working day (dd/mm/yyyy). IP will not appear from next contribution period. This option is valid only if Wage Period is April/October. In case any other month then IP will continue to appear in the list");

        allData.add(data5);

        LinkedCaseInsensitiveMap data6 = new LinkedCaseInsensitiveMap<>();
        data6.put("reason", "Expired");
        data6.put("code", 5);
        data6.put("note", "Please provide last working day (dd/mm/yyyy). IP will not appear from next wage period");

        allData.add(data6);

        LinkedCaseInsensitiveMap data7 = new LinkedCaseInsensitiveMap<>();
        data7.put("reason", "Non Implemented area");
        data7.put("code", 6);
        data7.put("note", "Please provide last working day (dd/mm/yyyy). ");

        allData.add(data7);

        LinkedCaseInsensitiveMap data8 = new LinkedCaseInsensitiveMap<>();
        data8.put("reason", "Compliance by Immediate Employer");
        data8.put("code", 7);
        data8.put("note", "Leave last working day as blank");

        allData.add(data8);

        LinkedCaseInsensitiveMap data9 = new LinkedCaseInsensitiveMap<>();
        data9.put("reason", "Suspension of work");
        data9.put("code", 8);
        data9.put("note", "Leave last working day as blank");

        allData.add(data9);

        LinkedCaseInsensitiveMap data10 = new LinkedCaseInsensitiveMap<>();
        data10.put("reason", "Strike/Lockout");
        data10.put("code", 9);
        data10.put("note", "Leave last working day as blank");

        allData.add(data10);

        LinkedCaseInsensitiveMap data11 = new LinkedCaseInsensitiveMap<>();
        data11.put("reason", "Retrenchment");
        data11.put("code", 10);
        data11.put("note", "Please provide last working day (dd/mm/yyyy). IP will not appear from next wage period");

        allData.add(data11);

        LinkedCaseInsensitiveMap data12 = new LinkedCaseInsensitiveMap<>();
        data12.put("reason", "No Work");
        data12.put("code", 11);
        data12.put("note", "Leave last working day as blank");

        allData.add(data12);

        LinkedCaseInsensitiveMap data13 = new LinkedCaseInsensitiveMap<>();
        data13.put("reason", "Doesnt Belong To This Employer");
        data13.put("code", 12);
        data13.put("note", "Leave last working day as blank");

        allData.add(data13);

        LinkedCaseInsensitiveMap data14 = new LinkedCaseInsensitiveMap<>();
        data14.put("reason", "Duplicate IP");
        data14.put("code", 13);
        data14.put("note", "Leave last working day as blank");

        allData.add(data12);

        return allData;

    }

    public ResponseEntity<byte[]> excelDataWithHeader(String mainHeader, List<LinkedCaseInsensitiveMap> excelData,
            String[] headerData, String[] rowData, String sheetName, String fileName, boolean isTotal) {

        // Set headless mode to prevent X11 font issues
        System.setProperty("java.awt.headless", "true");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        try {
            String excelFileName = fileName.concat(".xlsx");
            List<LinkedCaseInsensitiveMap> allData = excelData;

            if (!allData.isEmpty()) {
                Sheet sheet = workbook.createSheet(sheetName);
                CreationHelper creationHelper = workbook.getCreationHelper();

                // ---------- Styles ----------
                Font mainHeaderFont = workbook.createFont();
                mainHeaderFont.setBold(true);
                mainHeaderFont.setFontHeightInPoints((short) 14);

                CellStyle mainHeaderStyle = workbook.createCellStyle();
                mainHeaderStyle.setFont(mainHeaderFont);
                mainHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
                mainHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                Font colHeaderFont = workbook.createFont();
                colHeaderFont.setBold(true);

                CellStyle colHeaderStyle = workbook.createCellStyle();
                colHeaderStyle.setFont(colHeaderFont);
                colHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
                colHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                colHeaderStyle.setWrapText(true);

                Font totalFont = workbook.createFont();
                totalFont.setBold(true);

                CellStyle totalLabelStyle = workbook.createCellStyle();
                totalLabelStyle.setFont(totalFont);
                totalLabelStyle.setAlignment(HorizontalAlignment.CENTER);
                totalLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Integer-only (no decimals)
                CellStyle totalNumberStyle = workbook.createCellStyle();
                totalNumberStyle.setFont(totalFont);
                short intFormat = creationHelper.createDataFormat().getFormat("0");
                totalNumberStyle.setDataFormat(intFormat);
                totalNumberStyle.setAlignment(HorizontalAlignment.RIGHT);

                // ---------- Header rows ----------
                int currentRow = 0;

                // Main header (merged)
                Row mainHeaderRow = sheet.createRow(currentRow++);
                Cell mainHeaderCell = mainHeaderRow.createCell(0);
                mainHeaderCell.setCellValue(mainHeader);
                mainHeaderCell.setCellStyle(mainHeaderStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerData.length - 1));

                // Leave two blank rows after main header
                sheet.createRow(currentRow++); // First blank row
                sheet.createRow(currentRow++); // Second blank row

                // Column headers
                Row headerRow = sheet.createRow(currentRow++);
                for (int i = 0; i < headerData.length; i++) {
                    Cell c = headerRow.createCell(i);
                    c.setCellValue(headerData[i]);
                    c.setCellStyle(colHeaderStyle);
                }

                sheet.createFreezePane(0, currentRow);

                // ---------- Data rows ----------
                int rowStartIndex = currentRow;
                int rowNum = currentRow;

                // Track which columns contain numeric data
                boolean[] isNumericColumn = new boolean[headerData.length];
                Arrays.fill(isNumericColumn, false);

                for (LinkedCaseInsensitiveMap data : allData) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rowNum - rowStartIndex);

                    for (int i = 1; i <= rowData.length; i++) {
                        Object cellValue = data.get(rowData[i - 1]);
                        Cell cell = row.createCell(i);
                        if (cellValue instanceof Number) {
                            cell.setCellValue(((Number) cellValue).doubleValue());
                            isNumericColumn[i] = true;
                        } else if (cellValue != null) {
                            try {
                                double d = Double.parseDouble(cellValue.toString());
                                cell.setCellValue(d);
                                isNumericColumn[i] = true;
                            } catch (NumberFormatException nfe) {
                                cell.setCellValue(cellValue.toString());
                                // Not a numeric column
                            }
                        } else {
                            cell.setCellValue("");
                        }
                    }
                }

                // ---------- Totals row ----------
                if (isTotal) {
                    int firstDataRowExcel = rowStartIndex + 1;
                    int lastDataRowExcel = rowNum;

                    Row totalRow = sheet.createRow(rowNum);

                    // Merge first three columns for "Total"
                    sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 2));

                    Cell totalLabelCell = totalRow.createCell(0);
                    totalLabelCell.setCellValue("Total");
                    totalLabelCell.setCellStyle(totalLabelStyle);

                    // Create blank cells in merged region (for alignment)
                    totalRow.createCell(1).setCellStyle(totalLabelStyle);
                    totalRow.createCell(2).setCellStyle(totalLabelStyle);

                    // Formulas only for numeric columns starting from 3rd index (col 3 onward)
                    for (int col = 3; col < headerData.length; col++) {
                        Cell fCell = totalRow.createCell(col);

                        if (isNumericColumn[col]) {
                            // Only create SUM formula for columns that contain numeric data
                            String colLetter = CellReference.convertNumToColString(col);
                            String formula = String.format("SUM(%s%d:%s%d)", colLetter, firstDataRowExcel, colLetter, lastDataRowExcel);
                            fCell.setCellFormula(formula);
                            fCell.setCellStyle(totalNumberStyle);
                        } else {
                            // Leave non-numeric columns blank in total row
                            fCell.setCellValue("");
                            fCell.setCellStyle(totalLabelStyle);
                        }
                    }

                    // Evaluate formulas so totals show immediately
                    FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                    evaluator.evaluateAll();
                }

                // ---------- Set column widths (Manual to avoid font issues) ----------
                for (int i = 0; i < headerData.length; i++) {
                    // Set reasonable column widths based on header length
                    int width = Math.min(Math.max(headerData[i].length() * 400, 3000), 15000);
                    sheet.setColumnWidth(i, width);
                }

                workbook.write(out);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(excelFileName).build());
                return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("No Data Available").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in Excel Data generation Class " + e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        } finally {
            try {
                workbook.close();
            } catch (IOException ex) {
                logger.error("Error in Excel Data generation Class " + ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.error("Error in Excel Data generation Class " + ex);
            }
        }
    }

    public ResponseEntity<byte[]> excelDataForBonusRegister(List<LinkedCaseInsensitiveMap> excelData, String[] months, String sheetName, String fileName) {
        // Set headless mode to prevent font issues
        System.setProperty("java.awt.headless", "true");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();

        try {
            String excelFileName = fileName.concat(".xlsx");

            if (!excelData.isEmpty()) {
                Sheet sheet = workbook.createSheet(sheetName);
                createBonusRegisterSheet(workbook, sheet, excelData, months);

                workbook.write(out);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(excelFileName).build());
                return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("No data available").getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in Excel Data generation Class " + e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        } finally {
            try {
                workbook.close();
            } catch (IOException ex) {
                logger.error("Error in Excel Data generation Class " + ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.error("Error in Excel Data generation Class " + ex);
            }
        }
    }

    private void createBonusRegisterSheet(Workbook workbook, Sheet sheet, List<LinkedCaseInsensitiveMap> excelData, String[] months) {
        // Create cell styles
        CellStyle boldCenterStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldCenterStyle.setFont(boldFont);
        boldCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        boldCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle boldStyle = workbook.createCellStyle();
        boldStyle.setFont(boldFont);

        CellStyle regularCenterStyle = workbook.createCellStyle();
        regularCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        regularCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle leftAlignStyle = workbook.createCellStyle();
        leftAlignStyle.setAlignment(HorizontalAlignment.LEFT);
        leftAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Create right-aligned style for numeric columns
        CellStyle rightAlignStyle = workbook.createCellStyle();
        rightAlignStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightAlignStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Create right-aligned bold style for numeric totals
        CellStyle rightAlignBoldStyle = workbook.createCellStyle();
        rightAlignBoldStyle.setFont(boldFont);
        rightAlignBoldStyle.setAlignment(HorizontalAlignment.RIGHT);
        rightAlignBoldStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Create title rows
        Row titleRow1 = sheet.createRow(0);
        createCell(titleRow1, 0, "Bonus Register", boldCenterStyle);
        // Merge first four columns for the title
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

        Row titleRow2 = sheet.createRow(1);
        // Empty row for spacing

        // Create main header row (row 2) - this will have merged cells spanning row 2-3 for basic info and totals
        Row mainHeaderRow = sheet.createRow(2);
        Row subHeaderRow = sheet.createRow(3);

        // Create merged headers for basic employee info (spanning 2 rows)
        String[] mergedHeaders = {"EMP_ID", "Employee", "Status", "DOJ"};
        int colIndex = 0;

        for (String header : mergedHeaders) {
            createCell(mainHeaderRow, colIndex, header, boldCenterStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 3, colIndex, colIndex));
            createCell(subHeaderRow, colIndex, "", regularCenterStyle); // Empty cell in sub-header row
            colIndex++;
        }

        // Create month headers (each month spans 2 columns but only 1 row, with sub-headers below)
        for (String month : months) {
            createCell(mainHeaderRow, colIndex, month, boldCenterStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, colIndex, colIndex + 1));

            // Create sub-headers for this month
            createCell(subHeaderRow, colIndex, "Pay Days", boldCenterStyle);
            createCell(subHeaderRow, colIndex + 1, "Amount", boldCenterStyle);

            colIndex += 2;
        }

        // Create merged headers for totals section (spanning 2 rows)
        String[] totalHeaders = {"Total Days", "Total Amount", "Bonus Payable", "ExGratia", "Remarks"};
        for (String header : totalHeaders) {
            createCell(mainHeaderRow, colIndex, header, boldCenterStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 3, colIndex, colIndex));
            createCell(subHeaderRow, colIndex, "", regularCenterStyle); // Empty cell in sub-header row
            colIndex++;
        }

        // Create data rows
        int rowNum = 4;
        for (LinkedCaseInsensitiveMap data : excelData) {
            Row row = sheet.createRow(rowNum++);

            // Basic employee info
            createCell(row, 0, getStringValue(data.get("EMP_ID")), regularCenterStyle);
            createCell(row, 1, getStringValue(data.get("Employee")), leftAlignStyle);
            createCell(row, 2, getStringValue(data.get("Status")), regularCenterStyle);
            createCell(row, 3, getStringValue(data.get("DOJ")), regularCenterStyle);

            // Monthly data - Pay Days and Amount
            colIndex = 4;
            for (String month : months) {
                String payDaysKey = month.replace("/", "_") + "_PayDays";
                String amountKey = month.replace("/", "_") + "_Amount";

                // Handle numeric values properly
                Object payDaysValue = data.get(payDaysKey);
                Object amountValue = data.get(amountKey);

                // Pay Days (RIGHT aligned)
                if (payDaysValue instanceof Number) {
                    Cell payDaysCell = row.createCell(colIndex);
                    payDaysCell.setCellValue(((Number) payDaysValue).doubleValue());
                    payDaysCell.setCellStyle(rightAlignStyle);
                } else {
                    createCell(row, colIndex, getStringValue(payDaysValue), rightAlignStyle);
                }

                // Amount (RIGHT aligned)
                if (amountValue instanceof Number) {
                    Cell amountCell = row.createCell(colIndex + 1);
                    amountCell.setCellValue(((Number) amountValue).doubleValue());
                    amountCell.setCellStyle(rightAlignStyle);
                } else {
                    createCell(row, colIndex + 1, getStringValue(amountValue), rightAlignStyle);
                }

                colIndex += 2;
            }

            // Total Days (RIGHT aligned)
            Object totalDaysValue = data.get("TotalDays");
            if (totalDaysValue instanceof Number) {
                Cell totalDaysCell = row.createCell(colIndex);
                totalDaysCell.setCellValue(((Number) totalDaysValue).doubleValue());
                totalDaysCell.setCellStyle(rightAlignStyle);
            } else {
                createCell(row, colIndex, getStringValue(totalDaysValue), rightAlignStyle);
            }

            // Total Amount (RIGHT aligned)
            Object totalAmountValue = data.get("TotalAmount");
            if (totalAmountValue instanceof Number) {
                Cell totalAmountCell = row.createCell(colIndex + 1);
                totalAmountCell.setCellValue(((Number) totalAmountValue).doubleValue());
                totalAmountCell.setCellStyle(rightAlignStyle);
            } else {
                createCell(row, colIndex + 1, getStringValue(totalAmountValue), rightAlignStyle);
            }

            // Bonus Payable (RIGHT aligned)
            Object bonusPayableValue = data.get("BonusPayable");
            if (bonusPayableValue instanceof Number) {
                Cell bonusPayableCell = row.createCell(colIndex + 2);
                bonusPayableCell.setCellValue(((Number) bonusPayableValue).doubleValue());
                bonusPayableCell.setCellStyle(rightAlignStyle);
            } else {
                createCell(row, colIndex + 2, getStringValue(bonusPayableValue), rightAlignStyle);
            }

            // ExGratia (RIGHT aligned)
            Object exGratiaValue = data.get("ExGratia");
            if (exGratiaValue instanceof Number) {
                Cell exGratiaCell = row.createCell(colIndex + 3);
                exGratiaCell.setCellValue(((Number) exGratiaValue).doubleValue());
                exGratiaCell.setCellStyle(rightAlignStyle);
            } else {
                createCell(row, colIndex + 3, getStringValue(exGratiaValue), rightAlignStyle);
            }

            // Remarks (left aligned)
            createCell(row, colIndex + 4, getStringValue(data.get("Remarks")), leftAlignStyle);
        }

        // Create Grand Total row
        if (!excelData.isEmpty()) {
            Row grandTotalRow = sheet.createRow(rowNum);

            // Create "Grand Total" label
            createCell(grandTotalRow, 0, "", boldStyle);
            createCell(grandTotalRow, 1, "Grand Total", boldStyle);
            createCell(grandTotalRow, 2, "", boldStyle);
            createCell(grandTotalRow, 3, "", boldStyle);

            // Calculate grand totals manually for each column
            colIndex = 4;

            // For monthly Pay Days and Amount columns
            for (int i = 0; i < months.length; i++) {
                double payDaysTotal = 0;
                double amountTotal = 0;

                for (LinkedCaseInsensitiveMap data : excelData) {
                    String payDaysKey = months[i].replace("/", "_") + "_PayDays";
                    String amountKey = months[i].replace("/", "_") + "_Amount";

                    Object payDaysObj = data.get(payDaysKey);
                    Object amountObj = data.get(amountKey);

                    if (payDaysObj instanceof Number) {
                        payDaysTotal += ((Number) payDaysObj).doubleValue();
                    }

                    if (amountObj instanceof Number) {
                        amountTotal += ((Number) amountObj).doubleValue();
                    }
                }

                // Pay Days total (RIGHT aligned)
                Cell payDaysCell = grandTotalRow.createCell(colIndex);
                payDaysCell.setCellValue(payDaysTotal);
                payDaysCell.setCellStyle(rightAlignBoldStyle);

                // Amount total (RIGHT aligned)
                Cell amountCell = grandTotalRow.createCell(colIndex + 1);
                amountCell.setCellValue(amountTotal);
                amountCell.setCellStyle(rightAlignBoldStyle);

                colIndex += 2;
            }

            // Calculate totals for Total Days, Total Amount, Bonus Payable, ExGratia
            double totalDaysSum = 0;
            double totalAmountSum = 0;
            double bonusPayableSum = 0;
            double exGratiaSum = 0;

            for (LinkedCaseInsensitiveMap data : excelData) {
                Object totalDaysObj = data.get("TotalDays");
                Object totalAmountObj = data.get("TotalAmount");
                Object bonusPayableObj = data.get("BonusPayable");
                Object exGratiaObj = data.get("ExGratia");

                if (totalDaysObj instanceof Number) {
                    totalDaysSum += ((Number) totalDaysObj).doubleValue();
                }

                if (totalAmountObj instanceof Number) {
                    totalAmountSum += ((Number) totalAmountObj).doubleValue();
                }

                if (bonusPayableObj instanceof Number) {
                    bonusPayableSum += ((Number) bonusPayableObj).doubleValue();
                }

                if (exGratiaObj instanceof Number) {
                    exGratiaSum += ((Number) exGratiaObj).doubleValue();
                }
            }

            // Total Days (RIGHT aligned)
            Cell totalDaysCell = grandTotalRow.createCell(colIndex);
            totalDaysCell.setCellValue(totalDaysSum);
            totalDaysCell.setCellStyle(rightAlignBoldStyle);

            // Total Amount (RIGHT aligned)
            Cell totalAmountCell = grandTotalRow.createCell(colIndex + 1);
            totalAmountCell.setCellValue(totalAmountSum);
            totalAmountCell.setCellStyle(rightAlignBoldStyle);

            // Bonus Payable (RIGHT aligned)
            Cell bonusPayableCell = grandTotalRow.createCell(colIndex + 2);
            bonusPayableCell.setCellValue(bonusPayableSum);
            bonusPayableCell.setCellStyle(rightAlignBoldStyle);

            // ExGratia (RIGHT aligned)
            Cell exGratiaCell = grandTotalRow.createCell(colIndex + 3);
            exGratiaCell.setCellValue(exGratiaSum);
            exGratiaCell.setCellStyle(rightAlignBoldStyle);

            createCell(grandTotalRow, colIndex + 4, "", boldStyle);
        }

        // Set optimized fixed column widths with smaller amount columns
        setOptimizedColumnWidths(sheet, months);

        // Set row heights for merged header rows
        mainHeaderRow.setHeightInPoints(25);
        subHeaderRow.setHeightInPoints(20);
    }

    private void setOptimizedColumnWidths(Sheet sheet, String[] months) {
        // Define optimized fixed column widths with smaller amount columns
        int totalColumns = 4 + (months.length * 2) + 5; // Changed from 4 to 5 for the new ExGratia column

        for (int i = 0; i < totalColumns; i++) {
            int width;
            if (i == 0) { // EMP_ID
                width = 2500;
            } else if (i == 1) { // Employee Name
                width = 6000;
            } else if (i == 2) { // Status
                width = 3500;
            } else if (i == 3) { // DOJ
                width = 3500;
            } else if (i >= 4 && i < 4 + (months.length * 2)) {
                // Monthly data columns
                if ((i - 4) % 2 == 0) { // Pay Days columns (even indices)
                    width = 3500;
                } else { // Amount columns (odd indices) - SMALLER
                    width = 4000; // Reduced from 4500
                }
            } else {
                // Total columns (last 5 columns)
                int totalColumnIndex = i - (4 + months.length * 2);
                switch (totalColumnIndex) {
                    case 0: // Total Days
                        width = 4000;
                        break;
                    case 1: // Total Amount - SMALLER
                        width = 4500; // Reduced from 5000
                        break;
                    case 2: // Bonus Payable - SMALLER
                        width = 4500; // Reduced from 5000
                        break;
                    case 3: // ExGratia - NEW COLUMN
                        width = 4500; // Same as Bonus Payable
                        break;
                    case 4: // Remarks
                        width = 6000;
                        break;
                    default:
                        width = 4000;
                }
            }
            sheet.setColumnWidth(i, width);
        }
    }

// Helper methods
    private void createCell(Row row, int columnIndex, String value) {
        createCell(row, columnIndex, value, null);
    }

    private void createCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(value);
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    private String getStringValue(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public ResponseEntity<byte[]> locationAndDepartmentWiseExcel(
            String location,
            List<LinkedCaseInsensitiveMap> excelData,
            String[] headerData,
            String[] rowData,
            String sheetName,
            String fileName) {

        System.setProperty("java.awt.headless", "true");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();

        try {
            String excelFileName = fileName.concat(".xlsx");

            Sheet sheet = workbook.createSheet(sheetName);

            if (excelData == null || excelData.isEmpty()) {
                Row noDataRow = sheet.createRow(0);
                Cell noDataCell = noDataRow.createCell(0);
                noDataCell.setCellValue("No Data Found");
                CellStyle centeredStyle = workbook.createCellStyle();
                centeredStyle.setAlignment(HorizontalAlignment.CENTER);
                centeredStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                noDataCell.setCellStyle(centeredStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerData.length - 1));
                workbook.write(out);
                HttpHeaders emptyHeaders = new HttpHeaders();
                emptyHeaders.setContentType(
                        new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                emptyHeaders.setContentDisposition(
                        ContentDisposition.builder("attachment").filename(excelFileName).build());
                return new ResponseEntity<>(out.toByteArray(), emptyHeaders, HttpStatus.OK);
            }
            CreationHelper creationHelper = workbook.getCreationHelper();

            // ---------- Fonts & Styles ----------
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setWrapText(true);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            Font boldFont = workbook.createFont();
            boldFont.setBold(true);

            CellStyle departmentStyle = workbook.createCellStyle();
            departmentStyle.setFont(boldFont);
            departmentStyle.setAlignment(HorizontalAlignment.CENTER);
            departmentStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            departmentStyle.setBorderBottom(BorderStyle.THIN);
            departmentStyle.setBorderTop(BorderStyle.THIN);
            departmentStyle.setBorderLeft(BorderStyle.THIN);
            departmentStyle.setBorderRight(BorderStyle.THIN);

            CellStyle numericStyle = workbook.createCellStyle();
            numericStyle.setAlignment(HorizontalAlignment.RIGHT);
            short numberFormat = creationHelper.createDataFormat().getFormat("#,##0.00");
            numericStyle.setDataFormat(numberFormat);

            CellStyle numericBoldStyle = workbook.createCellStyle();
            numericBoldStyle.setFont(boldFont);
            numericBoldStyle.setAlignment(HorizontalAlignment.RIGHT);
            numericBoldStyle.setDataFormat(numberFormat);
            numericBoldStyle.setBorderBottom(BorderStyle.THIN);
            numericBoldStyle.setBorderTop(BorderStyle.THIN);
            numericBoldStyle.setBorderLeft(BorderStyle.THIN);
            numericBoldStyle.setBorderRight(BorderStyle.THIN);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setAlignment(HorizontalAlignment.CENTER);
            textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            textStyle.setBorderBottom(BorderStyle.THIN);
            textStyle.setBorderTop(BorderStyle.THIN);
            textStyle.setBorderLeft(BorderStyle.THIN);
            textStyle.setBorderRight(BorderStyle.THIN);

            CellStyle locationStyle = workbook.createCellStyle();
            locationStyle.setAlignment(HorizontalAlignment.CENTER);
            locationStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            locationStyle.setBorderBottom(BorderStyle.THIN);
            locationStyle.setBorderTop(BorderStyle.THIN);
            locationStyle.setBorderLeft(BorderStyle.THIN);
            locationStyle.setBorderRight(BorderStyle.THIN);

            CellStyle totalStyle = workbook.createCellStyle();
            totalStyle.setFont(boldFont);
            totalStyle.setAlignment(HorizontalAlignment.CENTER);
            totalStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalStyle.setBorderBottom(BorderStyle.THIN);
            totalStyle.setBorderTop(BorderStyle.THIN);
            totalStyle.setBorderLeft(BorderStyle.THIN);
            totalStyle.setBorderRight(BorderStyle.THIN);

            // ---------- Title ----------
            int rowIndex = 0;
            Row titleRow = sheet.createRow(rowIndex++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Locationwise & Departmentwise Summary");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headerData.length - 1));

            // Blank Row
            rowIndex++;

            // ---------- Header ----------
            Row headerRow = sheet.createRow(rowIndex++);
            for (int i = 0; i < headerData.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headerData[i]);
                cell.setCellStyle(headerStyle);
            }

            // Blank Row after header
            rowIndex++;

            // ---------- Data Rows ----------
            int srNo = 1;
            int dataStartRow = rowIndex;

            double totalManpower = 0;
            double totalPresentDay = 0;
            double totalOtHrs = 0;
            double totalOtAmount = 0;
            double totalSalary = 0;
            double totalNetAmount = 0;

            for (LinkedCaseInsensitiveMap data : excelData) {
                Row row = sheet.createRow(rowIndex++);

                for (int i = 0; i < headerData.length; i++) {
                    Cell cell = row.createCell(i);
                    Object value = null;

                    if (i == 0) { // Sr. No
                        cell.setCellValue(srNo++);
                        cell.setCellStyle(textStyle);
                        continue;
                    }

                    if (i == 1) { // Location
                        cell.setCellValue(location);
                        cell.setCellStyle(locationStyle);
                        continue;
                    }

                    if (i == 2) { // Department
                        value = data.get(rowData[0]);
                        cell.setCellValue(value != null ? value.toString() : "");
                        cell.setCellStyle(departmentStyle);
                        continue;
                    }

                    int fieldIndex = i - 2;
                    if (fieldIndex >= 1 && fieldIndex < rowData.length) {
                        String fieldName = rowData[fieldIndex];
                        value = data.get(fieldName);

                        if (value instanceof Number) {
                            double numValue = ((Number) value).doubleValue();
                            switch (i) {
                                case 3:
                                    totalManpower += numValue;
                                    break;
                                case 4:
                                    totalPresentDay += numValue;
                                    break;
                                case 5:
                                    totalOtHrs += numValue;
                                    break;
                                case 6:
                                    totalOtAmount += numValue;
                                    break;
                                case 7:
                                    totalSalary += numValue;
                                    break;
                                case 8:
                                    totalNetAmount += numValue;
                                    break;
                            }
                        }
                    }

                    if (value == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(textStyle);
                    } else if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                        cell.setCellStyle(numericStyle);
                    } else {
                        cell.setCellValue(value.toString());
                        cell.setCellStyle(textStyle);
                    }
                }
            }

            // ---------- Merge Location Cells ----------
            if (dataStartRow < rowIndex) {
                int dataRowCount = rowIndex - dataStartRow;
                if (dataRowCount > 1) {
                    sheet.addMergedRegion(new CellRangeAddress(dataStartRow, rowIndex - 1, 1, 1));
                }
            }

            // ---------- Total Row ----------
            Row totalRow = sheet.createRow(rowIndex++);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("Total");
            totalLabelCell.setCellStyle(totalStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 2));

            Object[] totalValues = {
                null, null, null,
                totalManpower, totalPresentDay, totalOtHrs,
                totalOtAmount, totalSalary, totalNetAmount
            };

            for (int i = 3; i < headerData.length; i++) {
                Cell cell = totalRow.createCell(i);
                Object value = totalValues[i];
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    cell.setCellValue("");
                }
                cell.setCellStyle(numericBoldStyle);
            }

            // Blank Row
            rowIndex++;

            // ---------- Note ----------
            Row noteRow = sheet.createRow(rowIndex++);
            Cell noteCell = noteRow.createCell(0);
            noteCell.setCellValue("Note : Departments are shown for reference only.");
            CellStyle noteStyle = workbook.createCellStyle();
            noteStyle.setAlignment(HorizontalAlignment.CENTER);
            noteCell.setCellStyle(noteStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, headerData.length - 1));

            // ---------- Column Widths ----------
            for (int i = 0; i < headerData.length; i++) {
                sheet.setColumnWidth(i, 4000);
            }

            // ---------- Return File ----------
            workbook.write(out);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                    new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(
                    ContentDisposition.builder("attachment").filename(excelFileName).build());

            return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error: " + e.getMessage()).getBytes());
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
            }
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
    }
    
    
    public ResponseEntity<byte[]> excelFAFData(List<LinkedCaseInsensitiveMap> excelData, String[] headerData, String[] rowData,String orgNameAndAddress, String sheetName, String fileName) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        try {
            
            
            
            int rowIndex = 0;

            String excelFileName = fileName.concat(".xlsx");
            List<LinkedCaseInsensitiveMap> allData = excelData;
           
           
            if (!allData.isEmpty()) {
                Sheet sheet = workbook.createSheet(sheetName);
                
   
                
            // Create Bold Font
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            
            
            // Create Cell Style
            CellStyle boldStyle = workbook.createCellStyle();
            boldStyle.setFont(boldFont);
            
                // Create centered style for title
                CellStyle centerStyle = workbook.createCellStyle();
                centerStyle.cloneStyleFrom(boldStyle);
                centerStyle.setAlignment(HorizontalAlignment.CENTER);
                centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
                
                // For Column Header
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(boldFont);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

                // Set background color #c9e3f5
                XSSFColor headerColor = new XSSFColor(new java.awt.Color(201, 227, 245), null);
                ((XSSFCellStyle) headerStyle).setFillForegroundColor(headerColor);
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                
                // Set borders
                headerStyle.setBorderTop(BorderStyle.THIN);
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                
                
            Row row0 = sheet.createRow(rowIndex++);
            row0.setHeightInPoints(40);
            Cell cell0 = row0.createCell(0);
            cell0.setCellValue("REGISTER OF FULL AND FINAL");
            cell0.setCellStyle(centerStyle);
            
            // Merge columns 0 to 3 in this row
            sheet.addMergedRegion(new CellRangeAddress(row0.getRowNum(), row0.getRowNum(), 0, headerData.length-1));
            boldStyle.setAlignment(HorizontalAlignment.CENTER);
            
            

            Row row1 = sheet.createRow(rowIndex++);
            row1.setHeightInPoints(30);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("Name & Address of Contractor:");
            cell1.setCellStyle(boldStyle);
            sheet.addMergedRegion(new CellRangeAddress(row1.getRowNum(), row1.getRowNum(), 0, 3));
            boldStyle.setAlignment(HorizontalAlignment.LEFT);
            
                      
            Cell row1Cell4 = row1.createCell(4);
            row1Cell4.setCellValue(orgNameAndAddress);
            row1Cell4.setCellStyle(boldStyle);
            
            sheet.addMergedRegion(new CellRangeAddress(row1.getRowNum(), row1.getRowNum(), 4, headerData.length - 1));
            

            Row row2 = sheet.createRow(rowIndex++);
            row2.setHeightInPoints(30);
            Cell cell2 = row2.createCell(0);
            cell2.setCellValue("Nature & Location of work :");
            cell2.setCellStyle(boldStyle);
            sheet.addMergedRegion(new CellRangeAddress(row2.getRowNum(), row2.getRowNum(), 0, 3));
            boldStyle.setAlignment(HorizontalAlignment.LEFT);
            
            Cell row2Cell4 = row2.createCell(4);
            row2Cell4.setCellValue("");
            row2Cell4.setCellStyle(boldStyle);
            
            sheet.addMergedRegion(new CellRangeAddress(row2.getRowNum(), row2.getRowNum(), 4, headerData.length - 1));
            
            

            Row row3 = sheet.createRow(rowIndex++);
            row3.setHeightInPoints(30);
            Cell cell3 = row3.createCell(0);
            cell3.setCellValue("Work Order No :");
            cell3.setCellStyle(boldStyle);
            sheet.addMergedRegion(new CellRangeAddress(row3.getRowNum(), row3.getRowNum(), 0, 3));
            boldStyle.setAlignment(HorizontalAlignment.LEFT);
            
                     Cell row3Cell4 = row3.createCell(4);
            row3Cell4.setCellValue("");
            row3Cell4.setCellStyle(boldStyle);
            
            sheet.addMergedRegion(new CellRangeAddress(row3.getRowNum(), row3.getRowNum(), 4, headerData.length - 1));
            

            Row row4 = sheet.createRow(rowIndex++);
            row4.setHeightInPoints(35);
            Cell cell4 = row4.createCell(0);
            cell4.setCellValue("");
            cell4.setCellStyle(boldStyle);
            sheet.addMergedRegion(new CellRangeAddress(row4.getRowNum(), row4.getRowNum(), 0, 3));
            boldStyle.setAlignment(HorizontalAlignment.LEFT);
            
            Cell row4Cell4 = row4.createCell(4);
            row4Cell4.setCellValue("FULL AND FINAL SETTLEMENT");
            row4Cell4.setCellStyle(boldStyle);
            
            sheet.addMergedRegion(new CellRangeAddress(row4.getRowNum(), row4.getRowNum(), 4, headerData.length - 1));
            
            
            
            
            Row headerRow = sheet.createRow(rowIndex++);
            headerRow.setHeightInPoints(25); // set row height
            for (int i = 0; i < headerData.length; i++) {
                headerRow.createCell(i).setCellValue(headerData[i]);
               Cell cell = headerRow.createCell(i);
               cell.setCellValue(headerData[i]);
               cell.setCellStyle(headerStyle);
            }
            
                // OR auto size columns (run after setting cell values)
//                for (int i = 0; i < headerData.length; i++) {
//                    sheet.autoSizeColumn(i);
//                }


                int columnCount = Math.min(headerData.length, rowData.length);

                for (int i = 0; i < columnCount; i++) {
                    int maxLength = headerData[i].length();

                    for (LinkedCaseInsensitiveMap row : excelData) {
                        Object value = row.get(rowData[i]);
                        if (value != null) {
                            maxLength = Math.max(maxLength, value.toString().length());
                        }
                    }

                    sheet.setColumnWidth(i, (maxLength + 2) * 256);
                }        

                int rowNum = rowIndex;
                int sNo=1;
                for (LinkedCaseInsensitiveMap data : allData) {
                    // Your logic here using the 'data'
                    sNo++;
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(sNo - 1);
                    for (int i = 1; i <= rowData.length; i++) {

                        Object cellValue = data.get(rowData[i - 1]);
                        if (cellValue instanceof Number) {
                            // If the cell contains a number, set the numeric value directly
                            row.createCell(i).setCellValue(((Number) cellValue).doubleValue());
                        } else if (cellValue != null) {
                            // If the cell contains text, set the cell value as a string
                            row.createCell(i).setCellValue(cellValue.toString());
                        } else {
                            // If the cell value is null, set the cell value to an empty string
                            row.createCell(i).setCellValue("");
                        }

                    }

                }
                workbook.write(out);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                headers.setContentDisposition(ContentDisposition.builder("attachment").filename(excelFileName).build()); //file name in .xlsx 
                return new ResponseEntity<>(out.toByteArray(), headers, HttpStatus.OK);
            } else {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(("Internal Server Error").getBytes());

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception in Excell Data generation Class " + e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Internal Server Error").getBytes());
        } finally {
            try {
                workbook.close();
            } catch (IOException ex) {
                logger.error("Error in Excell Data generation Class " + ex);
            }
            try {
                out.close();
            } catch (IOException ex) {
                logger.error("Error in Excell Data generation Class " + ex);
            }
        }
    }

}
