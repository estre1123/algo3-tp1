# Árbol B+ persistente — Índice IMDb

grupo:g_tq6
Nro tarea: TP3
Integrantes:
	Espinola Aguero,Anibal Julian  CIC=6362752  SECCION=TQ
	Hsu Yang, Estrella  CIC=5837842  SECCION=TQ
Nosotros, Anibal Julian Espinola Aguero y Estrella Hsu Yang:
No hemos discutido el código fuente de nuestra tarea con ningún otro grupo, solo con el Profesor o el AER.
No hemos usado código obtenido de otro estudiante o de cualquier otra fuente no autorizada, modificada o no modificada.
Cualquier código o documentación utilizada en nuestro programa obtenido de fuentes, tales como libros o notas de curso, ha sido claramente indicada en nuestra tarea.


## Implementación y verificación

El índice B+ se almacena en `indice.dat` mediante páginas de tamaño fijo `P` y acceso con `RandomAccessFile`; `datos.dat` contiene los registros completos y las hojas del índice guardan sus posiciones en bytes. `leerNodo` y `escribirNodo` operan sobre páginas individuales. Las hojas están enlazadas mediante `siguiente`. Los contadores de lecturas y escrituras corresponden a páginas de nodos (no a la cabecera).

**Prueba con P = 60:** se insertaron las claves `50, 20, 80, 10, 30, 60, 90, 40, 70, 25, 35, 45, 15`, con `pos = 10 × clave`. El árbol final tiene **3 niveles, 6 hojas, 13 claves y 10 páginas (600 bytes)**. `verificarInvariantes()` devolvió `OK`, también después de cerrar y reabrir el archivo. `buscar(10)` y `buscar(90)` devolvieron 100 y 900; `buscar(33)` devolvió −1; las tres consultas leyeron 3 páginas. `insertar(45)` produjo dos divisiones, 2 lecturas y 5 escrituras. `buscarRango(22,62)` devolvió 7 claves con 6 lecturas. Se rechazaron claves duplicadas y rangos inválidos.

## Análisis (seis puntos)

**Cómo leer las fórmulas:** `*` significa multiplicación, `/` división y `^` potencia. `<=` significa menor o igual y `>=`, mayor o igual. `floor(x)` redondea hacia abajo (por ejemplo, `floor(3,7)=3`); `ceil(x)` redondea hacia arriba (`ceil(3,2)=4`). `max(a,b)` elige el mayor. `log_base_M(x)` es el logaritmo de `x` en base `M`: el exponente al que hay que elevar `M` para obtener `x`. `Theta(...)` o `Θ(...)` indica una cota ajustada del crecimiento del costo.

### 1. Capacidad de página

Definimos `P` como bytes por página, `L` como cantidad máxima de pares en una hoja y `M` como cantidad máxima de hijos en un nodo interno. Una **hoja** contiene `tipo` (4 bytes), `n` (4), `siguiente` (4) y `L` pares `(clave, pos)` de 8 + 8 = 16 bytes. Por ello:

```text
12 + 16 * L <= P
L <= (P - 12) / 16
L = floor((P - 12) / 16)
```

Un **interno** contiene 12 bytes de cabecera, `M` referencias a hijos de 4 bytes y `M-1` separadores de 8 bytes:

```text
12 + 4 * M + 8 * (M - 1) <= P
4 + 12 * M <= P
M = floor((P - 4) / 12)
```

| P | L | M | Cálculo hoja / bytes libres | Cálculo interno / bytes libres |
|---:|---:|---:|---|---|
| 60 | 3 | 4 | `60-(12+16 * 3)=0` | `60-[12+4 * 4+8 * 3]=8` |
| 4096 | 255 | 341 | `4096-(12+16 * 255)=4` | `4096-[12+4 * 341+8 * 340]=0` |

`M ≠ L` porque una hoja guarda **clave y posición** (16 bytes por entrada), mientras que un interno guarda **hijos y separadores** (4 y 8 bytes, respectivamente).

### 2. Altura

Sea `N ≥ 1` la cantidad de claves y `h` los niveles, contando la raíz como nivel 1.

**Caso A: páginas llenas (altura mínima).** Con un solo nivel caben `L` claves; con dos, `L * M`; con tres, `L * M^2`. En general, la capacidad máxima de `h` niveles es `L * M^(h - 1)`. Para almacenar `N` claves:

