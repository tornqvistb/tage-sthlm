package se.goteborg.retursidan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.dao.AdvertisementDAO;
import se.goteborg.retursidan.dao.RequestDAO;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class ExpireService {
	private static Log logger = LogFactoryUtil.getLog(ExpireService.class);
	
	@Autowired
	AdvertisementDAO advertisementDAO;
	
	@Autowired
	RequestDAO requestDAO;
	
	public void expireAds(int days) {
		logger.debug("Expiring ads older than " + days + " days.");
		int count = advertisementDAO.expireOldAds(days);
		logger.debug(count + " ads were expired.");
	}

	public void expireRequests(int days) {
		logger.debug("Expiring requests older than " + days + " days.");
		int count = requestDAO.expireOldRequests(days);
		logger.debug(count + " requests were expired.");
	}
}
