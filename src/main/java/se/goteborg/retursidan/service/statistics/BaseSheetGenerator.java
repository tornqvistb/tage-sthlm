package se.goteborg.retursidan.service.statistics;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import se.goteborg.retursidan.model.DateSpan;
import se.goteborg.retursidan.model.MonthData;
import se.goteborg.retursidan.model.entity.Category;
import se.goteborg.retursidan.model.entity.Unit;
import se.goteborg.retursidan.service.StatisticsService;

public class BaseSheetGenerator {

	protected Workbook workbook;
	protected Map<String, Object> model;
	protected List<Unit> allUnits;
	protected List<Category> allCategories;
    protected StatisticsService statisticsService;
    protected CellStyle headerStyle;
    protected CellStyle dataStyle;
	
    public BaseSheetGenerator(Workbook workbook, Map<String, Object> model) {
		super();
		this.workbook = workbook;
		this.model = model;
		this.allUnits = getAllUnits();
		this.allCategories = getAllCategories();
		this.statisticsService = getStatisticsService();
		Font font = workbook.createFont();
		this.headerStyle = workbook.createCellStyle();
		font.setBold(true);
		this.headerStyle.setFont(font);
		this.dataStyle = workbook.createCellStyle();
		font.setBold(false);
		this.dataStyle.setFont(font);
	}

    protected int getYearFromDate(Date date) {
    	Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);	    	
    }
    
    protected List<MonthData> getMonthList() {
    	String firstMonth = "2017-03-01";
    	
    	List<MonthData> monthList = new ArrayList<MonthData>();
    	 
		try {
			DateFormat formatter ; 
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = (Date)formatter.parse(firstMonth);
			while (startDate.before(new Date())) {			
				DateSpan span = new DateSpan(startDate, DateUtils.addMonths(startDate, 1));
				MonthData monthData = new MonthData();
				monthData.setDateSpan(span);
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				int year = cal.get(Calendar.YEAR);
				monthData.setYear(String.valueOf(year));					
				DateFormat df = new SimpleDateFormat("MMM", new Locale("sv"));
				monthData.setMonth(df.format(startDate));
				monthList.add(monthData);
				startDate = DateUtils.addMonths(startDate, 1);
			}
			
		} catch (ParseException e) {
		}
    	
    	return monthList;
    }
    protected List<MonthData> getYearList() {
    	String firstYear = "2017-01-01";
    	
    	List<MonthData> monthList = new ArrayList<MonthData>();
    	 
		try {
			DateFormat formatter ; 
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = (Date)formatter.parse(firstYear);
			while (startDate.before(new Date())) {			
				DateSpan span = new DateSpan(startDate, DateUtils.addMonths(startDate, 12));
				MonthData monthData = new MonthData();
				monthData.setDateSpan(span);
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				int year = cal.get(Calendar.YEAR);
				monthData.setYear(String.valueOf(year));					
				DateFormat df = new SimpleDateFormat("MMM", new Locale("sv"));
				monthData.setMonth(df.format(startDate));
				monthList.add(monthData);
				startDate = DateUtils.addMonths(startDate, 12);
			}
			
		} catch (ParseException e) {
		}
    	
    	return monthList;
    }
    protected DateSpan getLifeTimeSpan() {
    	try {
			DateFormat formatter;
			formatter = new SimpleDateFormat("yyyy-MM-dd");
			String firstMonth = "2017-03-01";
			String lastMonth = "2030-12-31";
			DateSpan span = new DateSpan((Date)formatter.parse(firstMonth), (Date)formatter.parse(lastMonth));
			return span;
		} catch (ParseException e) {
			return null;
		}
    }
    
    private List<Unit> getAllUnits() {
    	return (List<Unit>) model.get("allUnits");
    }
    private List<Category> getAllCategories() {
    	return (List<Category>) model.get("subCategories");
    }
    private StatisticsService getStatisticsService() {
    	return (StatisticsService) model.get("statisticsService");
    }
}