```text
N <= L * M^(h - 1)
N / L <= M^(h - 1)
log_base_M(N / L) <= h - 1

h_min = max(1, 1 + ceil(log_base_M(N / L)))
```

Se usa el techo porque el número de niveles debe ser entero; el máximo con 1 garantiza al menos la raíz.

**Caso B: ocupación mínima (I3, altura máxima).** Si la raíz es interna, tiene al menos **2 hijos**; cada otro interno tiene al menos `m = ceil(M / 2)` hijos y cada hoja no raíz contiene al menos `l = ceil(L / 2)` claves. Así, los mínimos de hojas para `h=2,3,4,…` son `2`, `2 * m`, `2 * m^2`, …, `2 * m^(h - 2)`; multiplicando por `l`, las claves mínimas son `2 * l`, `2 * m * l`, `2 * m^2 * l`, … . Por ello, si `h ≥ 2`:

```text
N >= 2 * l * m^(h - 2)
N / (2 * l) >= m^(h - 2)
log_base_m(N / (2 * l)) >= h - 2

h <= 2 + floor(log_base_m(N / (2 * l)))
```

Si la raíz también es hoja, `h=1`. Por ejemplo, para `P=60`, `N=13`, `L=3`, `M=4`, `l=m=2`, las cotas dan **3 ≤ h ≤ 3**, tal como en la prueba. En la **Tabla A, N=1 000 000**, con `P=128` se obtuvieron **8 niveles creciente / 7 aleatorio**; con `P=2048`, **4 / 3**. En la **Tabla B, N=300 000 y P=4096**, ambos órdenes tienen **3 niveles**, que coinciden con el mínimo teórico `1 + ceil(log_base_341(300000 / 255)) = 3`. El **ABB perfectamente balanceado** de la motivación requiere aproximadamente `ceil(log_base_2(N + 1))` páginas por búsqueda, suponiendo un nodo por página. Para `N=300000` son **19 páginas**, frente a las **3 del B+**. Cada interno del B+ permite elegir entre muchos hijos, reduciendo los niveles recorridos.

### 3. E/S por operación

`buscar()` sigue **un único camino** desde la raíz hasta la hoja: una lectura por nivel, encuentre o no la clave. Por tanto, `R = niveles()` y `W = 0`. `insertar()` también lee ese camino (`R = niveles()`, considerando la altura antes de insertar). Si la clave es nueva y hay `s` divisiones, cada una escribe **dos páginas** (nodo original y nuevo) y se escribe **una página final** donde termina la propagación (o la nueva raíz):

```text
W = 2 * s + 1
```

Si la clave ya existe, `insertar` devuelve `false`, lee un camino completo y no escribe ninguna página: **`R=h`, `W=0`**.

Por ejemplo, `insertar(45)` tuvo `s=2`, luego `W=2 * 2+1=5`, como en la prueba. No se cuentan las escrituras de cabecera. Como `h=Θ(log_base_M(N))` y `s≤h`, el costo de E/S de las operaciones es **`Θ(log_base_M(N))` en el peor caso**. La búsqueda binaria dentro de una página realiza `Θ(log M)` comparaciones en el peor caso; al recorrer `Θ(log_base_M(N))` niveles:

```text
Comparaciones de busqueda, en el peor caso:

Theta(log_base_M(N) * log_base_2(M))
= Theta((log_base_2(N) / log_base_2(M)) * log_base_2(M))
= Theta(log(N))
```

### 4. Ocupación

Con inserción **creciente**, cada clave nueva llega a la hoja más a la derecha. Cuando se desborda, la hoja izquierda queda aproximadamente a media capacidad y ya no recibe claves mayores; por eso la ocupación se acerca al 50 % (para hojas pequeñas puede superar ese valor). Al dividir `L+1` claves, la hoja izquierda conserva `a = ceil((L + 1) / 2)` claves. La primera hoja admite `L` claves sin dividirse; la primera división ocurre al insertar la clave número `L+1` y, desde entonces, cada `a` inserciones provocan otra división, que agrega una hoja. Por ello, para esta política de división y `N≥1`:

