package com.catalis.domain.people.core.orchestrator.address;

public class AddressConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_ADD_ADDRESS_NAME = "AddAddressSaga";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_ADD_NEW_ADDRESS = "addNewAddress";

    // ============================== COMPENSATE METHODS ==============================
    public static final String COMPENSATE_REMOVE_ADDRESS = "removeAddress";

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_ADDRESS_ADDED = "address.added";


}
