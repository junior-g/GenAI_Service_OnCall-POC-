#!/bin/bash
echo "Testing JAMVANT Phase 2 - System Knowledge"
echo "=========================================="

# Test 1: API Knowledge
echo -e "\n=== Test 1: API Knowledge ==="
echo "Question: Hey! JAMVANT, provide curl command to create user with name=John, email=john@test.com, age=25"
ollama run jamvant:v2.0 "Hey! JAMVANT, provide curl command to create user with name=John, email=john@test.com, age=25"

# Test 2: Architecture Knowledge
echo -e "\n=== Test 2: Architecture Knowledge ==="
echo "Question: Hey! JAMVANT, explain the 5-layer architecture and data flow from UserController to users.json"
ollama run jamvant:v2.0 "Hey! JAMVANT, explain the 5-layer architecture and data flow from UserController to users.json"

# Test 3: Business Logic
echo -e "\n=== Test 3: Business Logic ==="
echo "Question: Hey! JAMVANT, what happens when someone tries to create a user with an existing email?"
ollama run jamvant:v2.0 "Hey! JAMVANT, what happens when someone tries to create a user with an existing email?"

# Test 4: Component Knowledge
echo -e "\n=== Test 4: Component Knowledge ==="
echo "Question: Hey! JAMVANT, list all main components and their responsibilities"
ollama run jamvant:v2.0 "Hey! JAMVANT, list all main components and their responsibilities"

# Test 5: Monitoring System
echo -e "\n=== Test 5: Monitoring System ==="
echo "Question: Hey! JAMVANT, explain how RequestMonitoringInterceptor works in our system"
ollama run jamvant:v2.0 "Hey! JAMVANT, explain how RequestMonitoringInterceptor works in our system"