#!/bin/bash
echo "🔗 JAMVANT Service Integration Validation"
echo "========================================"

BASE_URL="http://localhost:11434"
MODEL="jamvant:v4.0"

# Function to test API integration
test_api_integration() {
    local test_name="$1"
    local prompt="$2"
    local expect_json="$3"

    echo -e "\n=== $test_name ==="

    response=$(curl -s -X POST "$BASE_URL/api/chat" \
        -H "Content-Type: application/json" \
        -d "{
            \"model\": \"$MODEL\",
            \"messages\": [
                {
                    \"role\": \"user\",
                    \"content\": \"$prompt\"
                }
            ],
            \"stream\": false
        }")

    if [ $? -ne 0 ]; then
        echo "❌ API Call Failed"
        return 1
    fi

    # Check if response has expected structure
    if ! echo "$response" | jq -e '.message.content' > /dev/null 2>&1; then
        echo "❌ Invalid Ollama API Response Structure"
        echo "Response: $response"
        return 1
    fi

    content=$(echo "$response" | jq -r '.message.content')

    if [ "$expect_json" = "true" ]; then
        # Validate JSON content
        if echo "$content" | jq '.' > /dev/null 2>&1; then
            echo "✅ Valid JSON Response"
            echo "Content Preview:"
            echo "$content" | jq -C '.' | head -10
        else
            echo "❌ Invalid JSON in Content"
            echo "Content: $content"
            return 1
        fi
    else
        echo "✅ Text Response Received"
        echo "Content: $content"
    fi

    return 0
}

# Test Suite
test_api_integration "Identity Test" "Hey! JAMVANT, who are you?" "false"

test_api_integration "JSON Analysis Test" "Hey! JAMVANT, analyze VALIDATION_ERROR: Invalid email format, 25 occurrences, 15% error rate. Provide analysis in JSON format." "true"

test_api_integration "System Boundary Test" "Hey! JAMVANT, what do you know about Docker?" "false"

test_api_integration "Complete Error Analysis" "Hey! JAMVANT, analyze FILE_OPERATION_ERROR: Disk space critically low - unable to write to users.json, 8 occurrences, system context: 2GB available disk space, 67% memory usage. Provide complete analysis in JSON format with recommendations." "true"

test_api_integration "Healing Priority Test" "Hey! JAMVANT, we have both VALIDATION_ERROR and FILE_OPERATION_ERROR. Which should we prioritize and why?" "false"

echo -e "\n🎯 Service Integration Tests Complete!"