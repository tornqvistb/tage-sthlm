package se.goteborg.retursidan.portlet.controller;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.util.DateHelper;

/**
 * Controller handling republish of ads
 *
 */
@Controller
@RequestMapping("VIEW")
public class RepublishAdController extends BaseController {
	private static Log logger = LogFactoryUtil.getLog(RepublishAdController.class);

	@ModelAttribute("advertisement")
	public Advertisement getAdvertisement(@RequestParam(value="advertisementId", required=false) Integer advertisementId, PortletRequest request) {
		return modelService.getAdvertisement(advertisementId);
	}
	
	@RenderMapping(params="page=republishAd")
	public String republishAd() {
		return "republish_ad";
	}

	@ActionMapping("performRepublishing")
	public void performRepublishing(@ModelAttribute("advertisement") Advertisement advertisement, ActionRequest request, ActionResponse response) {
		logger.debug("Republishing ad with id=" + advertisement.getId());
		advertisement.setStatus(Advertisement.Status.PUBLISHED);
		advertisement.setPublishDate(DateHelper.getCurrentDate());
		modelService.updateAd(advertisement);
		logger.debug("Ad republished");
	}
}
