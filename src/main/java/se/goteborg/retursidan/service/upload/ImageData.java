package se.goteborg.retursidan.service.upload;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;

public class ImageData {
	/*
	 * if (orientation.equals("6")) {
			img = id.rotate(Rotation.CW_90);
		} else if (orientation.equals("3")) {
			img = id.rotate(Rotation.CW_180);
		} else if (orientation.equals("8")) {
			img = id.rotate(Rotation.CW_270);
		} else {
			img = createImageFromBytes(bais);
		}
	 */
	protected final static String ORIENTATION_NORMAL = "1";
	protected final static String ORIENTATION_ROTATED_90_LEFT = "6";
	protected final static String ORIENTATION_ROTATED_180 = "3";
	protected final static String ORIENTATION_ROTATED_90_RIGHT = "8";
	
	
	private Logger logger = Logger.getLogger(ImageData.class.getName());
    private byte[] imageData;

    public ImageData(byte[] imageDataPar) throws IOException {
        imageData  = imageDataPar;
    }

    protected synchronized BufferedImage rotate(Rotation rotation) throws IOException, ImageReadException, ImageWriteException {

    	BufferedImage image = readImage(imageData);

        TiffImageMetadata metadata = readExifMetadata(imageData);
        
        image = Scalr.rotate(image, rotation);

        byte[] rotatedData = writeJPEG(image);

        if (metadata != null) {
            this.imageData = writeExifMetadata(metadata, rotatedData);
        } else {
            this.imageData = rotatedData;
        }
                
        return image;
    }

    
    private TiffImageMetadata readExifMetadata(byte[] jpegData) throws ImageReadException, IOException {
    	ImageMetadata imageMetadata = Imaging.getMetadata(jpegData);
        if (imageMetadata == null) {
            return null;
        }
        JpegImageMetadata jpegMetadata = (JpegImageMetadata)imageMetadata;
        TiffImageMetadata exif = jpegMetadata.getExif();
        if (exif == null) {
            return null;
        }
        return exif;
    }


    private byte[] writeExifMetadata(TiffImageMetadata metadata, byte[] jpegData) 
                                throws ImageReadException, ImageWriteException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new ExifRewriter().updateExifMetadataLossless(jpegData, out, metadata.getOutputSet());
        out.close();
        return out.toByteArray();
    }


    private BufferedImage readImage(byte[] data) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(data));
    }

    private byte[] writeJPEG(BufferedImage image) throws IOException {
        ByteArrayOutputStream jpegOut = new ByteArrayOutputStream();
        ImageIO.write(image, "JPEG", jpegOut);
        jpegOut.close();
        return jpegOut.toByteArray();
    }

    protected void printAllExifData() throws ImageReadException, IOException {
    	        
			final ImageMetadata metadata = Imaging.getMetadata(this.imageData);

			logger.finest("Metadata:");
			logger.finest(metadata.toString());
    }    

    protected String getOrientation() throws ImageReadException, IOException {
        
    	String result = ORIENTATION_NORMAL;
    	
		final ImageMetadata metadata = Imaging.getMetadata(this.imageData);
		if (metadata != null &&  metadata.getItems() != null &&  metadata.getItems().size() > 0) {
			for (ImageMetadataItem item : metadata.getItems()) {
				if (item.toString().startsWith("Orientation")) {
					result = item.toString().substring(13);
				}
			}
		}
    	return result;

    }    
    
}