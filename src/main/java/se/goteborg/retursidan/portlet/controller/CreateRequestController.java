package se.goteborg.retursidan.portlet.controller;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.entity.Category;
import se.goteborg.retursidan.model.entity.Person;
import se.goteborg.retursidan.model.entity.Request;
import se.goteborg.retursidan.model.entity.Unit;
import se.goteborg.retursidan.model.form.Config;
import se.goteborg.retursidan.model.form.MailComposition;
import se.goteborg.retursidan.model.form.Texts;
import se.goteborg.retursidan.portlet.binding.PhotoListPropertyEditor;
import se.goteborg.retursidan.portlet.validation.RequestValidator;
import se.goteborg.retursidan.service.MailService;
import se.goteborg.retursidan.util.CreateURLHelper;

/**
 * Controller handling creating requests
 *
 */
@Controller
@RequestMapping("VIEW")
public class CreateRequestController extends BaseController {
	private static Log logger = LogFactoryUtil.getLog(CreateRequestController.class);

	@Autowired
	private MailService mailService;
	
	@InitBinder("request")
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(new RequestValidator());	
		binder.registerCustomEditor(List.class, "photos", new PhotoListPropertyEditor());
	}
	
	@RenderMapping(params="externalPage=createRequest")
	public String createAdExternal(Model model) {
		Request request = new Request();
		model.addAttribute("request", request);
		
		List<Unit> units = modelService.getUnits();
		model.addAttribute("units", units);
	
		List<Category> topCategories = modelService.getTopCategories();
		model.addAttribute("topCategories", topCategories);

		return "create_request";
	}

	@RenderMapping(params="page=createRequest")
	public String createAd(Model model, PortletRequest portletRequest) {
		// work-around for Spring form bug/misbehavior, errors are not persisted in model 
		Request request;
		if(model.containsAttribute("request")) {
			request = (Request)model.asMap().get("request");
		} else {
			request = new Request();
		}
		request.setContact(getCurrentUser(portletRequest));
		request.setUnit(userService.getUsersUnit(portletRequest));

		model.addAttribute("request", request);
		
		List<Unit> units = modelService.getUnits();
		model.addAttribute("units", units);
	
		List<Category> topCategories = modelService.getTopCategories();
		model.addAttribute("topCategories", topCategories);

		if (request.getTopCategory() != null && request.getTopCategory().getId() > 0) {
			List<Category> subCategories = modelService.getSubCategories(request.getTopCategory().getId());
			model.addAttribute("subCategories", subCategories);		
		}	
		return "create_request";
	}
	
	@ActionMapping("saveRequest")
	public void saveAd(@Valid @ModelAttribute("request") Request requestAd, BindingResult bindingResult, ActionRequest request, ActionResponse response, Model model) {
		requestAd.setCreatorUid(getUserId(request));
		response.setRenderParameter("externalPage", "");
		if (!bindingResult.hasErrors()) {
			logger.debug("Saving request: " + requestAd);
			requestAd.setStatus(Request.Status.PUBLISHED);
			Person creator = getCurrentUser(request);
			requestAd.setCreatorName(creator.getName());
			requestAd.setCreatorMail(creator.getEmail());
			requestAd.setCreatorUnitCode(creator.getUnitCode());
			int id = modelService.saveRequest(requestAd);
			logger.debug("Request saved with id = " + id);
			
			// Send mail to the person who created the request
			requestAd = modelService.getRequest(id);
			Texts texts = getTexts(request);
			Config config = getConfig(request);
			String adLink = CreateURLHelper.createHttpBaseURL(request) + getConfig(request).getPocURIBase() +  REQUEST_URI + id;
			MailComposition composition = new MailComposition(null,
					texts.getMailSenderAddressNewRequest(),
					new String[] {requestAd.getContact().getEmail()},	
					"",
					texts.getMailSubjectNewRequest(),
					requestAd.getTitle(),
					"",	"",	"",
					requestAd.getContact().getName(),
					requestAd.getContact().getPhone(),
					requestAd.getContact().getEmail(),
					"",	"",	"",	"",	"",	"", "",
					adLink,
					texts.getMailBodyNewRequest(),
					config.getRulesUrl(),
					config.getLogoUrl(),
					id,
					0);

			if (!requestAd.getPhotos().isEmpty()) {				
				composition.setPhoto(modelService.getPhoto(requestAd.getPhotos().get(0).getId()));
			}
			
			mailService.composeAndSendMail(composition);
			logger.debug("Create Request, mail has been sent for request with id = " + id);
			// reload the request into the model to get all data populated
			model.addAttribute("request", modelService.getRequest(id));

			response.setRenderParameter("page", "viewRequest");		
		} else {
			response.setRenderParameter("page", "createRequest");			
		}
	}

}
