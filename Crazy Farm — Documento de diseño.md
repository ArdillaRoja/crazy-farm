# Crazy Farm — Documento de diseño

Oct 14, 2026 · @Ardilla Negra

## Resumen

Crazy Farm es un mod de agricultura para Minecraft 1.21.1 con cultivos 3D, un sistema de vida basado en frutas, un granjero comerciante con su propia moneda (VitCoin) y un mineral nuevo, el Vitilium. Está pensado como mod grande, ampliable con addons.

| Aspecto | Decisión |
| --- | --- |
| Nombre / ID | Crazy Farm / `crazyfarm` (el ID no se cambia una vez publicado) |
| Versión | Minecraft 1.21.1 primero; después un port a 1.20.1 en otra rama de Git |
| Loaders | Forge primero (MDK 52.1.0). NeoForge y Fabric quedan para más adelante |
| Estructura | Por ahora un solo proyecto Forge en `forge-1.21.1-52.1.0/`. La plantilla MultiLoader (módulo `common` + módulos por loader) se evalúa cuando se sumen más loaders |
| Librería opcional | GeckoLib para modelos y animaciones del granjero y los topos |
| Estilo visual | Plantas con hojas 2D y frutos/raíces en 3D (modelos JSON) |

**Herramientas:** JDK 21, IntelliJ IDEA + plugin Minecraft Development, Gradle, Blockbench (modelos), un editor de pixel art (LibreSprite, Pixelorama o Aseprite), Prism Launcher para probar y Git/GitHub.

**Reparto del trabajo:** Claude escribe el código, modelos JSON, datos y traducciones, y revisa errores y logs. Ardilla compila, prueba en el juego y hace el arte final. El entorno de Claude no puede compilar porque no tiene acceso a los repositorios de Minecraft.

## Sistema de vida

El jugador puede llegar a 25 corazones: 10 base, 10 permanentes (Fruta de la Vida) y 5 temporales. Hay dos frutas con roles distintos.

**Fruto del Rejuvenecimiento** (fruto natural): crece en las Lush Caves con poca luz, sobre musgo o colgando del techo. Su planta se ve con hojas 2D y el fruto en 3D. Los corazones temporales que da se pierden al morir.

**Brote del Rejuvenecimiento:** ítem que se planta bajo un techo para criar la planta. Solo existe en creativo o por comandos y nunca se obtiene en supervivencia. La planta también aparece sola colgando del techo de las Lush Caves, crece solo con luz baja (7 o menos), se cosecha con clic derecho al madurar y vuelve a la etapa 1, así da frutos de forma continua. La versión sobre musgo queda para más adelante.

**Fruta de la Vida** (fruto mejorado): da corazones permanentes, que se conservan al morir.

| Situación del jugador | Fruto del Rejuvenecimiento | Fruta de la Vida |
| --- | --- | --- |
| 0 corazones permanentes | +1 corazón temporal (máx. 5) + Regeneración I y Resistencia I por 10 s | +1 corazón permanente |
| 1 a 9 permanentes | Solo Regeneración I y Resistencia I por 20 s | +1 corazón permanente |
| 10 permanentes (tope) | Regeneración II y Resistencia II por 20 s | +1 corazón temporal (máx. 5) |

**Cómo se consigue la Fruta de la Vida:**

1. En la mesa de transmutación: Fruto del Rejuvenecimiento + amatista + diamantes + varas de blaze + Vitilium + un ingrediente por definir.
2. Sale una semilla de la vida: no tiene efectos, solo se planta.
3. Solo crece cerca de un dispositivo de riego de nivel 4. Tarda mucho, pero después produce frutos de forma casi ilimitada.

**Otros usos:** la Fruta de la Vida se le puede dar al granjero para subir su vida (hasta +5 corazones). Los Frutos del Rejuvenecimiento se le pueden vender.

**Visual a futuro:** corazones con estilo propio para cada tipo (temporal, permanente y del set de armadura). Al principio se usan los corazones normales.

## Cultivos

Cada cultivo tiene hojas 2D y su parte comestible en 3D, asomando de la tierra. Esto se hace solo con modelos JSON (cubos), sin renderizado especial.

**Vegetales iniciales:** cebolla, yuca, nabo y rábano. Cada uno lleva su bloque de cultivo por etapas, su ítem comestible y su versión mutante.

