# Guía de Contribución

¡Gracias por tu interés en contribuir al Sistema de Gamificación Deportiva! Esta guía te ayudará a comenzar.

## Tabla de Contenidos

- [Código de Conducta](#código-de-conducta)
- [¿Cómo Puedo Contribuir?](#cómo-puedo-contribuir)
- [Proceso de Desarrollo](#proceso-de-desarrollo)
- [Guías de Estilo](#guías-de-estilo)
- [Reporte de Bugs](#reporte-de-bugs)
- [Solicitud de Funcionalidades](#solicitud-de-funcionalidades)

## Código de Conducta

Este proyecto se adhiere a un código de conducta. Al participar, se espera que mantengas este código. Por favor, reporta comportamientos inaceptables.

## ¿Cómo Puedo Contribuir?

### Reportar Bugs

Los bugs se rastrean como [GitHub Issues](https://github.com/Keneth-Huerta/GamificacionDeportiva/issues). Crea un issue y proporciona la siguiente información:

- **Título descriptivo** para el problema
- **Pasos para reproducir** el comportamiento
- **Comportamiento esperado** vs **comportamiento actual**
- **Capturas de pantalla** si es posible
- **Información del entorno**:
  - Versión de Java
  - Sistema operativo
  - Versión de MySQL

### Sugerir Mejoras

Las mejoras también se rastrean como GitHub Issues. Al crear un issue de mejora:

- **Usa un título claro y descriptivo**
- **Proporciona una descripción detallada** de la mejora sugerida
- **Explica por qué esta mejora sería útil** para la mayoría de los usuarios
- **Incluye ejemplos** si es posible

### Pull Requests

1. Fork el repositorio
2. Crea tu rama de características (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Proceso de Desarrollo

### Configuración del Entorno

1. **Instala las dependencias**:
   - Java JDK 17+
   - MySQL 8.0+
   - Un IDE (IntelliJ IDEA recomendado)

2. **Clona el repositorio**:
   ```bash
   git clone https://github.com/Keneth-Huerta/GamificacionDeportiva.git
   cd GamificacionDeportiva
   ```

3. **Configura la base de datos**:
   - Crea una base de datos local
   - Actualiza `Configuracion.java` con tus credenciales

4. **Compila y ejecuta**:
   ```bash
   javac -d out src/gamificaciondeportiva/*.java
   java -cp out gamificaciondeportiva.MainGamificacionDeportiva
   ```

### Workflow de Git

- Usa ramas descriptivas: `feature/`, `bugfix/`, `hotfix/`, `docs/`
- Mantén commits atómicos y con mensajes descriptivos
- Sincroniza frecuentemente con la rama principal
- Resuelve conflictos antes de hacer PR

### Pruebas

Antes de enviar un PR:

- [ ] El código compila sin errores
- [ ] La aplicación inicia correctamente
- [ ] Las funcionalidades nuevas/modificadas funcionan como se espera
- [ ] No se introdujeron regresiones
- [ ] El código está documentado (JavaDoc)

## Guías de Estilo

### Código Java

1. **Nomenclatura**:
   - Clases: `PascalCase`
   - Métodos y variables: `camelCase`
   - Constantes: `UPPER_SNAKE_CASE`
   - Packages: `lowercase`

2. **Formato**:
   - Indentación: 4 espacios
   - Llaves en la misma línea (estilo K&R)
   - Longitud máxima de línea: 120 caracteres

3. **Documentación**:
   - Todas las clases públicas deben tener JavaDoc
   - Métodos públicos deben estar documentados
   - Incluye ejemplos de uso cuando sea apropiado

   ```java
   /**
    * Descripción breve del método.
    * <p>
    * Descripción detallada si es necesaria.
    *
    * @param parametro Descripción del parámetro
    * @return Descripción del valor de retorno
    * @throws Exception Descripción de la excepción
    */
   public ReturnType metodo(TipoParametro parametro) throws Exception {
       // implementación
   }
   ```

4. **Buenas Prácticas**:
   - Evita código duplicado
   - Mantén métodos cortos y con una sola responsabilidad
   - Usa nombres descriptivos para variables y métodos
   - Maneja excepciones apropiadamente
   - Cierra recursos (usa try-with-resources)

### SQL

1. **Nomenclatura**:
   - Tablas: `PascalCase`
   - Columnas: `camelCase`
   - Índices: `idx_tabla_columna`

2. **Formato**:
   - Palabras clave SQL en mayúsculas
   - Identifica claramente las relaciones de clave foránea

### Commits

Formato de mensajes de commit:

```
tipo: Descripción breve (máx 50 caracteres)

Descripción detallada si es necesaria (máx 72 caracteres por línea).

Fixes #123
```

Tipos de commit:
- `feat`: Nueva funcionalidad
- `fix`: Corrección de bug
- `docs`: Cambios en documentación
- `style`: Formato, punto y coma faltantes, etc.
- `refactor`: Refactorización de código
- `test`: Agregar tests
- `chore`: Mantenimiento

Ejemplos:
```
feat: Agregar sistema de notificaciones para logros

fix: Corregir cálculo de calorías en natación

docs: Actualizar README con instrucciones de instalación
```

## Reporte de Bugs

Al reportar un bug, incluye:

```markdown
## Descripción
Descripción clara del bug

## Pasos para Reproducir
1. Ve a '...'
2. Haz clic en '...'
3. Desplázate hasta '...'
4. Ver error

## Comportamiento Esperado
Lo que debería pasar

## Comportamiento Actual
Lo que realmente pasa

## Capturas de Pantalla
Si aplica

## Entorno
- OS: [ej. Windows 10]
- Java: [ej. 17.0.5]
- MySQL: [ej. 8.0.31]

## Contexto Adicional
Cualquier otra información relevante
```

## Solicitud de Funcionalidades

Al solicitar una funcionalidad:

```markdown
## Problema
Descripción del problema que resuelve esta funcionalidad

## Solución Propuesta
Descripción de cómo debería funcionar

## Alternativas Consideradas
Otras soluciones que consideraste

## Contexto Adicional
Mockups, ejemplos, etc.
```

## Preguntas?

Si tienes preguntas sobre cómo contribuir, no dudes en:

- Abrir un [Issue](https://github.com/Keneth-Huerta/GamificacionDeportiva/issues)
- Contactar al mantenedor

---

¡Gracias por contribuir! 🎉
