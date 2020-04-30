/**
 * 
 */
package com.nera.nms.repositories.impl;

import com.nera.nms.models.PersistentLogin;
import com.nera.nms.repositories.IPersistentLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository("persistentTokenRepository")
@Transactional
public class PersistentTokenDaoImp implements PersistentTokenRepository {

    @Autowired
    IPersistentLoginRepository iPersistentLoginRepository;

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        PersistentLogin login = new PersistentLogin();
        login.setUsername(token.getUsername());
        login.setSeries(token.getSeries());
        login.setToken(token.getTokenValue());
        login.setLastUsed(token.getDate());
        this.iPersistentLoginRepository.save(login);
        
    }

    @Override
    public void updateToken(String series, String tokenValue, Date lastUsed) {
    	PersistentLogin login = iPersistentLoginRepository.findBySeries(series);
    	if(login != null)
    	{
    		login.setToken(tokenValue);
    		login.setLastUsed(lastUsed);
    		this.iPersistentLoginRepository.save(login);
    	}
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        PersistentLogin login = iPersistentLoginRepository.findBySeries(seriesId);
        if (login != null) {
            return new PersistentRememberMeToken(login.getUsername(), 
                String.valueOf(login.getSeries()), login.getToken(), login.getLastUsed());
          }

        return null;
    }

    @Override
    public void removeUserTokens(String username) {
        List<PersistentLogin> logins = this.iPersistentLoginRepository.findByUsername(username);
        if(logins != null)
        {
        	for (PersistentLogin login : logins) {
        		this.iPersistentLoginRepository.delete(login);
			}
        }
    }

}
