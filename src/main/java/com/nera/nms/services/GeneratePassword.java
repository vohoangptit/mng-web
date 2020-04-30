/**
 * 
 */
package com.nera.nms.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author Martin Do
 *
 */
@Service
public class GeneratePassword {

    public String generatePassword() {
    	String randomPassword = RandomStringUtils.randomAlphanumeric(8);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(randomPassword);
    }
}
