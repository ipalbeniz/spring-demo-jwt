# spring-demo-jwt
Prueba de API con token de seguridad (JWT) para acceder a un Endpoint seguro

### Autenticación
Llamar a http://localhost:8080/auth con dos parámetros:
- user=iperez
- password=1234

Ejemplo: http://localhost:8080/auth?user=iperez&password=1234

En la respuesta vendrá el token que debemos guardarnos:

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcGVyZXoiLCJleHAiOjE0NzYxMzQxMTV9.Zf77elufuhYpls4I4feR5WauLmd5C3z5-BfL8TfJ3ZlI4eNgcYS4DXvPEZScax9z3pk4_kg8XN255Jih5p_zcQ",
  "expirationDate": "2016-10-10T23:15:15.538"
}
```

### Endpoint seguro
Una vez tengamos el token podemos llamar a http://localhost:8080//secured-message enviando la siguiente cabecera:
- Authorization={{token}}

El API validará el token y si es correcto devolverá el mensaje *Hello secured world!*