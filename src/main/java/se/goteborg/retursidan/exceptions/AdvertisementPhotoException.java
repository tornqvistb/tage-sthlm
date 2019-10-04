package se.goteborg.retursidan.exceptions;

/**
 * Exception thrown when a booking is made on an ad that has already been booked 
 *
 */
public class AdvertisementPhotoException extends AdvertisementException {
	private static final long serialVersionUID = 4945863902575358710L;
	
	public AdvertisementPhotoException(Integer advertisementId) {
		super("Error when retrieving advertisement photos", "error.advertisement.photoError", advertisementId);
	}
}
