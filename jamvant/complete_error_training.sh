#!/bin/bash
echo "🎯 JAMVANT Error Training - Generate All Error Types"
echo "=================================================="

# Function to generate errors and test JAMVANT responses
test_jamvant_error_analysis() {
    local error_type="$1"
    local description="$2"
    
    echo -e "\n=== Testing $error_type Analysis ==="
    echo "Description: $description"
    echo "Question: Hey! JAMVANT, analyze this $error_type and provide recommendations"
    ollama run jamvant:v4.0 "Hey! JAMVANT, analyze this $error_type: $description. Provide analysis in JSON format with recommendations."
}

# 1. Generate VALIDATION_ERROR
echo -e "\n🔴 Generating VALIDATION_ERROR..."
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name": "", "age": 200, "email": "invalid-email"}' \
  -w "\nHTTP Status: %{http_code}\n"

test_jamvant_error_analysis "VALIDATION_ERROR" "Invalid email format user@invalid, 25 occurrences, 15% error rate, 350ms response time"

# 2. Generate USER_NOT_FOUND
echo -e "\n🔴 Generating USER_NOT_FOUND..."
curl -X GET "http://localhost:8080/api/v1/users/nonexistent@example.com" \
  -H "Accept: application/json" \
  -w "\nHTTP Status: %{http_code}\n"

test_jamvant_error_analysis "USER_NOT_FOUND" "Lookup for non-existent email addresses, 12 occurrences, potential data inconsistency"

# 3. Generate DUPLICATE_EMAIL
echo -e "\n🔴 Generating DUPLICATE_EMAIL..."
# First create a user
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Test User", "age": 30, "email": "test@example.com"}'

# Then try to create duplicate
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Duplicate User", "age": 25, "email": "test@example.com"}' \
  -w "\nHTTP Status: %{http_code}\n"

test_jamvant_error_analysis "DUPLICATE_EMAIL" "Attempt to create user with existing email, violates uniqueness constraint, 8 occurrences"

# 4. Generate FILE_OPERATION_ERROR
echo -e "\n🔴 Generating FILE_OPERATION_ERROR..."
curl -X POST "http://localhost:8080/api/v1/monitoring/test-error" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "errorType=FILE_OPERATION_ERROR&errorMessage=Disk space critically low - unable to write to users.json" \
  -w "\nHTTP Status: %{http_code}\n"

test_jamvant_error_analysis "FILE_OPERATION_ERROR" "Disk space critically low - unable to write to users.json, 8 occurrences, system context: 2GB available disk space, 67% memory usage"

# 5. Generate INTERNAL_ERROR
echo -e "\n🔴 Generating INTERNAL_ERROR..."
curl -X POST "http://localhost:8080/api/v1/monitoring/test-error" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "errorType=INTERNAL_ERROR&errorMessage=OutOfMemoryError - heap space exhausted" \
  -w "\nHTTP Status: %{http_code}\n"

test_jamvant_error_analysis "INTERNAL_ERROR" "OutOfMemoryError - heap space exhausted, memory leaks detected, 5 occurrences"

# 6. Test Healing Priority
echo -e "\n🎯 Testing Healing Priority Logic..."
echo "Question: Hey! JAMVANT, we have both VALIDATION_ERROR and FILE_OPERATION_ERROR. Which should we prioritize and why?"
ollama run jamvant:v4.0 "Hey! JAMVANT, we have both VALIDATION_ERROR and FILE_OPERATION_ERROR. Which should we prioritize and why?"

# 7. Test AI Analysis
echo -e "\n🤖 Testing AI Analysis Integration..."
curl -X POST http://localhost:8080/api/v1/ai/analyze \
  -H "Content-Type: application/json"

echo -e "\n✅ Error Training Complete!"
echo "All error types generated and JAMVANT responses tested."