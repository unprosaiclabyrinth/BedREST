package org.CS441.hw3.hdongr2

import helper.ConfigParser.*

import sttp.client4.*
import sttp.client4.logging.slf4j.Slf4jLoggingBackend
import sttp.model.MediaType

import scala.annotation.tailrec
import scala.io.StdIn.readLine

object RestClient:
  private val backend = Slf4jLoggingBackend(DefaultSyncBackend())

  private def generate(prompt: String): String =
    // Send HTTP POST request to the rest server to get the LLM generation
    val httpRequest = basicRequest
      .post(uri"http://$getRestServerHost:$getRestServerPort/query")
      .contentType(MediaType.ApplicationXWwwFormUrlencoded)
      .body(s"prompt=$prompt&maxTokens=$getMaxTokens")
      .response(asString)

    try {
      httpRequest.send(backend).body match {
        case Left(e) => e
        case Right(s) => s
      }
    } catch
      case e => s"Failed to connect to backend :("

  // Recursive function for the client loop
  @tailrec
  private def clientLoop(): Unit = {
    val prompt = readLine("PROMPT> ")
    if (prompt.toLowerCase != "quit") {
      println(s"λλMGen> ${generate(prompt)}")
      clientLoop() // Recursive call
    }
  }

  @main
  def runClientLoop(): Unit = {
    println("Welcome to the BedREST, the client loop for λλM! Type 'quit' to exit.")
    clientLoop() // Start the loop
  }

