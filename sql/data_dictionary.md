# Diccionario de Datos — Oktodo (Room Local)

Base de datos local SQLite (`oktodo.db`, versión 14).  
Estrategia de migración: `fallbackToDestructiveMigration()` (sin migraciones).

---

## 1. tasks

Almacena las tareas del dashboard. Soporta recurrencia y categorización.

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID único generado desde la app |
| title | TEXT | NO | — | Título visible de la tarea |
| time | TEXT | NO | — | Hora en formato `HH:mm` |
| priority | TEXT | NO | — | `Alta`, `Media` o `Baja` |
| colorArgb | INTEGER | NO | — | Color de la tarjeta en formato ARGB |
| isCompleted | INTEGER | NO | 0 | 0 = pendiente, 1 = completada |
| pointsReward | INTEGER | NO | 10 | Puntos que otorga al marcarla como hecha |
| category | TEXT | NO | '' | Categoría opcional (ej. "Personal", "Trabajo", "📅 Evento" para eventos del calendario) |
| recurrenceType | TEXT | NO | 'none' | `none` / `daily` / `weekly` / `monthly` |
| recurrenceInterval | INTEGER | NO | 1 | Cada cuántas unidades se repite |
| dateString | TEXT | NO | — | Fecha en formato `yyyy-MM-dd` (default hoy) |

---

## 2. calendar_events

Eventos del calendario (pantalla Calendario).

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID único |
| title | TEXT | NO | — | Título del evento |
| dateString | TEXT | NO | — | Fecha en formato `yyyy-MM-dd` |
| timeString | TEXT | SÍ | — | Hora opcional en formato `HH:mm` |
| location | TEXT | SÍ | — | Ubicación opcional |
| colorArgb | INTEGER | NO | — | Color de la tarjeta en ARGB |
| description | TEXT | SÍ | — | Notas o descripción opcional |

---

## 3. friends

Amigos del usuario (lista local desnormalizada).  
En Supabase se almacena como tabla junction normalizada.

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID del usuario amigo |
| name | TEXT | NO | — | Nombre visible del amigo |
| points | INTEGER | NO | — | Puntos acumulados del amigo |
| events | INTEGER | NO | — | Cantidad de eventos compartidos |
| avatar | TEXT | NO | — | Emoji del avatar del amigo |
| avatarColorArgb | INTEGER | NO | — | Color de fondo del avatar en ARGB |

---

## 4. groups

Grupos para eventos compartidos.  
Los miembros se almacenan como strings delimitados por coma (desnormalizado).

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID único del grupo |
| name | TEXT | NO | — | Nombre del grupo |
| icon | TEXT | NO | — | Emoji del icono del grupo |
| membersJoined | TEXT | NO | — | Nombres de miembros separados por coma |
| memberIdsJoined | TEXT | NO | '' | IDs de miembros separados por coma |
| eventCount | INTEGER | NO | — | Cantidad de eventos compartidos en el grupo |
| creatorId | TEXT | NO | '' | UUID del usuario creador |

---

## 5. shared_events

Eventos compartidos dentro de un grupo.

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID único |
| title | TEXT | NO | — | Título del evento |
| creator | TEXT | NO | — | Nombre visible del creador |
| creatorId | TEXT | NO | '' | UUID del creador |
| groupId | TEXT | NO | — | UUID del grupo al que pertenece |
| date | TEXT | NO | — | Fecha en formato `yyyy-MM-dd` |
| time | TEXT | NO | — | Hora en formato `HH:mm` |
| location | TEXT | NO | — | Ubicación |
| participantsJoined | TEXT | NO | — | Nombres de participantes separados por coma |
| canEditJoined | TEXT | NO | '' | IDs de quienes pueden editar, separados por coma |

---

## 6. shop_items

