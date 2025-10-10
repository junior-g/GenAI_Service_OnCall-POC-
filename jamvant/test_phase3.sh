#!/bin/bash
echo "Testing JAMVANT Phase 3 - Error Analysis"
echo "======================================="

# Test 1: Error Pattern Recognition
echo -e "\n=== Test 1: VALIDATION_ERROR Analysis ==="
echo "Question: Hey! JAMVANT, analyze VALIDATION_ERROR: Invalid email format user@invalid, 25 occurrences, 15% error rate"
ollama run jamvant:v3.0 "Hey! JAMVANT, analyze VALIDATION_ERROR: Invalid email format user@invalid, 25 occurrences, 15% error rate"

# Test 2: Critical Error Priority
echo -e "\n=== Test 2: FILE_OPERATION_ERROR Analysis ==="
echo "Question: Hey! JAMVANT, analyze FILE_OPERATION_ERROR: Disk space critically low - unable to write to users.json, 8 occurrences"
ollama run jamvant:v3.0 "Hey! JAMVANT, analyze FILE_OPERATION_ERROR: Disk space critically low - unable to write to users.json, 8 occurrences"

# Test 3: Healing Priority Test
echo -e "\n=== Test 3: Healing Priority ==="
echo "Question: Hey! JAMVANT, we have both VALIDATION_ERROR and FILE_OPERATION_ERROR. Which should we prioritize and why?"
ollama run jamvant:v3.0 "Hey! JAMVANT, we have both VALIDATION_ERROR and FILE_OPERATION_ERROR. Which should we prioritize and why?"

# Test 4: Business Impact Assessment
echo -e "\n=== Test 4: Business Impact ==="
echo "Question: Hey! JAMVANT, what is the business impact of DUPLICATE_EMAIL errors in our system?"
ollama run jamvant:v3.0 "Hey! JAMVANT, what is the business impact of DUPLICATE_EMAIL errors in our system?"

# Test 5: Healing Strategy
echo -e "\n=== Test 5: Healing Strategy ==="
echo "Question: Hey! JAMVANT, what healing strategies would you recommend for INTERNAL_ERROR patterns?"
ollama run jamvant:v3.0 "Hey! JAMVANT, what healing strategies would you recommend for INTERNAL_ERROR patterns?"