package com.nera.nms.services;

import com.nera.nms.constants.CommonConstants;
import com.nera.nms.dto.UserAssignDTO;
import com.nera.nms.models.User;
import com.nera.nms.models.UserDepartment;
import com.nera.nms.models.UserJobTitle;
import com.nera.nms.repositories.IUserDepartmentRepository;
import com.nera.nms.repositories.IUserJobTitleRepository;
import com.nera.nms.repositories.IUserRepository;
import com.nera.nms.repositories.impl.UserDaoImpl;
import com.nera.nms.utils.BeanConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private IUserRepository iUserRepository;

	@Autowired
	private IUserDepartmentRepository iUserDepartmentRepository;

	@Autowired
	private IUserJobTitleRepository iUserJobTitleRepository;

	@Autowired
	private UserDaoImpl userDaoImpl;

	@PersistenceContext
	EntityManager em;

	public List<User> findAll() {
		return iUserRepository.findAll();
	}

	@Transactional
	public User saveUser(User user) {
		return iUserRepository.save(user);
	}

	public long count() {
		em.getEntityManagerFactory();
		javax.persistence.Query query = em.createQuery("SELECT COUNT(u.id) FROM User u");
		return (long) query.getSingleResult();
	}

	public void getUsers(String page, String pageSize, String sortBy, String orderBy, final List<User> userList) {
		int pageInt = Integer.parseInt(page) - 1;

		Direction direction = Sort.Direction.fromString(orderBy);
		Sort sort = new Sort(direction, sortBy);
		Pageable pageable = PageRequest.of(pageInt, Integer.parseInt(pageSize), sort);

		Page<User> users = iUserRepository.findAll(pageable);

		users.getContent().forEach(user -> {
			StringBuilder groupNames = new StringBuilder();
			user.getGroups().forEach(groupName -> {
				if (groupNames.length() > 0) {
					groupNames.append(", ");
				}
				groupNames.append(groupName.getGroupName());

			});
			user.setGroupNames(groupNames.toString());

			if (user.getDepartmentId() != null) {
				Optional<UserDepartment> department = iUserDepartmentRepository.findById(user.getDepartmentId());
				if (department.isPresent()) {
					user.setDepartmentName(department.get().getDepartmentName());
				}
			}

			if (user.getJobTitleId() != null) {
				Optional<UserJobTitle> jobTitle = iUserJobTitleRepository.findById(user.getJobTitleId());
				if (jobTitle.isPresent()) {
					user.setJobTitleName(jobTitle.get().getJobTitleName());
				}
			}
		});
		userList.addAll(users.getContent());
		
	}

    public long getUsers(String page, String pageSize, String sortBy, String orderBy, final List <User> userList, Map<String, String> mapCondition) {

    	long countResults = 0;
    	List <User> userListExtracted = new ArrayList<>();
    	
    	countResults = userDaoImpl.findUserWithQuery(page, pageSize, sortBy, orderBy, userListExtracted, mapCondition);
    	
    	userList.addAll(userListExtracted);
    	return countResults;
    }
    
    @Transactional
    public boolean unblockUser(User user) {
    	
    	em.getEntityManagerFactory();
    	em.joinTransaction();
    	javax.persistence.Query query = em.createQuery("UPDATE User SET login_count = 0, isActive = true, password = :password where id = :id");
    	query.setParameter("password", user.getPassword());
    	query.setParameter("id", user.getId());    	
    	int updatedCount = query.executeUpdate();
    	return (updatedCount == 1);
    }
    
    @Transactional
    public boolean updateStatus(List<Long> idLong, String activeOption) {
    	
    	em.getEntityManagerFactory();
    	em.joinTransaction();
    	javax.persistence.Query query = em.createQuery("UPDATE User SET isActive = :activeOption where id IN :ids");
    	query.setParameter("activeOption", Boolean.parseBoolean(activeOption));
    	query.setParameter("ids", idLong);    	
    	int updatedCount = query.executeUpdate();
    	return (updatedCount >= 1);
    }
    
    @Transactional
    public boolean deleteUsers(List <Long> idList) {
    	
    	em.getEntityManagerFactory();
    	em.joinTransaction();
    	javax.persistence.Query query = em.createQuery("UPDATE User SET isDeleted = true where id IN :ids");
    	query.setParameter("ids", idList);    	
    	int updatedCount = query.executeUpdate();
    	return (updatedCount >= 1);
    }
    @Override
    public UserDetails loadUserByUsername(String email) {
    	User user = iUserRepository.selectUserByEmail(email);
		List<String> listPermission = new ArrayList<>(iUserRepository.selectPermissionByEmail(email));
		String permission = listPermission.stream().collect(Collectors.joining(CommonConstants.COMMA));
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}

		boolean isAccountNonLocked = true;

		if (!user.isActive())
			isAccountNonLocked = false;

		return new org.springframework.security.core.userdetails.User(
				user.getEmail(), user.getPassword(), true, true, true, isAccountNonLocked,
				mapRolesToAuthorities(permission));
    }

    private List<GrantedAuthority> mapRolesToAuthorities(String roles) {
    	if(roles==null){
    		return new ArrayList<>();
    	}
		List<GrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
		String[] permissions = roles.trim().split("\\,");
		for (String role : permissions) {
			simpleGrantedAuthorities.add(new SimpleGrantedAuthority(role));
		}
		simpleGrantedAuthorities = simpleGrantedAuthorities.stream().distinct().collect(Collectors.toList());
		return simpleGrantedAuthorities;
	}

    
    /**
     * find a user by email
     * @param email
     * @return
     */
    public User findUserByEmail(String email) {
        return iUserRepository.selectUserByEmail(email);
    }
    
    /**
     * find a user by id
     * @param id
     * @return
     */
    public User findUserById(String id) {
        return iUserRepository.findById(Long.parseLong(id));
    }
    
    /**
     * insert and update user
     * @param user
     * @return
     */
    public User save(User user) {
        return iUserRepository.save(user);
    }

	public long countById() {
		return iUserRepository.count();
	}
	
	public List<UserAssignDTO> getListPlanner() {
		return BeanConvertUtils.copyList(iUserRepository.getPlanner(),UserAssignDTO.class);
	}
	
	public List<UserAssignDTO> getListAssignee() {
		return BeanConvertUtils.copyList(iUserRepository.getAssignee(),UserAssignDTO.class);
	}

	public Set<String> getCreatedByJobManagement() {
		return iUserRepository.getCreatedByJobManagement();
	}
	
	/**
     * find a user by id
     * @param id
     * @return
     */
    public User findById(long id) {
        return iUserRepository.findById(id);
    }
}
