package com.shop.model;

/** Аналог таблицы Роли. */
public class Role {

    private int idRole;
    private String name;
    private int accessLevel;

    public Role() {
    }

    public Role(int idRole, String name, int accessLevel) {
        this.idRole = idRole;
        this.name = name;
        this.accessLevel = accessLevel;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String toString() {
        return name;
    }
}
