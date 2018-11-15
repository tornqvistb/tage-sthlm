package se.goteborg.retursidan.portlet.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.entity.Unit;
import se.goteborg.retursidan.portlet.controller.CreateAdController;

@Component
public class UnitValidator implements Validator {
	private static Log logger = LogFactoryUtil.getLog(UnitValidator.class);
	
	@Override
	public boolean supports(Class<?> clz) {
		return Unit.class.isAssignableFrom(clz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "unit.name.missing");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "administrationId", "unit.administrationId.missing");
	
		if (errors.hasErrors()) {
			logger.info(errors.toString());
		}
	}

}
