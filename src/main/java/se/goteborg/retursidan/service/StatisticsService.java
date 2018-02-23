package se.goteborg.retursidan.service;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import se.goteborg.retursidan.dao.AdvertisementDAO;
import se.goteborg.retursidan.dao.RequestDAO;
import se.goteborg.retursidan.model.DateSpan;
import se.goteborg.retursidan.model.UserStats;
import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.model.entity.Advertisement.Status;
import se.goteborg.retursidan.model.entity.Category;
import se.goteborg.retursidan.model.entity.Request;
import se.goteborg.retursidan.model.entity.Unit;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class StatisticsService {
	
	@Autowired
	private AdvertisementDAO advertisementDAO;

	@Autowired
	private RequestDAO requestDAO;

	public Integer getTotalNumberOfAds() {
		return advertisementDAO.count();
	}

	public Integer getTotalNumberOfRequests() {
		return requestDAO.count();
	}

	public Integer getTotalAdsForUnit(Unit unit) {
		return advertisementDAO.count(unit);
	}

	public Integer getTotalRequestsForUnit(Unit unit) {
		return requestDAO.count(unit);
	}

	public Integer getTotalNumberOfBookedAds() {
		return advertisementDAO.count(Status.BOOKED);
	}

	public Integer getBookedAdsForUnit(Unit unit) {
		return advertisementDAO.count(Status.BOOKED, unit);
	}

	public Integer getTotalNumberOfExpiredAds() {
		return advertisementDAO.count(Status.EXPIRED);
	}

	// Methods for advanced statistics
	public Integer getNumberOfAdsByPeriod(DateSpan span) {
		return advertisementDAO.count(span);
	}
	public Integer getNumberOfAdsForUnitByPeriod(DateSpan span, Unit unit) {
		return advertisementDAO.count(span, unit);
	}
	public Integer getNumberOfBookedAdsByPeriod(DateSpan span) {
		return advertisementDAO.count(Status.BOOKED, span);
	}
	public Integer getNumberOfBookedAdsForUnitByPeriod(DateSpan span, Unit unit) {
		return advertisementDAO.count(Status.BOOKED, span, unit);
	}
	public Integer getNumberOfRequestsByPeriod(DateSpan span) {
		return requestDAO.count(span);
	}
	public Integer getNumberOfRequestsForUnitByPeriod(Unit unit, DateSpan span) {
		return requestDAO.count(unit, span);
	}
	public Integer getNumberOfAdsForCategoryByPeriod(DateSpan span, Category category) {
		return advertisementDAO.count(span, category);
	}
	public Integer getNumberOfAdsForCategory(Category category) {
		return advertisementDAO.count(category);
	}
	public Integer getNumberOfBookedAdsForCategoryByPeriod(DateSpan span, Category category) {
		return advertisementDAO.count(Status.BOOKED, span, category);
	}
	public Integer getNumberOfBookedAdsForCategory(Category category) {
		return advertisementDAO.count(Status.BOOKED, category);
	}
	public Integer getNumberOfAdsForCategoryAndUnitByPeriod(DateSpan span, Category category, Unit unit) {
		return advertisementDAO.count(unit, span, category);
	}
	public Integer getNumberOfRequestsForCategoryAndUnitByPeriod(DateSpan span, Category category, Unit unit) {
		return requestDAO.count(span, category, unit);
	}
	public Integer getNumberOfRequestsForCategoryByPeriod(DateSpan span, Category category) {
		return requestDAO.count(span, category);
	}
	public Integer getNumberOfAdsForPersonByPeriod(DateSpan span, String creatorUid) {
		return advertisementDAO.countByCreator(span, creatorUid);
	}
	public Integer getNumberOfAdsForContactByPeriod(DateSpan span, String contactEmail) {
		return advertisementDAO.countByContact(span, contactEmail);
	}
	public Integer getNumberOfBookedAdsForPersonByPeriod(DateSpan span, String bookerUid) {
		return advertisementDAO.countByBooker(span, bookerUid);
	}
	public Integer getNumberOfRequestsForCreatorByPeriod(DateSpan span, String creatorUid) {
		return requestDAO.count(span, creatorUid);
	}
	/*
	private List<Advertisement> getAdsForUnitByPeriod(DateSpan span, Unit unit) {
		return advertisementDAO.findByUnitAndPeriod(unit, span);
	}
	*/
	public List<UserStats> getUserStatsListByUnitAndPeriodForAdvertiser(DateSpan span, Unit unit) {
		List<Advertisement> ads = advertisementDAO.findByCreatorUnitAndPeriodOrderByCreator(unit.getAdministrationId(), span);
		List<UserStats> userStats = new ArrayList<UserStats>();
		String lastUser = "";
		UserStats stat = new UserStats();
		for (Advertisement ad : ads) {
			if (!lastUser.equals(ad.getCreatorUid())){
				if (StringUtils.isNotEmpty(lastUser)) {
					userStats.add(stat);
				}
				stat = new UserStats();
				stat.setUserId(ad.getCreatorUid());
				stat.setEmail(ad.getCreatorMail());
				stat.setName(ad.getCreatorName());
				stat.setCount(1);
				lastUser = ad.getCreatorUid();
			} else {
				stat.setCount(stat.getCount() + 1);
			}
		}
		// Add last one if not empty
		if (StringUtils.isNotEmpty(stat.getUserId())) {
			userStats.add(stat);
		}
		return userStats;
	}

	public List<UserStats> getUserStatsListByUnitAndPeriodForContact(DateSpan span, Unit unit) {
		List<Advertisement> ads = advertisementDAO.findByCreatorUnitAndPeriodOrderByCreator(unit.getAdministrationId(), span);
		List<UserStats> userStats = new ArrayList<UserStats>();
		String lastUser = "";
		UserStats stat = new UserStats();
		for (Advertisement ad : ads) {
			if (!lastUser.equals(ad.getContact().getEmail())){
				if (StringUtils.isNotEmpty(lastUser)) {
					userStats.add(stat);
				}
				stat = new UserStats();
				stat.setUserId(ad.getContact().getUserId());
				stat.setEmail(ad.getContact().getEmail());
				stat.setName(ad.getContact().getName());
				stat.setCount(1);
				lastUser = ad.getContact().getEmail();
			} else {
				stat.setCount(stat.getCount() + 1);
			}
		}
		// Add last one if not empty
		if (StringUtils.isNotEmpty(stat.getUserId())) {
			userStats.add(stat);
		}
		return userStats;
	}

	
	public List<UserStats> getUserStatsListByUnitAndPeriodForBooker(DateSpan span, Unit unit) {
		List<Advertisement> ads = advertisementDAO.findByCreatorUnitAndPeriodOrderByCreator(unit.getAdministrationId(), span);
		List<UserStats> userStats = new ArrayList<UserStats>();
		String lastUser = "";
		UserStats stat = new UserStats();
		for (Advertisement ad : ads) {
			if (ad.getBooker() != null) {
				if (!lastUser.equals(ad.getBooker().getUserId())) {
					if (StringUtils.isNotEmpty(lastUser)) {
						userStats.add(stat);
					}
					stat = new UserStats();
					stat.setUserId(ad.getBooker().getUserId());
					stat.setEmail(ad.getBooker().getEmail());
					stat.setName(ad.getBooker().getName());
					stat.setCount(1);				
					lastUser = ad.getBooker().getUserId();
				} else {
					stat.setCount(stat.getCount() + 1);
				}
			}
		}
		// Add last one if not empty
		if (StringUtils.isNotEmpty(stat.getUserId())) {
			userStats.add(stat);
		}
		return userStats;
	}

	public List<UserStats> getUserStatsListByUnitAndPeriodForRequester(DateSpan span, Unit unit) {
		List<Request> requests = requestDAO.findByUnitAndPeriodOrderByRequester(unit.getAdministrationId(), span);		
		List<UserStats> userStats = new ArrayList<UserStats>();
		String lastUser = "";
		UserStats stat = new UserStats();
		for (Request req : requests) {
			if (!lastUser.equals(req.getCreatorUid())) {
				if (StringUtils.isNotEmpty(lastUser)) {
					userStats.add(stat);
				}
				stat = new UserStats();
				stat.setUserId(req.getCreatorUid());
				stat.setEmail(req.getCreatorMail());
				stat.setName(req.getCreatorName());
				stat.setCount(1);
				lastUser = req.getCreatorUid();
			} else {
				stat.setCount(stat.getCount() + 1);
			}
		}
		// Add last one if not empty
		if (StringUtils.isNotEmpty(stat.getUserId())) {
			userStats.add(stat);
		}
		return userStats;
	}

}