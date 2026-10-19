# Árbol B+ persistente — Índice IMDb

## Implementación y verificación

El índice B+ se almacena en `indice.dat` mediante páginas de tamaño fijo `P` y acceso con `RandomAccessFile`; `datos.dat` contiene los registros completos y las hojas del índice guardan sus posiciones en bytes. `leerNodo` y `escribirNodo` operan sobre páginas individuales. Las hojas están enlazadas mediante `siguiente`. Los contadores de lecturas y escrituras corresponden a páginas de nodos (no a la cabecera).

**Prueba con P = 60:** se insertaron las claves `50, 20, 80, 10, 30, 60, 90, 40, 70, 25, 35, 45, 15`, con `pos = 10 × clave`. El árbol final tiene **3 niveles, 6 hojas, 13 claves y 10 páginas (600 bytes)**. `verificarInvariantes()` devolvió `OK`, también después de cerrar y reabrir el archivo. `buscar(10)` y `buscar(90)` devolvieron 100 y 900; `buscar(33)` devolvió −1; las tres consultas leyeron 3 páginas. `insertar(45)` produjo dos divisiones, 2 lecturas y 5 escrituras. `buscarRango(22,62)` devolvió 7 claves con 6 lecturas. Se rechazaron claves duplicadas y rangos inválidos.

## Análisis (seis puntos)

### 1. Capacidad de página

Definimos `P` como bytes por página, `L` como cantidad máxima de pares en una hoja y `M` como cantidad máxima de hijos en un nodo interno. Una **hoja** contiene `tipo` (4 bytes), `n` (4), `siguiente` (4) y `L` pares `(clave, pos)` de 8 + 8 = 16 bytes. Por ello:

\[
12+16L\le P\quad\Rightarrow\quad L\le\frac{P-12}{16}\quad\Rightarrow\quad
\boxed{L=\left\lfloor\frac{P-12}{16}\right\rfloor}.
\]

Un **interno** contiene 12 bytes de cabecera, `M` referencias a hijos de 4 bytes y `M−1` separadores de 8 bytes:

\[
12+4M+8(M-1)\le P\Rightarrow4+12M\le P\Rightarrow
\boxed{M=\left\lfloor\frac{P-4}{12}\right\rfloor}.
\]

| P | L | M | Cálculo hoja / bytes libres | Cálculo interno / bytes libres |
|---:|---:|---:|---|---|
| 60 | 3 | 4 | `60−(12+16·3)=0` | `60−[12+4·4+8·3]=8` |
| 4096 | 255 | 341 | `4096−(12+16·255)=4` | `4096−[12+4·341+8·340]=0` |

`M ≠ L` porque una hoja guarda **clave y posición** (16 bytes por entrada), mientras que un interno guarda **hijos y separadores** (4 y 8 bytes, respectivamente).

### 2. Altura

Sea `N ≥ 1` la cantidad de claves y `h` los niveles, contando la raíz como nivel 1.

**Caso A: páginas llenas (altura mínima).** Con un solo nivel caben `L` claves; con dos, `L·M`; con tres, `L·M²`. En general, la capacidad máxima de `h` niveles es `L·M^(h−1)`. Para almacenar `N` claves:

\[
N\le LM^{h-1}\Rightarrow\frac NL\le M^{h-1}
\Rightarrow\log_M\left(\frac NL\right)\le h-1
\Rightarrow\boxed{h_{\min}=\max\left(1,1+\left\lceil\log_M\frac NL\right\rceil\right)}.
\]

Se usa el techo porque el número de niveles debe ser entero; el máximo con 1 garantiza al menos la raíz.

**Caso B: ocupación mínima (I3, altura máxima).** Si la raíz es interna, tiene al menos **2 hijos**; cada otro interno tiene al menos `m=⌈M/2⌉` hijos y cada hoja no raíz contiene al menos `l=⌈L/2⌉` claves. Así, los mínimos de hojas para `h=2,3,4,…` son `2`, `2m`, `2m²`, …, `2m^(h−2)`; multiplicando por `l`, las claves mínimas son `2l`, `2ml`, `2m²l`, … . Por ello, si `h ≥ 2`:

\[
N\ge2l\,m^{h-2}\Rightarrow\frac{N}{2l}\ge m^{h-2}
\Rightarrow\log_m\left(\frac{N}{2l}\right)\ge h-2
\Rightarrow\boxed{h\le2+\left\lfloor\log_m\frac{N}{2l}\right\rfloor}.
\]