- Idea: la yuca cruda da náuseas o veneno y la cocida alimenta mucho. Podría ser un cultivo más alto, de 2 bloques.
- Cómo se consiguen al inicio (decidido en la Etapa 1): cofres de casas de aldea (40 %, 1 a 3), 2 % de los zombis, y los aldeanos granjeros las plantan. No salen al romper pasto. El comerciante errante y las granjas de aldea con cultivos ya plantados quedan pendientes.
- Cosecha: cada cultivo maduro da de 1 a 4 frutos. Cada fruto extra exige haber conseguido el anterior (sin riego: 75 % uno, 19 % dos, 5 % tres, 1,5 % cuatro). Cada nivel del dispositivo de riego sube la probabilidad de cada tirada extra en 12 puntos. Pendiente decidir si además hay un mínimo garantizado por nivel.
- Bonemeal: avanza 1 etapa (2 con un 25 % de suerte), así que hacen falta unos 2 o 3 polvos.
- La yuca se planta con un esqueje. Cruda da náuseas (siempre) y veneno (40 %); cocida se hace en horno, ahumador o fogata.

**Cultivos vanilla en 3D:** se reemplazan los modelos de zanahoria, papa y remolacha (`carrots_stage0..3`, etc.) desde el propio mod. Un resource pack que cambie esos mismos modelos tendría prioridad.

**Cultivos mutantes:** son una versión gigante del vegetal. Cuando un cultivo madura cerca de un dispositivo de riego (nivel 2 o más), puede mutar. La planta muestra el vegetal gigante en el bloque y suelta un ítem como "Cebolla gigante". El granjero paga bien por ellos.

## Dispositivo de riego

Es un bloque que mantiene húmeda la tierra arada en un radio. No necesita agua ni recargas: solo muestra el efecto visual de riego. Cada nivel se fabrica usando el nivel anterior en la receta.

| Nivel | Se fabrica con | Radio | Velocidad de crecimiento | Mutación | Especial |
| --- | --- | --- | --- | --- | --- |
| 1 | Receta básica (por definir) | 4 | Normal | 0% | Solo hidrata |
| 2 | Nivel 1 + por definir | 5 | ×1.5 | 3% |  |
| 3 | Nivel 2 + por definir | 6 | ×2 | 6% |  |
| 4 | Nivel 3 + por definir | 7 | ×3 | 10% | Hace crecer la semilla de la vida |

Los radios, velocidades y porcentajes son valores iniciales y se ajustan probando. El área que revisa el bloque debe ser limitada para no causar lag.

**Implementación (Etapa 2):** cada nivel es un bloque distinto. El dispositivo revisa cada 2 segundos un cuadrado de lado 2×radio+1, desde 2 bloques por debajo hasta 1 por encima de él. Hidrata la tierra arada y acelera cualquier cultivo de tipo `CropBlock` (también los vanilla) dándole tiradas de crecimiento extra. También sube la probabilidad de frutos extra por cosecha (ver Cultivos). Recetas provisionales: nivel 1 = tolva + cubo de agua + 4 lingotes de hierro; nivel 2 = nivel 1 + 4 lingotes de oro; nivel 3 = nivel 2 + 4 diamantes; nivel 4 = nivel 3 + 1 lingote de netherita. Los valores están en `farming/Irrigation.java`.

**Mutantes:** al madurar un cultivo de Crazy Farm, si hay riego, puede cambiar a su versión gigante (cebolla, nabo, rábano o yuca gigante). Al romperla suelta el ítem gigante y 1 o 2 semillas. Los ítems gigantes no son comestibles; sirven para vender al granjero.

## Vitilium, VitCoin y equipo

El Vitilium es el mineral nuevo. Abunda en las Lush Caves y es menos frecuente por debajo de la capa 0 (variante de pizarra profunda). La VitCoin (VC) sale del lingote, así que cada moneda cuesta netherita y tiene valor real.

**Cadena de procesado:**

1. Mineral de Vitilium: al romperlo da pepitas.
2. Pepitas: se combinan en una barra de Vitilium impura (¿mesa de crafteo u horno? por definir).
3. Barra impura + netherita + Fruto del Rejuvenecimiento: lingote de Vitilium.
4. Lingote: se usa para hacer VitCoins, herramientas y armadura.

**Mejora a Vitilium:** igual que la netherita vanilla, en la mesa de herrería. Se pone placa de Vitilium (hecha en la mesa de transmutación, funciona como plantilla) + pieza de netherita + lingote de Vitilium.

**Herramientas de Vitilium:** están confirmadas; sus estadísticas y habilidades están por definir.

**Armadura de Vitilium:**

