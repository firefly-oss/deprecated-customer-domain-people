package com.firefly.domain.people.core.orchestrator.channel;

public class ChannelConstants {

    // ============================== SAGA CONFIGURATION ==============================
    public static final String SAGA_SET_PREFERRED_CHANNEL_NAME = "SetPreferredChannelSaga";

    // ============================== STEP IDENTIFIERS ==============================
    public static final String STEP_UPDATE_CHANNEL = "updateChannel";

    // ============================== COMPENSATE METHODS ==============================

    // ============================== EVENT TYPES ==============================
    public static final String EVENT_PREFERRED_CHANNEL_UPDATED = "preferredChannel.updated";

}