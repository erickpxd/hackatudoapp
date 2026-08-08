package com.hackatudo.conscious.core.network

import org.junit.Assert.assertEquals
import org.junit.Test
import com.google.gson.Gson
import java.util.UUID

class SharedPayloadPrivacyTest {
    @Test fun dtoUsesStrictAllowlist() {
        val json = Gson().toJsonTree(SharedSessionSummaryDto(UUID.randomUUID(), UUID.randomUUID(), 30, true, "2026-09-12T12:00:00Z")).asJsonObject
        assertEquals(setOf("id", "groupId", "durationMinutes", "completed", "occurredAt"), json.keySet())
    }
}
