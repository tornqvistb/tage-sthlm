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

import se.goteborg.retursidan.model.entity.Request;

/**
 * Controller handling manual expire of requests
 *
 */
@Controller
@RequestMapping("VIEW")
public class ExpireRequestController extends BaseController {
	private static Log logger = LogFactoryUtil.getLog(ExpireRequestController.class);

	@ModelAttribute("request")
	public Request getRequest(@RequestParam(value="requestId", required=false) Integer requestId, PortletRequest request) {
		return modelService.getRequest(requestId);
	}
	
	@RenderMapping(params="page=expireRequest")
	public String expireRequest() {
		return "expire_request";
	}

	@ActionMapping("performExpire")
	public void performExpire(@ModelAttribute("request") Request request, ActionRequest portletRequest, ActionResponse portletResponse) {
		logger.debug("Expiring request with id=" + request.getId());
		request.setStatus(Request.Status.EXPIRED);
		modelService.updateRequest(request);
		logger.debug("Reqauest has been expired");
		portletResponse.setRenderParameter("externalPage", "");
	}
}
