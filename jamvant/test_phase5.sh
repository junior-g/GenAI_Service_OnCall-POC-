#!/bin/bash
echo "Testing JAMVANT Phase 5 - Full Integration"
echo "========================================="

# Test 1: Complete System Understanding
echo -e "\n=== Test 1: System Status Awareness ==="
echo "Question: Hey! JAMVANT, what is the current status of our self-healing system and what capabilities do we have? Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v5.0 "Hey! JAMVANT, what is the current status of our self-healing system and what capabilities do we have? Here timestamp=13th Sept, 2025 12:00 PM"

# Test 2: Context Integration
echo -e "\n=== Test 2: Context Integration ==="
echo "Question: Hey! JAMVANT, explain how monitoring data flows to AI analysis and then to automated healing. Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v5.0 "Hey! JAMVANT, explain how monitoring data flows to AI analysis and then to automated healing. Here timestamp=13th Sept, 2025 12:00 PM"

# Test 3: Multi-Component Analysis
echo -e "\n=== Test 3: Multi-Component Analysis ==="
echo "Question: Hey! JAMVANT, if we have high error rates in UserController and low disk space, how would you prioritize and coordinate healing actions? Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v5.0 "Hey! JAMVANT, if we have high error rates in UserController and low disk space, how would you prioritize and coordinate healing actions? Here timestamp=13th Sept, 2025 12:00 PM"

# Test 4: Performance Context
echo -e "\n=== Test 4: Performance Context ==="
echo "Question: Hey! JAMVANT, our system is experiencing 300ms response times with 8 concurrent users. Analyze this against our performance targets. Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v5.0 "Hey! JAMVANT, our system is experiencing 300ms response times with 8 concurrent users. Analyze this against our performance targets. Here timestamp=13th Sept, 2025 12:00 PM"

# Test 5: Complete Healing Cycle
echo -e "\n=== Test 5: Complete Healing Cycle ==="
echo "Question: Hey! JAMVANT, walk me through a complete healing cycle from error detection to validation for a FILE_OPERATION_ERROR.  Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v5.0 "Hey! JAMVANT, walk me through a complete healing cycle from error detection to validation for a FILE_OPERATION_ERROR. Here timestamp=13th Sept, 2025 12:00 PM"

# Test 6: System Limitations Awareness
echo -e "\n=== Test 6: System Limitations ==="
echo "Question: Hey! JAMVANT, what are the current limitations of our system and what's needed to complete Phase 3? Here timestamp=13th Sept, 2025 12:00 PM"
ollama run jamvant:v5.0 "Hey! JAMVANT, what are the current limitations of our system and what's needed to complete Phase 3? Here timestamp=13th Sept, 2025 12:00 PM"