package com.firefly.domain.people.core.orchestrator.phone;

public class PhoneConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_ADD_PHONE_NAME = "AddPhoneSaga";
    public static final String SAGA_REMOVE_PHONE_NAME = "RemovePhoneSaga";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_ADD_NEW_PHONE = "addNewPhone";
    public static final String STEP_REMOVE_NEW_PHONE = "removeNewPhone";

    // ============================== COMPENSATE METHODS ==============================
    public static final String COMPENSATE_REMOVE_PHONE = "removePhone";

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_PHONE_ADDED = "phone.added";
    public static final String EVENT_PHONE_REMOVED = "phone.removed";


}