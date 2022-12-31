package com.example.springboot_security.security;

import com.example.springboot_security.dao.CustomerDAO;
import com.example.springboot_security.models.Customer;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;


@Configuration  //анотація щоб створювати @BEAN
@EnableWebSecurity  //впроваджує дефолтні налаштування щоб наше секюріті почала обробку запитів
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private CustomerDAO customerDAO;
    @Bean // те що повертається з метода робиться об'єктом і кладе його під контейнер(який можна використовувати в MainController
    public PasswordEncoder passwordEncoder(){ // метод розшифровує пароль
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // тут ми приймаємо логін паспорт і знайти обєкт в базі даних
         auth.userDetailsService(new UserDetailsService() {//знаходить об'єкт в бд
             @Override
             public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                 System.out.println(username);
               Customer customer=  customerDAO.findByLogin(username);
//               List<SimpleGrantedAuthority> roles =Arrays.asList(new SimpleGrantedAuthority(customer.getRole()));
               User user=new User(  //в якості аргумента йому треба передати логін пароль і список ролей (в обгортку з UserDetails)
                       customer.getLogin(),
                       customer.getPassword(),//він отримує шифрований пароль
                       Arrays.asList(new SimpleGrantedAuthority(customer.getRole()))
               );
                 return user;
             }
         });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // тут ми конфігуруємо http request (їхні дозволи і заборони)
        http.csrf().disable()
                .cors().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET,"/","/open").permitAll()
                .antMatchers(HttpMethod.POST,"/save").permitAll()
                .antMatchers(HttpMethod.GET,"/open2").denyAll()  ///403 forbidden (заборонено по цій урл)
                .antMatchers(HttpMethod.GET,"/secure").hasAnyRole("ADMIN","CLIENT")  //доступ до урли лише ті хто має певну роль(ADMIN і CLIENT)
                .and() // перетворює об'єкт зверху на об'єкт типу http security
                .httpBasic() // базова http аутентифікація
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // система буде без сесій
    }
}
