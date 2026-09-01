# Certificate Desk

Web platform for campus seminars: an admin uploads a finished certificate image, a developer places the standard attendee fields on that image, and a time-bound link lets students download a personalised PDF.

This repository is a **Tomcat 9** WAR built with **Java 17**, **Servlet 4.0 (`javax.servlet`)**, **JSP**, **JSTL 1.2**, **Bootstrap 5**, and **jQuery**.

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
| App server | Apache Tomcat 9 | Servlet 4.0 / JSP 2.3 WAR deploy |
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

- Full name (required)
- Email (required, format-checked)
- Mobile number (optional, 10-digit format-checked when entered)
- Institute (required)
- Speciality (required)
- Designation (required)

The developer chooses which of these values are drawn on each seminar’s certificate (add/remove on the alignment screen). The attendee form does not change.

### Certificate image standard

Uploads must be **1920 × 1358 pixels**, PNG or JPEG. That is A4 landscape proportion at a web-practical resolution. Change `AppConfig.CERT_WIDTH` / `CERT_HEIGHT` if the client locks a different size.

The design team supplies one complete flat image (college name, logo, borders). The application only paints the six field values.

### Link control

Each link is a random token (`/c/{token}`). After `expires_at`, the form is replaced by a static expired message. Sharing is copy-paste (WhatsApp, email); there is no bulk send.

### Admin and developer lists

Seminar dashboards load **25 rows per page**, with search on title or organiser and a status filter. Link and download totals come from two counted subqueries on the current page, not one query per row. The developer queue lists **pending approval first**. Indexes on `seminars(created_at)`, `seminars(status)`, `share_links(seminar_id)`, and `submissions(seminar_id)` are created on startup.

### Out of scope (honoured)

- Per-seminar custom form builders
- Automatic WhatsApp/SMS/email
- Logo overlay or certificate layout design in the app

## Run locally with Tomcat

Requirements: JDK 17, Maven 3.8+, Tomcat 9 (the helper script downloads Tomcat 9 if needed). Do not deploy this WAR on Tomcat 10 — that server expects `jakarta.servlet`.

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

Copy link on a seminar page works over HTTP as well as HTTPS (the clipboard API is blocked on plain HTTP, so the page falls back to `document.execCommand`). jQuery is shipped in the WAR, so the admin screens do not depend on `code.jquery.com`.

## Project layout

```
src/main/java/com/certify/   servlets, filters, JDBC DAOs, PDF overlay
src/main/webapp/WEB-INF/jsp  JSP views
src/main/webapp/assets       CSS / jQuery pages
scripts/run-local.sh         Build WAR and start Tomcat
```

Noto Sans fonts under `WEB-INF/fonts` are licensed as SIL Open Font License (Google Noto).

## Run in Eclipse (Tomcat 9)

This repo is a **Maven Dynamic Web Project** named `seminar-certificates`. Your Eclipse workspace is:

`C:\Users\Ankit\eclipse-workspace`

On your Windows PC, run the helper script so the project appears inside that workspace, then import it in Eclipse.

### 0. Put the project in `C:\Users\Ankit\eclipse-workspace` (Windows)

1. Clone or copy this repository to any folder on your PC (for example `C:\Users\Ankit\git\seminar-certificates`).
2. Double-click `scripts\add-to-eclipse-workspace.bat`  
   or from Command Prompt:

```bat
scripts\add-to-eclipse-workspace.bat C:\Users\Ankit\eclipse-workspace
```

That creates `C:\Users\Ankit\eclipse-workspace\seminar-certificates` (a junction to the clone when possible, otherwise a copy).

3. In Eclipse: **File → Switch Workspace → Other…** → `C:\Users\Ankit\eclipse-workspace` → Launch.

### 1. Import into that workspace

Use **Eclipse IDE for Enterprise Java and Web Developers** (M2E + WTP). Register JDK 17+ under **Window → Preferences → Java → Installed JREs**.

1. **File → Import… → Maven → Existing Maven Projects**.
2. **Root Directory**: `C:\Users\Ankit\eclipse-workspace\seminar-certificates`  
   (or the folder that contains `pom.xml` if you skip the script).
3. Finish. You should see project `seminar-certificates`.

If Maven import is missing: **File → Open Projects from File System…**, then right-click the project → **Configure → Convert to Maven Project**.

### 2. Add Apache Tomcat 9 as a server

1. **Window → Show View → Servers**.
2. In the Servers view: **No servers are available. Click this link to create a new server…** (or right-click → **New → Server**).
3. **Apache → Tomcat v9.0 Server** → Next.
4. **Tomcat installation directory**: your Tomcat 9 home (for example `C:\apache-tomcat-9.0.xx`).
5. Finish.

### 3. Run the app on that server

1. Right-click **seminar-certificates** → **Properties → Project Facets** and confirm **Dynamic Web Module 4.0** and **Java 17**.
2. Right-click the project → **Run As → Run on Server** → choose **Tomcat v9.0** → Finish.
3. Open `http://localhost:8080/seminar-certificates/` (Eclipse deploys with that context root, not `/`).

Demo accounts: `admin` / `Admin@123` and `developer` / `Dev@123`. Attendee demo: `http://localhost:8080/seminar-certificates/c/demo-nwcj-2026`.

Data files are written under Tomcat’s `certify-data` directory (or `catalina.base/certify-data`). To pin them, add a VM argument on the Tomcat server (**Open launch configuration → Arguments → VM arguments**):

```
-Dcertify.data.dir=C:\Users\Ankit\eclipse-workspace\seminar-certificates\data
```

To deploy at the server root instead of `/seminar-certificates`, double-click the Tomcat server → **Modules** → edit the path to `/`.

A **Maven package** launch config lives in `eclipse/Maven package.launch` (clean + package, skip tests). Use it to build `target/ROOT.war` without starting Tomcat.
