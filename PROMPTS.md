# Bitácora de Prompts

Herramienta usada en todo el desarrollo: **Claude (Claude Code)**.

---

### 1. Creación del modelo de dominio

**Prompt (resumen):** se pidió armar el proyecto de N-Capas para el sistema de pedidos de restaurante según la definición del parcial, empezando por las entidades JPA (Sucursal, Mesa, Producto, Usuario, Pedido, ItemPedido, RefreshToken) y sus enums de estado y rol.

**Qué generó la IA:** las entidades con Lombok y las relaciones `@ManyToOne`/`@OneToMany` entre Mesa-Sucursal, Pedido-Mesa-Usuario-ItemPedido.

**Qué se corrigió y por qué:** la IA había agregado de más un campo `tokenVersion` en `Usuario` pensado para invalidar tokens por cambio de contraseña (mecanismo de la Opción A del enunciado). Se sacó porque se decidió implementar solo la Opción B (autorización por sucursal) y no mezclar dos reglas de negocio distintas sin necesidad.

---

### 2. Seguridad JWT

**Prompt (resumen):** implementar JWT con access token corto (15 min) y refresh token largo (7 días) con rotación, usando la librería `jjwt`.

**Qué generó la IA:** `JwtService`, `JwtAuthenticationFilter`, `SecurityConfig` y `UsuarioPrincipal`.

**Qué se corrigió y por qué:** al compilar en IntelliJ salió un error real: la versión de Spring Security que trae Spring Boot 4.1.0 ya no tiene el constructor vacío de `DaoAuthenticationProvider` ni el método `setUserDetailsService()`. La IA había generado el código con la API vieja porque en su propia terminal no pudo compilar el proyecto (ver punto 6) y no tenía forma de detectarlo hasta que se probó en un entorno real. Se corrigió pasando el `UserDetailsService` directo por el constructor: `new DaoAuthenticationProvider(userDetailsService)`.

---

### 3. Validación de la configuración del JWT y manejo de claves

**Prompt (resumen):** se pidió revisar que el JWT cumpliera buenas prácticas (tiempos de expiración correctos, firma, claims) y que las claves/secretos estuvieran protegidas correctamente, sin quedar expuestas en el código.

**Qué generó la IA:** confirmó la estructura general del JWT, pero al revisar `application.yaml` y `docker-compose.yml` se identificó que el `JWT_SECRET` y las credenciales de la base de datos tenían un valor por defecto ("fallback") escrito directamente en el archivo, visible en el repositorio. Esto se retoma con más detalle en `REFLEXION.md`.

---

### 4. Error al pushear sin hacer pull antes

**Prompt (resumen):** se pidió ayuda porque al pushear a un repositorio propio nuevo tiraba `! [rejected] main -> main (non-fast-forward)`.

**Qué generó la IA:** explicó que el repo de GitHub tenía un commit inicial (README generado al crear el repo) que no existía en el historial local, y dio el comando `git pull origin main --allow-unrelated-histories` para unificar los historiales antes de volver a pushear.

**Qué se corrigió:** no fue un tema de código, sino de git; se aplicó el comando indicado y el push se completó sin problema.

---

### 5. AuthController y AuthService

**Prompt (resumen):** armar el controller y el service de autenticación (`/api/auth/login`, `/api/auth/refresh`) usando el `JwtService` ya creado, y validar que quedaran correctamente conectados con el mecanismo de JWT.

**Qué generó la IA:** `AuthController` con los dos endpoints y `AuthService` con rotación del refresh token (cada refresh invalida el token anterior y persiste uno nuevo en base de datos).

**Qué se corrigió:** se validó que los tiempos de expiración coincidieran con lo pedido en el enunciado (15 min access / 7 días refresh) y que el refresh token viejo quedara marcado como revocado al usarse.

---

### 6. Servicios de Sucursales, Mesas, Productos y Usuarios

**Prompt (resumen):** se pidió crear los servicios tomando en cuenta la diferencia de negocio entre un ADMINISTRADOR (maneja todas las sucursales) y un ENCARGADO (maneja solamente la sucursal a la que pertenece).

**Qué generó la IA:** `MesaService` con un método `validarAccesoSucursal()` que compara la sucursal de la mesa contra la sucursal del usuario autenticado, y lo mismo en `PedidoService` para pedidos — no se resuelve solo con el rol.

**Qué se corrigió:** una comparación de rol que la IA había hecho con texto (`actor.getUsuario().getRol().name().equals("ENCARGADO")`) se cambió a comparación directa del enum (`== Rol.ENCARGADO`) para que quedara más prolijo y consistente con el resto del código.

---

### 7. Chequeo de compilación con JDK 25

**Prompt (comando exacto pedido):**
```
cd "C:\Users\srodriguez\Downloads\uca\N-CAPAS\Parcial Final\pnc-parcial-final-restaurante-012026" && JAVA_HOME="C:\Users\srodriguez\.jdks\ms-25.0.3" ./gradlew compileJava --console=plain 2>&1 | tail -100
```
Se pidió correr la compilación del proyecto y revisar el log generado para detectar errores.

**Qué generó la IA:** en su propia terminal, el comando falló con `Unable to establish loopback connection` al intentar levantar el daemon de Gradle. Se le pidió investigar la causa.

**Qué se corrigió/diagnosticó:** la IA armó una prueba mínima con `Selector.open()` de Java puro (sin ninguna dependencia del proyecto) para confirmar que el problema era del entorno de esa terminal específica (sockets Unix bloqueados a nivel de red), no del proyecto. La compilación real se hizo después en IntelliJ, donde sí funcionó y ahí se detectó el bug real de `DaoAuthenticationProvider` del punto 2.

---

### 8. Validación del Dockerfile y docker-compose

**Prompt (resumen):** se pidió armar el `Dockerfile` y el `docker-compose.yml` para levantar la API junto con Postgres, y correrlo de forma local para comprobar que todo funcionara correctamente.

**Qué generó la IA:** `Dockerfile` multi-stage (build con JDK 25, imagen final solo con el JRE) y `docker-compose.yml` con la API, la base de datos y un healthcheck para que la API espere a que Postgres esté lista.

**Qué se corrigió/quedó pendiente de validar:** al correr `docker compose up` en el entorno local dio error de conexión al engine de Docker (`open //./pipe/dockerDesktopLinuxEngine`) porque Docker Desktop no estaba iniciado — no es un error del Dockerfile. Se resolvió iniciando Docker Desktop antes de volver a levantar los contenedores.
