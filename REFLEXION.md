# Reflexión

## 1. ¿Qué partes generó bien la IA sin necesidad de corrección?

La estructura general de capas (entity/repository/service/controller/dto/security) salió ordenada desde el principio y no hubo que reacomodarla. Los repositorios de Spring Data, el filtro JWT (`JwtAuthenticationFilter`) y el armado inicial del `docker-compose.yml` con el healthcheck de la base de datos funcionaron sin tener que tocarlos. La separación entre `AuthService`/`AuthController` también quedó bien planteada desde el primer intento, con la rotación del refresh token incluida sin que se la pidiera explícitamente.

## 2. ¿Qué errores o decisiones incorrectas tomó la IA, especialmente en temas de seguridad?

El error más importante fue en el manejo de claves: en `application.yaml` y en `docker-compose.yml` la IA dejó valores por defecto para el `JWT_SECRET` y para las credenciales de la base de datos (`restaurante`/`restaurante`) escritos directamente en el archivo, como "fallback" en caso de que no se defina la variable de entorno. Técnicamente el mecanismo está bien armado (se puede sobreescribir con una variable de entorno), pero el problema es que ese fallback queda committeado en el repositorio de GitHub: si en producción alguien se olvida de configurar las variables de entorno, la aplicación va a levantar igual, pero usando un secreto de JWT y una contraseña de base de datos que cualquiera puede ver en el código. La IA generó la estructura "correcta" en el papel, pero no reflexionó por su cuenta que dejar ese valor de respaldo débil y visible en el repo es en sí mismo un riesgo — hubo que pedirle explícitamente que revisara el manejo de claves para que lo señalara.

## 3. ¿Cómo detectaron esos errores y cómo los corrigieron?

El problema de las claves se detectó pidiéndole a la IA una revisión puntual de la configuración de JWT, no porque lo haya marcado de forma proactiva la primera vez que generó los archivos. El otro error, el de `DaoAuthenticationProvider` sin constructor vacío en la versión nueva de Spring Security, ni siquiera lo pudo detectar la IA sola: su propia terminal no podía compilar el proyecto por una restricción de red del entorno donde corre, así que el bug real recién apareció al compilar en IntelliJ. Eso deja una lección clara: el hecho de que la IA "no marque errores" no significa que el código esté probado, hay que compilarlo y correrlo de verdad para confirmarlo.

## 4. Si tuviera que explicarle a un compañero cómo funciona la autorización por sucursal, ¿qué le diría?

Le diría que no alcanza con revisar `hasRole("ENCARGADO")` en `SecurityConfig`, porque eso solo confirma el rol, no a qué sucursal pertenece el usuario. La sucursal se guarda como claim (`sucursalId`) dentro del JWT al momento del login, y se recupera en cada request a través de `UsuarioPrincipal`. Después, antes de dejar modificar una mesa o un pedido, `MesaService.validarAccesoSucursal()` y `PedidoService.validarAcceso()` comparan el `sucursalId` del usuario logueado contra la sucursal real de la mesa (o de la mesa asociada al pedido). Si no coinciden, se devuelve `403 Forbidden` aunque el rol sea el correcto. Un `ADMIN` se salta esa comparación porque tiene acceso a todas las sucursales, y un `CLIENTE` en cambio se valida distinto: contra si el pedido le pertenece a él, no contra una sucursal.

---

En general la IA ayudó a avanzar mucho más rápido de lo que hubiera podido hacerlo solo, sobre todo en la parte repetitiva (entidades, DTOs, repositorios). Pero quedó claro que "funciona" no es lo mismo que "está bien". El caso de las claves con valores por defecto committeados es el ejemplo más claro: el código corre sin errores y cumple lo que se le pidió, pero hay que revisar con criterio propio qué se está validando realmente, porque la IA no siempre reflexiona sobre las brechas de seguridad que va dejando pasar.
