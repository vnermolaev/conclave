package com.ing.counter.enclave

import com.r3.conclave.enclave.Enclave
import com.r3.conclave.mail.EnclaveMail

class Counter: Enclave() {
    /**
     * Receive information send directly from the host hosting the enclave.
     */
    override fun receiveFromUntrustedHost(bytes: ByteArray): ByteArray {
        println("Processing message from the host")
        return countDown(bytes.first())
    }

    /**
     * Receive encrypted message from a remote client.
     */
    override fun receiveMail(id: Long, routingHint: String?, mail: EnclaveMail) {
        println("Processing message from the client $id")

        val sender = mail.authenticatedSender
        require(sender != null) { "Received mail from an unauthenticated sender, dropping..." }
        println("Authentication: $sender")

        val body = mail.bodyAsBytes
        val reply = createMail(sender, countDown(body.first()))
        postMail(reply, routingHint)
    }

    private fun countDown(from: Byte) = ByteArray(from.toInt()) { i ->
        (from - i).coerceAtLeast(0).toByte()
    }
}