# Mind API v2

Mind API v2 is a stateless REST API secured with OAuth2 using the **Authorization Code Flow**.

The API exposes both public and protected endpoints.  
Protected endpoints require a valid **ID Token** issued by Google OAuth2.

For full API documentation, refer to Swagger UI:

<http://localhost:8080/swagger-ui/index.html>

---

## Requirements

- Java 17+
- Maven
- MongoDB
- Internet access (Google OAuth2)

---

## Run the API (Development)

If the API is not deployed, start it locally with:

```bash
mvn spring-boot:run
```

The API will be available at: **<http://localhost:8080>**

## Authentication (OAuth2)

To access protected endpoints, you must obtain a valid ID Token.

Step-by-step tutorial using Google OAuth Playground

Go to:

**<https://developers.google.com/oauthplayground/>**

**1** - Open Settings (top right corner) and configure:
        Authorization endpoint: <https://accounts.google.com/o/oauth2/v2/auth>
        Token endpoint: <https://oauth2.googleapis.com/token>

**2** - select the following scopes:
        **openid profile email**

**3** - Click Authorize APIs.

**4** - click Exchange authorization code for tokens.
        Copy the ID Token from the response.

**5** - Accessing Protected Endpoints

### Linux / macOS

```bash
curl -H "Authorization: Bearer <ID_TOKEN>" \
http://localhost:8080/api/v2/<END_POINT>
```

### Windows (PowerShell)

```powershell
curl -Headers @{ Authorization = "Bearer <ID_TOKEN>" } `
http://localhost:8080/api/v2/<END_POINT>
```

## Public Endpoints (no authentication needed)

### Health Check

```bash
❯ curl 'http://localhost:8080/api/v2/actuator/health'  -X GET -H 'Accept: application/json' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   347   0   347   0     0   412     0  --:--:-- --:--:-- --:--:--   412
```

Should give you this

```JSON
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 511017222144,
        "free": 477076635648,
        "threshold": 10485760,
        "path": "/home/shrek/workspace/SE-G01-02/back/fer/.",
        "exists": true
      }
    },
    "mongo": {
      "status": "UP",
      "details": {
        "maxWireVersion": 25
      }
    },
    "ping": {
      "status": "UP"
    },
    "ssl": {
      "status": "UP",
      "details": {
        "validChains": [],
        "invalidChains": []
      }
    }
  }
}

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 511017222144,
        "free": 477076635648,
        "threshold": 10485760,
        "path": "/home/shrek/workspace/SE-G01-02/back/fer/.",
        "exists": true
      }
    },
    "mongo": {
      "status": "UP",
      "details": {
        "maxWireVersion": 25
      }
    },
    "ping": {
      "status": "UP"
    },
    "ssl": {
      "status": "UP",
      "details": {
        "validChains": [],
        "invalidChains": []
      }
    }
  }
}
```

### Error Endpoint

```bash
curl 'http://localhost:8080/api/v2/error' -X GET -H 'Accept: application/json' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    32   0    32   0     0  5282     0  --:--:-- --:--:-- --:--:--  5333
```

Should give you this

```json
{
  "error": "Something went wrong"
}
```

## Notes

The API is fully stateless.

Authentication relies on Google-issued ID Tokens.

Refer to Swagger UI for the complete list of endpoints and schemas.

API Documentation
Swagger UI is available at:

<http://localhost:8080/swagger-ui/index.html>
