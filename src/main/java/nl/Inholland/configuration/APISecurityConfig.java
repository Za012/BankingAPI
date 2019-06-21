package nl.Inholland.configuration;

import nl.Inholland.filters.JwtTokenFilter;
import nl.Inholland.security.CustomUserDetailsService;
import nl.Inholland.security.JwtConfigurer;
import nl.Inholland.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class APISecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private JwtTokenFilter filter;
    @Autowired
    private CustomUserDetailsService userDetailsService;
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(authProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        filter = new JwtTokenFilter(jwtTokenProvider);

        //@formatter:off
        http
                .cors()
                .and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()

                .antMatchers(HttpMethod.POST, "/Login").permitAll()

                .antMatchers(HttpMethod.GET, "/Customer/**").hasRole("CUSTOMER")
                .antMatchers(HttpMethod.POST, "/Customer/**").hasRole("CUSTOMER")
                .antMatchers(HttpMethod.PUT, "/Customer/**").hasRole("CUSTOMER")

                .antMatchers(HttpMethod.POST, "/Employee/**").hasRole("EMPLOYEE")
                .antMatchers(HttpMethod.GET, "/Employee/**").hasRole("EMPLOYEE")


                .antMatchers(HttpMethod.PUT, "/Employee/**").hasRole("EMPLOYEE")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));

        //@formatter:on
    }

    @Bean
    public PasswordEncoder passwordEncoder() { // BCrypt
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


}