Ítems de la tienda OctoShop. Unifica catálogo + ownership + equipamiento.  
En Supabase se divide en `shop_catalog` y `user_items`.

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | Identificador del ítem (ej. "s1", "s2") |
| userId | TEXT | NO | — | UUID del usuario propietario |
| title | TEXT | NO | — | Nombre visible del ítem |
| emoji | TEXT | NO | — | Emoji del ítem |
| price | INTEGER | NO | — | Costo en puntos |
| category | TEXT | NO | — | Categoría: `Accesorio`, `Ropa`, `Premium`, `Skin` |
| isPurchased | INTEGER | NO | — | 0 = no comprado, 1 = comprado |
| isEquipped | INTEGER | NO | 0 | 0 = no equipado, 1 = equipado actualmente |

**Primary Key compuesta:** `(id, userId)` — cada usuario tiene su propia copia del catálogo con su estado de compra/equipo.

---

## 7. notifications

Notificaciones sociales (solicitudes de amistad, invitaciones a grupos, etc.).

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID único |
| userId | TEXT | NO | — | UUID del usuario destino |
| title | TEXT | NO | — | Título corto de la notificación |
| message | TEXT | NO | — | Cuerpo del mensaje |
| icon | TEXT | NO | — | Emoji representativo |
| isRead | INTEGER | NO | — | 0 = no leída, 1 = leída |
| createdAt | INTEGER | NO | — | Timestamp en milisegundos (epoch) |

---

## 8. user_profiles

Perfiles de usuarios registrados localmente. Solo almacena datos de identidad y autenticación.  
Los datos de gamificación (`points`, `streakDays`, `weeklyGoal`, etc.) viven en DataStore.

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID único del perfil |
| displayName | TEXT | NO | — | Nombre visible |
| username | TEXT | NO | — | Identificador único para login/búsqueda |
| avatarEmoji | TEXT | NO | — | Emoji del avatar |
| passwordHash | TEXT | NO | — | Hash SHA-256 de la contraseña |
| createdAt | INTEGER | NO | `currentTimeMillis()` | Timestamp de creación en milisegundos |

**Índice único:**
- `idx_user_profiles_username` sobre `username`

---

## 9. friend_requests

Solicitudes de amistad pendientes (desnormalizado con datos del perfil).

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID único |
| fromUserId | TEXT | NO | — | UUID del remitente |
| toUserId | TEXT | NO | — | UUID del destinatario |
| fromDisplayName | TEXT | NO | — | Nombre visible del remitente |
| fromAvatarEmoji | TEXT | NO | — | Avatar del remitente |
| toDisplayName | TEXT | NO | — | Nombre visible del destinatario |
| toAvatarEmoji | TEXT | NO | — | Avatar del destinatario |
| status | TEXT | NO | — | `pending` / `accepted` / `declined` |
| createdAt | INTEGER | NO | `currentTimeMillis()` | Timestamp en milisegundos |

**Índice único:**
- `idx_friend_requests_from_to` sobre `(fromUserId, toUserId)`

---

## 10. group_invitations

Invitaciones a grupos (desnormalizado con datos del perfil y del grupo).

| Columna | Tipo | Nulo | Default | Descripción |
|---|---|---|---|---|
| id | TEXT | NO | — | UUID único |
| fromUserId | TEXT | NO | — | UUID del usuario que invita |
| fromDisplayName | TEXT | NO | — | Nombre visible de quien invita |
| toUserId | TEXT | NO | — | UUID del usuario invitado |
| toDisplayName | TEXT | NO | — | Nombre visible del invitado |
| groupId | TEXT | NO | — | UUID del grupo destino |
| groupName | TEXT | NO | — | Nombre del grupo |
| groupIcon | TEXT | NO | — | Emoji del grupo |
| status | TEXT | NO | — | `pending` / `accepted` / `declined` |
| createdAt | INTEGER | NO | `currentTimeMillis()` | Timestamp en milisegundos |

**Índice único:**
- `idx_group_invitations_to_group` sobre `(toUserId, groupId)`
