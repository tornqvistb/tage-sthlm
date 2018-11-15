package se.goteborg.retursidan.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import se.goteborg.retursidan.dao.AdvertisementDAO;
import se.goteborg.retursidan.exceptions.AdvertisementAlreadyBookedException;
import se.goteborg.retursidan.exceptions.AdvertisementBookingFailedException;
import se.goteborg.retursidan.exceptions.AdvertisementExpiredException;
import se.goteborg.retursidan.exceptions.AdvertisementNotFoundException;
import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.model.entity.Person;
import se.goteborg.retursidan.model.form.Config;
import se.goteborg.retursidan.model.form.MailComposition;
import se.goteborg.retursidan.model.form.Texts;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BookingService {
	private static Log logger = LogFactoryUtil.getLog(BookingService.class);
	
	@Autowired
	private AdvertisementDAO advertisementDAO;

	@Autowired
	private MailService mailService;

	
	public void bookAdvertisement(Integer advertisementId, Person contact, Texts texts, Config config, String adLink, Person currentUser) throws AdvertisementBookingFailedException {		
	
		Advertisement advertisement = advertisementDAO.findById(advertisementId);
		
		if (advertisement == null) {
			logger.error("Could not find advertisement with id=" + advertisementId + " to book.");
			throw new AdvertisementNotFoundException(advertisementId);
		}
					
		// make sure only one thread can book at a time
		synchronized(this) {
			if (Advertisement.Status.BOOKED.equals(advertisement.getStatus())) {
				logger.error("Advertisement with id=" + advertisementId + " is already booked.");
				throw new AdvertisementAlreadyBookedException(advertisementId);
			} else if (Advertisement.Status.EXPIRED.equals(advertisement.getStatus())) {
				logger.error("Advertisement with id=" + advertisementId + " is expired and can not be booked.");
				throw new AdvertisementExpiredException(advertisementId);
			}
			advertisement.setBooker(contact);
			advertisement.setStatus(Advertisement.Status.BOOKED);
			advertisement.setBookingTime(new Date());
			advertisement.setBookerUid(currentUser.getUserId());
			advertisement.setBookerName(currentUser.getName());
			advertisement.setBookerUnitCode(currentUser.getUnitCode());
			advertisement.setBookerMail(currentUser.getEmail());
			
			advertisementDAO.merge(advertisement);
			
			logger.debug("Advertisement with id=" + advertisementId + " merged.");
			
			int count = 0;
			if (advertisement.getCount() != null)
				count = advertisement.getCount();
			
			MailComposition composition = new MailComposition(null,
					texts.getMailSenderAddress(),
					null,	"",
					texts.getMailSubject(),
					advertisement.getTitle(),
					contact.getName(),
					contact.getPhone(),
					contact.getEmail(),
					advertisement.getContact().getName(),
					advertisement.getContact().getPhone(),
					advertisement.getContact().getEmail(),
					"",	"",	"",	"",	"",	"", "",
					adLink,
					texts.getMailBody(),
					config.getRulesUrl(),
					config.getLogoUrl(),
					advertisementId,
					count);

			if (!advertisement.getPhotos().isEmpty()) {
				composition.setPhoto(advertisement.getPhotos().get(0));
			}
			
			String bookerMail = contact.getEmail();
			String advertiserMail = advertisement.getContact().getEmail();
						
			composition.setReveiverAdress(new String[] {bookerMail});
			composition.setReplyToAdress(advertiserMail);
			mailService.composeAndSendMail(composition);
			composition.setReveiverAdress(new String[] {advertiserMail});
			composition.setReplyToAdress(bookerMail);
			mailService.composeAndSendMail(composition);
			
			logger.debug("Mails sent at booking for Advertisement with id=" + advertisementId);
						
		}
	}

}
