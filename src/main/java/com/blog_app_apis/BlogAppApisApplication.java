package com.blog_app_apis;

import com.blog_app_apis.Entity.Role;
import com.blog_app_apis.Entity.User;
import com.blog_app_apis.config.AppConstants;
import com.blog_app_apis.repository.RoleRepository;
import com.blog_app_apis.repository.UserRepo;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class BlogAppApisApplication implements CommandLineRunner {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private UserRepo userRepo;

    public static void main(String[] args) {
        SpringApplication.run(BlogAppApisApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println(this.passwordEncoder.encode("Admin"));

        try {
            Role role = new Role();
            role.setId(AppConstants.ADMIN_USER);
            role.setName("ADMIN_USER");

            Role role1 = new Role();
            role1.setId(AppConstants.NORMAL_USER);
            role1.setName("NORMAL_USER");

            List<Role> roles = List.of(role, role1);
            List<Role> result = this.roleRepo.saveAll(roles);
            result.forEach(r -> {
                System.out.println(r.getName());
            });

            // Seed Admin User if not exists
            if (this.userRepo.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@gmail.com");
                admin.setPassword(this.passwordEncoder.encode("admin123"));
                admin.setAbout("System Administrator");
                
                admin.getRoles().add(role);
                this.userRepo.save(admin);
                System.out.println("Default admin user seeded successfully: admin@gmail.com / admin123");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// http://localhost:8080/v3/api-docs swager postman
// http://localhost:8080/swagger-ui/index.html#/user-contrroller/updateUser