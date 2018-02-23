package se.goteborg.retursidan.service.statistics;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import se.goteborg.retursidan.model.MonthData;
import se.goteborg.retursidan.model.UserStats;
import se.goteborg.retursidan.model.entity.Unit;

public class Sheet11Generator extends BaseSheetGenerator {
	
    public Sheet11Generator(Workbook workbook, Map<String, Object> model) {
		super(workbook, model);
	}

	
    public void createSheet() {
    	Sheet sheet = workbook.createSheet("Annons. per kat., per förv.bol.kontakt");
        sheet.setDefaultColumnWidth((short) 20);
        Row pageHeaderRow = sheet.createRow(0);
        
        Cell pageHeaderCell = pageHeaderRow.createCell(0);	        
        pageHeaderCell.setCellStyle(headerStyle);
        pageHeaderCell.setCellValue("Antal skapade annonser per förvaltning/bolag, per kontaktperson, per månad och totalt");
        
        //Create year header
        Row subHeaderRow = sheet.createRow(2);
        	        
        //Loop through the months
        Cell headerCell = subHeaderRow.createCell(0);
        headerCell.setCellStyle(headerStyle);
        headerCell.setCellValue("Förvaltning/Bolag");
        List<MonthData> monthList = getMonthList();
        int cellNumber = 2;
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
        	cell.setCellStyle(headerStyle);
        	cell.setCellValue(unit.getName());
        	cellNumber = 2;
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
    		// Loopa över alla annonsörer i denna förvaltning
    		List<UserStats> fullUserList = statisticsService.getUserStatsListByUnitAndPeriodForAdvertiser(getLifeTimeSpan(), unit);
    		for (UserStats stats : fullUserList) {
    			rowNumber++;
    			row = sheet.createRow(rowNumber);
    			Cell dataCell = row.createCell(0);
    			dataCell.setCellValue(stats.getEmail());
    			dataCell = row.createCell(1);
    			dataCell.setCellValue(stats.getName());
            	cellNumber = 2;
            	for (MonthData month : monthList) {
            		dataCell = row.createCell(cellNumber);
            		int count = statisticsService.getNumberOfAdsForPersonByPeriod(month.getDateSpan(), stats.getUserId());
            		dataCell.setCellValue(String.valueOf(count));
            		cellNumber++;
            		if (month.spanOver2Years()) {
                		dataCell = row.createCell(cellNumber);
                		dataCell.setCellStyle(headerStyle);
                		count = statisticsService.getNumberOfAdsForPersonByPeriod(month.getDateSpanForYear(), stats.getUserId());
                		dataCell.setCellValue(String.valueOf(count));
                		cellNumber++;
                	}
            		
            	}
    		}        	
        	rowNumber++;
        }
        Row row = sheet.createRow(rowNumber);
		Cell cell = row.createCell(0);
		cell.setCellStyle(headerStyle);
		cell.setCellValue("Totalt");
        cellNumber = 2;
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
        	// Loopa över alla annonsörer i denna förvaltning och räkna ut antal annonser
        	
        }		
    }
}
