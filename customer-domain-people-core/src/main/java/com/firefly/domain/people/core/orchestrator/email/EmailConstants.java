package com.firefly.domain.people.core.orchestrator.email;

public class EmailConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_ADD_EMAIL_NAME = "AddEmailSaga";
    public static final String SAGA_REMOVE_EMAIL_NAME = "RemoveEmailSaga";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_ADD_NEW_EMAIL = "addNewEmail";
    public static final String STEP_REMOVE_NEW_EMAIL = "removeNewEmail";

    // ============================== COMPENSATE METHODS ==============================
    public static final String COMPENSATE_REMOVE_EMAIL = "removeEmail";

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_EMAIL_ADDED = "email.added";
    public static final String EVENT_EMAIL_REMOVED = "email.removed";


}