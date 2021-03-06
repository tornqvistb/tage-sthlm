package se.goteborg.retursidan.portlet.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.form.Feedback;

@Component
public class FeedbackValidator implements Validator {
	private static Log logger = LogFactoryUtil.getLog(FeedbackValidator.class);

	@Autowired
	PersonValidator personValidator;
	
	@Override
	public boolean supports(Class<?> clz) {
		return Feedback.class.isAssignableFrom(clz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Feedback feedback = (Feedback)obj;

		if (StringUtils.isEmpty(feedback.getMessage())) {
			errors.rejectValue("message", "message.mandatory");
		}

		ValidationUtils.invokeValidator(personValidator, feedback.getContact(), errors);
		
		if (errors.hasErrors()) {
			logger.info(errors.toString());
		}
	}

}