Si la raíz también es hoja, `h=1`. Por ejemplo, para `P=60`, `N=13`, `L=3`, `M=4`, `l=m=2`, las cotas dan **3 ≤ h ≤ 3**, tal como en la prueba. En la **Tabla A, N=1 000 000**, con `P=128` se obtuvieron **8 niveles creciente / 7 aleatorio**; con `P=2048`, **4 / 3**. En la **Tabla B, N=300 000 y P=4096**, ambos órdenes tienen **3 niveles**, que coinciden con el mínimo teórico `1+⌈log₃₄₁(300000/255)⌉=3`. El **ABB perfectamente balanceado** de la motivación requiere aproximadamente `ceil(log₂(N+1))` páginas por búsqueda, suponiendo un nodo por página. Para `N=300000` son **19 páginas**, frente a las **3 del B+**. Cada interno del B+ permite elegir entre muchos hijos, reduciendo los niveles recorridos.

### 3. E/S por operación

`buscar()` sigue **un único camino** desde la raíz hasta la hoja: una lectura por nivel, encuentre o no la clave. Por tanto, `R = niveles()` y `W = 0`. `insertar()` también lee ese camino (`R = niveles()`, considerando la altura antes de insertar). Si la clave es nueva y hay `s` divisiones, cada una escribe **dos páginas** (nodo original y nuevo) y se escribe **una página final** donde termina la propagación (o la nueva raíz):

\[
\boxed{W=2s+1}.
\]

Si la clave ya existe, `insertar` devuelve `false`, lee un camino completo y no escribe ninguna página: **`R=h`, `W=0`**.

Por ejemplo, `insertar(45)` tuvo `s=2`, luego `W=2·2+1=5`, como en la prueba. No se cuentan las escrituras de cabecera. Como `h=Θ(log_M N)` y `s≤h`, el costo de E/S de las operaciones es **`Θ(log_M N)` en el peor caso**. La búsqueda binaria dentro de una página realiza `Θ(log M)` comparaciones en el peor caso; al recorrer `Θ(log_M N)` niveles:

\[
\Theta(\log_M N\cdot\log_2 M)
=\Theta\left(\frac{\log_2N}{\log_2M}\cdot\log_2M\right)
=\boxed{\Theta(\log N)}.
\]

### 4. Ocupación

Con inserción **creciente**, cada clave nueva llega a la hoja más a la derecha. Cuando se desborda, la hoja izquierda queda aproximadamente a media capacidad y ya no recibe claves mayores; por eso la ocupación se acerca al 50 % (para hojas pequeñas puede superar ese valor). Al dividir `L+1` claves, la hoja izquierda conserva `a=⌈(L+1)/2⌉` claves. La primera hoja admite `L` claves sin dividirse; la primera división ocurre al insertar la clave número `L+1` y, desde entonces, cada `a` inserciones provocan otra división, que agrega una hoja. Por ello, para esta política de división y `N≥1`:

\[
\boxed{H_{\mathrm{creciente}}=1+\max\left(0,\left\lceil\frac{N-L}{a}\right\rceil\right)},
\qquad a=\left\lceil\frac{L+1}{2}\right\rceil.
\]

**Predicción exacta (Tabla A, N=1 000 000):** para `P=128`, `L=7`, `a=4`: `H=1+ceil((1000000−7)/4)=250000`; para `P=2048`, `L=127`, `a=64`: `H=1+ceil((1000000−127)/64)=15625`. Coinciden con las mediciones. Las ejecuciones pequeñas se conservan en el anexo como pruebas adicionales.

Para **N=1 000 000**, con `P=128` hubo **250000 hojas, 57,14 % y 8 niveles** en creciente frente a **196962 hojas, 72,53 % y 7 niveles** en aleatorio. Con `P=2048` hubo **15625 hojas, 50,39 % y 4 niveles** frente a **11438 hojas, 68,84 % y 3 niveles**. La inserción aleatoria reutiliza espacio en distintas hojas, reduce páginas y tamaño del índice y, en estos casos, también niveles; no siempre reduce la altura (en Tabla B ambas variantes tienen 3).