| Pieza | Habilidad |
| --- | --- |
| Corona (casco) | Visión nocturna permanente |
| Pechera | Reduce mucho el empuje |
| Pantalones | Resistencia al fuego |
| Botas | Velocidad permanente |
| **Set completo** | **+5 corazones extra** |

**Notas técnicas:**

- Empuje, velocidad y los corazones del set van como atributos, sin íconos de efecto.
- La visión nocturna va como efecto oculto que se renueva solo, para que no parpadee.
- Los 5 corazones del set se quitan al sacarse cualquier pieza. Se suman al tope de 25, así que el máximo posible pasa a 30 corazones (3 filas).

**Decisiones de la Etapa 3:**

- **Mineral:** tan duro como la obsidiana (dureza 50) y solo se pica con pico de diamante o mejor. Suelta de 2 a 5 pepitas (Fortuna sube la cantidad). Lush Caves: 12 vetas de 6 por chunk entre las alturas -64 y 60. Bajo la capa 0 (pizarra profunda): 2 vetas de 4 por chunk. Moderado; se ajusta probando.
- **Cadena:** 9 pepitas = 1 barra impura (mesa de crafteo). Barra impura + lingote de netherita + Fruto del Rejuvenecimiento = lingote (receta temporal en mesa de crafteo hasta la Etapa 5). 1 lingote = 2 VitCoin. La placa se hace temporalmente con 8 diamantes + 1 lingote en mesa de crafteo, y la mejora a Vitilium se hace en la mesa de herrería con placa + pieza de netherita + lingote (conserva los encantamientos).
- **Estadísticas** (siempre mejores que la netherita): herramientas 6000 usos (netherita 2031), velocidad 12 (9), daño +5 (+4), encantabilidad 25 (15). Armadura 4/9/7/4 puntos (netherita 3/8/6/3), dureza 4, durabilidad ×100 (×37).
- **Habilidades por etiqueta** (`crazyfarm:abilities/*`; cualquier ítem puede tenerlas sin tocar código): pico = fundición automática de minerales crudos, hacha = tala el árbol completo, pala = excava 3×3, azada = ara 3×3 (agachado desactiva las de área), espada = 20 % de golpe crítico ×1,5 y recupera el 10 % del daño como vida.
- **Armadura:** corona = visión nocturna oculta; pechera = +0,6 de resistencia al empuje; pantalones = resistencia al fuego oculta; botas = +20 % de velocidad; set completo = +5 corazones.
- **Encantamientos exclusivos (futuro):** todas las piezas están en la etiqueta `crazyfarm:enchantable/vitilium`, lista para usarla como objetivo de encantamientos propios. Ya aceptan los encantamientos vanilla que les corresponden.

## El granjero

Es un personaje único por partida: compra cultivos, paga en VitCoins y se defiende con escopeta. Será una criatura propia que solo se parece a un aldeano y usa su pantalla de comercio. Así los aldeanos normales no le roban la mesa, los zombis no lo convierten y no participa en invasiones.

**Invocación con la Armónica del Letargo:**

- Es de un solo uso y el granjero aparece donde se elige, 5 minutos después. La cuenta regresiva se guarda en el mundo.
- Solo puede haber uno vivo por partida.
- Tiene niveles según el número de invocación: Armónica I para el primero, II para el primo, y así. Cada nivel es más caro de fabricar.
- Después se le asignan una cama y su mesa de trabajo especial.

**Comercio:**

- Paga precios ridículos por los cultivos normales y buenos precios por los mutantes. También compra Frutos del Rejuvenecimiento.
- Paga en VitCoin (VC).
- Acepta Fruta de la Vida para subir su propia vida, hasta +5 corazones.

**Niveles de ventas y accesorios:** funcionan como los niveles vanilla. Más ventas desbloquean mejores tratos y accesorios visibles (propuesta):

| Nivel | Título | Accesorio |
| --- | --- | --- |
| 1 | Pueblerino | Sombrero de paja |
| 2 | Comerciante | Diente de oro |
| 3 | Empresario | Cadena de oro |
| 4 | Magnate | Manoplas de oro |
| 5 | Cripto-granjero | Lentes oscuros y cadena con una VC gigante |

**Combate:**

- Dispara con escopeta a los topos, a otros monstruos y a los jugadores que lo ataquen.
- Si un jugador lo ataca, responde y recibe al instante más vida, Absorción, Resistencia y Regeneración.

**Muerte y primos:**

- Suelta su escopeta y algunos cultivos al azar.
- Se puede invocar otro, que se presenta en el chat como primo del anterior. Ejemplo: *"Soy Ramiro, primo del difunto Cleto. Me dejó dicho que le debía plata."*
- El primo empieza con el nivel del anterior menos 1, y nunca por debajo del nivel 2.

