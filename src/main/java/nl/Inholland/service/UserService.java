package nl.Inholland.service;

import nl.Inholland.QueryBuilder.SpecSearchCriteria;
import nl.Inholland.QueryBuilder.Specifications.UserSpecification;
import nl.Inholland.model.Users.Customer;
import nl.Inholland.model.Users.Employee;
import nl.Inholland.model.Users.User;
import nl.Inholland.model.requests.UserRequest;
import nl.Inholland.repository.AccountRepository;
import nl.Inholland.repository.IbanRepository;
import nl.Inholland.repository.TransactionRepository;
import nl.Inholland.repository.UserRepository;
import nl.Inholland.security.JwtTokenProvider;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.*;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/*
 * User service covers actions to be performed for users
 * Inherits AbstractService for access to all repositories and specification builder
 * Takes care of encrypted password for login and whether user can login or if its locked
 */

@Service
public class UserService extends AbstractService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepo, TransactionRepository tranRepo, AccountRepository accoRepo, IbanRepository ibanRepo, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        super(userRepo, tranRepo, accoRepo, ibanRepo);
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }


    public void registerUser(UserRequest userRequest) {
        User newUser;
        if(userRequest.getInitRole().equals("ROLE_EMPLOYEE")){
            newUser = new Employee();
        }else if (userRequest.getInitRole().equals("ROLE_CUSTOMER")){
            newUser = new Customer();
        }else{
            throw new NoSuchElementException();
        }
        if(userRequest != null){
            if(!userRequest.getFirstName().equals(null) || !userRequest.getFirstName().isEmpty()) newUser.setFirstName(userRequest.getFirstName());
            if(!userRequest.getLastName().equals(null) || !userRequest.getLastName().isEmpty()) newUser.setLastName(userRequest.getLastName());
            if(!userRequest.getEmail().equals(null) || !userRequest.getEmail().isEmpty()) newUser.setEmail(userRequest.getEmail());
            if(!userRequest.getPhone().equals(null) || !userRequest.getPhone().isEmpty()) newUser.setPhone(userRequest.getPhone());
            if(!userRequest.getUsername().equals(null) || !userRequest.getUsername().isEmpty()) newUser.setUsername(userRequest.getUsername());
            if(!userRequest.getPassword().equals(null) || !userRequest.getPassword().isEmpty()) newUser.setPassword(userRequest.getPassword());
            if(!userRequest.getDateCreated().equals(null) || !userRequest.getDateCreated().isEmpty()) newUser.setDateCreated(userRequest.getDateCreated());
            if(!userRequest.getBirthday().equals(null) || !userRequest.getBirthday().isEmpty()) newUser.setBirthday(userRequest.getBirthday());
        }else{
            throw new NullPointerException();
        }
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepo.save(newUser);
    }

    public List<User> getUsers(String search) {
        Specification<User> spec = getBuilder(search).build(searchCriteria -> new UserSpecification((SpecSearchCriteria) searchCriteria));
        return userRepo.findAll(spec);
    }

    public User getUser(Long id) {
        User user = userRepo.findById(id).orElse(null);
        if (user != null) {
            return user;
        } else {
            throw new NoSuchElementException();
        }
    }

    public String auth(String username, String rawPassword) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("username " + username + "not found"));
        check(user);
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            return jwtTokenProvider.createToken(username, user.getRoles());
        } else {
            throw new BadCredentialsException("Invalid password!");
        }
    }
    protected final MessageSourceAccessor messages = SpringSecurityMessageSource
            .getAccessor();

    private void check(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException(messages.getMessage(
                    "AccountStatusUserDetailsChecker.locked", "User account is locked"));
        }

        if (!user.isEnabled()) {
            throw new DisabledException(messages.getMessage(
                    "AccountStatusUserDetailsChecker.disabled", "User is disabled"));
        }

        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException(
                    messages.getMessage("AccountStatusUserDetailsChecker.expired",
                            "User account has expired"));
        }

        if (!user.isCredentialsNonExpired()) {
            throw new CredentialsExpiredException(messages.getMessage(
                    "AccountStatusUserDetailsChecker.credentialsExpired",
                    "User credentials have expired"));
        }
    }
    public void disableUser(Long id){
        User user;
        try{
            user = this.getUser(id);
        }catch (NoSuchElementException exp){
            throw exp;
        }
        user.setNotLocked(false);
        userRepo.save(user);
    }
}
