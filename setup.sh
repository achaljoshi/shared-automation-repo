#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# setup.sh — First-time project setup (macOS / Linux)
#
# Run once after cloning:
#   chmod +x setup.sh && ./setup.sh
#
# What it does:
#   1. Verifies Java 11 is present
#   2. Makes the Maven wrapper executable
#   3. Builds shared-core modules
#   4. Builds team-a and installs its test-jar (+ test-sources.jar) to .m2
#   5. Compiles team-b to verify the dependency resolves
#   6. Prints a "ready" message with next steps
# ─────────────────────────────────────────────────────────────────────────────
set -e

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; NC='\033[0m'

echo ""
echo "════════════════════════════════════════════════════════════"
echo "  Shared Automation Framework — First Time Setup"
echo "════════════════════════════════════════════════════════════"
echo ""

# ── 1. Check Java 11 ─────────────────────────────────────────────────────────
echo "▶ Checking Java version..."
JAVA_VER=$(java -version 2>&1 | grep -oE '"[0-9]+' | head -1 | tr -d '"')
if [ "$JAVA_VER" != "11" ]; then
  echo -e "${RED}✗ Java 11 required. Found: $JAVA_VER${NC}"
  echo "  Install OpenJDK 11 and set JAVA_HOME, then re-run this script."
  exit 1
fi
echo -e "${GREEN}✓ Java $JAVA_VER found${NC}"

# ── 2. Maven wrapper ──────────────────────────────────────────────────────────
chmod +x mvnw
echo -e "${GREEN}✓ Maven wrapper ready${NC}"

# ── 3. Build shared-core ──────────────────────────────────────────────────────
echo ""
echo "▶ Building shared-core modules..."
./mvnw install \
  -pl shared-core/cucumber-base,shared-core/selenium-base,shared-core/api-base,shared-core/testdata-base \
  -am -DskipTests -q
echo -e "${GREEN}✓ shared-core installed${NC}"

# ── 4. Build team-a + install test-jar and test-sources.jar ───────────────────
echo ""
echo "▶ Building team-a-system1 (shared step-jar + sources)..."
./mvnw install -pl team-a-system1 -DskipTests -q
echo -e "${GREEN}✓ team-a-system1 installed${NC}"
echo "    JARs in ~/.m2/repository/com/sharedframework/team-a-system1/:"
ls ~/.m2/repository/com/sharedframework/team-a-system1/1.0.0-SNAPSHOT/*.jar 2>/dev/null \
  | xargs -I{} basename {} | sed 's/^/    • /'

# ── 5. Compile team-b ─────────────────────────────────────────────────────────
echo ""
echo "▶ Verifying team-b-system2 resolves team-a dependency..."
./mvnw compile -pl team-b-system2 -q
echo -e "${GREEN}✓ team-b-system2 compiles successfully${NC}"

# ── 6. Done ───────────────────────────────────────────────────────────────────
echo ""
echo "════════════════════════════════════════════════════════════"
echo -e "${GREEN}  Setup complete! Dependencies installed.${NC}"
echo "════════════════════════════════════════════════════════════"
echo ""
echo -e "${YELLOW}  NEXT — open your IDE NOW (setup must finish before IDE import):${NC}"
echo ""
echo "  ┌─ IntelliJ IDEA ─────────────────────────────────────────┐"
echo "  │  File → Open → select THIS folder                       │"
echo "  │  Click 'Open as Maven Project' when prompted            │"
echo "  │  Run configs appear in toolbar automatically            │"
echo "  │  Ctrl+Click any .feature step → jumps to correct .java  │"
echo "  └──────────────────────────────────────────────────────────┘"
echo ""
echo "  ┌─ VS Code ────────────────────────────────────────────────┐"
echo "  │  Run: code .                                             │"
echo "  │  Click 'Install' on extension recommendations popup     │"
echo "  │  Wait for Java indexing (progress bar bottom-left)      │"
echo "  │  Ctrl+Click any .feature step → jumps to correct .java  │"
echo "  └──────────────────────────────────────────────────────────┘"
echo ""
echo "  Run tests from terminal (IDE optional):"
echo "    ./mvnw test -pl team-a-system1"
echo "    ./mvnw test -pl team-b-system2"
echo ""
