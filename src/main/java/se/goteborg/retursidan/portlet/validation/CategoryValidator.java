package se.goteborg.retursidan.portlet.validation;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.entity.Category;

@Component
public class CategoryValidator implements Validator {
	private static Log logger = LogFactoryUtil.getLog(CategoryValidator.class);
	
	@Override
	public boolean supports(Class<?> clz) {
		return Category.class.isAssignableFrom(clz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "category.title.missing");
	
		if (errors.hasErrors()) {
			logger.info(errors.toString());
		}
	}

}
