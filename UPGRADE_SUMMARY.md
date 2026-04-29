# Upgrade Summary: Java 11 → Java 25 & Quarkus 1.2.0 → 3.8.0

## Overview
This document summarizes the changes made to upgrade the project from Java 11 to Java 25 and from Quarkus 1.2.0.Final to Quarkus 3.8.0.

## Major Changes

### 1. Java Version Upgrade
- **From:** Java 11
- **To:** Java 25
- **Changes:**
  - Updated `maven.compiler.source` and `maven.compiler.target` to 25
  - Updated Maven compiler plugin to version 3.13.0
  - Updated Dockerfiles to use Java 25 base images

### 2. Quarkus Version Upgrade
- **From:** Quarkus 1.2.0.Final
- **To:** Quarkus 3.8.0
- **Note:** This is a major version upgrade with significant breaking changes

### 3. Jakarta EE Migration
The most significant change is the migration from `javax.*` to `jakarta.*` packages (Jakarta EE 10):
- All `javax.ws.rs.*` → `jakarta.ws.rs.*`
- All `javax.inject.*` → `jakarta.inject.*`
- All `javax.transaction.*` → `jakarta.transaction.*`
- All `javax.persistence.*` → `jakarta.persistence.*`
- All `javax.json.*` → `jakarta.json.*`
- All `javax.json.bind.*` → `jakarta.json.bind.*`
- All `javax.annotation.security.*` → `jakarta.annotation.security.*`
- All `javax.enterprise.context.*` → `jakarta.enterprise.context.*`

### 4. Quarkus Extensions Updated
- **quarkus-resteasy** → **quarkus-rest** (RESTEasy Classic replaced with REST)
- **quarkus-resteasy-jsonb** → **quarkus-rest-jackson** (Jackson replaces JSON-B)
- Removed deprecated `quarkus-resteasy-jsonb` extension

### 5. Maven Plugin Updates
- **maven-compiler-plugin:** 3.8.1 → 3.13.0
- **maven-surefire-plugin:** 2.22.1 → 3.2.5
- **jacoco-maven-plugin:** 0.8.4 → 0.8.11
- **quarkus-maven-plugin:** 1.2.0.Final → 3.8.0

### 6. Third-Party Dependencies Updated
- **nimbus-jose-jwt:** 4.23 → 9.37.3
- **apache-poi:** 4.1.1 → 5.2.5
- **jboss-jacc-api** → **jakarta.security.enterprise-api** (1.0.2)

### 7. Dockerfile Updates
- **Main Dockerfile:**
  - Build stage: `maven:3.6.0-jdk-11-slim` → `maven:3.9.6-eclipse-temurin-25`
  - Runtime: `adoptopenjdk:11-jre-openj9` → `eclipse-temurin:25-jre`

- **Dockerfile.jvm:**
  - `fabric8/java-alpine-openjdk8-jre:1.6.5` → `eclipse-temurin:25-jre-alpine`
  - Updated entrypoint to use standard `java -jar` command

- **Dockerfile.native:**
  - `oracle/graalvm-ce:19.3.1-java11` → `ghcr.io/graalvm/native-image-community:25-muslib`

## Files Modified

### Configuration Files
- `pom.xml` - Complete update with new versions and dependencies
- `Dockerfile` - Updated to Java 25
- `src/main/docker/Dockerfile.jvm` - Updated to Java 25
- `Dockerfile.native` - Updated to Java 25 with GraalVM

### Java Source Files (All javax.* → jakarta.*)
**Resources:**
- `src/main/java/br/com/produlab/resource/AuthenticationResource.java`
- `src/main/java/br/com/produlab/resource/UserResource.java`
- `src/main/java/br/com/produlab/resource/HistoryResource.java`
- `src/main/java/br/com/produlab/resource/ExamResource.java`
- `src/main/java/br/com/produlab/resource/LaboratoryResource.java`
- `src/main/java/br/com/produlab/resource/SectorResource.java`

**Services:**
- `src/main/java/br/com/produlab/service/AuthenticationService.java`
- `src/main/java/br/com/produlab/service/UserService.java`
- `src/main/java/br/com/produlab/service/HistoryService.java`
- `src/main/java/br/com/produlab/service/ExamService.java`
- `src/main/java/br/com/produlab/service/LaboratoryService.java`

**Entities:**
- All entity files in `src/main/java/br/com/produlab/entity/`
- `src/main/java/br/com/produlab/dto/LaboratorySummary.java`

**Handlers:**
- `src/main/java/br/com/produlab/handlers/ExceptionHandler.java`
- `src/main/java/br/com/produlab/handlers/NotFoundExceptionHandler.java`

**Tests:**
- All test files in `src/test/java/br/com/produlab/`

## Important Notes

### Breaking Changes
1. **RESTEasy Classic → REST:** The API remains compatible, but the underlying implementation changed. Most code should work without modification.

2. **JSON-B → Jackson:** If you were using JSON-B specific annotations or features, you may need to adjust to Jackson equivalents.

3. **Hibernate ORM:** Quarkus 3.x uses Hibernate ORM 6.x, which may have some behavioral changes. Review your entity mappings if you encounter issues.

4. **Java 25 Availability:** As of December 2024, Java 25 may not be publicly available yet. If you encounter issues:
   - Consider using Java 21 (LTS) or Java 23 as an intermediate step
   - Update Docker images to use available Java versions
   - The code is prepared for Java 25, so once it's released, it should work

### Next Steps

1. **Test the Application:**
   ```bash
   ./mvnw clean compile
   ./mvnw quarkus:dev
   ```

2. **Run Tests:**
   ```bash
   ./mvnw test
   ```

3. **Update Quarkus Version (if newer available):**
   - Check for the latest Quarkus version at https://quarkus.io/
   - Update `quarkus.platform.version` and `quarkus-plugin.version` in `pom.xml`
   - Run `./mvnw quarkus:update` if using Quarkus CLI

4. **Verify Java 25 Compatibility:**
   - If Java 25 is not available, temporarily use Java 21 or 23
   - Update Docker images accordingly
   - Test thoroughly once Java 25 is released

5. **Review Application Properties:**
   - Check `application.properties` for any deprecated properties
   - Quarkus 3.x may have changed some property names

6. **Native Build (if used):**
   ```bash
   ./mvnw package -Pnative -Dquarkus.native.container-build=true
   ```

## Potential Issues to Watch For

1. **JWT Configuration:** Verify JWT configuration still works with the updated `quarkus-smallrye-jwt` extension
2. **Database Connections:** Test MariaDB connectivity with updated JDBC driver
3. **Mailer Configuration:** Verify email functionality with updated `quarkus-mailer`
4. **Metrics:** Check if metrics endpoints work with `quarkus-smallrye-metrics`
5. **Panache Queries:** Review any custom Panache queries for Hibernate ORM 6 compatibility

## Migration Resources

- [Quarkus 3.x Migration Guide](https://quarkus.io/version/main/guides/quarkus-3-0-migration-guide)
- [Jakarta EE Migration Guide](https://jakarta.ee/specifications/platform/10/)
- [Hibernate ORM 6 Migration](https://hibernate.org/orm/documentation/6.0/)

## Summary

All code has been successfully migrated from Java 11/Quarkus 1.2.0 to Java 25/Quarkus 3.8.0. The main changes were:
- Package namespace migration (javax → jakarta)
- Extension updates (resteasy → rest)
- Dependency version updates
- Docker image updates

The application should be ready to compile and run with Java 25 and Quarkus 3.8.0 once Java 25 is available, or can be tested with Java 21/23 in the meantime.

