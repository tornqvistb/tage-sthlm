package se.goteborg.retursidan.portlet.controller.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.model.entity.Person;
import se.goteborg.retursidan.model.entity.Request;
import se.goteborg.retursidan.portlet.controller.BaseController;
import se.goteborg.retursidan.service.ExcelViewBuilder;
import se.goteborg.retursidan.service.StatisticsService;

@Controller
@Transactional
@RequestMapping("EDIT")
public class AdvancedStatisticsController extends BaseController {
	
	@Autowired
	private StatisticsService statisticsService;
	
	@ResourceMapping("advancedStatistics")
	public ModelAndView showStatistics(ResourceRequest request, ResourceResponse response, Model model) {
		Map<String, Object> excelModel = new HashMap<String, Object>();
		
		excelModel.put("bookname", "Statistik");
		excelModel.put("statisticsService", statisticsService);
		excelModel.put("allUnits", modelService.getUnits());
		excelModel.put("subCategories", modelService.getAllSubCategoriesSorted());
		
        response.setContentType( "application/vnd.ms-excel" );
        response.setProperty("Content-Disposition", "attachment; filename=statistik.xls"); 
        return new ModelAndView(new ExcelViewBuilder(), excelModel);		
		
	}

	@RenderMapping(params="page=prepareDBForAdvancedStatistics")
	public String prepareDBForAdvancedStatistics(RenderRequest portletRequest, Model model) {
		List<Advertisement> allAds = modelService.getAllAds();
		for (Advertisement ad : allAds) {
			if (StringUtils.isEmpty(ad.getCreatorName())) {
				// Update creator fields
				Person person = userService.getUserByUserID(portletRequest, ad.getCreatorUid());
				if (person != null) {
					ad.setCreatorMail(person.getEmail());
					ad.setCreatorName(person.getName());
					ad.setCreatorUnitCode(person.getUnitCode());
				}
			}
			if (StringUtils.isEmpty(ad.getBookerUid()) && ad.getBooker() != null) {				
				// Update booking fields
				ad.setBookerUid(ad.getBooker().getUserId());
				ad.setBookerMail(ad.getBooker().getEmail());
				ad.setBookerName(ad.getBooker().getName());
				ad.setBookerUnitCode(ad.getBooker().getUnitCode());
				ad.setBookingTime(ad.getCreated());
			}
			if (ad.getCount() == null) {
				ad.setCount(1);
			}
			modelService.updateAd(ad);
		}
		List<Request> allRequests = modelService.getAllRequests();
		for (Request req : allRequests) {
			if (StringUtils.isEmpty(req.getCreatorName())) {
				// Update creator fields
				Person person = userService.getUserByUserID(portletRequest, req.getCreatorUid());
				if (person != null) {
					req.setCreatorMail(person.getEmail());
					req.setCreatorName(person.getName());
					req.setCreatorUnitCode(person.getUnitCode());
				}
			}
		}
		return "config/start";
	}

}