**Escopeta:** es propia del mod; no depende de TaCZ, porque ese mod no existe de forma oficial para los tres loaders en 1.21.1. Imita mecánicas tipo TaCZ sin copiar su código ni sus modelos:

- Varios perdigones con dispersión.
- Recarga, retroceso, sonido y durabilidad.
- Usa cartuchos como munición.
- Los jugadores la pueden usar.

Se podría agregar compatibilidad opcional con TaCZ más adelante.

## Mesa de transmutación

Es un bloque con interfaz propia y es la pieza que une la economía. Sus recetas se definen en archivos de datos, no con un sistema de valor automático, para controlar exactamente qué se obtiene y que los addons puedan agregar recetas.

| Función | Ejemplo |
| --- | --- |
| Monedas a minerales | VC → hierro, oro, diamante, etc. |
| Monedas + objetos a otro objeto de igual o mayor valor | VC + materiales → objeto mejor |
| Semilla de la vida | Rejuvenecimiento + amatista + diamantes + varas de blaze + Vitilium + ingrediente por definir |
| Placa de Vitilium | Plantilla para la mejora a Vitilium (receta por definir) |

Las recetas concretas y los precios están pendientes.

## Mob hostil: topos

Los topos son el primer mob hostil. Funcionan como las lepismas vanilla, pero con tierra en vez de piedra.

- Se esconden en bloques de tierra "infestados" que se ven iguales a los normales: tierra, pasto, tierra gruesa y tierra arada.
- Salen al romper el bloque y llaman a otros topos cercanos cuando los golpeas.
- Para que molesten seguido, también emergen de noche de la tierra arada cerca de los jugadores. El juego normal solo genera monstruos en total oscuridad, así que esto lleva una regla de aparición propia.
- El granjero les dispara.

## Notas técnicas y riesgos

El mayor riesgo técnico es lo que cambia en cada loader. Casi toda la lógica vive en `common`, pero hay partes que hay que escribir tres veces.

| Tema | Detalle |
| --- | --- |
| Código por loader | Registro de bloques e ítems, pestañas creativas, generación de mundo (Lush Caves, Vitilium), criaturas y sus atributos, interfaces (mesa de transmutación), drops de pasto, transparencia de modelos en Fabric |
| Vida permanente | En 1.21 los modificadores de vida se pierden al morir; los permanentes se vuelven a aplicar al reaparecer (mixin común o evento de cada loader) |
| Barra de vida | Hasta 30 corazones; los corazones con estilo propio requieren dibujar la barra en cada loader |
| Rendimiento | El riego y la aparición de topos deben revisar áreas limitadas |
| Dependencias | GeckoLib es opcional pero recomendado; TaCZ no se usa como dependencia |
| Compilación | Se hace en el PC de Ardilla; el entorno de Claude no tiene acceso a los repositorios de Minecraft |

## Hoja de ruta

Se construye por etapas, y cada una se prueba en el juego antes de pasar a la siguiente.

1. Base multi-loader, los 4 vegetales, cultivos vanilla en 3D y Fruto del Rejuvenecimiento en las Lush Caves.
2. Dispositivo de riego (4 niveles) y cultivos mutantes.
3. Vitilium: mineral, cadena de procesado, VitCoin, herramientas y armadura.
4. Granjero: Armónica, comercio, niveles, accesorios, escopeta y primos.
5. Mesa de transmutación.
6. Topos.
7. Fruta de la Vida: semilla de la vida y vida permanente.
8. Corazones con estilo propio y pulido final.

## Pendientes por decidir

Ninguno de estos puntos bloquea la etapa 1.

- [ ] Último ingrediente de la receta de la semilla de la vida
- [ ] Ingredientes de cada nivel del dispositivo de riego
- [ ] Ingredientes y niveles de la Armónica del Letargo (costo por invocación)
- [ ] Recetas y precios de la mesa de transmutación (monedas a minerales, intercambios, placa de Vitilium)
- [ ] Precios del granjero por cultivo normal, mutante y Rejuvenecimiento
- [ ] Receta de los cartuchos y estadísticas de la escopeta
- [ ] Cómo se hace la barra impura (mesa de crafteo u horno)
- [ ] Estadísticas y habilidades de las herramientas de Vitilium
- [ ] Confirmar los títulos y accesorios de cada nivel del granjero
- [ ] Lista de nombres y frases de los primos
- [ ] Diseño de los corazones con estilo propio
