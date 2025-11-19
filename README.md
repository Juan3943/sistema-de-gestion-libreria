# 📚 Librería- Sistema de Gestión Integral

Sistema de gestión desarrollado en JavaFX para administrar una librería/papelería, incluyendo control de inventario, ventas, compras, proveedores y reportes.

## 🎯 Descripción del Proyecto

**Librería** El sistema fue desarrollado para satisfacer las necesidades de comercios dedicados a la venta de artículos de librería y papelería, permitiendo digitalizar operaciones y mejorar la eficiencia en la gestión diaria.

- Control de inventario y stock
- Gestión de ventas y facturación
- Administración de compras a proveedores
- Registro de clientes
- Generación de reportes y estadísticas
- Control de usuarios y permisos

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Java 17
- **Framework UI**: JavaFX 17
- **Base de Datos**: MySQL 8.0
- **IDE**: IntelliJ IDEA
- **Arquitectura**: MVC + DAO Pattern
- **Gestor de Dependencias**: Maven

## 📋 Requisitos Previos

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- **Java JDK 17** o superior
- **MySQL Server 8.0** o superior
- **Maven** (generalmente incluido en IntelliJ IDEA)
- **IntelliJ IDEA** (recomendado) o cualquier IDE compatible con JavaFX

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio
```bash
git clone https://github.com/TuUsuario/libreria-papelitos.git
cd libreria-papelitos
```

### 2. Configurar la Base de Datos

#### 2.1 Crear la base de datos en MySQL:

Abre MySQL Workbench o la terminal de MySQL y ejecuta:
```sql
CREATE DATABASE libreria_papelitos;
```

#### 2.2 Importar el script SQL con datos de prueba:

El proyecto incluye un dump completo con la estructura de tablas y datos de ejemplo.

**Opción A: Desde la terminal (CMD/PowerShell/Bash)**
```bash
# Navega a la carpeta del proyecto
cd C:\Users\TuUsuario\IdeaProjects\Libreria

# Importa el dump (te pedirá la contraseña de MySQL)
mysql -u root -p libreria_papelitos < database/DumpLibreriaPapelitos.sql
```

**Opción B: Desde MySQL Workbench**

1. Abre MySQL Workbench
2. Conecta a tu servidor local
3. Ve a: **Server → Data Import**
4. Selecciona: **"Import from Self-Contained File"**
5. Busca el archivo: `database/DumpLibreriaPapelitos.sql`
6. En **"Default Target Schema"** selecciona: `libreria_papelitos`
7. Clic en **"Start Import"**

#### 2.3 Verificar que se importó correctamente:
```sql
USE libreria_papelitos;
SHOW TABLES;
```

Deberías ver todas las tablas del sistema: `usuarios`, `productos`, `ventas`, `clientes`, `proveedores`, etc.

**Datos de prueba incluidos:**
- ✅ Usuario administrador y empleados de ejemplo
- ✅ Productos de librería/papelería
- ✅ Proveedores
- ✅ Categorías de productos
- ✅ Métodos de pago
- ✅ Clientes de ejemplo

### 3. Configurar la Conexión a la Base de Datos

#### 3.1 Copiar el archivo de configuración de ejemplo:

**En Windows:**
```cmd
copy database.properties.example database.properties
```

**En Linux/Mac:**
```bash
cp database.properties.example database.properties
```

#### 3.2 Editar `database.properties` con tus credenciales:

Abre el archivo `database.properties` (que acabas de crear) y edita con tus datos de MySQL:
```properties
db.url=jdbc:mysql://localhost:3306/libreria_papelitos
db.user=root
db.password=TU_CONTRASEÑA_MYSQL
db.driver=com.mysql.cj.jdbc.Driver
```

⚠️ **IMPORTANTE**: 
- Reemplaza `TU_CONTRASEÑA_MYSQL` con tu contraseña real de MySQL
- El archivo `database.properties` está en `.gitignore` y NO se subirá a GitHub (por seguridad)
- NUNCA compartas este archivo con contraseñas reales

### 4. Abrir el Proyecto en IntelliJ IDEA

