package office.attendance.employee.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRespository {

    void insert(Employee employee);

    int update(Employee employee);

    Optional<Employee> findById(UUID id);

    List<Employee> findAll();

    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID id);
    boolean existsByEmployeeNumber(String employeeNumber);
}
