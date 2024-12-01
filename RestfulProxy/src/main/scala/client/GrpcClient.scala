package client

import io.grpc.ManagedChannelBuilder
import helper.ConfigParser.*
import helper.CreateLogger
import llmquery.{LambdaLlmQueryGrpc, LambdaLlmRequest}

object GrpcClient:
  private val logger = CreateLogger(classOf[GrpcClient.type])

  def grpcForGeneration(prompt: String, maxTokens: Int): String =
    val channel = ManagedChannelBuilder.forAddress(getGrpcHost, getGrpcPort).usePlaintext().build
    val blockingStub = LambdaLlmQueryGrpc.blockingStub(channel)

    try {
      logger.info(s"Got prompt=\"$prompt\" and maxTokens=$maxTokens.")
      val response = blockingStub.generateText(LambdaLlmRequest(prompt =  prompt, maxTokens = maxTokens))
      logger.info(s"Got generation from gRPC proxy: \"${response.generation}\".")
      println(s"Response from server: ${response.generation}")
      response.generation
    } finally {
      channel.shutdown()
    }

