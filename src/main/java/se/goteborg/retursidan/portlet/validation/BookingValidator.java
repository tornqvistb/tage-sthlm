package se.goteborg.retursidan.portlet.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.model.form.Booking;

@Component
public class BookingValidator implements Validator {
	private static Log logger = LogFactoryUtil.getLog(BookingValidator.class);

	@Autowired
	PersonValidator personValidator;
	
	@Override
	public boolean supports(Class<?> clz) {
		return Booking.class.isAssignableFrom(clz);
	}

	@Override
	public void validate(Object obj, Errors errors) {
		Booking booking = (Booking)obj;

		if (!booking.isTransportationConfirmed()) {
			errors.rejectValue("transportationConfirmed", "transportationConfirmed.mandatory");
		}

		ValidationUtils.invokeValidator(personValidator, booking.getContact(), errors);
		
		if (errors.hasErrors()) {
			logger.info(errors.toString());
		}
	}

}
