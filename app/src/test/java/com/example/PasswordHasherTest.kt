package com.example

import com.example.auth.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

  @Test
  fun `hash produces deterministic 64-char hex digest`() {
    val first = PasswordHasher.hash("student123")
    val second = PasswordHasher.hash("student123")

    assertEquals(first, second)
    assertEquals(64, first.length)
    assertTrue(first.matches(Regex("[0-9a-f]{64}")))
  }

  @Test
  fun `hash never stores plaintext`() {
    val digest = PasswordHasher.hash("admin123")

    assertFalse(digest.contains("admin123"))
    assertNotEquals("admin123", digest)
  }

  @Test
  fun `verify accepts the correct password`() {
    val digest = PasswordHasher.hash("coe123")

    assertTrue(PasswordHasher.verify("coe123", digest))
  }

  @Test
  fun `verify rejects wrong password`() {
    val digest = PasswordHasher.hash("coe123")

    assertFalse(PasswordHasher.verify("coe1234", digest))
    assertFalse(PasswordHasher.verify("", digest))
  }

  @Test
  fun `different passwords produce different digests`() {
    assertNotEquals(PasswordHasher.hash("student123"), PasswordHasher.hash("student124"))
  }
}
