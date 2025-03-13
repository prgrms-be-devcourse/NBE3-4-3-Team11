package com.pofo.backend.common.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class testGetUsername {

    @DisplayName("Test")
    @Test
    public void testGetUsername() {
        Admin admin = new Admin();
        admin.setUsername("leesangeok");
        AdminDetails adminDetails = new AdminDetails(admin);
        assertEquals("leesangeok", adminDetails.getUsername());
    }
}
