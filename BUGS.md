# Bugs encontrados en Oktodo

## 🔴 HIGH

### H1 — Race condition en eventCount
- **Archivo:** `ui/viewmodel/FriendsViewModel.kt:381,395`
- **Problema:** `eventCount` se lee de `groups.value` y se incrementa en memoria, luego se escribe. Si dos corrutinas ejecutan `createSharedEvent` concurrentemente, ambos leen `N` y escriben `N+1`, perdiendo un incremento.
- **Fix:** Usar `UPDATE groups SET eventCount = eventCount + 1 WHERE id = :groupId` atómico.

### H2 — Membresía de grupos por displayName (se rompe al renombrar)
- **Archivos:** `ui/viewmodel/CalendarViewModel.kt:36`, `ui/viewmodel/FriendsViewModel.kt:56`
- **Problema:** Los miembros de grupos se almacenan como displayNames (mutables). Cuando el usuario cambia su nombre en el perfil, `group.members.contains(p.displayName)` falla, y el usuario desaparece de sus propios grupos. Los eventos compartidos dejan de verse.
- **Fix:** Almacenar miembros por userId (inmutable) en vez de displayName.

### H3 — Contadores de puntos/tareas se desvían bajo concurrencia
- **Archivo:** `ui/viewmodel/TasksViewModel.kt:78-83`
- **Problema:** Si `toggleTaskCompletion` y `deleteTask` se ejecutan concurrentemente, los contadores de puntos, completedTasks y weeklyCompleted pueden desviarse.
- **Fix:** Serializar operaciones con Mutex o consultar estado canónico de DB antes de decidir.

### H4 — Missing key en LazyColumn/LazyVerticalGrid items()
- **Archivos:** DashboardScreen.kt:273,302 | FriendsScreen.kt:140,159,164,197,206,245 | GroupDetailScreen.kt:261 | SearchFriendBottomSheet.kt:102 | CreateGroupBottomSheet.kt:92 | ColorPicker.kt:53
- **Problema:** `items()` sin `key` usa índices, causando recomposición innecesaria y pérdida de estado en scroll/animaciones.
- **Fix:** Agregar `key = { it.id }` (o similar) en cada llamada.

### H5 — ShopItem.toEntity() pierde isPurchased/isEquipped
- **Archivo:** `data/local/mapper/Mappers.kt:214-221`
- **Problema:** `ShopItem.toEntity()` tiene parámetros por defecto `false` que IGNORAN `this.isPurchased`/`this.isEquipped`. Comprar o equipar un item NO se persiste.
- **Fix:** Usar `this.isPurchased` y `this.isEquipped` directamente.

### H6 — LocalDate.parse/LocalTime.parse crashean sin try-catch
- **Archivo:** `data/local/mapper/Mappers.kt:34-35,93`
- **Problema:** `LocalDate.parse(dateString)` y `LocalTime.parse(timeString)` lanzan `DateTimeParseException` si el string almacenado no es ISO. Esto crashea la app dentro de un Flow reactivo.
- **Fix:** Agregar try-catch con fallback como ya se hace en line 94 para time.

### H7 — Timer coroutine leak en FocusScreen
- **Archivo:** `ui/screens/FocusScreen.kt:171-187`
- **Problema:** Cada vez que se presiona play, se lanza una nueva corrutina sin cancelar la anterior. Si el usuario toca rápido, múltiples corrutinas descuentan `timeLeft` simultáneamente.
- **Fix:** Usar una variable `Job` para cancelar la corrutina anterior antes de lanzar una nueva.

### H8 — Stale event data en GroupDetailScreen
- **Archivo:** `ui/screens/GroupDetailScreen.kt:65-67`
- **Problema:** `remember(selectedDate, viewModel.sharedEvents)` usa la referencia del StateFlow (nunca cambia), no el valor. Los eventos nunca se actualizan.
- **Fix:** Colectar `sharedEvents` con `collectAsState()` y usarlo como dependencia.

### H9 — Early return desde Composable en GroupDetailScreen
- **Archivo:** `ui/screens/GroupDetailScreen.kt:41`
- **Problema:** `val group = groups.find { it.id == groupId } ?: return` — un early return en un Composable deja la composición inconsistente.
- **Fix:** Usar if/else con fallback visual.

### H10 — Nulls sorteados primero en DayCalendar
- **Archivo:** `ui/components/DayCalendar.kt:161`
- **Problema:** `events.sortedBy { it.time }` con `time: LocalTime?` pone los nulls primero.
- **Fix:** Usar `it.time ?: LocalTime.MAX`.

### H11 — Botón de "Crear evento" sin enabled, falla silenciosamente
- **Archivo:** `ui/components/CreateSharedEventBottomSheet.kt:196-216`
- **Problema:** El botón no tiene `enabled`; el onClick envuelve la acción en un `if` que no da retroalimentación al usuario.
- **Fix:** Agregar `enabled` condicional.

### H12 — SharedEventCard muestra fila de ubicación vacía
- **Archivo:** `ui/components/SharedEventCard.kt:80-92`
- **Problema:** La fila de ubicación siempre se renderiza incluso si `event.location` está vacío.
- **Fix:** Envolver en `if (event.location.isNotBlank())`.

