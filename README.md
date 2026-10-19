# Análisis del árbol B+

## 1. Capacidad de página

Una hoja almacena una cabecera de 12 bytes (`tipo`, `n`, `siguiente`) y hasta `L` pares (`clave`, `pos`) de 16 bytes cada uno. Por lo tanto, `12 + 16L ≤ P` y:

\[
L=\left\lfloor\frac{P-12}{16}\right\rfloor.
\]

Un nodo interno almacena una cabecera de 12 bytes, `M` referencias a hijos de 4 bytes y `M−1` claves separadoras de 8 bytes. Así, `12+4M+8(M-1) ≤ P` y:

\[
M=\left\lfloor\frac{P-4}{12}\right\rfloor.
\]

| P (bytes) | L | M | Libres en hoja | Libres en interno |
|---:|---:|---:|---:|---:|
| 60 | 3 | 4 | 0 | 8 |
| 4096 | 255 | 341 | 4 | 0 |

`M ≠ L` porque las hojas guardan pares de 16 bytes, mientras que los internos guardan referencias a hijos y claves separadoras, con otra distribución de bytes.

## 2. Altura

Sea `h` la cantidad de niveles (la raíz cuenta como un nivel). Con todas las páginas llenas, un árbol de `h` niveles admite hasta `L·M^(h−1)` claves. En consecuencia, para `N ≥ 1`:

\[
h_{\min}=\max\left(1,\ 1+\left\lceil\log_M\frac{N}{L}\right\rceil\right).
\]

Para la ocupación mínima (I3), sea `m = ⌈M/2⌉` el mínimo de hijos de un interno no raíz y `l = ⌈L/2⌉` el mínimo de claves de una hoja no raíz. Si `h ≥ 2`, la raíz tiene al menos dos hijos y el árbol contiene al menos `2·m^(h−2)·l` claves. Por tanto:

\[
h_{\max}\le 2+\left\lfloor\log_m\frac{N}{2l}\right\rfloor
\quad(h\ge2).
\]

En la Tabla A, para `N = 1 000 000`, con `P = 128` se observaron **8 niveles** en orden creciente y **7** en aleatorio; con `P = 2048`, **4** y **3**, respectivamente. Todos quedan dentro de las cotas teóricas. En la Tabla B (`N = 300000`, `P = 4096`, `L = 255`, `M = 341`), tanto **Archivo** como **Aleatorio** presentan **3 niveles**: la cota inferior es 3, y ambos resultados la alcanzan. Frente al ABB no balanceado de la motivación, cuya altura puede llegar a `Θ(N)` al insertar claves ordenadas, el B+ mantiene una altura `Θ(log_M N)` gracias a las divisiones y a la ocupación mínima de sus nodos.

## 3. E/S por operación

`buscar()` recorre exactamente un camino desde la raíz hasta una hoja: efectúa `niveles()` lecturas de páginas y ninguna escritura, tanto si encuentra la clave como si no. `insertar()` también efectúa `niveles()` lecturas para localizar la hoja; si se producen `s` divisiones, escribe dos páginas por división y una página final, es decir, `2s + 1` escrituras de nodos (sin contar actualizaciones de la cabecera).

Como `h = Θ(log_M N)` para `P` fijo y `s ≤ h`, ambas operaciones requieren `Θ(log_M N)` accesos a disco en el análisis de peor caso. La búsqueda binaria dentro de cada página requiere `Θ(log M)` comparaciones por nivel, de modo que el total es `Θ(log_M N · log M) = Θ(log N)` comparaciones.

## 4. Ocupación

Con inserción creciente, las claves nuevas siempre llegan a la hoja más a la derecha. Después de una división, las hojas de la izquierda dejan de recibir claves y quedan aproximadamente a media capacidad. Si la división de `L+1` claves deja `a = ⌈(L+1)/2⌉` en la hoja izquierda, la cantidad **exacta** de hojas para esta implementación es:

\[
H_{\mathrm{creciente}}=1+\max\left(0,\left\lceil\frac{N-L}{a}\right\rceil\right).
\]

Por ejemplo, con `N = 10 000`: para `P = 128`, `L = 7`, `a = 4` y `H = 2500`; para `P = 2048`, `L = 127`, `a = 64` y `H = 156`. Coinciden exactamente con la Tabla A; la misma fórmula reproduce sus 18 valores de `HojOrd` para `N = 1000`, `10 000` y `1 000 000`.

Para `N = 1 000 000`:

