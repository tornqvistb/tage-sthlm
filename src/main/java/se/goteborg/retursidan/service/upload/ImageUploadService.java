package se.goteborg.retursidan.service.upload;

import static org.imgscalr.Scalr.Rotation.CW_180;
import static org.imgscalr.Scalr.Rotation.CW_270;
import static org.imgscalr.Scalr.Rotation.CW_90;
import static se.goteborg.retursidan.service.upload.ImageData.ORIENTATION_ROTATED_180;
import static se.goteborg.retursidan.service.upload.ImageData.ORIENTATION_ROTATED_90_LEFT;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.portlet.ResourceRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Service
public class ImageUploadService {
	private final String PARAM_UPLOAD_FILENAME = "qqfile";
	private Logger logger = Logger.getLogger(ImageUploadService.class.getName());

	public UploadedImage getUploadedImage(ResourceRequest request) throws IOException, FileUploadException, ImageReadException, ImageWriteException {
		UploadedImage upload = new UploadedImage();

		PortletFileUpload pfu = new PortletFileUpload();
		ResourceRequestContext ctx = new ResourceRequestContext(request);
		InputStream is = null;
		String filename = null;
		byte[] bais = null;
		
		// incoming file uploaded as multipart/form-data 
		if(PortletFileUpload.isMultipartContent(ctx)) {
        	logger.log(Level.FINE, "Multi-part upload initiated");
			FileItemIterator iter = pfu.getItemIterator(ctx);
			if(iter.hasNext()) {
				FileItemStream item = iter.next();
				is = item.openStream();
				bais =  IOUtils.toByteArray(is);
				filename = item.getName();
			}
		// incoming file uploaded as application/octet-stream
		} else if ("application/octet-stream".equals(request.getContentType())) {
        	logger.log(Level.FINE, "Octet-stream upload initiated");
			is = request.getPortletInputStream();
			bais =  IOUtils.toByteArray(is);
			filename = request.getParameter(PARAM_UPLOAD_FILENAME);
		} else {
        	logger.log(Level.WARNING, "Invalid file upload, request is neither multipart nor octet-stream");
        	upload.setValidUpload(false);
			return upload;
		}
		ImageData id = new ImageData(bais);
		System.out.println("UploadedImage 5");
		String orientation = id.getOrientation();
		BufferedImage img;
		if (orientation.equals(ORIENTATION_ROTATED_90_LEFT)) {
			img = id.rotate(CW_90);
		} else if (orientation.equals(ORIENTATION_ROTATED_180)) {
			img = id.rotate(CW_180);
		} else if (orientation.equals(ImageData.ORIENTATION_ROTATED_90_RIGHT)) {
			img = id.rotate(CW_270);
		} else {
			img = createImageFromBytes(bais);
		}
		// read uploaded file into a BufferedImage object
		IOUtils.closeQuietly(is);
		
		upload.setValidUpload(true);
		upload.setImage(img);
		// IE sends the complete file path, just use the filename
		if (filename != null) {
			int pos = filename.lastIndexOf("\\");
			if (pos != -1) {
				filename = filename.substring(pos + 1);
			}
		}
		upload.setFilename(filename);
		return upload;
	}
	
	private BufferedImage createImageFromBytes(byte[] imageData) {
	    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	    try {
	        return ImageIO.read(bais);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}

}
