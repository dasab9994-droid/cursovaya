package com.shop.service;

import com.shop.model.Customer;
public final class Session {

    private static Customer currentUser;

    private Session() {
    }

    public static void login(Customer customer) {
        currentUser = customer;
    }

    public static void logout() {
        currentUser = null;
    }

    public static Customer getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole() != null
                && currentUser.getRole().getAccessLevel() >= 2;
    }
}
