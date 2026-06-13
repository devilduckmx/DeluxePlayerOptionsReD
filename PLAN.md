# PLAN DE REFACTORIZACIÓN - DeluxePlayerOptionsReD

## Resumen Ejecutivo
Unificar el plugin de Minecraft DeluxePlayerOptions en un único JAR, eliminando la arquitectura "jar-in-jar" obsoleta y actualizando para compatibilidad con Minecraft 1.16 - 1.21.11.

---

## 1. Estructura del Nuevo Proyecto

### Estructura de Paquetes
```
net.redm1ne.deluxeplayeroptionsred/
├── DeluxePlayerOptions.java          # Clase principal (Main)
├── api/
│   └── PlaceholderExpansion.java    # Integración PlaceholderAPI
├── command/
│   ├── CommandManager.java
│   └── commands/
│       ├── CommandSpeed.java
│       ├── CommandFly.java
│       ├── CommandJump.java
│       ├── CommandDoubleJump.java
│       ├── CommandStacker.java
│       ├── CommandVisibility.java
│       ├── CommandChat.java
│       ├── CommandRadio.java
│       ├── CommandPvP.java
│       └── CommandOptions.java
├── config/
│   ├── ConfigManager.java
│   └── MessageManager.java
├── data/
│   ├── DatabaseManager.java
│   └── SQLiteConnection.java
├── listener/
│   └── PlayerListener.java
├── manager/
│   ├── MenuManager.java
│   ├── ToggleItemManager.java
│   └── OptionManager.java
├── option/
│   ├── Option.java (abstract)
│   ├── OptionSpeed.java
│   ├── OptionFly.java
│   ├── OptionJump.java
│   ├── OptionDoubleJump.java
│   ├── OptionStacker.java
│   ├── OptionVisibility.java
│   ├── OptionChat.java
│   ├── OptionRadio.java
│   └── OptionPvP.java
├── util/
│   ├── ColorUtils.java
│   ├── MaterialCompat.java          # Abstracción de materiales
│   ├── SoundCompat.java              # Abstracción de sonidos
│   ├── VersionUtils.java            # Detección de versión
│   └── TextUtils.java
└── integration/
    ├── JukeBoxIntegration.java
    └── RadioIntegration.java
```

---

## 2. Dependencias Clave (del pom.xml Original)

### Dependencias a Migrar
| Dependencia Original | Versión Original | Nueva Versión | Propósito |
|---------------------|------------------|---------------|-----------|
| `org.spigotmc:spigot-api` | 1.18.1 | 1.21-R0.1-SNAPSHOT | API principal |
| `com.google.code.gson:gson` | (implícita) | 2.10.1 | JSON |
| `org.json.simple` | (embebida) | Eliminar - usar Gson | JSON legacy |
| `com.zaxxer:HikariCP` | 4.0.3 | 5.1.0 | Pool de conexiones |
| `org.slf4j:slf4j-api` | 1.7.25 | 2.0.12 | Logging |
| `org.bstats:bstats-bukkit` | (embebido) | 3.0.2 | Métricas |

### Dependencias a Eliminar
- `CoreLibBukkit` (era un shaded dependency del loader)
- `commons-lang:commons-lang` (usar métodos nativos de Java)
- `commons-codec:commons-codec` (no necesario)

### Dependencias Opcionales (soft-depend)
- PlaceholderAPI
- icJukeBox / JukeBox

---

## 3. Estrategia de Compatibilidad Multi-versión (1.16 - 1.21.11)

### 3.1 Detección de Versión
```java
public class VersionUtils {
    private static final int MAJOR_VERSION;
    
    static {
        String bukkitVersion = Bukkit.getBukkitVersion().split("-")[0];
        String[] parts = bukkitVersion.split("\\.");
        MAJOR_VERSION = Integer.parseInt(parts[1]); // 16, 17, 18... 21
    }
    
    public static boolean isAtLeast(int minorVersion) {
        return MAJOR_VERSION >= minorVersion;
    }
}
```

### 3.2 Materiales (IDs → Namespaced Keys)

| Material | 1.16 | 1.21 | Solución |
|----------|------|------|----------|
| INK_SACK | ✓ | ✗ | Usar `INK_SAC` para >=1.17 |
| STAINED_GLASS | ✓ | ✗ | Usar colores específicos |
| WOOL | numérico | ✗ | Usar colores específicos |

