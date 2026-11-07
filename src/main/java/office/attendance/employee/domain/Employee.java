package office.attendance.employee.domain;

import java.util.UUID;

public class Employee {

    private final UUID id;
    private final String employeeNumber;
    private final String fullName;
    private final boolean active;
    private final String email;

    public  Employee(UUID id, String employeeNumber, String fullName, String email, boolean active){
        this.id = id;
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.active = active;
        this.email = email;

    }
    public static  Employee register(String employeeNumber, String fullName, String email){
        return new Employee(UUID.randomUUID(), employeeNumber, fullName, email, true);
    }
    public Employee withDetails(String fullName, String email){
        return new Employee(id, employeeNumber, fullName, email, active);
    }
    public Employee deactivated(){
        return new Employee(id, employeeNumber, fullName, email, false);
    }

    public UUID getId(){
        return id;
    }
    public  String getEmployeeNumber(){
        return employeeNumber;
    }
    public String getFullName(){
        return fullName;
    }
    public String getEmail(){
        return email;
    }
    public boolean isActive(){
        return active;
    }
}
