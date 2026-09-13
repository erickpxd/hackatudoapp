package com.hackatudo.conscious.data.apps

import com.hackatudo.conscious.core.network.SharedSessionSummaryDto
import com.google.gson.Gson
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class UsageStatsPrivacyTest {
    @Test fun sharedPayloadCannotSerializeUsageDetails() {
        val json = Gson().toJsonTree(SharedSessionSummaryDto(UUID.randomUUID(), UUID.randomUUID(), 25, true, "2026-09-12T12:00:00Z")).asJsonObject
        val fields = json.keySet()
        assertEquals(setOf("id", "groupId", "durationMinutes", "completed", "occurredAt"), fields)
        assertFalse(json.toString().contains(Regex("package|usage|intention|reason", RegexOption.IGNORE_CASE)))
    }
}
