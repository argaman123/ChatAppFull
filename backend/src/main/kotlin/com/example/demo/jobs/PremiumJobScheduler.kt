package com.example.demo.jobs

import com.example.demo.entities.User
import org.jobrunr.scheduling.JobScheduler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.temporal.ChronoUnit

@Service
class PremiumJobScheduler @Autowired constructor(
    private val jobScheduler: JobScheduler,
    private val premiumJobService: PremiumJobService
) {

}