En **IMDb (Tabla B, P=4096, N=300000)**, Archivo produjo **2343 hojas, 50,21 % de ocupación, 3 niveles e índice de 9658368 bytes**; Aleatorio, **1805 hojas, 65,18 %, 3 niveles e índice de 7434240 bytes**. La diferencia es **2224128 bytes (≈23,03 %)**. Se comprobó directamente el orden de los **300000 `tconst`** usados: **0 pares fuera de orden creciente estricto**. Por tanto, en modo **Archivo las claves llegan en orden numérico estrictamente creciente**; en **Aleatorio** se mezclan antes de insertarlas. Esto explica la ocupación cercana al 50 % de Archivo.

### 5. Secuencial contra índice

Una búsqueda secuencial de una clave **existente** elegida uniformemente revisa en promedio `(N+1)/2` registros. Nuestra medición suma una lectura adicional mediante `leer(pos)` para recuperar el registro encontrado, por lo que la esperanza del contador es **`(N+1)/2 + 1`**. Para `N=300000`, son **150001,5 registros/consulta**. La **Tabla C** midió **152494,28** sobre una muestra de 50 consultas, que no tiene que coincidir exactamente con la esperanza. Una clave **inexistente** requiere revisar los `N` registros, y la tabla confirma **300000**. El B+ leyó **3 páginas del índice/consulta** en ambos casos, más **1 registro de datos** si la clave existía y **0** si no existía.

La búsqueda secuencial crece **`Θ(N)`** en promedio (existentes uniformes) y en el peor caso; el B+ crece **`Θ(log_M N)`** en páginas de índice, más una lectura del registro encontrado. No se equipara una página de índice con un registro de datos: son unidades distintas.

### 6. Rango

`buscarRango(desde,hasta)` desciende **una sola vez** de la raíz a la primera hoja y luego avanza por los enlaces `siguiente`. Sean `h` los niveles, `s` los registros entregados, `L` la capacidad de hoja y `α` la ocupación media como fracción. Cada hoja contiene aproximadamente `αL` claves. Por ello:

\[
\boxed{P_{\mathrm{rango}}\approx(h-1)+\frac{s}{\alpha L}+O(1)}.
\]

El término `h−1` representa los nodos internos del descenso inicial; `s/(αL)` aproxima las hojas recorridas. El término constante contempla hojas parcialmente cubiertas. **No es una igualdad exacta:** influyen la posición de los extremos, la ocupación local y los huecos entre claves. Con ocupación mínima garantizada, el costo es **`Θ(h+s/L)`**, también para rangos vacíos porque `h≥1`. Las hojas intermedias recorridas están completamente dentro del rango; los extremos agregan un número constante de hojas. No hace falta que los identificadores sean consecutivos. Para `s` búsquedas independientes, el costo es **`Θ(s·h)`**.

En la **Tabla D**, `h=3`, y las medias `(s, páginas, hojas)` fueron: `w=10: (9,56; 3,03; 1,03)`, `w=100: (95,36; 3,70; 1,70)`, `w=1000: (956,33; 10,44; 8,44)` y `w=10000: (9351,75; 75,99; 73,99)`. En cada caso `páginas−hojas=2=h−1`, confirmando que los internos se recorren una sola vez. Para `w=10000`, **75,99 páginas** frente a **9351,75·3=28055,25 páginas** de búsquedas independientes (sin caché). La ventaja proviene de recorrer hojas enlazadas sin reiniciar la búsqueda desde la raíz.

---

## Tablas experimentales

Las mediciones se realizan sin buffer de páginas propio. Los tiempos dependen del equipo y del caché del sistema operativo; usamos los contadores de páginas como medida principal del trabajo del algoritmo y los tiempos como información complementaria.

### Tabla A — Estructura sintética (N = 1000000)

| N | P | M | L | NivOrd | HojOrd | OcupHOrd% | OcupIOrd% | NivAle | HojAle | OcupHAle% | OcupIAle% |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 1000000 | 128 | 10 | 7 | 8 | 250000 | 57.14 | 60.0 | 7 | 196962 | 72.53 | 71.69 |
| 1000000 | 256 | 21 | 15 | 6 | 125000 | 53.33 | 52.39 | 6 | 94394 | 70.63 | 69.88 |
| 1000000 | 512 | 42 | 31 | 5 | 62500 | 51.61 | 52.4 | 5 | 46245 | 69.75 | 67.65 |
| 1000000 | 1024 | 85 | 63 | 4 | 31250 | 50.79 | 50.66 | 4 | 22847 | 69.48 | 62.82 |
| 1000000 | 2048 | 170 | 127 | 4 | 15625 | 50.39 | 50.54 | 3 | 11438 | 68.84 | 58.09 |
| 1000000 | 4096 | 341 | 255 | 3 | 7812 | 50.2 | 50.09 | 3 | 5627 | 69.69 | 57.18 |

