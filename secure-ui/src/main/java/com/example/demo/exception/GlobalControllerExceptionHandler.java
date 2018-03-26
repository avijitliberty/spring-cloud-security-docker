package com.example.demo.exception;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalControllerExceptionHandler {
	
	private static Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(HttpClientErrorException exception, HttpServletRequest request, final Model model) {
        logger.error("Exception during execution of SpringSecurity application handleNotFoundException", exception);
               
        model.addAttribute("errorCode", "Error " + exception.getStatusCode());
        logger.error("HTTPStatus: " + exception.getStatusCode());
        
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<ul>");
        errorMessage.append("<li>").append(escapeTags(exception.getMessage())).append("</li>");
        errorMessage.append("</ul>");
        
        model.addAttribute("errorMessage", errorMessage.toString());//
        
        return "error";
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException exception, HttpServletRequest request, final Model model) {
        logger.error("Exception during execution of SpringSecurity application handleAccessDeniedException", exception);
               
        model.addAttribute("errorCode", "Error " + HttpStatus.FORBIDDEN);
        logger.error("errorCode: " + HttpStatus.FORBIDDEN);
        
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<ul>");
        errorMessage.append("<li>").append(escapeTags(exception.getMessage())).append("</li>");
        errorMessage.append("</ul>");
        model.addAttribute("errorMessage", errorMessage.toString());//
        
        return "error";
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnknownException(Exception ex, HttpServletRequest request, final Model model) {
        logger.error("Exception during execution of SpringSecurity application handleUnknownException", ex);
               
        model.addAttribute("errorCode", "Error " + ex.getCause());
        logger.error("Exception Caused by: " + ex.getCause());
        
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<ul>");
        errorMessage.append("<li>").append(escapeTags(ex.getMessage())).append("</li>");
        errorMessage.append("</ul>");
        
        model.addAttribute("errorMessage", errorMessage.toString());//
        
        return "error";
    }
    
    @ExceptionHandler(OAuth2Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleOAuth2Exception(OAuth2Exception ex, HttpServletRequest request, final Model model) {
        logger.error("Exception during execution of SpringSecurity application handleOAuth2Exception", ex);
               
        model.addAttribute("errorCode", "Error " + OAuth2Exception.ERROR);
        logger.error("Exception Caused by: " + OAuth2Exception.ERROR);
        
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<ul>");
        errorMessage.append("<li>").append(escapeTags(ex.getMessage())).append("</li>");
        errorMessage.append("</ul>");
        
        model.addAttribute("errorMessage", errorMessage.toString());//
        
        return "error";
    }
    
    /** Substitute 'less than' and 'greater than' symbols by its HTML entities. */
    private String escapeTags(String text) {
        if (text == null) {
            return null;
        }
        return text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }
}