```text
a = ceil((L + 1) / 2)
H_creciente = 1 + max(0, ceil((N - L) / a))
```

**Predicción exacta (Tabla A, N=1 000 000):** para `P=128`, `L=7`, `a=4`: `H=1+ceil((1000000-7)/4)=250000`; para `P=2048`, `L=127`, `a=64`: `H=1+ceil((1000000-127)/64)=15625`. Coinciden con las mediciones. Las ejecuciones pequeñas se conservan en el anexo como pruebas adicionales.

Para **N=1 000 000**, con `P=128` hubo **250000 hojas, 57,14 % y 8 niveles** en creciente frente a **196962 hojas, 72,53 % y 7 niveles** en aleatorio. Con `P=2048` hubo **15625 hojas, 50,39 % y 4 niveles** frente a **11438 hojas, 68,84 % y 3 niveles**. La inserción aleatoria reutiliza espacio en distintas hojas, reduce páginas y tamaño del índice y, en estos casos, también niveles; no siempre reduce la altura (en Tabla B ambas variantes tienen 3).

En **IMDb (Tabla B, P=4096, N=300000)**, Archivo produjo **2343 hojas, 50,21 % de ocupación, 3 niveles e índice de 9658368 bytes**; Aleatorio, **1805 hojas, 65,18 %, 3 niveles e índice de 7434240 bytes**. La diferencia es **2224128 bytes (≈23,03 %)**. Se comprobó directamente el orden de los **300000 `tconst`** usados: **0 pares fuera de orden creciente estricto**. Por tanto, en modo **Archivo las claves llegan en orden numérico estrictamente creciente**; en **Aleatorio** se mezclan antes de insertarlas. Esto explica la ocupación cercana al 50 % de Archivo.

### 5. Secuencial contra índice

Una búsqueda secuencial de una clave **existente** elegida uniformemente revisa en promedio `(N+1)/2` registros. Nuestra medición suma una lectura adicional mediante `leer(pos)` para recuperar el registro encontrado, por lo que la esperanza del contador es **`(N+1)/2 + 1`**. Para `N=300000`, son **150001,5 registros/consulta**. La **Tabla C** midió **152494,28** sobre una muestra de 50 consultas, que no tiene que coincidir exactamente con la esperanza. Una clave **inexistente** requiere revisar los `N` registros, y la tabla confirma **300000**. El B+ leyó **3 páginas del índice/consulta** en ambos casos, más **1 registro de datos** si la clave existía y **0** si no existía.

La búsqueda secuencial crece **`Θ(N)`** en promedio (existentes uniformes) y en el peor caso; el B+ crece **`Θ(log_base_M(N))`** en páginas de índice, más una lectura del registro encontrado. No se equipara una página de índice con un registro de datos: son unidades distintas.

### 6. Rango

`buscarRango(desde,hasta)` desciende **una sola vez** de la raíz a la primera hoja y luego avanza por los enlaces `siguiente`. Sean `h` los niveles, `s` los registros entregados, `L` la capacidad de hoja y `alpha` la ocupación media como fracción. Cada hoja contiene aproximadamente `alpha * L` claves. Por ello:

```text
Paginas leidas = (h - 1) + hojas recorridas

Hojas recorridas: aproximadamente s / (alpha * L),
mas un ajuste constante por los extremos del rango.
```

El término `h-1` representa los nodos internos del descenso inicial; `s / (alpha * L)` aproxima las hojas recorridas. El término constante contempla hojas parcialmente cubiertas. **No es una igualdad exacta:** influyen la posición de los extremos, la ocupación local y los huecos entre claves. Con ocupación mínima garantizada, el costo es **`Θ(h+s/L)`**, también para rangos vacíos porque `h≥1`. Las hojas intermedias recorridas están completamente dentro del rango; los extremos agregan un número constante de hojas. No hace falta que los identificadores sean consecutivos. Para `s` búsquedas independientes, el costo es **`Θ(s * h)`**.

En la **Tabla D**, `h=3`, y las medias `(s, páginas, hojas)` fueron: `w=10: (9,56; 3,03; 1,03)`, `w=100: (95,36; 3,70; 1,70)`, `w=1000: (956,33; 10,44; 8,44)` y `w=10000: (9351,75; 75,99; 73,99)`. En cada caso `páginas-hojas=2=h-1`, confirmando que los internos se recorren una sola vez. Para `w=10000`, **75,99 páginas** frente a **9351,75·3=28055,25 páginas** de búsquedas independientes (sin caché). La ventaja proviene de recorrer hojas enlazadas sin reiniciar la búsqueda desde la raíz.

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

