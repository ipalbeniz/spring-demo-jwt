# spring-demo-jwt
Prueba de API con token de seguridad (JWT) para acceder a un Endpoint seguro

### Obtener Token

Para obtener un token se debe realizar una petición GET a http://localhost:8080/token con dos query params:
- user=iperez
- password=1234

Ejemplo: 

```
curl "http://localhost:8080/token?user=iperez&password=1234"
```

En la respuesta vendrán 4 valores:
 - access_token: el token de acceso a los recursos seguros
 - token_type: el tipo de token
 - expires_in: tiempo de vida en segundos del token de acceso
 - refresh_token: token especial para refrescar el token de acceso

```json
{
  "access_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcGVyZXoiLCJleHAiOjE0NzkzMjc1MTZ9.-kYRDxGvSaSFxq3VcmqJoXByFXyfytMYz2DMTSRBFGEa02eaOOFpYeezKG5VWqVRZO73TWiYDWayKGhbF-_4Yg",
  "token_type": "Bearer",
  "expires_in": 60,
  "refresh_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcGVyZXoiLCJleHAiOjE0Nzk0MTM4NTZ9.kjOhsAvVdeHCb_PcYOonXXlZzAaJCxCH2lAApw1EqZOUyEHDIwWkAAJp9oV2t7ZhJUvxpIdV5aXvFPLQca--xQ"
}
```

### Refrescar Token

Cuando el token ha expirado podríamos obtener un nuevo token sin tener que volver a enviar el usuario y password.
Para ello es necesario realizar una petición GET a http://localhost:8080/refresh-token con un query param:
- refresh_token={{refresh_token}}

Ejemplo: 

```
curl "http://localhost:8080/refresh-token?refresh_token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcGVyZXoiLCJleHAiOjE0Nzk0MTM0NDF9.MjELbpYivuekxNNYefugkb50EGsW4f02MsAjAEbfA96HyRz5QODO0D5rDbrnOMkZscbU88rlMk9IbK43I6UEnA"
```

La respuesta tendrá la misma información que el endpoint de obtener token, incluyendo el nuevo token de acceso.

```json
{
  "access_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcGVyZXoiLCJleHAiOjE0NzkzMjc0MTd9.AmpEGWb9d6X0khgbDq69CvdHuSENhfkopn2ThZ9LrZOcD430W3KPj4Yln4Z9tbo7fh8h4lADG6c_i2LjXGDbyw",
  "token_type": "Bearer",
  "expires_in": 60,
  "refresh_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcGVyZXoiLCJleHAiOjE0Nzk0MTM0NDF9.MjELbpYivuekxNNYefugkb50EGsW4f02MsAjAEbfA96HyRz5QODO0D5rDbrnOMkZscbU88rlMk9IbK43I6UEnA"
}
```

### Endpoint Seguro

Una vez tengamos un token de acceso podemos llamar a http://localhost:8080/secured-message enviando la siguiente cabecera:
- Authorization: Bearer {{access_token}}

Ejemplo:

```
curl -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJpcGVyZXoiLCJleHAiOjE0NzkzMzAwOTB9.V6n8UU0Y08M147Oy-sTnZi6HQes1CNoxC1JmPaAUCg_DihY05ky1y_N8BU-1O5gkUthiW5y44H8pxFc7ltqT3Q" "http://localhost:8080/secured-message"
```

El API validará el token, los permisos y si todo es correcto devolverá el mensaje *Hello secured world!*