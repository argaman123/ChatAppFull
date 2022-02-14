package com.example.demo.repositories

import com.example.demo.entities.Premium
import org.springframework.data.jpa.repository.JpaRepository

interface PremiumRepository : JpaRepository<Premium, Long>