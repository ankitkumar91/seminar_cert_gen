# Certificate Desk

Web platform for campus seminars: an admin uploads a finished certificate image, a developer places the standard attendee fields on that image, and a time-bound link lets students download a personalised PDF.

This repository is a **Tomcat 10** WAR built with **Java 17+**, **Jakarta Servlet 6**, **JSP**, **JSTL**, **Bootstrap 5**, and **jQuery**.

## Architecture (planning)

```
Attendee (shared URL)          Admin                         Developer
        |                        |                               |
        |                        |-- create seminar              |
        |                        |-- upload 1920×1358 PNG       |
        |                        |------------------------------>|
        |                        |                     align fields
        |                        |                     approve design
        |                        |<------------------------------|
        |                        |-- generate link + expiry      |
        |<--- WhatsApp (manual) --|                               |
        | fill fixed form                                         |
        | overlay text on image → PDF download                    |
```

| Layer | Choice | Why |
| --- | --- | --- |
| App server | Apache Tomcat 10.1 | Jakarta Servlet/JSP, WAR deploy |
| UI | JSP + Bootstrap 5 + jQuery | Matches the requested stack |
| Persistence | H2 file database | Zero extra services; swap JDBC URL for MySQL later |
| Files | Disk under `CERTIFY_DATA_DIR` | Certificate PNGs stay off the classpath |
| PDF | Java2D overlay + Apache PDFBox | Draw submitted values at stored coordinates, wrap as PDF |
| Auth | Session + BCrypt | Separate `ADMIN` and `DEVELOPER` roles |

### Status flow

1. **Draft** — seminar saved, no image (or image not yet accepted).
2. **Pending developer approval** — image uploaded; admin cannot create links.
3. **Approved** — developer saved field coordinates and approved; admin may create links.

Re-uploading a design returns the seminar to pending approval.

### Fixed form fields (Section 5 — working set until the client confirms)

These fields are identical for every seminar:

- Full name
- Email
- Mobile number
- College / organisation
- Enrollment / roll number
- Role (Student / Faculty / Research scholar / Participant)

### Certificate image standard

Uploads must be **1920 × 1358 pixels**, PNG or JPEG. That is A4 landscape proportion at a web-practical resolution. Change `AppConfig.CERT_WIDTH` / `CERT_HEIGHT` if the client locks a different size.

The design team supplies one complete flat image (college name, logo, borders). The application only paints the six field values.

### Link control

Each link is a random token (`/c/{token}`). After `expires_at`, the form is replaced by a static expired message. Sharing is copy-paste (WhatsApp, email); there is no bulk send.

### Out of scope (honoured)

- Per-seminar custom form builders
- Automatic WhatsApp/SMS/email
- Logo overlay or certificate layout design in the app

## Run locally with Tomcat

Requirements: JDK 17+, Maven 3.8+, Tomcat 10.1 (the helper script downloads Tomcat if needed).

```bash
chmod +x scripts/run-local.sh
./scripts/run-local.sh
```

The app listens on **http://127.0.0.1:18473/**.

### Demo accounts

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `Admin@123` |
| Developer | `developer` | `Dev@123` |

Seeded attendee link (valid 30 days from first start): `/c/demo-nwcj-2026`

Data directory: `./data` (H2 file + uploads). Delete it to re-seed.

### Manual Tomcat deploy

```bash
mvn -DskipTests package
# copy target/ROOT.war into $CATALINA_HOME/webapps/ROOT.war
export CATALINA_OPTS="-Dcertify.data.dir=/var/lib/certify"
```

## Project layout

```
src/main/java/com/certify/   servlets, filters, JDBC DAOs, PDF overlay
src/main/webapp/WEB-INF/jsp  JSP views
src/main/webapp/assets       CSS / jQuery pages
scripts/run-local.sh         Build WAR and start Tomcat
```

Noto Sans fonts under `WEB-INF/fonts` are licensed as SIL Open Font License (Google Noto).
