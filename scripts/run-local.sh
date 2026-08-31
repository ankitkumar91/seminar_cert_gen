#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
PORT="${PORT:-18473}"
TOMCAT_HOME="${TOMCAT_HOME:-$ROOT/tools/tomcat}"
DATA_DIR="${CERTIFY_DATA_DIR:-$ROOT/data}"
export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64}"

if [[ ! -x "$TOMCAT_HOME/bin/catalina.sh" ]] || ! grep -q "Apache Tomcat Version 9" "$TOMCAT_HOME/RELEASE-NOTES" 2>/dev/null; then
  mkdir -p "$ROOT/tools"
  echo "Downloading Apache Tomcat 9.0.121…"
  curl -fsSL -o /tmp/tomcat.tgz "https://downloads.apache.org/tomcat/tomcat-9/v9.0.121/bin/apache-tomcat-9.0.121.tar.gz"
  tar -xzf /tmp/tomcat.tgz -C "$ROOT/tools"
  rm -rf "$TOMCAT_HOME"
  mv "$ROOT/tools/apache-tomcat-9.0.121" "$TOMCAT_HOME"
  rm -rf "$TOMCAT_HOME/webapps/docs" "$TOMCAT_HOME/webapps/examples" "$TOMCAT_HOME/webapps/host-manager"
fi

echo "Building WAR…"
cd "$ROOT"
mvn -q -DskipTests package

if [[ -f "$TOMCAT_HOME/temp/certify.pid" ]]; then
  "$TOMCAT_HOME/bin/catalina.sh" stop || true
  sleep 2
fi

python3 - "$TOMCAT_HOME/conf/server.xml" "$PORT" <<'PY'
import sys
from pathlib import Path
path, port = Path(sys.argv[1]), sys.argv[2]
text = path.read_text()
import re
text = re.sub(r'(<Server\b[^>]*\bport=")(\d+)(")', r'\g<1>18005\g<3>', text, count=1)
text = re.sub(r'(<Connector\b[^>]*\bport=")(\d+)(")', rf'\g<1>{port}\g<3>', text, count=1)
if 'URIEncoding="UTF-8"' not in text:
    text = text.replace(f'port="{port}"', f'port="{port}" URIEncoding="UTF-8"', 1)
path.write_text(text)
print(f"Tomcat HTTP port -> {port}")
PY

rm -rf "$TOMCAT_HOME/webapps/ROOT" "$TOMCAT_HOME/webapps/ROOT.war"
cp "$ROOT/target/ROOT.war" "$TOMCAT_HOME/webapps/ROOT.war"
mkdir -p "$DATA_DIR" "$TOMCAT_HOME/temp" "$TOMCAT_HOME/logs"

export CATALINA_OPTS="-Dcertify.data.dir=${DATA_DIR} -Dfile.encoding=UTF-8"
export CATALINA_PID="$TOMCAT_HOME/temp/certify.pid"

echo "Starting Tomcat on http://127.0.0.1:${PORT}/"
exec "$TOMCAT_HOME/bin/catalina.sh" run
