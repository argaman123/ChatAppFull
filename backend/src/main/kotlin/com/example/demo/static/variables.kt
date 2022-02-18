package com.example.demo.static
import java.time.Duration
import java.time.temporal.TemporalAmount


// free users
const val freeUserMessageLimit = 100
// premium times
val timeToRemindTheUserToRenew: TemporalAmount = Duration.ofMinutes(10)
val premiumMonthPeriod: TemporalAmount = Duration.ofMinutes(10)