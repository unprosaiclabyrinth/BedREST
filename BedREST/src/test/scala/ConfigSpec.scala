package org.CS441.hw3.hdongr2

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.typesafe.config.{Config, ConfigFactory}

class ConfigSpec extends AnyFlatSpec with Matchers {
  private val config: Config = ConfigFactory.load("application.conf")

  "The root config" should "have max tokens in the path" in {
    config.getConfig("application").hasPath("max_tokens") should be (true)
  }

  it should "have a RESTful server configuration in the path" in {
    config.getConfig("application").hasPath("server") should be (true)
  }

  "The server config" should "should have its host IP in the path" in {
    config.getConfig("application.server").hasPath("rest_server_host") should be(true)
  }

  it should "should have its port in the path" in {
    config.getConfig("application.server").hasPath("rest_server_port") should be(true)
  }
}
