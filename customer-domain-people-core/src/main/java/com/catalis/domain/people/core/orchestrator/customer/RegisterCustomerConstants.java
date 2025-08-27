package com.catalis.domain.people.core.orchestrator.customer;

public class RegisterCustomerConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_REGISTER_CUSTOMER_NAME = "RegisterCustomerSaga";

    // ============================== CUSTOMER TYPE CONSTANTS ==============================
    public static final String TYPE_LEGAL_PERSON = "ORGANIZATION";
    public static final String TYPE_NATURAL_PERSON = "INDIVIDUAL";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_REGISTER_PARTY = "registerParty";
    public static final String STEP_REGISTER_NATURAL_PERSON = "registerNaturalPerson";
    public static final String STEP_REGISTER_LEGAL_PERSON = "registerLegalPerson";
    public static final String STEP_REGISTER_STATUS_ENTRY = "registerStatusEntry";
    public static final String STEP_REGISTER_PEP = "registerPep";
    public static final String STEP_REGISTER_IDENTITY_DOCUMENT = "registerIdentityDocument";
    public static final String STEP_REGISTER_ADDRESS = "registerAddress";
    public static final String STEP_REGISTER_EMAIL = "registerEmail";
    public static final String STEP_REGISTER_PHONE = "registerPhone";
    public static final String STEP_REGISTER_ECONOMIC_ACTIVITY_LINK = "registerEconomicActivityLink";
    public static final String STEP_REGISTER_CONSENT = "registerConsent";
    public static final String STEP_REGISTER_PARTY_PROVIDER = "registerPartyProvider";
    public static final String STEP_REGISTER_PARTY_RELATIONSHIP = "registerPartyRelationship";
    public static final String STEP_REGISTER_PARTY_GROUP_MEMBERSHIP = "registerPartyGroupMembership";

    // ============================== COMPENSATE METHODS ==============================
    public static final String COMPENSATE_REMOVE_PARTY = "removeParty";
    public static final String COMPENSATE_REMOVE_NATURAL_PERSON = "removeNaturalPerson";
    public static final String COMPENSATE_REMOVE_LEGAL_PERSON = "removeLegalPerson";
    public static final String COMPENSATE_REMOVE_STATUS_ENTRY = "removeStatusEntry";
    public static final String COMPENSATE_REMOVE_PEP = "removePep";
    public static final String COMPENSATE_REMOVE_IDENTITY_DOCUMENT = "removeIdentityDocument";
    public static final String COMPENSATE_REMOVE_ADDRESS = "removeAddress";
    public static final String COMPENSATE_REMOVE_EMAIL = "removeEmail";
    public static final String COMPENSATE_REMOVE_PHONE = "removePhone";
    public static final String COMPENSATE_REMOVE_ECONOMIC_ACTIVITY_LINK = "removeEconomicActivityLink";
    public static final String COMPENSATE_REMOVE_CONSENT = "removeConsent";
    public static final String COMPENSATE_REMOVE_PARTY_PROVIDER = "removePartyProvider";
    public static final String COMPENSATE_REMOVE_PARTY_RELATIONSHIP = "removePartyRelationship";
    public static final String COMPENSATE_REMOVE_PARTY_GROUP_MEMBERSHIP = "removePartyGroupMembership";

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_PARTY_REGISTERED = "party.registered";
    public static final String EVENT_NATURAL_PERSON_REGISTERED = "naturalperson.registered";
    public static final String EVENT_LEGAL_PERSON_REGISTERED = "legalperson.registered";
    public static final String EVENT_PARTY_STATUS_REGISTERED = "partystatus.registered";
    public static final String EVENT_PEP_REGISTERED = "pep.registered";
    public static final String EVENT_IDENTITY_DOCUMENT_REGISTERED = "identitydocument.registered";
    public static final String EVENT_ADDRESS_REGISTERED = "address.registered";
    public static final String EVENT_EMAIL_REGISTERED = "email.registered";
    public static final String EVENT_PHONE_REGISTERED = "phone.registered";
    public static final String EVENT_ECONOMIC_ACTIVITY_REGISTERED = "economicactivity.registered";
    public static final String EVENT_CONSENT_REGISTERED = "consent.registered";
    public static final String EVENT_PARTY_PROVIDER_REGISTERED = "partyprovider.registered";
    public static final String EVENT_PARTY_RELATIONSHIP_REGISTERED = "partyrelationship.registered";
    public static final String EVENT_PARTY_GROUP_MEMBERSHIP_REGISTERED = "partygroupmembership.registered";


}
