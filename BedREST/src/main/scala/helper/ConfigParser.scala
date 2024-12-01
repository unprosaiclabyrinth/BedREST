package org.CS441.hw3.hdongr2
package helper

import com.typesafe.config.{Config, ConfigFactory}

object ConfigParser:
  private val config: Config = ConfigFactory.load("application.conf")

  def getMaxTokens: Int = config.getInt("application.max_tokens")

  def getRestServerHost: String = config.getString("application.server.rest_server_host")

  def getRestServerPort: Int = config.getInt("application.server.rest_server_port")
