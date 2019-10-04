package se.goteborg.retursidan.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import se.goteborg.retursidan.exceptions.AdvertisementPhotoException;
import se.goteborg.retursidan.model.DateSpan;
import se.goteborg.retursidan.model.PagedList;
import se.goteborg.retursidan.model.entity.Advertisement;
import se.goteborg.retursidan.model.entity.Advertisement.Status;
import se.goteborg.retursidan.model.entity.Category;
import se.goteborg.retursidan.model.entity.Photo;
import se.goteborg.retursidan.model.entity.Unit;
import se.goteborg.retursidan.util.StringFormatter;

/**
 * Data access object for the Advertisement entity objects
 *
 */
@Repository
public class AdvertisementDAO extends BaseDAO<Advertisement> {
	
	
	@Autowired
	private PhotoDAO photoDAO;

	/**
	 * @see BaseDAO#findById(int)
	 */
	@Override
	public Advertisement findById(int id) throws AdvertisementPhotoException{
		Advertisement advertisement = (Advertisement)getSessionFactory().getCurrentSession().get(Advertisement.class, id);
		if (advertisement != null) {
			replaceProxiedPhoto(advertisement);
		}
		return advertisement;
	}

	/**
	 * @see BaseDAO#findAll()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Advertisement> findAll() {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Advertisement.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.addOrder(Order.desc("created"));
		return replaceProxiedPhotos(criteria.list());
	}

	/**
	 * Returns a list of advertisements på Unit and Period.
	 * @param unit
	 * @param span
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Advertisement> findByCreatorUnitAndPeriodOrderByCreator(String unitCode, DateSpan span) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Advertisement.class);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		if (unitCode != null) {
			criteria.add(Restrictions.eq("creatorUnitCode", unitCode));
		}
		if (span != null) {
			criteria.add(Restrictions.ge("created", span.getFromDate()));
		    criteria.add(Restrictions.le("created",  span.getToDate()));
		}
		criteria.addOrder(Order.asc("creatorUid"));
		return criteria.list();
	}

	/**
	 * Returns a list of advertisements på Unit and Period.
	 * @param unit
	 * @param span
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Advertisement> findByUnitAndPeriodOrderByContactEmail(Unit unit, DateSpan span) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Advertisement.class, "a");
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		if (unit != null) {
			criteria.add(Restrictions.eq("unit", unit));
		}
		if (span != null) {
			criteria.add(Restrictions.ge("created", span.getFromDate()));
		    criteria.add(Restrictions.le("created",  span.getToDate()));
		}
		criteria.createAlias("a.contact", "c");
		criteria.addOrder(Order.asc("c.email"));
		return criteria.list();
	}

	
	/**
	 * Retrieve a paged list of advertisements from the database, filtered using the method parameters
	 * @param creatorUid The userId of the author of the advertisement, or null for all authors
	 * @param status The advertisement status, or null for all statuses
	 * @param topCategory The top category for the advertisements, or null for all categories
	 * @param category The sub category for the advertisements, or null for all sub categories
	 * @param unit The unit of the advertisement, or null for all units
	 * @param usingDisplayOption If true and no unit is set, fetch only advertisements aimed for the entire city 	
	 * @param page The page number
	 * @param pageSize The size of each page
	 * @return a PagedList containing the results
	 */
	@SuppressWarnings("unchecked")
	public PagedList<Advertisement> find(String creatorUid, Status status, Category topCategory, Category category, Unit unit, String bookerUid, String searchString, boolean usingDisplayOption, int page, int pageSize) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Advertisement.class, "a");
		if (creatorUid != null) {
			criteria.add(Restrictions.eq("creatorUid", creatorUid));
		}
		if (status != null) {
			criteria.add(Restrictions.eq("status", status));
		}
		if (topCategory != null && topCategory.getId() != -1) {
			criteria.createCriteria("category").add(Restrictions.eq("parent", topCategory));
			if (category != null && category.getId() != -1) {
				criteria.add(Restrictions.eq("category", category));
			}
		}
		if (unit  != null && unit.getId() != -1) {
			criteria.add(Restrictions.eq("unit", unit));
		} else if (usingDisplayOption){
			criteria.add(Restrictions.eq("displayOption", Advertisement.DisplayOption.ENTIRE_CITY));
		}
		
		// Search string
		if (StringUtils.isNotEmpty(searchString)) {
			String[] searchParts = searchString.split(" ");
			criteria.createAlias("a.contact", "c");
			for (String part : searchParts) {
				part = part.trim();
				Criterion likeTitle = Restrictions.like("title", "%" + part + "%");
				Criterion likeDescription = Restrictions.like("description", "%" + part + "%");
				Criterion likeCreatorUid = Restrictions.like("creatorUid", "%" + part + "%");				
				Criterion likeContactName = Restrictions.like("c.name", "%" + part + "%");
				Criterion likeContactPhone = Restrictions.like("c.phone", "%" + part + "%");
				Criterion likeContactEmail = Restrictions.like("c.email", "%" + part + "%");
				Criterion likePickupAddress = Restrictions.like("pickupAddress", "%" + part + "%");
				Criterion likePickupConditions = Restrictions.like("pickupConditions", "%" + part + "%");
				Criterion equalsID;
				if (StringFormatter.isInteger(part)) {
					equalsID = Restrictions.eq("id", Integer.parseInt(part));
				} else {
					equalsID = Restrictions.eq("id", -1);
				}
							
				criteria.add(Restrictions.or(likeTitle, likeDescription, likeCreatorUid, likeContactName, 
						likeContactPhone, likeContactEmail, likePickupAddress, likePickupConditions, equalsID));
			}
		}
		
		// Booker UID
		if (bookerUid != null) {
			criteria.createAlias("a.booker", "b");
			criteria.add(Restrictions.eq("b.userId", bookerUid));
		}
		
		// get the row count for this query
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		criteria.setProjection(Projections.rowCount());
		long totalCount = (Long)criteria.list().get(0);
		
		// clear the projection and set the result transformer again
		criteria.setProjection(null);
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		criteria.addOrder(Order.desc("publishDate"));
		criteria.setMaxResults(pageSize);
		criteria.setFetchSize(pageSize);
		criteria.setFirstResult((page - 1) * pageSize);
		List<Advertisement> list = criteria.list();
				
		return new PagedList<Advertisement>(replaceProxiedPhotos(list), page, pageSize, (int)totalCount);
	}
	
	/**
	 * Replace all photo objects attached to this advertisement with new, non-proxied objects. This is needed in order to 
	 * avoid errors regarding missing hibernate session when the photo objects are accessed in the JSP pages. We do not need the 
	 * proxied objects because we are not interested of the image byte content but only the id's of the images, in order to construct
	 * photo serving resource URL's. 
	 * @param advertisement The advertisement for which all photos should be replaced
	 * @return The advertisement with replaced photos
	 */
	private Advertisement replaceProxiedPhoto(Advertisement advertisement) throws AdvertisementPhotoException {
		try {
			List<Photo> photos = advertisement.getPhotos();
			List<Photo> newPhotos = new ArrayList<Photo>();
			for (Photo photo : photos) {
				Photo newPhoto = new Photo();
				newPhoto.setId(photo.getId());
				newPhotos.add(newPhoto);
			}
			advertisement.setPhotos(newPhotos);
			return advertisement;
		} catch (Exception e) {
			throw new AdvertisementPhotoException(advertisement.getId());
		}
	}	
	/**
	 * Replace all photos for all advertisements in a list
	 * @see AdvertisementDAO#replaceProxiedPhoto(Advertisement)
	 * @param list The list of advertisements
	 * @return The list with advertisements that has been updated with replaced photos
	 */
	private List<Advertisement> replaceProxiedPhotos(List<Advertisement> list) {
		for (Advertisement advertisement : list) {
			replaceProxiedPhoto(advertisement);
		}
		return list;
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain datespan
	 * @return the number of advertisements
	 */
	public Integer count(DateSpan span) {
		return count(null, null, span, null, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain datespan and unit
	 * @return the number of advertisements
	 */
	public Integer count(DateSpan span, Unit unit) {
		return count(null, unit, span, null, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain status and unit
	 * @return the number of advertisements
	 */
	public Integer count(Status status, DateSpan span) {
		return count(status, null, span, null, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain datespan, unit and status
	 * @return the number of advertisements
	 */
	public Integer count(Status status, DateSpan span, Unit unit) {
		return count(status, unit, span, null, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain datespan and category
	 * @return the number of advertisements
	 */
	public Integer count(DateSpan span, Category category) {
		return count(null, null, span, category, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain category
	 * @return the number of advertisements
	 */
	public Integer count(Category category) {
		return count(null, null, null, category, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain category, datespan and status
	 * @return the number of advertisements
	 */
	public Integer count(Status status, DateSpan span, Category category) {
		return count(status, null, span, category, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain category, datespan and status
	 * @return the number of advertisements
	 */
	public Integer count(Status status, Category category) {
		return count(status, null, null, category, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain unit, datespan and category
	 * @return the number of advertisements
	 */
	public Integer count(Unit unit, DateSpan span, Category category) {
		return count(null, unit, span, category, null, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain datespan and creatorUid
	 * @return the number of advertisements
	 */
	public Integer countByCreator(DateSpan span, String creatorUid) {
		return count(null, null, span, null, creatorUid, null, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain datespan and creatorUid
	 * @return the number of advertisements
	 */
	public Integer countByContact(DateSpan span, String contactEmail) {
		return count(null, null, span, null, null, null, contactEmail);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain datespan and creatorUid
	 * @return the number of advertisements
	 */
	public Integer countByBooker(DateSpan span, String bookerUid) {
		return count(null, null, span, null, null, bookerUid, null);
	}
	/**
	 * Retrieve the count of all advertisements in the database with a certain status, datespan and creatorUid
	 * @return the number of advertisements
	 */
	public Integer count(Status status, DateSpan span, String creatorUid) {
		return count(null, null, span, null, creatorUid, null, null);
	}	
	/**
	 * Retrieve the count of all advertisements in the database with a certain status and unit
	 * @return the number of advertisements
	 */
	public Integer count(Status status, Unit unit) {
		return count(status, unit, null, null, null, null, null);
	}	
	
	/**
	 * Retrieve the count of all advertisements in the database
	 * @return the number of advertisements
	 */
	public Integer count() {
		return count(null, null, null, null, null, null, null);
	}

	/**
	 * Retrieve the count of all advertisements for the specified unit
	 * @param unit The unit to use as filter
	 * @return the number of advertisements
	 */
	public Integer count(Unit unit) {
		return count(null, unit, null, null, null, null, null);
	}

	/**
	 * Retrieve the count of all advertisements for the specified status
	 * @param status The status to use as a filter
	 * @return the number of advertisements
	 */
	public Integer count(Status status) {
		return count(status, null, null, null, null, null, null);
	}

	/**
	 * Count the number of advertisements in the database, filtered using the provided method parameters
	 * @param status The status to filter with, or null for all statuses
	 * @param unit The unit to filter with, or null for all units
	 * @return the number of advertisements
	 */
	private Integer count(Status status, Unit unit, DateSpan span, Category category, String creatorUid, String bookerUid, String contactEmail) {
		Criteria criteria = getSessionFactory().getCurrentSession().createCriteria(Advertisement.class, "a");
		if (unit != null) {
			criteria.add(Restrictions.eq("unit", unit));
		}
		if (status != null) {
			criteria.add(Restrictions.eq("status", status));
		}
		if (span != null) {
			criteria.add(Restrictions.ge("created", span.getFromDate()));
		    criteria.add(Restrictions.le("created",  span.getToDate()));
		}
		if (category != null) {
			criteria.add(Restrictions.eq("category", category));
		}
		if (creatorUid != null) {
			criteria.add(Restrictions.eq("creatorUid", creatorUid));
		}
		if (bookerUid != null) {
			criteria.add(Restrictions.eq("bookerUid", bookerUid));
		}
		if (contactEmail != null) {
			criteria.createAlias("a.contact", "c");
			criteria.add(Restrictions.eq("c.email", contactEmail));
		}
		return ((Number)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
	}

	/**
	 * Expire any ad that is older than the provided amount of days
	 * @param days The maximum number of days 
	 * @return the number of ads removed
	 */
	public int expireOldAds(int days) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, -days);
		Date maxDate = cal.getTime();
		Query query = getSessionFactory().getCurrentSession().createQuery("UPDATE Advertisement SET status=:status WHERE publishDate <= :maxDate AND status=:publishStatus");
		query.setParameter("status", Status.EXPIRED);
		query.setParameter("publishStatus", Status.PUBLISHED);
		query.setDate("maxDate", maxDate);
		return query.executeUpdate();
	}
	
	@Override
	/**
	 * Delete an entity from the database
	 * @param entity the entity to delete
	 */
	public void delete(Advertisement advertisement) {
		for (Photo photo : advertisement.getPhotos()) {
			Query query = getSessionFactory().getCurrentSession().createSQLQuery( "delete from photoset where photos_id=:id")
		            .setParameter("id", photo.getId()); 
			query.executeUpdate();
			query = getSessionFactory().getCurrentSession().createSQLQuery( "delete from photo where id=:id")
		            .setParameter("id", photo.getId()); 
			query.executeUpdate();
		}
		Query query = getSessionFactory().getCurrentSession().createSQLQuery( "delete from advertisement where id=:id")
	            .setParameter("id", advertisement.getId()); 
		query.executeUpdate();
	}

	 /**
	  * Counts ads by category
	  * 
	  * @param category
	  * @return
	  */
	public Integer countByCategory(Category category) {
		String hql = "select count(*) from Advertisement ad"
            + " where ad.category.id = :id";
		Query query = getSessionFactory().getCurrentSession().createQuery(hql).setParameter("id", category.getId());
		return ((Number) query.uniqueResult()).intValue();
	}
	
}
