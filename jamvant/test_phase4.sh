#!/bin/bash

now=$(date -u +"%Y-%m-%dT%H:%M:%SZ")

echo "Testing JAMVANT Phase 4 - Healing Recommendations"
echo "================================================"

# PreSet: Setting CURRENT_TIMESTAMP

echo - "\n=== PreSet: Setting CURRENT_TIMESTAMP ==="
echo "Hey"

# Test 1: JSON Format Validation
echo -e "\n=== Test 1: JSON Format Analysis ==="
echo "Question: Hey! JAMVANT, analyze FILE_OPERATION_ERROR: Disk space critically low - unable to write to users.json, 8 occurrences, system context: 2GB available disk space, 67% memory usage. Provide analysis in exact JSON format. Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v4.0 "Hey! JAMVANT, analyze FILE_OPERATION_ERROR: Disk space critically low - unable to write to users.json, 8 occurrences, system context: 2GB available disk space, 67% memory usage. Provide analysis in exact JSON format. Here timestamp=13th Sept, 2025 12:00 PM"

# Test 2: Healing Action Recommendations
echo -e "\n=== Test 2: Healing Actions ==="
echo "Question: Hey! JAMVANT, what automated healing actions would you recommend for high CPU usage in our system? Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v4.0 "Hey! JAMVANT, what automated healing actions would you recommend for high CPU usage in our system? Here timestamp=13th Sept, 2025 12:00 PM"

# Test 3: Prevention Strategies
echo -e "\n=== Test 3: Prevention Strategies ==="
echo "Question: Hey! JAMVANT, what prevention strategies would you recommend for VALIDATION_ERROR patterns? Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v4.0 "Hey! JAMVANT, what prevention strategies would you recommend for VALIDATION_ERROR patterns? Here timestamp=13th Sept, 2025 12:00 PM"

# Test 4: Automation Opportunities
echo -e "\n=== Test 4: Automation Assessment ==="
echo "Question: Hey! JAMVANT, assess automation opportunities for DUPLICATE_EMAIL errors in our system.  Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v4.0 "Hey! JAMVANT, assess automation opportunities for DUPLICATE_EMAIL errors in our system.  Here timestamp=13th Sept, 2025 12:00 PM"

# Test 5: Complete Analysis with Context
echo -e "\n=== Test 5: Complete Analysis ==="
echo "Question: Hey! JAMVANT, analyze VALIDATION_ERROR: Invalid email format, 25 occurrences, 15% error rate, 350ms response time. Include business impact and healing recommendations. Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v4.0 "Hey! JAMVANT, analyze VALIDATION_ERROR: Invalid email format, 25 occurrences, 15% error rate, 350ms response time. Include business impact and healing recommendations. Here timestamp=13th Sept, 2025 12:00 PM"