**Abstracción MaterialCompat:**
```java
public class MaterialCompat {
    public static Material getOnOffItem(boolean on) {
        if (VersionUtils.isAtLeast(21)) {
            return on ? Material.LIME_DYE : Material.GRAY_DYE;
        }
        // 1.16-1.20
        return on ? Material.valueOf("LIME_DYE") : Material.valueOf("INK_SAC");
    }
}
```

### 3.3 Sonidos (Nombres Cambiantes)

| Sonido 1.16 | Sonido 1.21+ | Solución |
|-------------|--------------|----------|
| EXPLODE | ENTITY_GENERIC_EXPLODE | Try-catch con fallback |
| EYE_OF_ENDER | ENDER_EYE | Verificación de existencia |

### 3.4 Colores HEX

- 1.16+: Soporte nativo para colores HEX (`#RRGGBB`)
- Anterior a 1.16: Solo colores legacy

### 3.5 NMS - ELIMINAR COMPLETAMENTE

Las siguientes clases usaban NMS y deben refactorizarse:
- `CoreReflection.java` → Usar métodos de la API de Bukkit
- `CoreUtils.getPlayerTextureLocal()` → Usar Player.getUniqueId() + API de Mojang

---

## 4. Configuración de Compilación Maven

### Compilador
- **Target**: Java 21
- **Source**: Java 21
- **Release**: 21

### Plugins Críticos
1. `maven-compiler-plugin` - Configurar Java 21
2. `maven-shade-plugin` - Empaquetar dependencias en JAR único
3. `maven-resources-plugin` - Copiar resources

---

## 5. Clases que Requieren Refactorización Mayor

### 5.1 PlayerOptionsLoader (ELIMINAR)
Era el loader del jar-in-jar. Su funcionalidad se integra en la nueva clase Main.

### 5.2 PlayerOptions → DeluxePlayerOptions (RENOMBRAR + REFACTORIZAR)
- Eliminar referencia a `CorePlugin` interface
- Eliminar `CoreLoader` interface
- Eliminar `CoreURLClassLoader` y `LibraryLoader`

### 5.3 CoreMaterial.java → MaterialCompat.java
- Eliminar mapa estático de IDs numéricos
- Usar `Material.matchMaterial()` con namespace

### 5.4 CoreReflection.java (ELIMINAR)
Eliminar completamente. Usar:
- `Player.sendTitle()` en lugar de packets NMS
- `Player.playSound()` en lugar de reflection

### 5.5 OptionJump.java
Eliminar reflection para PotionEffectType:
```java
// Antes (NMS):
CoreReflection.getPotionEffect("JUMP")

// Después (API Bukkit):
PotionEffectType.JUMP_BOOST // 1.21+
PotionEffectType.getByName("JUMP") // 1.16 fallback
```

---

## 6. Orden de Implementación

### Fase 1: Infraestructura
1. Crear estructura de paquetes
2. Generar pom.xml optimizado
3. Crear clase Main

### Fase 2: Utilidades Base
1. VersionUtils.java
2. MaterialCompat.java
3. ColorUtils.java
4. TextUtils.java

### Fase 3: Core del Plugin
1. ConfigManager.java
2. DatabaseManager.java
3. Option.java (abstracta)
4. OptionManager.java

### Fase 4: Comandos y Listeners
1. Todos los comandos
2. PlayerListener.java
3. Integraciones (PlaceholderAPI, JukeBox)

### Fase 5: UI
1. MenuManager.java
2. ToggleItemManager.java

---

## 7. Archivos a Eliminar del Código Decompilado

- `com/pedrojm96/playeroptions/*` (todo el paquete - se migra)
- `org/json/simple/*` (se reemplaza con Gson)
- `playeroptions-plugin.jar/*` (todo el contenido interno)
- `playeroptions-plugin.jarinjar` (archivo de recursos)
- Todas las clases `Core*` del loader antiguo
- `META-INF/maven/com.pedrojm96/*` (metadata antigua)

---

## 8. Lista de Verificación Final

- [ ] Compilación exitosa con Java 21
- [ ] Sin errores de runtime en 1.21.11
- [ ] Sin errores de runtime en 1.16.5
- [ ] Comandos funcionando
- [ ] Menú GUI funcionando
- [ ] Persistencia de datos (SQLite/MySQL)
- [ ] Integración PlaceholderAPI
- [ ] Métricas bStats funcionando
- [ ] Sin imports de NMS
- [ ] Tamaño de JAR optimizado (< 2MB)