| P | Hojas creciente | Hojas aleatorio | Ocupación hojas creciente | Ocupación hojas aleatorio | Niveles creciente / aleatorio |
|---:|---:|---:|---:|---:|---:|
| 128 | 250000 | 196962 | 57,14 % | 72,53 % | 8 / 7 |
| 2048 | 15625 | 11438 | 50,39 % | 68,84 % | 4 / 3 |

La inserción aleatoria aprovecha mejor el espacio disponible: genera menos hojas y, en estos dos casos, menos niveles. También reduce el tamaño del índice en páginas; no obstante, la reducción de niveles no ocurre necesariamente para cualquier `N` (por ejemplo, con `N = 1000` ambos órdenes tienen los mismos niveles para esas dos capacidades).

En la Tabla B de IMDb (`N = 300000`, `P = 4096`), **Archivo** produjo **2343 hojas**, **14 internos**, **50,21 %** de ocupación de hojas, **3 niveles** y un índice de **9658368 bytes**. **Aleatorio** produjo **1805 hojas**, **9 internos**, **65,18 %** de ocupación, **3 niveles** y **7434240 bytes**. El índice aleatorio ocupa **2224128 bytes menos** (aproximadamente **23,03 %**), aunque la altura no cambia. El modo **Archivo** inserta las claves según aparecen en `title.basics.tsv`, mientras que **Aleatorio** las mezcla antes de insertarlas. La ocupación cercana al 50 % es compatible con un orden predominantemente creciente, pero no demuestra por sí sola que todos los `tconst` estén ordenados numéricamente.

## 5. Búsqueda secuencial contra índice

Según la Tabla C (`N = 300000`), para claves existentes la búsqueda secuencial leyó en promedio **152494,28 registros**, cerca del valor teórico `(N+1)/2 = 150000,5` si las posiciones son equiprobables. Para claves inexistentes leyó los **300000 registros**, porque debe revisar todo el archivo.

En cambio, el B+ leyó **3 páginas del índice** por consulta, tanto para claves existentes como inexistentes. En las exitosas leyó además **1 registro de datos**, y en las fallidas **0**. La búsqueda secuencial crece linealmente, `Θ(N)` en promedio para claves existentes uniformes y en el peor caso; el índice B+ requiere `Θ(log_M N)` páginas, más una lectura de datos cuando la clave existe. Las páginas del índice y los registros de datos son unidades distintas, por lo que no deben interpretarse como lecturas de idéntico tamaño.

## 6. Búsqueda por rango

`buscarRango(desde, hasta)` baja una sola vez desde la raíz hasta la primera hoja y continúa por los enlaces `siguiente`. Sean `h` los niveles, `s` los registros entregados, `L` la capacidad de una hoja y `α` su ocupación media (entre 0 y 1). Si las claves del intervalo están distribuidas de forma representativa, cada hoja contiene aproximadamente `αL` claves y las páginas leídas se estiman por:

\[
P_{\mathrm{rango}}\approx(h-1)+\frac{s}{\alpha L}+O(1).
\]

El término `h−1` corresponde a las páginas internas del descenso inicial; el resto, a las hojas recorridas. Es una **estimación**, no una igualdad exacta, porque el intervalo puede comenzar o terminar dentro de una hoja y la ocupación local puede variar. Para `P` fijo y rangos con `s` resultados, el costo de recorrido es `Θ(log_M N + s/L + 1)` bajo ocupación acotada por I3.

La Tabla D confirma la tendencia:

| Ancho `w` | Registros entregados (`s`) | Páginas leídas | Hojas leídas | `s·h` (búsquedas independientes, `h=3`) |
|---:|---:|---:|---:|---:|
| 10 | 9,56 | 3,03 | 1,03 | 28,68 |
| 100 | 95,36 | 3,70 | 1,70 | 286,08 |
| 1000 | 956,33 | 10,44 | 8,44 | 2868,99 |
| 10000 | 9351,75 | 75,99 | 73,99 | 28055,25 |

En todos los casos, `páginas leídas − hojas leídas = 2 = h−1`. Para `w = 10000`, una consulta por rango leyó **75,99 páginas** en promedio; realizar `s` búsquedas independientes requeriría aproximadamente `9351,75 · 3 = 28055,25` lecturas de páginas, sin considerar caché. El recorrido por rango es más eficiente porque no repite el descenso desde la raíz para cada resultado.

---

**Comprobación pendiente:** verificar directamente en el TSV si los `tconst` del modo Archivo llegan en orden numérico estrictamente creciente. Las Tablas A, B, C y D corresponden a las ejecuciones experimentales realizadas.
