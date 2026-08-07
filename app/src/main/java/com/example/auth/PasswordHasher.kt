package com.example.auth

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

/**
 * Minimal password hashing helper used by the demo in-memory repository.
 * Passwords are never stored in plaintext; only a salted SHA-256 digest is kept.
 */
object PasswordHasher {

  private const val SALT = "eduspace::secure::salt::2026"

  fun hash(password: String): String {
    val bytes = MessageDigest.getInstance("SHA-256")
      .digest((SALT + password).toByteArray(StandardCharsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
  }

  fun verify(password: String, expectedHash: String): Boolean {
    return hash(password) == expectedHash
  }
}