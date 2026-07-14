package com.resolvo.backend.complaint;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.resolvo.backend.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryUploadService {

    private final Cloudinary cloudinary;

    /**
     * Uploads to Cloudinary and returns only the secure URL - the raw file
     * bytes never touch our own database, per the spec's storage requirement.
     *
     * Cloudinary's SDK throws a plain RuntimeException (not a checked one)
     * for API-level failures - e.g. missing/invalid credentials surface as
     * "cloud_name is disabled". Catching Exception broadly here and
     * re-wrapping as ImageUploadException means a misconfigured Cloudinary
     * account produces a clear, actionable API error instead of a generic
     * 500 - and critically, it no longer takes down complaint creation as
     * an unexplained server error.
     */
    @SuppressWarnings("unchecked")
    public String upload(MultipartFile file) {
        try {
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "resolvo/complaints"));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            log.error("Image upload failed reading file bytes", e);
            throw new ImageUploadException("Could not read the uploaded image file", e);
        } catch (RuntimeException e) {
            log.error("Image upload to Cloudinary failed: {}", e.getMessage(), e);
            throw new ImageUploadException(
                    "Image upload is currently unavailable. Please try again shortly, "
                            + "or submit the complaint without a photo.", e);
        }
    }
}