import json
import boto3
import logging


logger = logging.getLogger()
logger.setLevel(logging.INFO)


# Amazon Titan Text G1 - Premier model
MODEL_ID = "amazon.titan-text-premier-v1:0"

def generate_text(prompt, max_tokens):
    bedrock = boto3.client(
        service_name="bedrock-runtime",
        region_name="us-east-1"
    )

    body = {
        "inputText": prompt,
        "textGenerationConfig": {
            "maxTokenCount": max_tokens,
            "stopSequences": [],
            "temperature": 0.7,
            "topP": 0.9
        }
    }

    response = bedrock.invoke_model(
        modelId=MODEL_ID,
        body=json.dumps(body),
        accept="application/json",
        contentType="application/json"
    )

    # Retrieve generated text from JSON response and return it
    return json.loads(response.get("body").read())["results"][0]["outputText"]


def lambda_handler(event, context):
    print(f"Event: {json.dumps(event)}")

    # request = json.loads(event["body"])
    prompt = event["prompt"]
    max_tokens = event["maxTokens"]

    # Get generated text from the AWS Bedrock LLM
    text_response = generate_text(prompt, max_tokens)

    return { "generation": text_response }

