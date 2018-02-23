package se.goteborg.retursidan.service.statistics;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import se.goteborg.retursidan.model.MonthData;
import se.goteborg.retursidan.model.entity.Unit;

public class Sheet2Generator extends BaseSheetGenerator {
	
    public Sheet2Generator(Workbook workbook, Map<String, Object> model) {
		super(workbook, model);
	}

	
    public void createSheet() {
    	Sheet sheet = workbook.createSheet("Annonser förv.bolag per mån. år");
        sheet.setDefaultColumnWidth((short) 20);
        Row pageHeaderRow = sheet.createRow(0);
        
        Cell pageHeaderCell = pageHeaderRow.createCell(0);	        
        pageHeaderCell.setCellStyle(headerStyle);
        pageHeaderCell.setCellValue("Totalt antal publicerade annonser per förvaltning/bolag per år, per månad och totalt");
        
        //Create year header
        Row subHeaderRow = sheet.createRow(2);
        	        
        //Loop through the months
        Cell headerCell = subHeaderRow.createCell(0);
        headerCell.setCellStyle(headerStyle);
        headerCell.setCellValue("Förvaltning/Bolag");
        List<MonthData> monthList = getMonthList();
        int cellNumber = 1;
        for (MonthData month : monthList) {
        	Cell cell = subHeaderRow.createCell(cellNumber);
        	cell.setCellStyle(headerStyle);
        	cell.setCellValue("Antal " + month.getMonth() + " " + month.getYear());
        	cellNumber++;
        	if (month.spanOver2Years()) {
        		Cell cellSummary = subHeaderRow.createCell(cellNumber);
        		cellSummary.setCellStyle(headerStyle);
        		cellSummary.setCellValue("Antal totalt " + month.getYear());
        		cellNumber++;
        	}
        	
        }
        
        int rowNumber = 3;
        for (Unit unit : allUnits) {
        	//maybe check if unit has any ads
        	Row row = sheet.createRow(rowNumber);
        	Cell cell = row.createCell(0);
        	cell.setCellValue(unit.getName());
        	cellNumber = 1;
        	for (MonthData month : monthList) {
        		cell = row.createCell(cellNumber);
        		int count = statisticsService.getNumberOfAdsForUnitByPeriod(month.getDateSpan(), unit);
        		cell.setCellValue(String.valueOf(count));
        		cellNumber++;
        		if (month.spanOver2Years()) {
            		Cell cellSummary = row.createCell(cellNumber);
            		cellSummary.setCellStyle(headerStyle);
            		count = statisticsService.getNumberOfAdsForUnitByPeriod(month.getDateSpanForYear(), unit);
            		cellSummary.setCellValue(String.valueOf(count));
            		cellNumber++;
            	}
        	}        	
        	rowNumber++;
        }
        Row row = sheet.createRow(rowNumber);
		Cell cell = row.createCell(0);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("Totalt");
        cellNumber = 1;
        for (MonthData month : monthList) {
        	cell = row.createCell(cellNumber);
        	cell.setCellStyle(headerStyle);
        	int count = statisticsService.getNumberOfAdsByPeriod(month.getDateSpan());
        	cell.setCellValue(String.valueOf(count));
        	cellNumber++;
        	if (month.spanOver2Years()) {
        		cell = row.createCell(cellNumber);
        		cell.setCellStyle(headerStyle);
            	count = statisticsService.getNumberOfAdsByPeriod(month.getDateSpanForYear());
            	cell.setCellValue(String.valueOf(count));
        		cellNumber++;
        	}
        	
        }		
    }
}
