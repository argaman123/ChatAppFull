package com.example.demo.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.util.Date
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name="premium")
data class Premium(
    @Id val id: Long = 0,
    @Column(name="payment_datetime") @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss") val lastPayment: Date = Date(),
    @Column(name="payment_method") val paymentMethod :PaymentMethod = PaymentMethod.OneMonth
)

enum class PaymentMethod {
    Subscription, OneMonth
}
