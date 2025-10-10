#!/bin/bash

# AI API Testing Script - Phase 3 Validation
# This script tests all AI endpoints systematically

echo "🤖 Starting AI API Testing - Phase 3 Validation"
echo "=============================================="

BASE_URL="http://localhost:8080"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to test endpoint
test_endpoint() {
    local name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local headers="$5"
    
    echo -e "\n${YELLOW}Testing: $name${NC}"
    echo "Endpoint: $method $endpoint"
    
    if [ "$method" = "GET" ]; then
        response=$(curl -s -w "\n%{http_code}" "$BASE_URL$endpoint" -H "$headers")
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$BASE_URL$endpoint" -H "$headers" -d "$data")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" = "200" ]; then
        echo -e "${GREEN}✅ SUCCESS (HTTP $http_code)${NC}"
        echo "Response: $body" | jq '.' 2>/dev/null || echo "Response: $body"
    else
        echo -e "${RED}❌ FAILED (HTTP $http_code)${NC}"
        echo "Response: $body"
    fi
    
    echo "----------------------------------------"
}

echo -e "\n🔍 1. AI System Health Check"
test_endpoint "AI Health Check" "GET" "/api/v1/ai/health" "" "Accept: application/json"

echo -e "\n📊 2. AI System Statistics"
test_endpoint "AI Statistics" "GET" "/api/v1/ai/statistics" "" "Accept: application/json"

echo -e "\n🧠 3. AI Analysis (Current Patterns)"
test_endpoint "AI Analysis" "POST" "/api/v1/ai/analyze" "" "Content-Type: application/json"

echo -e "\n🔧 4. Test AI Analysis (Custom Scenario)"
test_endpoint "Test AI Analysis - Validation Error" "POST" "/api/v1/ai/test-analysis" "errorType=VALIDATION_ERROR&errorMessage=Invalid email format detected&severity=HIGH" "Content-Type: application/x-www-form-urlencoded"

echo -e "\n🔧 5. Test AI Analysis (File Error)"
test_endpoint "Test AI Analysis - File Error" "POST" "/api/v1/ai/test-analysis" "errorType=FILE_OPERATION_ERROR&errorMessage=Disk space critically low&severity=CRITICAL" "Content-Type: application/x-www-form-urlencoded"

echo -e "\n🚀 6. Execute Automated Healing"
test_endpoint "Automated Healing" "POST" "/api/v1/ai/heal" "" "Content-Type: application/json"

echo -e "\n📜 7. Healing History"
test_endpoint "Healing History" "GET" "/api/v1/ai/healing-history?limit=5" "" "Accept: application/json"

echo -e "\n🎯 8. Test Custom Error Analysis"
test_endpoint "Custom Error Analysis" "POST" "/api/v1/ai/test-analysis" "errorType=DUPLICATE_EMAIL&errorMessage=Email already exists in system&severity=MEDIUM" "Content-Type: application/x-www-form-urlencoded"

echo -e "\n${GREEN}🏁 AI API Testing Complete!${NC}"
echo "=============================================="
echo "Review the results above to validate AI system functionality."
echo ""
echo "Expected Results:"
echo "✅ All endpoints should return HTTP 200"
echo "✅ AI analysis should provide structured recommendations"
echo "✅ Healing actions should execute successfully"
echo "✅ Statistics should show system activity"
echo ""