1. Abre **IntelliJ IDEA**
2. **File → Open**
3. Selecciona la carpeta del proyecto: `Libreria`
4. Espera a que Maven descargue las dependencias automáticamente
5. Si aparece un mensaje sobre SDK, selecciona **Java 17**

### 5. Ejecutar la Aplicación

1. Navega en el explorador de proyectos hasta la clase principal: `LoginApp.java`
2. Haz clic derecho sobre `LoginApp.java`
3. Selecciona **Run 'LoginApp.main()'**

O bien, usa el atajo: **Shift + F10**

**Credenciales de acceso iniciales:**
- **Usuario:** `admin`
- **Contraseña:** `admin123`

⚠️ **IMPORTANTE**: Se recomienda cambiar estas credenciales después del primer login por razones de seguridad.

## 📁 Estructura del Proyecto
```
Libreria/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/libreria/
│   │   │       ├── controllers/      # Controladores JavaFX
│   │   │       │   ├── LoginController.java
│   │   │       │   ├── VentaController.java
│   │   │       │   ├── StockController.java
│   │   │       │   ├── CompraController.java
│   │   │       │   ├── ProveedorController.java
│   │   │       │   ├── ServiciosController.java
│   │   │       │   └── UsuariosController.java
│   │   │       │
│   │   │       ├── dao/              # Data Access Objects
│   │   │       │   ├── ProductoDAO.java
│   │   │       │   ├── VentaDAO.java
│   │   │       │   ├── CompraDAO.java
│   │   │       │   ├── ProveedorDAO.java
│   │   │       │   ├── UsuarioDAO.java
│   │   │       │   └── ...
│   │   │       │
│   │   │       ├── models/           # Clases de modelo
│   │   │       │   ├── Producto.java
│   │   │       │   ├── Venta.java
│   │   │       │   ├── Compra.java
│   │   │       │   ├── Proveedor.java
│   │   │       │   ├── Usuario.java
│   │   │       │   └── ...
│   │   │       │
│   │   │       ├── utils/            # Utilidades y helpers
│   │   │       │   ├── Database.java
│   │   │       │   ├── PasswordUtil.java
│   │   │       │   ├── SessionManager.java
│   │   │       │   └── FileManager.java
│   │   │       │
│   │   │       └── MainApp.java      # Clase principal
│   │   │
│   │   └── resources/
│   │       ├── views/                # Archivos FXML
│   │       │   ├── login-view.fxml
│   │       │   ├── carrito-view.fxml
│   │       │   ├── stock-view.fxml
│   │       │   └── ...
│   │       │
│   │       └── css/                  # Estilos CSS
│   │           └── styles.css
│   │
├── database/
│   └── DumpLibreria          # Script de base de datos
│
├── docs/
│   ├── Propuesta_Tecnica.docx        # Documentación del proyecto
│   └── Informe_de_Relevamiento.docx  # Análisis de requerimientos
│
├── database.properties.example       # ✅ Plantilla de configuración
├── .gitignore                        # Archivos ignorados por Git
├── pom.xml                           # Configuración Maven
└── README.md                         # Este archivo
```

## 🎨 Funcionalidades Principales

### 👤 Sistema de Usuarios
- Login con autenticación segura (bcrypt)
- Dos roles: **Administrador** y **Empleado**
- Gestión de permisos basada en roles
- Creación, modificación y eliminación de usuarios
- Cierre de sesión

### 📦 Gestión de Inventario (Stock)
- Alta, baja y modificación de productos
- Control de stock en tiempo real
- Alertas de stock bajo/crítico
- Categorización de productos
- Búsqueda y filtros dinámicos
- Paginación para grandes volúmenes de datos

### 🛒 Módulo de Ventas
- Carrito de compras interactivo
- Búsqueda de productos con autocompletado
- Aplicación de descuentos
- Múltiples métodos de pago
- Generación de tickets/comprobantes
- Registro de cliente (opcional o anónimo)
- Actualización automática de stock

### 📥 Módulo de Compras
- Gestión de órdenes de compra a proveedores
- Selección de productos por proveedor
- Registro de costos y cantidades
- Actualización automática de stock
- Historial de compras

