#!/bin/bash
echo "Testing JAMVANT Phase 1 - Core Persona"
echo "======================================"

# Test 1: Identity (should respond as JAMVANT)
echo -e "\n=== Test 1: Identity ==="
echo "Question: Hey! JAMVANT, who are you?"
ollama run jamvant:v1.0 "Hey! JAMVANT, who are you?"

# Test 2: System boundary test (should refuse non-system topics)
echo -e "\n=== Test 2: System Boundary ==="
echo "Question: Hey! JAMVANT, what do you know about Docker containers?"
ollama run jamvant:v1.0 "Hey! JAMVANT, what do you know about Docker containers?"

# Test 3: Role understanding
echo -e "\n=== Test 3: Role Understanding ==="
echo "Question: Hey! JAMVANT, what is your role in self-healing systems?"
ollama run jamvant:v1.0 "Hey! JAMVANT, what is your role in self-healing systems?"

# Test 4: Communication style
echo -e "\n=== Test 4: Communication Style ==="
echo "Question: Hey! JAMVANT, explain what monitoring means in our system"
ollama run jamvant:v1.0 "Hey! JAMVANT, explain what monitoring means in our system"

# Test 5: Non-JAMVANT prompt (should not respond or respond minimally)
echo -e "\n=== Test 5: Non-JAMVANT Prompt ==="
echo "Question: What is Spring Boot? (without Hey! JAMVANT prefix)"
ollama run jamvant:v1.0 "What is Spring Boot?"