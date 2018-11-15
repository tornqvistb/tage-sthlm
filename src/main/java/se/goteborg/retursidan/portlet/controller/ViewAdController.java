package se.goteborg.retursidan.portlet.controller;

import javax.portlet.PortletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.model.entity.Person;

/**
 * Controller handling viewing of ads
 *
 */


@Controller
@RequestMapping({"VIEW", "CONFIG"})
public class ViewAdController extends BaseController {
	private static Log logger = LogFactoryUtil.getLog(ViewAdController.class);
	
	@ModelAttribute("advertisement")
	public Advertisement loadAdvertisement(@RequestParam(value="advertisementId", required=false) Integer advertisementId) {
		if (advertisementId != null) {
			logger.debug("loading advertisement id=" + advertisementId);
			Advertisement advertisement = modelService.getAdvertisement(advertisementId);
			logger.debug("advertisement=" + advertisement);			
			return advertisement;
		}
		return null;
	}

	@ModelAttribute("adCreator")
	public Person loadAdCreator(@RequestParam(value="advertisementId", required=false) Integer advertisementId, PortletRequest request) {
		if (advertisementId != null) {
			Advertisement advertisement = modelService.getAdvertisement(advertisementId);
			Person adCreator = null;
			try {
				adCreator = userService.getUserByUserID(request, advertisement.getCreatorUid());
			} catch (Exception e) {
				adCreator = new Person();
				adCreator.setUserId(advertisement.getCreatorUid());
				adCreator.setEmail("");
				adCreator.setName("");
				adCreator.setPhone("");
				adCreator.setUnitCode("");
			}
			return adCreator;
		}
		return null;
	}
	
	
	@RenderMapping(params="page=viewAd")
	public String viewAd(@RequestParam(value="previousPage", required=false) String previousPage, 
						@RequestParam(value="pageIdx", required=false) String pageIdx, Model model) {
		logger.debug("viewAd start");
		if (previousPage != null) {
			model.addAttribute("previousPage", previousPage);
		}
		if (pageIdx != null) {
			model.addAttribute("pageIdx", pageIdx);
		}
		
		return "view_ad";
	}
}
