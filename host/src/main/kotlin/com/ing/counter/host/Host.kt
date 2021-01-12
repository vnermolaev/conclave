package com.ing.counter.host

import com.r3.conclave.host.AttestationParameters
import com.r3.conclave.host.EnclaveHost
import com.r3.conclave.host.EnclaveLoadException
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.ServerSocket

object Host {
    fun start(args: Array<String>) {
        // Verify SGX support.
        try {
            EnclaveHost.checkPlatformSupportsEnclaves(true)
            println("This platform supports enclaves in simulation, debug and release mode.")
        } catch (e: EnclaveLoadException) {
            println("This platform does not support hardware enclaves: " + e.message)
        }

        // Open connection.
        val port = 9998
        val acceptor = ServerSocket(port)
        val connection = acceptor.accept()
        val input = DataInputStream(connection.getInputStream())
        val output = DataOutputStream(connection.getOutputStream())

        // Load enclave.
        val enclave = EnclaveHost.load(enclaveClassName)
        enclave.start(
            // Attestation protocol. https://docs.conclave.net/writing-hello-world.html#configurating-attestation
            // Generic DCAP: "...you need to obtain API keys for Intel's PCCS service".
            AttestationParameters.DCAP(),
            object : EnclaveHost.MailCallbacks {
                override fun postMail(encryptedBytes: ByteArray, routingHint: String?) {
                    send(output, encryptedBytes)
                    output.close()
                }
            }
        )

        // Obtain remote attestation.
        val attestation = enclave.enclaveInstanceInfo
        println("ATTESTATION:\n$attestation")
        println("SECURITY: ${attestation.securityInfo.summary}; ${attestation.securityInfo.reason}")

        // Send remote attestation to connection.
        send(output, attestation.serialize())

        // Get data from the connection.
        val size = input.readInt()
        val mailBytes = ByteArray(size)
        input.readFully(mailBytes)

        // Deliver it. The enclave will give us the encrypted reply in the callback we provided above, which
        // will then send the reply to the client.
        enclave.deliverMail(1, mailBytes, "routingHint")

        // Call enclave directly, corresponds to invoking `receiveFromUntrustedHost` on the class extending `Enclave`.
        val reply = enclave.callEnclave(ByteArray(1) { 3 })!!
        println("Host calls enclave: ${reply.joinToString(separator = ",")}")
    }

    private fun send(destination: DataOutputStream, bytes: ByteArray) {
        destination.writeInt(bytes.size)
        destination.write(bytes)
        destination.flush()
    }

    // TODO load by getting class name
    private const val enclaveClassName = "com.ing.counter.enclave.Counter"

}

// class Networking(val port: Int) {
//     data class Envelope(val to: String, val body: ByteArray)
//
//     val connections = mutableMapOf<String, Socket>()
//     val mailChannel = Channel<Envelope>(Channel.UNLIMITED)
//
//     suspend fun start() {
//         val server = ServerSocket(port)
//
//         coroutineScope {
//             launch {
//                 val socket = server.accept()
//
//                 launch {
//                     socket.
//
//                 }
//             }
//         }
//     }
// }

fun main(args: Array<String>) {
    // Networking()
    Host.start(args)
}