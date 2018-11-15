package se.goteborg.retursidan.portlet.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.entity.Advertisement;

@Component
public class AdValidator implements Validator {
	private static Log logger = LogFactoryUtil.getLog(AdValidator.class);
	
	@Override
	public boolean supports(Class<?> clz) {
		return Advertisement.class.isAssignableFrom(clz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Advertisement ad = (Advertisement)obj;

		if(ad.getTopCategory() == null || ad.getTopCategory().getId() == -1) {
			errors.rejectValue("topCategory", "topCategory.missing");
		}
		
		if(ad.getCategory() == null || ad.getCategory().getId() == -1) {
			errors.rejectValue("category", "category.missing");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "title.missing");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "description.missing");
		
		PersonValidator personValidator = new PersonValidator();
		ValidationUtils.invokeValidator(personValidator, ad.getContact(), errors);

		ValidationUtils.rejectIfEmpty(errors, "pickupAddress", "pickupAddress.missing");
		
		ValidationUtils.rejectIfEmpty(errors, "count", "count.missing");
		if (ad.getCount() != null && ad.getCount() <= 0) {
			errors.rejectValue("count", "count.mustbegreaterthanzero");
		}
		
		if (errors.hasErrors()) {
			logger.info(errors.toString());
		}
	}

}
