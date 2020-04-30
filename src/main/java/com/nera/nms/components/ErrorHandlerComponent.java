/**
 * 
 */
package com.nera.nms.components;

import com.nera.nms.constants.CommonConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

/**
 * @author Martin Do
 *
 */
@Component
@PropertySource("classpath:messages.properties")
public class ErrorHandlerComponent {

    private static final String STATUS_CODE = "statusCode";

    @Value("${message.403}")
    private String message403;

    @Value("${message.404}")
    private String message404;

    @Value("${message.500}")
    private String message500;

    public void settingErrorModel(Integer statusCode, Model model) {
        switch (statusCode) {
            case 403:
                model.addAttribute(STATUS_CODE, 403);
                model.addAttribute(CommonConstants.MESSAGE, message403);
            break;

            case 404:
                model.addAttribute(STATUS_CODE, 404);
                model.addAttribute(CommonConstants.MESSAGE, message404);
            break;

            case 500:
                model.addAttribute(STATUS_CODE, 500);
                model.addAttribute(CommonConstants.MESSAGE, message500);
            break;
            default:
        }
    }
}
