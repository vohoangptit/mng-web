package com.nera.nms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PlaybookExceptionController {

    @ExceptionHandler(value = PlaybookNotFoundException.class)
    public ResponseEntity<Object> playbookNotFoundException(PlaybookNotFoundException exception){
        return new ResponseEntity<>("Playbook not found ", HttpStatus.NOT_FOUND);
    }
}