### Tabla B — Construcción IMDb (N = 300000, P = 4096, M = 341, L = 255)

> **Pendiente de confirmar:** se conservaron las mediciones del borrador. Esta tabla indica `DatosBytes=27099041`, pero el archivo local `tp3/datos.dat` revisado mide `27099525` bytes. Confirmar la ejecución y versión del dataset de las que procede la tabla, registrar su fecha y usar mediciones de una misma construcción para B, C y D. No se modificaron cifras sin una nueva medición.

| Orden | Registros | DatosBytes | IndiceBytes | Niveles | Hojas | Internos | OcupH% | OcupI% | PagEscritas | Segundos |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| Archivo | 300000 | 27099041 | 9658368 | 3 | 2343 | 14 | 50.21 | 49.35 | 304708 | 25.438 |
| Aleatorio | 300000 | 27099041 | 7434240 | 3 | 1805 | 9 | 65.18 | 59.07 | 303622 | 9.083 |

### Tabla C — Búsquedas

| Método | Consultas | PagIndice/consulta | RegLeidos/consulta | Microseg/consulta |
|---|---:|---:|---:|---:|
| B+ existentes | 1000 | 3.000 | 1.000 | 27.657 |
| B+ inexistentes | 1000 | 3.000 | 0.000 | 24.607 |
| Secuencial existentes | 50 | 0.000 | 152494.280 | 1260690.426 |
| Secuencial inexistentes | 50 | 0.000 | 300000.000 | 1501051.454 |

### Tabla D — Rangos

| w | RegEntregados/consulta | PagLeidas/consulta | HojasLeidas/consulta | Microseg/consulta |
|---:|---:|---:|---:|---:|
| 10 | 9.560 | 3.030 | 1.030 | 32.547 |
| 100 | 95.360 | 3.700 | 1.700 | 15.233 |
| 1000 | 956.330 | 10.440 | 8.440 | 89.096 |
| 10000 | 9351.750 | 75.990 | 73.990 | 308.382 |

**Verificación del orden IMDb:** se revisaron los primeros 300000 registros de `title.basics.tsv`, omitiendo la cabecera y comparando numéricamente los `tconst` consecutivos: `Pares fuera de orden creciente estricto: 0`.

## Anexo — Pruebas sintéticas adicionales

| N | P | M | L | NivOrd | HojOrd | OcupHOrd% | OcupIOrd% | NivAle | HojAle | OcupHAle% | OcupIAle% |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 1000 | 128 | 10 | 7 | 4 | 250 | 57.14 | 60.82 | 4 | 196 | 72.89 | 65.71 |
| 1000 | 256 | 21 | 15 | 3 | 125 | 53.33 | 53.97 | 3 | 98 | 68.03 | 62.5 |
| 1000 | 512 | 42 | 31 | 3 | 62 | 52.03 | 50.79 | 3 | 45 | 71.68 | 37.3 |
| 1000 | 1024 | 85 | 63 | 2 | 31 | 51.2 | 36.47 | 2 | 22 | 72.15 | 25.88 |
| 1000 | 2048 | 170 | 127 | 2 | 15 | 52.49 | 8.82 | 2 | 11 | 71.58 | 6.47 |
| 1000 | 4096 | 341 | 255 | 2 | 7 | 56.02 | 2.05 | 2 | 6 | 65.36 | 1.76 |
| 10000 | 128 | 10 | 7 | 6 | 2500 | 57.14 | 60.08 | 5 | 1980 | 72.15 | 69.97 |
| 10000 | 256 | 21 | 15 | 4 | 1250 | 53.33 | 52.73 | 4 | 948 | 70.32 | 70.12 |
| 10000 | 512 | 42 | 31 | 3 | 625 | 51.61 | 53.61 | 3 | 460 | 70.13 | 66.67 |
| 10000 | 1024 | 85 | 63 | 3 | 312 | 50.88 | 46.91 | 3 | 229 | 69.31 | 54.82 |
| 10000 | 2048 | 170 | 127 | 2 | 156 | 50.47 | 91.76 | 2 | 120 | 65.62 | 70.59 |
| 10000 | 4096 | 341 | 255 | 2 | 78 | 50.28 | 22.87 | 2 | 64 | 61.27 | 18.77 |

