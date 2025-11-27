# Proyecto

## Descripción
Este repositorio sigue una arquitectura **monorepo**, donde backend, frontend y herramientas DevOps conviven bajo un único control de versiones, facilitando la integración, el despliegue continuo y la trazabilidad del proyecto.

---

## 📁 Estructura Principal del Proyecto

### 1. backend/
Contiene todo el código relacionado con la API y la lógica de negocio.

Incluye:
- Controladores REST
- Servicios y reglas de negocio
- Repositorios y acceso a datos
- Pruebas unitarias
- Configuración de dependencias

Responsable principal: **Juan**

---

### 2. frontend/
Contiene la aplicación web que interactúa con el backend.

Incluye:
- Componentes visuales
- Servicios HTTP
- Rutas y vistas
- Estilos y recursos estáticos

Responsable principal: **Milena**

---

### 3. docker/
Almacena toda la configuración relacionada con contenedores.

Incluye:
- docker-compose.yml
- Configuración de redes
- Volúmenes y servicios (backend, frontend, base de datos si aplica)

Responsable: **Edson**

---

### 4. jenkins/
Contiene la configuración del pipeline de integración continua.

Incluye:
- Jenkinsfile
- Scripts de automatización
- Etapas de build, test y despliegue

Responsable principal: **Cesar**

---

### 5. sonarqube/
Configuraciones para análisis estático de código y calidad.

Incluye:
- sonar-project.properties
- Reglas de calidad
- Integración con Jenkins

Responsable principal: **Joaquin**

---

## 🔀 Estrategia GitFlow

El proyecto utiliza un GitFlow simplificado con las siguientes ramas:

- main  → Rama estable para producción
- develop → Rama de integración
- feature/* → Nuevas funcionalidades
- release/* → Preparación de versiones
- hotfix/* → Correcciones urgentes en producción

### Flujo de trabajo:

1. Se crea una rama feature desde develop
2. Se desarrolla la funcionalidad
3. Se genera Pull Request hacia develop
4. Se revisa y aprueba
5. Se fusiona en develop
6. Al finalizar una versión se crea release/*
7. Release se fusiona en main

Ejemplo:
```

feature/backend-auth
feature/frontend-login
release/v1.0.0
hotfix/error-produccion

```

---

## 🧾 Convención de Commits

Se sigue una convención basada en Conventional Commits:

### Tipos permitidos

- feat: nueva funcionalidad
- fix: corrección de error
- docs: cambios en documentación
- style: formato, espacios, puntos y coma, sin cambiar lógica
- refactor: refactorización de código
- test: pruebas unitarias
- chore: tareas generales
- ci: cambios en CI/CD

### Formato
```

<tipo>: descripción breve en presente

```

### Ejemplos
```

feat: agregar endpoint de autenticación
fix: corregir validación de formulario
ci: configurar pipeline Jenkins
chore: actualizar dependencias

````