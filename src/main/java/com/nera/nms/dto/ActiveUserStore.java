/**
 * 
 */
package com.nera.nms.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author Martin Do
 *
 */
@Data
public class ActiveUserStore {

    public final List<String> users;
 
    public ActiveUserStore() {
        users = new ArrayList<>();
    }
}