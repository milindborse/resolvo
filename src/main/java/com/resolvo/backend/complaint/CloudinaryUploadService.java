package com.resolvo.backend.complaint;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadService {

    private final Cloudinary cloudinary;

    /**
     * Uploads to Cloudinary and returns only the secure URL - the raw file
     * bytes never touch our own database, per the spec's storage requirement.
     */
    @SuppressWarnings("unchecked")
    public String upload(MultipartFile file) {
        try {
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "resolvo/complaints"));
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary: " + e.getMessage(), e);
        }
    }
}
