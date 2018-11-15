package se.goteborg.retursidan.portlet.validation;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.form.Texts;

@Component
public class TextsValidator implements Validator {
	private static Log logger = LogFactoryUtil.getLog(TextsValidator.class);

	@Override
	public boolean supports(Class<?> clz) {
		return Texts.class.isAssignableFrom(clz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Texts texts = (Texts)obj;

		ValidationUtils.rejectIfEmpty(errors, "confirmCreateAdText", "config.confirmtext.empty");
		ValidationUtils.rejectIfEmpty(errors, "confirmBookingText", "config.confirmtext.empty");
		ValidationUtils.rejectIfEmpty(errors, "confirmRepublishText", "config.confirmtext.empty");
		ValidationUtils.rejectIfEmpty(errors, "confirmRemoveRequestText", "config.confirmtext.empty");
		ValidationUtils.rejectIfEmpty(errors, "bookingConfirmationText", "config.confirmtext.empty");

		ValidationUtils.rejectIfEmpty(errors, "mailSenderAddress", "config.mailsenderaddress.empty");
		if (texts.getMailSenderAddress() != null && texts.getMailSenderAddress().length() > 0 && !EmailValidator.getInstance().isValid(texts.getMailSenderAddress())) {
			errors.rejectValue("mailSenderAddress", "config.mailsenderaddress.badformat");
		}
		ValidationUtils.rejectIfEmpty(errors, "mailSubject", "config.mailsubject.empty");
		ValidationUtils.rejectIfEmpty(errors, "mailBody", "config.mailbody.empty");

		if (errors.hasErrors()) {
			logger.info(errors.toString());
		}
	}

}
