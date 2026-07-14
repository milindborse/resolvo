package com.resolvo.backend.exception;

/**
 * Wraps any failure from the image upload provider (Cloudinary) with a
 * message safe to show a client - the raw provider exception (which may
 * include internal details) is kept as the cause for server-side logs only.
 */
public class ImageUploadException extends RuntimeException {
    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}