### H13 — Puntos hardcodeados como "0" en FriendsTopBar
- **Archivo:** `ui/components/FriendsTopBar.kt:66`
- **Problema:** Los puntos se muestran como "0" fijo en vez de venir del ViewModel.
- **Fix:** Agregar parámetro `points: Int`.

### H14 — acceptGroupInvitation agrega miembros con coma líder y sin dedup
- **Archivo:** `data/repository/FriendsRepository.kt:176-179`
- **Problema:** Si `membersJoined` está vacío, se agrega ",Nombre" (coma líder). No hay deduplicación.
- **Fix:** Parsear, verificar duplicados, unir.

---

## 🟡 MEDIUM

### M1 — ShopItemDao.unequipAll() + equip() no atómicos
- **Archivo:** `data/local/dao/ShopItemDao.kt:21-25`
- **Problema:** Si la app crashea entre `unequipAll()` y `equip(id)`, ningún item queda equipado.

### M2 — CalendarEventDao sin @Update
- **Archivo:** `data/local/dao/CalendarEventDao.kt`
- **Problema:** Editar un evento requiere delete+reinsert (no atómico).

### M3 — TypeConverters es código muerto
- **Archivo:** `data/local/OktodoDatabase.kt:25`
- **Problema:** `@TypeConverters(Converters::class)` registra conversores para `List<String>` pero ninguna entidad los usa.

### M4 — userId: Flow<> vs getUserId() inconsistentes
- **Archivo:** `data/local/UserPreferencesDataStore.kt:141-145`
- **Problema:** `userId: Flow<String>` retorna `""` mientras `getUserId(): String?` retorna `null`.

### M5 — addPoints() mezcla puntos con conteo de tareas
- **Archivo:** `data/local/UserPreferencesDataStore.kt:102-113`
- **Problema:** `addPoints(-10)` también decrementa `completedTasks` y `weeklyCompleted`, nombre engañoso.

### M6 — IDs con System.currentTimeMillis() en lugar de UUID
- **Archivo:** `ui/viewmodel/FriendsViewModel.kt:347,383`
- **Problema:** Colisiones de ID en el mismo milisegundo.

### M7 — Salt hardcodeado y SHA-256 para passwords
- **Archivo:** `data/repository/FriendsRepository.kt:40,61-64`
- **Problema:** Sin key stretching, salt visible en código fuente.

### M8 — catch(Exception) traga errores silenciosamente
- **Archivo:** `ui/viewmodel/FriendsViewModel.kt:150,197,373`
- **Problema:** Todos los errores se silencian, el usuario no recibe feedback.

### M9 — equip/unequip no transaccional en ShopItemRepository
- **Archivo:** `data/repository/ShopItemRepository.kt:30-33`
- **Problema:** Dos statements SQL sin transacción (torn write).

### M10 — popUpTo(0) sin back stack
- **Archivo:** `ui/screens/SettingsScreen.kt:316-319`
- **Problema:** `popUpTo(0) { inclusive = true }` deja el back stack vacío.

### M11 — NotificationsScreen crea su propio ViewModel duplicado
- **Archivo:** `ui/screens/NotificationsScreen.kt:34`
- **Problema:** El dashboard usa `notificationViewModel` de AppNavigation, pero NotificationsScreen crea otro.

### M12 — Bottom nav no resalta shared_event_detail
- **Archivo:** `navigation/AppNavigation.kt:145-151`

### M13 — DaysOfWeekTitle duplicado con locale inconsistente
- **Archivos:** `MonthlyCalendar.kt:181-196`, `GroupDetailScreen.kt:375-390`
- **Problema:** MonthlyCalendar usa inglés, GroupDetailScreen usa español.

### M14 — Redundant filters en MonthlyCalendar
- **Archivo:** `ui/components/MonthlyCalendar.kt:133`
- **Problema:** `filter` se llama por cada celda del calendario (O(events * days)).

### M15 — Sin cascade delete de shared_events al borrar grupo
- **Archivo:** `data/repository/FriendsRepository.kt:191-193`

### M16 — Sin @Transaction en acceptFriendRequest
- **Archivo:** `data/repository/FriendsRepository.kt:104-116`

### M17 — state.value divorciado del combine en ProfileViewModel
- **Archivo:** `ui/viewmodel/ProfileViewModel.kt:85`
- **Problema:** Se re-lectura de `uiState.value` fuera del combine.

### M18 — Sin mapper Notification.toEntity()
- **Archivo:** `data/repository/NotificationRepository.kt:21-33,39-51`
- **Problema:** Construcción manual del entity.

### M19 — AddEventDialog es código muerto
- **Archivo:** `ui/components/AddEventDialog.kt`
- **Problema:** Definido pero nunca usado.

### M20 — Free-text date en AddEventBottomSheet
- **Archivo:** `ui/components/AddEventBottomSheet.kt:129-140`
- **Problema:** Viola la convención del proyecto (debería ser DatePickerDialog).