## Datos de entrega pendientes

- Completar la identificación del grupo y de cada integrante: apellido, nombre, CIC, sección y número de tarea. Incluir también la identificación exigida en cada fuente.
- Incorporar el texto exacto de la Declaración de Honor proporcionado por la cátedra.
- Confirmar la procedencia de las mediciones señalada en la Tabla B y conservar la salida de la comprobación del orden del TSV mencionada arriba.
- Comprobar el límite de tres páginas de análisis, más las tablas, en la presentación final; Markdown no fija la paginación.

## Uso de IA

Utilizamos ChatGPT mediante Codex como apoyo para revisar el código, detectar errores que habíamos pasado por alto, comprender los requisitos de la consigna y preparar pruebas. Solicitamos explicaciones de las operaciones del árbol B+, incluidas las divisiones de hojas e internos y la creación de una nueva raíz, y contrastamos sus resultados con las ejecuciones del programa.

### Participación por componente

| Componente | Uso de IA en la revisión y desarrollo |
|---|---|
| Formato de página y cálculo de M y L | Revisión y explicación de las fórmulas, capacidades y bytes libres. |
| Búsqueda puntual | Revisión del descenso, resultados y lecturas por nivel. |
| Split de hoja | Revisión de la traza, separador copiado y reenlace de hojas. |
| Split de interno y raíz nueva | Explicación y revisión de la propagación, numeración de páginas y escrituras. |
| Búsqueda por rango | Explicación del recorrido y preparación de pruebas de orden, posiciones y parada. |
| Persistencia: crear, abrir y close | Preparación de pruebas de reapertura, cabecera y rechazo de archivo inválido. |
| Verificación de invariantes | Revisión de los requisitos y de que los diagnósticos no alteren los contadores. |
| TestArbolBMas | Código de pruebas propuesto por IA, incorporado y adaptado al proyecto; ejecutamos y revisamos los resultados. |
| ArchivoDatos y BMasApp | Revisión y explicación del formato de registros, posiciones, consultas y contadores. |
| ExperimentoBMas | Parte del código inicial fue propuesta por IA; posteriormente revisamos y modificamos el experimento con su ayuda. |
| Análisis del README | Explicaciones, propuestas de redacción y correcciones técnicas realizadas con ayuda de IA, incluida esta declaración. |

**Pendiente de completar por los integrantes:** para los componentes cuyo origen inicial no está documentado aquí, indicar si la implementación fue manual, con ayuda de IA o generada por IA y modificada. La tabla describe la ayuda conocida y no sustituye esa clasificación del desarrollo original.

### Herramienta y prompts relevantes

Herramienta: ChatGPT mediante Codex. Prompts textuales de esta conversación:

- «Verifica las implementaciones actuales de la clase ArbolBMas»
- «Viendo la clase ArbolBMas por partes, explicame para que casos esta mal o hay que corregir sino para que funciona, como y que deberia esperar»
- «podes arreglarme la impresion de esa tabla o sea queda toda desfasadas las columnas
- «experimentoBMas cual es su finalidad?»
- «para que sirve especificamente archivosDatos y como se usa?»
- «explicame mejor la parte de la defensa del readme en el ej2»
- «explicame cuales son las funciones que se puede utilizar en java y como funcionan»
- «explicame como busca realmente con un grafico»

### Cambios introducidos y comprobaciones

- Corregimos el experimento sintético restaurando el arreglo antes de cada construcción creciente: antes reutilizaba claves mezcladas desde el segundo tamaño de página.
- Reemplazamos los tabuladores de las tablas A y B por formatos de ancho fijo y quitamos java.util.Locale.US para respetar las restricciones de la consigna.
- Ampliamos las pruebas de inserciones, búsquedas, duplicados, rangos, persistencia y archivo inválido, comparando resultados y contadores con valores esperados.
- Mejoramos nuestros comentarios que justifican los tipos de excepciones.
- Compilamos y ejecutamos las pruebas y los experimentos para contrastar las explicaciones con el comportamiento del programa.
