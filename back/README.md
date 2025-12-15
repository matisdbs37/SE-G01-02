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
 - 3. Click on step 1 on the left and add openid,profile,email and click Authorize api
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
    