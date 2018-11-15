package se.goteborg.retursidan.portlet.controller;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.model.entity.Category;
import se.goteborg.retursidan.model.entity.Unit;
import se.goteborg.retursidan.portlet.binding.PhotoListPropertyEditor;
import se.goteborg.retursidan.portlet.validation.AdValidator;

/**
 * Controller handling changing ads
 *
 */
@Controller
@RequestMapping("VIEW")
public class ChangeAdController extends BaseController {
	private static Log logger = LogFactoryUtil.getLog(ChangeAdController.class);

	
	@InitBinder("advertisement")
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(new AdValidator());
		binder.registerCustomEditor(List.class, "photos", new PhotoListPropertyEditor());		
	}

	@RenderMapping(params="page=changeAd")
	public String changeAd(Model model) {
		// work-around for Spring form bug/misbehavior, errors are not persisted in model 
		Advertisement advertisement;
		if(model.containsAttribute("advertisement")) {
			advertisement = (Advertisement)model.asMap().get("advertisement");
		} else {
			advertisement = new Advertisement();
		}
		
		List<Unit> units = modelService.getUnits();
		model.addAttribute("units", units);
	
		List<Category> topCategories = modelService.getTopCategories();
		model.addAttribute("topCategories", topCategories);

		if (advertisement.getTopCategory() != null && advertisement.getTopCategory().getId() > 0) {
			List<Category> subCategories = modelService.getSubCategories(advertisement.getTopCategory().getId());
			model.addAttribute("subCategories", subCategories);		
		}
			
		return "change_ad";
	}

	@ActionMapping("loadAd")
	public void loadAd(@RequestParam(value="advertisementId") Integer advertisementId, Model model, ActionResponse response) {
		Advertisement advertisement = modelService.getAdvertisement(advertisementId);
		advertisement.setTopCategory(advertisement.getCategory().getParent());
		model.addAttribute("advertisement", advertisement);
		response.setRenderParameter("page", "changeAd");
	}
	
	@ActionMapping("updateAd")
	public void updateAd(@Valid @ModelAttribute("advertisement") Advertisement advertisement, BindingResult bindingResult, ActionRequest request, ActionResponse response, Model model) {
		//advertisement.setCreatorUid(getUserId(request));
		if (!bindingResult.hasErrors()) {
			logger.debug("Updating advertisement: " + advertisement);
			modelService.updateAd(advertisement);
			logger.debug("Advertisement updated");
			// reload the ad into the model to get all data populated
			model.addAttribute("advertisement", modelService.getAdvertisement(advertisement.getId()));
			response.setRenderParameter("page", "viewAd");
		} else {
			logger.debug("Binding errors in Advertisement update");
			response.setRenderParameter("page", "changeAd");			
		}
	}		
}
