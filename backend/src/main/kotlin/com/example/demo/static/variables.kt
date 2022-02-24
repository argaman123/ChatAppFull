package com.example.demo.static
import java.time.Duration
import java.time.temporal.TemporalAmount


/**
 * The amount of messages a free user could send without paying for a premium plan.
 */
const val freeUserMessageLimit = 100

/**
 * The amount of time before a premium plan expires that the user should be alerted. Also, the amount of time after which
 * a renewal URL expires.
 */
val timeToRemindTheUserToRenew: TemporalAmount = Duration.ofMinutes(10)

/**
 * The amount of time it takes for a premium plan to expire.
 */
val premiumMonthPeriod: TemporalAmount = Duration.ofMinutes(10)