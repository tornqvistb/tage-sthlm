package se.goteborg.retursidan.portlet.controller.config;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.PagedList;
import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.model.entity.Advertisement.Status;
import se.goteborg.retursidan.portlet.controller.CreateAdController;
import se.goteborg.retursidan.portlet.controller.PhotoBaseController;
import se.goteborg.retursidan.util.DateHelper;

@Controller
@RequestMapping("EDIT")
public class AdminAdsController extends PhotoBaseController {
	private static Log logger = LogFactoryUtil.getLog(CreateAdController.class);
	
	@RenderMapping(params="page=adminAds")
	public String adminAds( @RequestParam(value="pageIdx", required=false) Integer pageIdx, RenderRequest request, RenderResponse response, Model model) {
	    logger.debug("params page = adminAds");
	    logger.debug("params pageIdx = " + pageIdx);
	    logger.debug("page size = " + getConfig(request).getPageSizeInt());
	    PagedList<Advertisement> pagedListTemp = modelService.getAllAdvertisements(null, (pageIdx != null) ? pageIdx : 1, getConfig(request).getPageSizeInt());
	    logger.debug("number of pages = " + pagedListTemp.getNumberOfPages());
	    if (pageIdx != null && pageIdx > pagedListTemp.getNumberOfPages()) {
	    	pageIdx = (int)pagedListTemp.getNumberOfPages();
	    	if(pageIdx == 0) pageIdx = 1;
	    }
		PagedList<Advertisement> pagedList = modelService.getAllAdvertisements(null, (pageIdx != null) ? pageIdx : 1, getConfig(request).getPageSizeInt());
		model.addAttribute("ads", pagedList);
		return "config/admin_ads";
	}	
		
	@ActionMapping("selectAdsPage")
	public void selectAds(@RequestParam(value="pageIdx", required=false) String pageIdx, Model model, ActionRequest request, ActionResponse response) {
	    logger.debug("params page = adminAds");
		response.setRenderParameter("page", "adminAds");
		response.setRenderParameter("pageIdx", pageIdx);
	}
	
	@ActionMapping("removeAd")
	public void removeAd(@RequestParam(value="advertisementId", required=true) Integer advertisementId, ActionResponse response) {
	    logger.debug("params page = removeAd");
		Advertisement ad = modelService.getAdvertisement(advertisementId);
		modelService.removeAdvertisement(ad);
		response.setRenderParameter("page", "adminAds");
	}
	
	@ActionMapping("expireAd")
	public void expireAd(@RequestParam(value="advertisementId", required=true) Integer advertisementId, ActionResponse response) {
	    logger.debug("params page = expireAd");
		Advertisement ad = modelService.getAdvertisement(advertisementId);
		ad.setStatus(Status.EXPIRED);
		modelService.updateAd(ad);
		response.setRenderParameter("page", "adminAds");
	}
	@ActionMapping("republishAd")
	public void republishAd(@RequestParam(value="pageIdx", required=false) Integer pageIdx, 
							@RequestParam(value="advertisementId", required=true) Integer advertisementId, ActionResponse response) {
	    logger.debug("params page = republishAd");
		Advertisement ad = modelService.getAdvertisement(advertisementId);
		ad.setStatus(Status.PUBLISHED);
		ad.setPublishDate(DateHelper.getCurrentDate());
		modelService.updateAd(ad);
		response.setRenderParameter("page", "adminAds");
		response.setRenderParameter("pageIdx", String.valueOf(pageIdx));
	}		
	
}
