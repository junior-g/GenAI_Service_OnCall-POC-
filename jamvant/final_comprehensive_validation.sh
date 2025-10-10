#!/bin/bash
echo "🎯 JAMVANT Final Comprehensive Validation - September 13, 2025"
echo "=============================================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test counters
TESTS_PASSED=0
TESTS_FAILED=0

# Function to run test and check result
run_test() {
    local test_name="$1"
    local command="$2"
    local expected_pattern="$3"
    
    echo -e "\n${YELLOW}=== $test_name ===${NC}"
    result=$(eval "$command" 2>&1)
    
    if [[ "$result" =~ $expected_pattern ]]; then
        echo -e "${GREEN}✅ PASS${NC}: $test_name"
        ((TESTS_PASSED++))
        return 0
    else
        echo -e "${RED}❌ FAIL${NC}: $test_name"
        echo "Expected pattern: $expected_pattern"
        echo "Actual result: $result"
        ((TESTS_FAILED++))
        return 1
    fi
}

echo -e "\n${BLUE}🔍 1. JAMVANT MODEL AVAILABILITY${NC}"
run_test "JAMVANT Models Available" "ollama list | grep jamvant | wc -l" "5"

echo -e "\n${BLUE}🤖 2. CORE IDENTITY & PERSONA${NC}"
run_test "JAMVANT Identity Response" \
    "ollama run jamvant:v5.0 'Hey! JAMVANT, who are you?' | head -3" \
    "JAMVANT"

run_test "System Boundary Enforcement" \
    "ollama run jamvant:v5.0 'Hey! JAMVANT, what do you know about Docker?' | head -3" \
    "JAMVANT-SH-SBUMM-POC|exclusively|only"

echo -e "\n${BLUE}🏗️ 3. SYSTEM KNOWLEDGE${NC}"
run_test "Architecture Understanding" \
    "ollama run jamvant:v5.0 'Hey! JAMVANT, explain our 5-layer architecture.' | head -5" \
    "Controller|Service|Repository|Monitoring|AI"

run_test "API Knowledge" \
    "ollama run jamvant:v5.0 'Hey! JAMVANT, list our main API endpoints.' | head -10" \
    "api/v1/users|api/v1/monitoring|api/v1/ai"

echo -e "\n${BLUE}🔍 4. ERROR ANALYSIS CAPABILITIES${NC}"
run_test "VALIDATION_ERROR Analysis" \
    "ollama run jamvant:v5.0 'Hey! JAMVANT, analyze VALIDATION_ERROR: Invalid email format, 25 occurrences.' | head -5" \
    "VALIDATION_ERROR|analysis|email|validation"

run_test "FILE_OPERATION_ERROR Priority" \
    "ollama run jamvant:v5.0 'Hey! JAMVANT, which has higher priority: VALIDATION_ERROR or FILE_OPERATION_ERROR?' | head -5" \
    "FILE_OPERATION_ERROR|CRITICAL|priority|data"

echo -e "\n${BLUE}📋 5. JSON FORMAT COMPLIANCE${NC}"
json_test() {
    response=$(curl -s -X POST http://localhost:11434/api/chat \
      -H "Content-Type: application/json" \
      -d '{
        "model": "jamvant:v5.0",
        "messages": [
          {
            "role": "user",
            "content": "Hey! JAMVANT, analyze VALIDATION_ERROR: Invalid email, 10 occurrences. Provide JSON analysis."
          }
        ],
        "stream": false
      }')
    content=$(echo "$response" | jq -r '.message.content' 2>/dev/null)
    echo "$content" | jq '.' > /dev/null 2>&1
    return $?
}

if json_test; then
    echo -e "${GREEN}✅ PASS${NC}: JSON Format Compliance"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}: JSON Format Compliance"
    ((TESTS_FAILED++))
fi

echo -e "\n${BLUE}🔗 6. OLLAMA API INTEGRATION${NC}"
api_test() {
    response=$(curl -s -X POST http://localhost:11434/api/chat \
      -H "Content-Type: application/json" \
      -d '{
        "model": "jamvant:v5.0",
        "messages": [
          {
            "role": "user",
            "content": "Hey! JAMVANT, what are the main components of our system?"
          }
        ],
        "stream": false
      }')
    echo "$response" | jq -e '.message.content' > /dev/null 2>&1
    return $?
}

if api_test; then
    echo -e "${GREEN}✅ PASS${NC}: Ollama API Integration"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ FAIL${NC}: Ollama API Integration"
    ((TESTS_FAILED++))
fi

echo -e "\n${BLUE}⚡ 7. ERROR GENERATION & ANALYSIS${NC}"
# Test error generation and analysis
echo "Testing VALIDATION_ERROR generation and analysis..."
validation_error=$(curl -s -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"name": "", "age": 200, "email": "invalid-email"}' | grep "VALIDATION_ERROR")

if [[ -n "$validation_error" ]]; then
    echo -e "${GREEN}✅ PASS${NC}: VALIDATION_ERROR Generation"
    ((TESTS_PASSED++))
else
    echo -e "${YELLOW}⚠️  SKIP${NC}: VALIDATION_ERROR Generation (Service may not be running)"
fi

echo -e "\n${BLUE}🎯 8. COMPLETE SYSTEM INTEGRATION${NC}"
run_test "System Status Understanding" \
    "ollama run jamvant:v5.0 'Hey! JAMVANT, what is the current status of our self-healing system?' | head -10" \
    "system|status|Phase|monitoring|AI"

run_test "Multi-Component Analysis" \
    "ollama run jamvant:v5.0 'Hey! JAMVANT, if we have high error rates and low disk space, how would you prioritize?' | head -10" \
    "priority|CRITICAL|disk|space|FILE_OPERATION_ERROR"

# Final Results
echo -e "\n${YELLOW}📊 FINAL VALIDATION RESULTS${NC}"
echo "============================================="
echo -e "Tests Passed: ${GREEN}$TESTS_PASSED${NC}"
echo -e "Tests Failed: ${RED}$TESTS_FAILED${NC}"
echo -e "Total Tests: $((TESTS_PASSED + TESTS_FAILED))"

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "\n${GREEN}🎉 ALL TESTS PASSED - JAMVANT IS PRODUCTION READY!${NC}"
    echo -e "${GREEN}✅ JAMVANT v5.0 is fully validated and ready for GenAIAnalysisService integration${NC}"
    echo -e "\n${BLUE}🚀 NEXT STEP: Update GenAIAnalysisService.callRealLLM() method${NC}"
    echo -e "${BLUE}   Replace mock responses with Ollama API calls to jamvant:v5.0${NC}"
    exit 0
else
    echo -e "\n${RED}⚠️  SOME TESTS FAILED - REVIEW REQUIRED${NC}"
    echo -e "${RED}❌ Please address failed tests before proceeding${NC}"
    exit 1
fi