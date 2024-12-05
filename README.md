# λλM

+ Author: **Himanshu Dongre**
+ Deplyment video link: [https://youtu.be/zUjTfXR4Euw](https://youtu.be/zUjTfXR4Euw)
+ Bonus video link: [https://youtu.be/2gh8NUU503Y](https://youtu.be/2gh8NUU503Y)

## Overview

λλM is designed to provide a web service in the cloud that enables HTTP clients to access LLM capabilities. The system is crafted to deliver a cloud-based web service, offering clients the ability to interact with an LLM through straightforward REST API endpoints.

## Architecture

There are four main components of λλM:-

1. **LLM backend:** The backend of λλM contains the LLM that is responsible for generating responses as well as the REST API endpoint to access it. This backend is situated in the cloud.
2. **gRPC proxy:** The HTTP requests from clients are not directly sent to the REST API endpoint in the cloud. λλM routes them via an *intermediate gRPC proxy server*, which acts as a forward proxy and forwards the requests to the LLM backend. The gRPC proxy directly accesses the REST API endpoints of the LLM backend.
3. **RESTful server:** λλM implements a RESTful API server which directly receives the HTTP requests from the clients and forwards them to the gRPC server.
4. **BedREST Client:** Of course the RESTful server can be accessed via its HTTP endpoint using HTTP clients such as `curl` or Postman. However, λλM provides its own client, which can be configured to access the RESTful server by simply updating the RESTful server's host and port in the client's config file.

Hence the pipeline that λλM showcases is as follows (λλM routes HTTP requests like this):-

λλM client -[HTTP/1.1]-> RESTful server -[gRPC]-> gRPC proxy -[HTTP/1.1]-> LLM backend

### LLM Backend

The LLM Backend that λλM implements leverages the **AWS Bedrock**, **AWS Lambda**, and **AWS API Gateway** services. AWS API Gateway is used to configure the REST API endpoint via which the Lambda function can be accessed. The HTTP requests are made to this endpoint, which triggers the Lambda function, which, in turn, fetches the generated response from the LLM provided by AWS Bedrock. The API Gateway provides the `/query` resource to trigger the Lambda function via an HTTP POST request. It expects a JSON payload, from which parameters dictating the LLM generation are extracted by the Lambda function. The two parameters that the client passes are the prompt to the LLM and the maximum number of tokens in the generated text.

The Lambda function is implemented in Python and uses `python3.13` as its runtime environment. It extracts the necessary parameters from the client's HTTP request, formats them in the expected input format of the LLM, and queries the LLM. It receives the response from the LLM, extracts the generated text from the JSON response, formats it as an HTTP response, and returns it to API Gateway, which, in turn, returns it to the gRPC server, and the entire pipeline is traversed in reverse order until the response reaches the client.

AWS Bedrock provides a multitude of choices for an LLM. λλM uses Amazon's **Amazon Titan Text G1 - Premier** which is an LLM for text generation. It is useful for a wide range of tasks including open-ended and context-based question answering, code generation, and summarization.

### gRPC Proxy

The intermediate gRPC proxy in λλM is a server implemented from generated protobuf stubs in Scala. It provides a service called **`LlmService`** with a **`generateText`** procedure API. The input to the function is a message of type **`LlmQueryRequest`**, and the function returns a message of type **`LlmQueryResponse`**. The function itself extracts the LLM query parameters (prompt and maximum number of generation tokens) from the input, structures them into a JSON payload and send them to the LLM backend as an HTTP POST request. It receives the generated text response from the LLM backend as an HTTP response with a JSON payload. The function `generateText` extracts the text generation from the response JSON payload, structures it as an `LlmQueryResponse` type and sends it back in the reverse λλM pipeline.

The gRPC proxy server contains a config file, which can be updated to configure the URL of the resource providing the LLM service in the cloud. It also configures the port on which the gRPC is listening for requests. The default port is 50051.

### RESTful server

λλM implements a lightweight web server in its pipeline. It is the direct server for λλM clients, and a client to the gRPC proxy, implemented using generated protobuf stubs. The RESTful server is implemented using Scala's **Scalatra** framework. It defines a `/query` resource, to which the REST client can send its HTTP requests. The server extracts generation parameters from the request, wraps them in an `LlmQueryRequest` object and gRPC's the gRPC server for the response. It receives an `LlmQueryResponse` object from the gRPC server, extracts the generated text from it, and sends it as an HTTP response to the λλM client.

The RESTful server contains a config file, which can be updated to configure the host for the gRPC server, the port for the gRPC server, and the port on which the RESTful server itself is listening. The default port for the RESTful server is 8080.

### RESTful client

Although λλM can be queried using HTTP clients such as `curl` or Postman, it provides its own custom, configurable client, which it calls the **BedREST client**. The client is implemented in Scala. It can either be run in the *active* or the *passive* mode.

+ In the active mode, it provides an interactive CLI to the user which takes in prompts, queries the RESTful server in the λλM pipeline, receives the HTTP response, and prints out the generated text to the screen.
+ In the passive mode, it queries λλM with *five pre-defined prompts*, and prints out their generations. This mode is useful for testing purposes.

**The parameter for maximum number of tokens in the generation can be configured from the config file of the client. A low `maxTokrns` parameter csn be why generations are cutting off mid-sentence.**

## Get Started

The λλM repo contains three SBT projects: the BedREST client, the RESTful server, and the gRPC proxy. The RESTful server and the gRPC proxy must be run first. This can be achieved by running

```shell
cd RestfulProxy
sbt clean compile run
cd ../GrpcProxy
sbt clean compile run
cd ..
```

The client can either be run in active mode or in passive mode. To run the client in active mode, do

```shell
cd BedREST
sbt clean compile "run active"
cd ..
```

To run the client in passive mode, do

```shell
cd BedREST
sbt clean compile "run passive"
cd ..
```

## AWS Deployment

λλM is deployed on AWS by running the RESTful server and the gRPC proxy server on two separate AWS EC2 instances, and running the client locally. The BedREST client can also be run on its *third* separate EC2 instance. The hosts and ports for BedREST client, RESTful server, and gRPC proxy can be configured in each appropriately.

## Testing and Logging

λλM uses **Scalatest** as the testing framework. The BedREST client, RESTful server, and the gRPC proxy contain tests that check whether the components have the correct configurations. The tests can be run in each directory by *first changing to the directory* and then running

```shell
sbt clean compile test
```

λλM uses **SLF4j** for logging.

---

## Bonus

Each of the three directories: BedREST client, RESTful server, and the gRPC proxy contain a **Dockerfile** which specifies the configuration to build Docker containers for the three services.

### Local testing

The λλM repo contains a `docker-compose.yml` file, which was used to locally test the orchestration of the Docker containers running the three services. The only challenge faced in this part was learning the syntax, which I did from the official Docker documentation. After that, it was pretty straightforward.

### AWS deployment

The Docker containers containing the RESTful server and gRPC proxy were pushed to **AWS ECR**. They were pulled onto two separate EC2 instances and run using the Docker API, `docker run`. The container containing the BedREST client was run locally using the Docker API. My bonus video on the deployment of my application on the cloud using Docker containers details the steps of how the Docker containers are used, and also how the Dockerfiles can be used to configure the containers (e.g. the client). There were really no challenges faced in this part. AWS ECR gives some handy commands in its guide for users to be able to push containers to it. The part of the deployment carried out locally and on EC2 instances was straightforward because it simply used the docker API to pull/run the containers. My bonus video goes over the deployment stages step-by-step.

**One part I did not show in the video but is tremendously important is to update the endpoints of the servers in the config files of the clients. I had done this when I had set up the demo for the video, and I had built the images with the correct endpoints configured. In case the endpoints are incorrect, the client will respond with a message like "Failed to connect to backend", or it will simply timeout.**