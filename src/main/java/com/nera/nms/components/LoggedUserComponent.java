/**
 * 
 */
package com.nera.nms.components;

import java.util.List;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.stereotype.Component;

import com.nera.nms.dto.ActiveUserStore;

import lombok.Data;

/**
 * @author Martin Do
 *
 */
@Component
@Data
public class LoggedUserComponent implements HttpSessionBindingListener {
 
    private String username; 
    private ActiveUserStore activeUserStore;
     
    public LoggedUserComponent(String username, ActiveUserStore activeUserStore) {
        this.username = username;
        this.activeUserStore = activeUserStore;
    }
     
    public LoggedUserComponent() {}
 
    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        if (activeUserStore == null)
            return;

        List<String> users = activeUserStore.getUsers();
        LoggedUserComponent user = (LoggedUserComponent) event.getValue();
        if (!users.contains(user.getUsername())) {
            users.add(user.getUsername());
        }
    }

    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        if (activeUserStore == null)
            return;

        List<String> users = activeUserStore.getUsers();
        LoggedUserComponent user = (LoggedUserComponent) event.getValue();
        if (users.contains(user.getUsername())) {
            users.remove(user.getUsername());
        }
    }
}