### 🏢 Gestión de Proveedores
- Registro completo de proveedores
- Asociación de productos con proveedores
- Datos de contacto y CUIT
- Historial de compras por proveedor
- Gestión de archivos adjuntos

### 👥 Gestión de Clientes
- Registro de clientes con validación de CUIT
- Formato automático de CUIT (XX-XXXXXXXX-X)
- Datos de contacto completos
- Historial de compras por cliente

### 🛠️ Módulo de Servicios
- Gestión de servicios no inventariables
- Precios de servicios
- Registro de servicios prestados

### 📊 Reportes y Estadísticas
- **Ventas por período**: Análisis temporal de ventas
- **Productos más vendidos**: Ranking de productos
- **Stock crítico**: Alertas de reposición
- **Reportes de clientes**: Análisis de comportamiento
- **Exportación a CSV**: Todos los reportes exportables
- **Gráficos estadísticos**: Visualización con charts

## 🔐 Seguridad

El sistema implementa múltiples capas de seguridad:

- **Contraseñas encriptadas**: Uso de bcrypt para hash de contraseñas
- **Validación de permisos**: Control de acceso basado en roles
- **Prepared Statements**: Prevención de SQL Injection
- **Sanitización de inputs**: Validación de datos del usuario
- **Eliminación lógica**: Los registros no se borran físicamente (soft delete)
- **Sesión de usuario**: Control de usuario activo con SessionManager
- **Validación de formularios**: Validación en tiempo real de campos

## 🗄️ Base de Datos


### Principales Tablas

- `usuarios`: Gestión de usuarios del sistema
- `tipo_usuario`: Roles (Admin, Empleado)
- `productos`: Catálogo de productos
- `categorias`: Clasificación de productos
- `stock`: Control de inventario
- `ventas`: Registro de ventas
- `detalle_venta`: Ítems de cada venta
- `compras`: Órdenes de compra
- `detalle_compra`: Ítems de cada compra
- `proveedores`: Datos de proveedores
- `clientes`: Registro de clientes
- `metodos_pago`: Formas de pago disponibles
- `servicios`: Servicios no inventariables

## 📖 Documentación Adicional

La documentación completa del proyecto incluye:

- **Diagrama Entidad-Relación (E-R)**: Modelo de base de datos
- **Diagramas de Clases UML**: Arquitectura del sistema
- **Diagramas de Secuencia**: Flujos de procesos principales
- **Diagramas de Flujo de Datos (DFD)**: Movimiento de información
- **Propuesta Técnica**: Especificaciones del proyecto
- **Informe de Relevamiento**: Análisis de requerimientos

Estos documentos se encuentran en la carpeta `/docs/`.

## 🐛 Solución de Problemas Comunes

### Error: "No se puede conectar a la base de datos"

1. Verifica que MySQL esté ejecutándose
2. Confirma las credenciales en `database.properties`
3. Asegúrate de que la base de datos `libreria_papelitos` exista
4. Verifica que el puerto 3306 esté disponible

### Error: "Class not found: com.mysql.cj.jdbc.Driver"

1. Verifica que Maven haya descargado las dependencias
2. En IntelliJ: **Maven → Reload Project**
3. Si persiste: **File → Invalidate Caches → Invalidate and Restart**

### Error: "JavaFX runtime components are missing"

1. Verifica que estés usando **Java 17** (no Java 18+)
2. Confirma que las dependencias de JavaFX estén en `pom.xml`
3. En IntelliJ: **File → Project Structure → SDKs** → Verifica Java 17

### La interfaz no se muestra correctamente

1. Verifica que los archivos `.fxml` estén en `src/main/resources/views/`
2. Confirma las rutas en los controladores
3. Revisa la consola para errores de carga de FXML



## 👨‍💻 Autor

Juan Fruhwirth


## 📌 Notas Importantes

### Para Desarrollo
- El sistema usa **eliminación lógica** (soft delete) en la mayoría de las tablas
- Los filtros y búsquedas son **persistentes** entre pestañas
- La sesión del usuario se mantiene durante toda la ejecución



⭐ **Si este proyecto te fue útil, no olvides darle una estrella en GitHub!**

---
