/**
 * 
 */
package com.nera.nms.controllers;

import com.nera.nms.components.ErrorHandlerComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Martin Do
 *
 */
@Controller
public class ErrorHandlerController implements ErrorController{


    @Autowired
    ErrorHandlerComponent errorHandlerComponent;
    @GetMapping("/error")
    public String handleError(Model model, HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        // init response error status code
        errorHandlerComponent.settingErrorModel(statusCode, model);
 
        return "errors/common_error";
    }
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @GetMapping("/403")
    public String handleAccessDenied() {
        return "/errors/403";
    }
}
