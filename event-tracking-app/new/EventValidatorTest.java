package com.example.cs360projectonelynneamarihugh;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EventValidatorTest {

    @Test
    public void title_normalText_isValid() {
        assertTrue(EventValidator.isValidTitle("Dentist appointment"));
    }

    @Test
    public void title_emptyOrBlankOrNull_isInvalid() {
        assertFalse(EventValidator.isValidTitle(""));
        assertFalse(EventValidator.isValidTitle("   "));
        assertFalse(EventValidator.isValidTitle(null));
    }

    @Test
    public void date_correctFormat_isValid() {
        assertTrue(EventValidator.isValidDate("10/17/2026"));
    }

    @Test
    public void date_leapDay_validOnlyInLeapYear() {
        assertTrue(EventValidator.isValidDate("02/29/2024"));
        assertFalse(EventValidator.isValidDate("02/29/2025"));
    }

    @Test
    public void date_impossibleValues_areInvalid() {
        assertFalse(EventValidator.isValidDate("13/01/2026"));
        assertFalse(EventValidator.isValidDate("02/30/2026"));
        assertFalse(EventValidator.isValidDate("04/31/2026"));
    }

    @Test
    public void date_wrongFormat_isInvalid() {
        assertFalse(EventValidator.isValidDate("2026-10-17"));
        assertFalse(EventValidator.isValidDate("10/17/26"));
        assertFalse(EventValidator.isValidDate("abc"));
        assertFalse(EventValidator.isValidDate(""));
        assertFalse(EventValidator.isValidDate(null));
    }

    @Test
    public void time_correctFormat_isValid() {
        assertTrue(EventValidator.isValidTime("3:30 PM"));
        assertTrue(EventValidator.isValidTime("12:00 AM"));
    }

    @Test
    public void time_twentyFourHourFormat_isInvalid() {
        assertFalse(EventValidator.isValidTime("15:30"));
    }

    @Test
    public void time_impossibleValues_areInvalid() {
        assertFalse(EventValidator.isValidTime("3:75 PM"));
        assertFalse(EventValidator.isValidTime("13:00 PM"));
    }

    @Test
    public void time_emptyOrGarbage_isInvalid() {
        assertFalse(EventValidator.isValidTime(""));
        assertFalse(EventValidator.isValidTime("abc"));
        assertFalse(EventValidator.isValidTime(null));
    }
}