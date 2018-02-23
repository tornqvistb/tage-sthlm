package se.goteborg.retursidan.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import se.goteborg.retursidan.service.statistics.Sheet10Generator;
import se.goteborg.retursidan.service.statistics.Sheet11Generator;
import se.goteborg.retursidan.service.statistics.Sheet12Generator;
import se.goteborg.retursidan.service.statistics.Sheet13Generator;
import se.goteborg.retursidan.service.statistics.Sheet14Generator;
import se.goteborg.retursidan.service.statistics.Sheet1Generator;
import se.goteborg.retursidan.service.statistics.Sheet2Generator;
import se.goteborg.retursidan.service.statistics.Sheet3Generator;
import se.goteborg.retursidan.service.statistics.Sheet4Generator;
import se.goteborg.retursidan.service.statistics.Sheet5Generator;
import se.goteborg.retursidan.service.statistics.Sheet6Generator;
import se.goteborg.retursidan.service.statistics.Sheet7Generator;
import se.goteborg.retursidan.service.statistics.Sheet8Generator;
import se.goteborg.retursidan.service.statistics.Sheet9Generator;


public class ExcelViewBuilder extends AbstractXlsView {
		
    @SuppressWarnings("unchecked")
    protected void buildExcelDocument(Map<String, Object> model,
            Workbook workbook,
            HttpServletRequest request,
            HttpServletResponse response)
    {
    	new Sheet1Generator(workbook, model).createSheet();
    	new Sheet2Generator(workbook, model).createSheet();
    	new Sheet3Generator(workbook, model).createSheet();
    	new Sheet4Generator(workbook, model).createSheet();
    	new Sheet5Generator(workbook, model).createSheet();
    	new Sheet6Generator(workbook, model).createSheet();    	
    	new Sheet7Generator(workbook, model).createSheet();
    	new Sheet8Generator(workbook, model).createSheet();
    	new Sheet9Generator(workbook, model).createSheet();
    	new Sheet10Generator(workbook, model).createSheet();
    	new Sheet11Generator(workbook, model).createSheet();
    	new Sheet12Generator(workbook, model).createSheet();
    	new Sheet13Generator(workbook, model).createSheet();
    	new Sheet14Generator(workbook, model).createSheet();
    }

}