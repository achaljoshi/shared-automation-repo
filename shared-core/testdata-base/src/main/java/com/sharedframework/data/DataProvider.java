package com.sharedframework.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class DataProvider {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private DataProvider() {
        // Utility class
    }

    public static <T> T loadFromJson(String filePath, Class<T> type) throws IOException {
        File file = resolveFile(filePath);
        return objectMapper.readValue(file, type);
    }

    public static <T> List<T> loadListFromJson(String filePath, Class<T> elementType) throws IOException {
        File file = resolveFile(filePath);
        return objectMapper.readValue(file,
                objectMapper.getTypeFactory().constructCollectionType(List.class, elementType));
    }

    public static Map<String, Object> loadJsonAsMap(String filePath) throws IOException {
        File file = resolveFile(filePath);
        return objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {});
    }

    public static List<Map<String, String>> loadFromExcel(String filePath, String sheetName) throws IOException {
        List<Map<String, String>> data = new ArrayList<>();
        File file = resolveFile(filePath);

        try (InputStream is = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in " + filePath);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                return data;
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValue(cell));
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                Map<String, String> rowData = new LinkedHashMap<>();
                boolean hasData = false;

                for (int colIndex = 0; colIndex < headers.size(); colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    String value = (cell != null) ? getCellValue(cell) : "";
                    rowData.put(headers.get(colIndex), value);
                    if (!value.isEmpty()) hasData = true;
                }

                if (hasData) {
                    data.add(rowData);
                }
            }
        }
        return data;
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toString();
                }
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue) && !Double.isInfinite(numValue)) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return String.valueOf(cell.getStringCellValue());
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BLANK:
            default:
                return "";
        }
    }

    private static File resolveFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file;
        }

        // Try classpath
        ClassLoader cl = DataProvider.class.getClassLoader();
        java.net.URL resource = cl.getResource(filePath);
        if (resource != null) {
            return new File(resource.getFile());
        }

        throw new IllegalArgumentException("File not found: " + filePath);
    }

    public static <T> T fromJson(String jsonString, Class<T> type) throws IOException {
        return objectMapper.readValue(jsonString, type);
    }

    public static String toJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
