package se.goteborg.retursidan.portlet.efterlysningar.controller;

import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.goteborg.retursidan.model.entity.Request.Status;
import se.goteborg.retursidan.portlet.controller.PhotoBaseController;
import se.goteborg.retursidan.service.ModelService;

/**
 * Controller for the Efterlysningar portlet
 *
 */
@Controller()
@RequestMapping("VIEW")
public class RequestPortletController extends PhotoBaseController{
	
	@Autowired
	private ModelService modelService;
	
	@RenderMapping
	public String render(RenderRequest request, RenderResponse response, Model model) {
		model.addAttribute("requests", modelService.getAllRequests(Status.PUBLISHED));		
		PortletPreferences prefs = request.getPreferences();
		model.addAttribute("baseURI", prefs.getValue("baseURI", ""));
		return "list_requests";
	}
	
	@RenderMapping(params="page=listMyRequests")
	public String listMyRequests(@ModelAttribute("userId") String userId, RenderRequest request, RenderResponse response, Model model) {
		model.addAttribute("requests", modelService.getAllRequestsForCreatorUid(Status.PUBLISHED, userId));		
		PortletPreferences prefs = request.getPreferences();
		model.addAttribute("baseURI", prefs.getValue("baseURI", ""));
		return "list_my_requests";		
	}

	@RenderMapping(params="page=listRequests")
	public String listRequests(RenderRequest request, RenderResponse response, Model model) {
		return render(request, response, model);
	}

}
