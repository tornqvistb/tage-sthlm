package se.goteborg.retursidan.portlet.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.form.RequestMailContact;

@Component
public class RequestMailContactValidator implements Validator {
	private static Log logger = LogFactoryUtil.getLog(RequestMailContactValidator.class);

	@Autowired
	PersonValidator personValidator;
	
	@Override
	public boolean supports(Class<?> clz) {
		return RequestMailContact.class.isAssignableFrom(clz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		RequestMailContact mailContact = (RequestMailContact)obj;

		if (StringUtils.isEmpty(mailContact.getMessage())) {
			errors.rejectValue("message", "mailMessage.mandatory");
		}

		ValidationUtils.invokeValidator(personValidator, mailContact.getContact(), errors);
		
		if (errors.hasErrors()) {
			logger.info(errors.toString());
		}
	}

}
