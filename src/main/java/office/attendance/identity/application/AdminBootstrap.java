package office.attendance.identity.application;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final AccountService accountService;
    private final String adminEmail;
    private final String adminPassword;

    public AdminBootstrap(AccountService accountService,
                          @Value("${app.bootstrap.admin-email:}") String adminEmail,
                          @Value("${app.bootstrap.admin-password:}") String adminPassword) {
        this.accountService = accountService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (accountService.adminExists()) {
            return;
        }
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.warn("No admin account exists. Set ADMIN_EMAIL and ADMIN_PASSWORD to create the first one.");
            return;
        }
        accountService.createAdmin(adminEmail, adminPassword);
        log.info("Created initial admin account: {}", adminEmail);
    }
}
