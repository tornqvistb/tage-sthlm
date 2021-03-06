package se.goteborg.retursidan.portlet.bildspel.controller;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.PagedList;
import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.portlet.controller.PhotoBaseController;
import se.goteborg.retursidan.service.ModelService;

/**
 * Controller for the Efterlysningar portlet
 *
 */
@Controller()
@RequestMapping("VIEW")
public class BildspelPortletController extends PhotoBaseController{
	
	@Autowired
	private ModelService modelService;
	
	private static Log logger = LogFactoryUtil.getLog(BildspelPortletController.class);
	
	@RenderMapping
	public String render(RenderRequest request, RenderResponse response, Model model) {
		logger.debug("entering BildspelPortletController render");
		PagedList<Advertisement> pagedList = modelService.getAllAdvertisements(Advertisement.Status.PUBLISHED, 1, 10);
		model.addAttribute("ads", pagedList);
		logger.debug("exiting BildspelPortletController render");
		return "bildspel";
	}
	

}
