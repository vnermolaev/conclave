package com.ing.counter.client

import com.r3.conclave.common.EnclaveInstanceInfo
import com.r3.conclave.mail.Curve25519KeyPairGenerator
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.UUID

fun main(args: Array<String>) {
    val from = 5.toByte()

    // Connect to the host, it will send us a remote attestation (EnclaveInstanceInfo).
    val socket = Socket("localhost", 9999)
    val fromHost = DataInputStream(socket.getInputStream())
    val toHost = DataOutputStream(socket.getOutputStream())

    val attestationBytes = ByteArray(fromHost.readInt())
    fromHost.readFully(attestationBytes)
    val attestation = EnclaveInstanceInfo.deserialize(attestationBytes)
    println("Received ATTESTATION:\n$attestation")
    println("SECURITY: ${attestation.securityInfo.summary}; ${attestation.securityInfo.reason}")

    // Generate our own Curve25519 keypair so we can receive a response.
    val myKey = Curve25519KeyPairGenerator().generateKeyPair()

    // // Now we checked the enclave's identity and are satisfied it's the enclave from this project,
    // // we can send mail to it. We will provide our own private key whilst encrypting, so the enclave
    // // gets our public key and can encrypt a reply.
    val encryptedMail = attestation.createMail(ByteArray(1) { from }).apply {
        privateKey = myKey.private
        // Set a random topic, so we can re-run this program against the same server.
        topic = "${UUID.randomUUID()}"
    }.encrypt()

    println("Sending the encrypted mail to the host.")

    toHost.writeInt(encryptedMail.size)
    toHost.write(encryptedMail)

    // Enclave will mail us back.
    val encryptedReply = ByteArray(fromHost.readInt())
    println("Reading reply mail of length ${encryptedReply.size} bytes.")
    fromHost.readFully(encryptedReply)

    val reply = attestation.decryptMail(encryptedReply, myKey.private)
    println("Enclave counted down: ${reply.bodyAsBytes.joinToString(separator = ",")}")

    toHost.close()
    fromHost.close()
}

