package com.shop.model;
public class Customer {

    private int idCustomer;
    private String name;
    private String phone;
    private String contactPerson;
    private String address;
    private String login;
    private String passwordHash;
    private Role role;

    public Customer() {
    }

    public Customer(int idCustomer, String name, String phone, String contactPerson,
                     String address, String login, String passwordHash, Role role) {
        this.idCustomer = idCustomer;
        this.name = name;
        this.phone = phone;
        this.contactPerson = contactPerson;
        this.address = address;
        this.login = login;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role != null && role.getAccessLevel() >= 2;
    }
}
