package office.attendance.identity.application;

import office.attendance.identity.domain.UserAccount;

public interface TokenService {
    IssuedToken issue(UserAccount account);
}
