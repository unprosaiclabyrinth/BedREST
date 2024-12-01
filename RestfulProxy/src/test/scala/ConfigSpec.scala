import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.typesafe.config.{Config, ConfigFactory}

class ConfigSpec extends AnyFlatSpec with Matchers {
  private val config: Config = ConfigFactory.load("proxy.conf")

  "The root config" should "the port of this server (self) in its path" in {
    config.getConfig("restfulProxy").hasPath("this_server_port") should be (true)
  }

  it should "have the gRPC server's host in its path" in {
    config.getConfig("restfulProxy").hasPath("grpc_server_host") should be (true)
  }

  it should "should gRPC server's port in its path" in {
    config.getConfig("restfulProxy").hasPath("grpc_server_port") should be(true)
  }

  it should "should have this server's base resource path as a string in its path" in {
    config.getConfig("restfulProxy").hasPath("base_resource_path_string") should be(true)
  }

  "The port that this server runs on" can "be 8080" in {
    config.getConfig("restfulProxy").getInt("this_server_port") should be (8080)
  }
}
