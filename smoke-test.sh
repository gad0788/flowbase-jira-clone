#!/usr/bin/env bash
set -e

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'
PASS=0
FAIL=0

pass() { PASS=$((PASS+1)); echo -e "${GREEN}[PASS]${NC} $1"; }
fail() { FAIL=$((FAIL+1)); echo -e "${RED}[FAIL]${NC} $1"; }

echo "========================================"
echo "  Flowbase Jira — Smoke Test"
echo "========================================"
echo ""

# 1. Check Docker
echo "--- Prerequisites ---"
if docker info >/dev/null 2>&1; then
  pass "Docker is running"
else
  fail "Docker is not running (start Docker Desktop first)"
fi

# 2. Check docker compose
if docker compose version >/dev/null 2>&1; then
  pass "Docker Compose available"
else
  fail "Docker Compose not found"
fi

echo ""
echo "--- Build & Start ---"

# 3. Build and start
if docker compose up -d --build >/dev/null 2>&1; then
  pass "Docker Compose up (build + start)"
else
  fail "Docker Compose failed"
fi

# 4. Wait for healthy backend
echo ""
echo "--- Health Checks ---"
sleep 5
for i in $(seq 1 30); do
  STATUS=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/actuator/health 2>/dev/null || echo "000")
  if [ "$STATUS" = "200" ]; then
    pass "Backend health endpoint"
    break
  fi
  if [ "$i" = "30" ]; then
    fail "Backend health endpoint (timeout)"
  fi
  sleep 2
done

# 5. Test frontend
FRONTEND_CODE=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:3000 2>/dev/null || echo "000")
if [ "$FRONTEND_CODE" = "200" ]; then
  pass "Frontend serves at localhost:3000"
else
  fail "Frontend not serving (HTTP $FRONTEND_CODE)"
fi

# 6. Test login
echo ""
echo "--- API Tests ---"
LOGIN_RESP=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@flowbase.com","password":"password"}')
echo "$LOGIN_RESP" | grep -q '"token"' && pass "Login with seed user" || fail "Login failed"

# 7. Extract token and test protected endpoint
TOKEN=$(echo "$LOGIN_RESP" | sed 's/.*"token":"\([^"]*\)".*/\1/')
if [ -n "$TOKEN" ]; then
  PROTECTED_CODE=$(curl -s -o /dev/null -w '%{http_code}' \
    -H "Authorization: Bearer $TOKEN" \
    http://localhost:8080/api/v1/projects)
  [ "$PROTECTED_CODE" = "200" ] && pass "Protected endpoint with valid token (200)" || fail "Protected endpoint (HTTP $PROTECTED_CODE)"
else
  fail "Could not extract token"
fi

# 8. Test protected endpoint without token (should 401/403)
UNAUTH_CODE=$(curl -s -o /dev/null -w '%{http_code}' http://localhost:8080/api/v1/projects)
[ "$UNAUTH_CODE" = "403" ] && pass "Protected endpoint without token (403)" || fail "Protected endpoint without token returned $UNAUTH_CODE (expected 403)"

# 9. Test registration
REG_RESP=$(curl -s -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"displayName":"Smoke Tester","email":"smoke@test.com","password":"test123"}')
echo "$REG_RESP" | grep -q '"token"' && pass "Register new user" || fail "Registration failed"

# 10. Test Maven tests pass locally
echo ""
echo "--- Unit Tests ---"
if mvn test -q 2>/dev/null; then
  pass "Maven tests (31 tests)"
else
  fail "Maven tests failed"
fi

echo ""
echo "========================================"
echo -e "  Results: ${GREEN}$PASS passed${NC}, ${RED}$FAIL failed${NC}"
echo "========================================"

[ "$FAIL" -eq 0 ] && exit 0 || exit 1
