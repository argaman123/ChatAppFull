package com.example.demo.repository

import com.example.demo.model.Premium
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PremiumRepository : JpaRepository<Premium, String>