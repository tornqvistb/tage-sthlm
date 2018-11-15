package se.goteborg.retursidan.portlet.controller.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.portlet.controller.BaseController;

@Controller
@RequestMapping("EDIT")
public class StartController extends BaseController {
	private static Log logger = LogFactoryUtil.getLog(StartController.class);
	
	@RenderMapping
	public String render() {
	    logger.debug("entering default render");
		return "config/start";
	}
}