## Uso de IA

Utilizamos ChatGPT como herramienta de apoyo durante el desarrollo y la revisión del trabajo. Principalmente lo empleamos para comprobar si nuestra implementación se comportaba según la consigna, detectar errores que habíamos pasado por alto y comprender algunos requisitos de los ejercicios y de las clases de prueba.

También solicitamos explicaciones sobre las operaciones más complejas del árbol B+, como la inserción con divisiones de hojas, nodos internos y creación de una nueva raíz. La generación de código se utilizó principalmente para ampliar los tests, comparar los resultados con los valores esperados y mejorar la impresión de las comprobaciones y de las tablas. También utilizamos código inicialmente propuesto por IA para parte de ExperimentoBMas, que posteriormente revisamos y modificamos.

### Alcance de la ayuda

- **Formato de páginas y cálculo de M y L:** revisión y explicaciones para contrastar las capacidades y la organización física con el enunciado.
- **Búsquedas, inserción y splits de hojas, internos y raíz:** revisión y explicaciones para comprender el descenso, la propagación de separadores y las divisiones.
- **Búsqueda por rango, persistencia y verificación de invariantes:** revisión del comportamiento esperado, de los contadores de páginas y de los casos que debían comprobarse.
- **`TestArbolBMas`:** incorporamos y adaptamos código de prueba propuesto por IA para verificar inserciones, búsquedas, duplicados, rangos, persistencia y archivos inválidos. Mejoramos la impresión de los casos y sus resultados, ejecutamos las pruebas y las contrastamos con la traza de la consigna.
- **`ArchivoDatos` y `BMasApp`:** explicaciones y revisión del formato de registros, las posiciones y el funcionamiento de las consultas.
- **`ExperimentoBMas`:** utilizamos código inicialmente propuesto por IA para parte del experimento y luego lo revisamos y modificamos, incluyendo la corrección del orden creciente y la impresión de las tablas.
- **README:** utilizamos IA como apoyo para comprender los resultados, organizar su explicación y revisar la redacción y las fórmulas, incluida esta declaración.

### Errores detectados y cambios realizados

La revisión permitió detectar que el experimento sintético reutilizaba el arreglo mezclado al construir el siguiente árbol etiquetado como creciente. Corregimos esto restaurando las claves al comienzo de cada iteración.

Además, ampliamos las pruebas de búsquedas, duplicados, rangos y persistencia; ajustamos la alineación de las tablas; quitamos el uso de `java.util.Locale.US` por las restricciones de la consigna y documentamos la elección de los tipos de excepciones. En el análisis corregimos la comparación con el ABB balanceado y aclaramos la lectura adicional del registro encontrado en la medición secuencial.

### Herramienta y prompts

Utilizamos ChatGPT mediante Codex. Algunos prompts textuales de esta conversación fueron:

- «Podes verificar el tp3?»
- «ok hay que terminar el tp pero vamos por partes y explicame no solo que esta mal o hay que corregir sino para que funciona, como y que deberia esperar»
- «podes arreglarme la impresion de esa tabla o sea queda toda desfasadas las coluimnas»
- «experimentoBMas que es lo que hace realmente?»
- «para que sirve especificamente archivosDatos y como se usa?»
- «explicame mejor la parte de la defensa del readme en el ej2»

También resumimos el tipo de consultas realizadas con estos ejemplos de formulación:

- Verificar las implementaciones actuales de la clase ArbolBMas.
- Revisar ArbolBMas por partes y explicar qué corregir, cómo funciona cada operación y qué resultados esperar.
- Explicar la finalidad de ExperimentoBMas.
- Explicar qué funciones de Java se pueden utilizar y cómo funcionan.
- Explicar el recorrido de una búsqueda con un gráfico.

Las salidas presentadas en las tablas se obtuvieron ejecutando el programa. Utilizamos las respuestas de IA como material de apoyo y las contrastamos con la consigna y con las pruebas realizadas.
