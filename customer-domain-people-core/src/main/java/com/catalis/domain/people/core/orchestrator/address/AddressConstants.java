package com.catalis.domain.people.core.orchestrator.address;

public class AddressConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_ADD_ADDRESS_NAME = "AddAddressSaga";
    public static final String SAGA_REMOVE_ADDRESS_NAME = "RemoveAddressSaga";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_ADD_NEW_ADDRESS = "addNewAddress";
    public static final String STEP_REMOVE_NEW_ADDRESS = "removeNewAddress";

    // ============================== COMPENSATE METHODS ==============================

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_ADDRESS_ADDED = "address.added";
    public static final String EVENT_ADDRESS_REMOVED = "address.removed";


}
