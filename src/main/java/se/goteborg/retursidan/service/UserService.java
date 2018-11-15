package se.goteborg.retursidan.service;

import javax.portlet.PortletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import se.goteborg.retursidan.dao.UnitDAO;
import se.goteborg.retursidan.model.entity.Person;
import se.goteborg.retursidan.model.entity.Unit;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserService {

	@Autowired
	private UnitDAO unitDAO;
	
	private static Log logger = LogFactoryUtil.getLog(UserService.class);
	private static final String UNKNOWN = "okänd";
	
	private Person getUnknownPerson() {
		Person person = new Person();
		person.setUserId(UNKNOWN);
		person.setEmail(UNKNOWN);
		person.setName(UNKNOWN);
		return person;
	}
	
	private Person userToPerson(User user) {
		Person person = new Person();
		if (user != null) {
			person.setUserId(user.getScreenName());
			person.setEmail(user.getEmailAddress());
			person.setName(user.getFullName());
			person.setUnitCode(user.getJobTitle());
		} else {
			logger.info("Returning unknown person.");
			person = getUnknownPerson();
		}
		return person;
	}
	
	public Person getCurrentUser(PortletRequest request) {
		ThemeDisplay td = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		User user = td.getUser();
		return userToPerson(user);
	}

	public String getCurrentUserId(PortletRequest request) {
		ThemeDisplay td = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		User user = td.getUser();		
		if (user != null) {
			return user.getScreenName();
		} else {
			return "";
		}
	}

	public Unit getUsersUnit(PortletRequest request) {
		ThemeDisplay td = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		User user = td.getUser();		
		Unit unit = null;
		if (user != null) {
			String adminId = user.getJobTitle();
			if (adminId != null) {
				unit = unitDAO.findByAdministrationId(adminId);
			}
		}
		return unit;
	}

	
	public Person getUserByUserID(PortletRequest request, String userId) {
		Person person = new Person();
		ThemeDisplay td = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			User user = UserLocalServiceUtil.getUserByScreenName(td.getCompanyId(), userId);
			person = userToPerson(user);
		} catch (PortalException e) {			
			logger.error("Error when looking for user in portal.", e);
			e.printStackTrace();
			person = getUnknownPerson();
		} catch (SystemException e) {
			logger.error("Error when looking for user in portal.", e);
			e.printStackTrace();
			person = getUnknownPerson();
		}
		return person;
	}	
}
