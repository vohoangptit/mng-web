/**
 * 
 */
package com.nera.nms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nera.nms.models.PersistentLogin;

/**
 * @author Martin Do
 *
 */
public interface IPersistentLoginRepository extends JpaRepository<PersistentLogin, Long>{

    PersistentLogin findBySeries(String series);
    List<PersistentLogin> findByUsername(String username);
}
