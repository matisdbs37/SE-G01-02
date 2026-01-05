# mind API v2 

### Stateless api secure with Oauth2 authorization code flow
To acces the protected resources/endpoint you need to follow these steps 
*for devs if not deployed, first dont forget to launch the api with mvn spring-boot:run*

 - 1. Go to this url https://developers.google.com/oauthplayground/
 - 2. Select settiings on the top right corner and add these urls : 
        ```bash
        Authorization endpoint: https://accounts.google.com/o/oauth2/v2/auth
        Token endpoint: https://oauth2.googleapis.com/token
        ```
 - 3. Click on step 1 on the left and add **openid,profile,email** and click Authorize api
 - 4. Click on step 2 and click on Exchange authorization code for token
 - 5. **Linux** use this command 
        ```bash
        curl -H "Authorization: Bearer <ID_TOKEN>" \
        http://localhost:8080/api/v2/<END_POINT>
        ```
      **Windows powerShell**    
        ```shell
        curl -Headers @{ Authorization = "Bearer <ID_TOKEN>" } http://localhost:8080/api/v2/<END_POINT>
        ```
    
## Public end points
### HEALTH
```bash
~ ❯ curl 'http://localhost:8080/api/v2/actuator/health'  -X GET -H 'Accept: application/json' | jq
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
```
### ERROR 
```bash
curl 'http://localhost:8080/api/v2/error' -X GET -H 'Accept: application/json' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    32   0    32   0     0  5282     0  --:--:-- --:--:-- --:--:--  5333
```
Should give you this
```JSON
{
  "error": "Something went wrong"
}
```
### LOGOUTé
```bash
~ ❯ curl 'http://localhost:8080/api/v2/logout' -X GET -H 'Accept: application/json' | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    49   0    49   0     0  3671     0  --:--:-- --:--:-- --:--:--  3769
```
Should give you this
```JSON
{
  "logout": "TODO: do something with the tokens ?"
}
```**