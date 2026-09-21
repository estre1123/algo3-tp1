# README - ABBAumentado

Uso rapido con jshell

Para usar jshell rapidamente, usar este comando:

```
/Code jshell ClaveNulaException.java ClaveInexistenteException.java IndiceFueraDeRangoException.java, RangoInvalidoException.java, ABBAumentado.java, TablaEncadenada.java, IndiceDoble.java
```

---

## PUNTO 5 - EJ1

### a- por qué `h_ord` es N − 1 y `vis_kEsimo_ord` es N /2?

R = `h_ord` es N - 1 porque al insertar 1, 2, ..., N en orden, cada clave nueva es mayor que todas las que ya estan, entonces siempre se agrega como hijo derecho del ultimo nodo y el arbol gana un nivel por cada insercion. Como la altura se mide en aristas (con n = 1, h = 0; con n = 2, h = 1), con N nodos queda h = N - 1. El arbol es en la practica una lista enlazada.

`vis_kEsimo_ord` es N/2 porque en este arbol ningun nodo tiene hijo izquierdo, entonces el tamano del subarbol izquierdo es siempre 0 y el atributo tamano no nos ayuda a saltear nada. Hay que bajar nodo por nodo desde la raiz hasta llegar al nodo N/2, lo que es similar a una busqueda lineal en un array, y eso son N/2 visitas.

### b- Por qué `vis_rango_aum` queda en unas decenas mientras `vis_rango_ing` vale N?

R = consultarRango aprovecha el atributo tamano de cada nodo: hace dos bajadas desde la raiz (una para contar los menores o iguales que b y otra para contar los menores que a) y cuando baja por la derecha suma de un solo golpe el tamano del subarbol izquierdo, sin recorrerlo. Entonces solo visita los nodos de esos dos caminos, que es como mucho `2 * (h + 1)`. En el arbol aleatorio h es chica (por ejemplo para N = 10000, `h_aleat` = 27), y por eso `vis_rango_aum` da 36 en vez de N..

`vis_rango_ing` es N porque rangoIngenuoRec visita CADA nodo del arbol, este o no dentro del rango, y va contando uno a uno los que cumplen. No descarta subarboles nunca, asi que siempre visita exactamente N nodos, sin importar a, b ni la forma del arbol.

### c- Arbol chico de la traza

consultarRango hace dos bajadas independientes, y cada una puede costar hasta h + 1 visitas, asi que en total puede costar hasta `2 * (h + 1)` visitas. El ingenuo cuesta siempre n. Con pocos nodos, `2 * (h + 1)` puede ser parecido o hasta mayor que n. Por ejemplo, en un arbol de 3 nodos (raiz con dos hijos, h = 1), consultarRango de todo el rango hace 2 + 2 = 4 visitas contra 3 del ingenuo. En la traza chica de 9 nodos (h = 3) el maximo es `2 * 4` = 8 y el ingenuo hace 9, por eso la diferencia es de apenas 1 (`vis_rango` = 8, `vis_rango_ing` = 9), porque los dos caminos (hacia 35 y hacia 65) ya pasan por casi todo el arbol y la raiz se cuenta dos veces.

Esto no contradice el punto anterior porque `2 * (h + 1)` crece con la altura, que en un arbol balanceado es del orden de log n, mientras que n crece linealmente, o sea que mientas que n crece, la diferencia entre ambos ira creciendo (el ingenuo crece mas rapido)

---

## Analisis asintotico de las operaciones

### 1) kEsimo en function de la altura

```
T (h) = T (h − 1) + Θ(1), T (1) = Θ(1)
```

Sea:

```
T (h) = T (h − 1) + Θ(1)
```

vamos desarrollando hasta T(1)

```
1- reemplazamos T(h-1) = [T(h -2) + Θ(1)]
	= [T(h-2) + Θ(1)] + Θ(1)
	= T(h - 2) + 2Θ(1)

2 - reemplazamos T(h - 2) por T(h - 3) + Θ(1)
	=[T(h - 3) + Θ(1)] + 2Θ(1)
	T(h - 3) + 3Θ(1)

3 - generalizamos para h - k, hasta el caso base T(1), o sea h - k = 1 ->  k = h - 1
	=T(h - k) + (h-1)Θ(1)
	=T(1) + hΘ(1) - Θ(1)
	=Θ(1) + hΘ(1) - Θ(1)
	=hΘ(1)
```

por lo tanto, tendremos un costo de Θ(1), h veces

4 - Finalmente, la solucion es:

> **T(h) = Θ(h)**

### 2) Busqueda (o kEsimo) en un ABB perfectamente balanceado

Como cada paso descarta como mucho, la mitad de nodos, tenemos que

```
T(n) = T(n/2) + Θ(1), T(1) = Θ(1)
```

Por teorema maestro, tenemos que

```
a = 1
b = 2
f(n) = Θ(1)
```

hallando el logaritmo

```
log_2(1) = 0
```

sabemos que `f(n) = Θ(1) = Θ(n^0 * log^(0)n)`, donde k = 0

Caso 2 del Teorema Maestro, la solucion sera:

```
Θ(n) = n^(log_2(1)) * log^(k + 1)_2(n)
Θ(n) = log_2(n)
Θ(n) = log(n)
```

### 3) Búsqueda en un ABB degenerado (inserción ordenada): T (n) = T (n − 1) + Θ(1).

El Teorema maestro no aplica, ya que el subproblema no tiene la tamano de n/b, sino n - 1

Resolvemos desenrollando hasta que n = 1

```
T (n) 	= T (n − 1) + Θ(1)
	= [ T(n - 2) + Θ(1) ] + Θ(1)
	= T(n - 2) + 2Θ(1)
	= [ T(n - 3) + Θ(1) ] + 2Θ(1)
	= T(n - 3) + 3Θ(1)
...generalizando para n - k = 1 -> k = n - 1
	= T(n - k) + (n - 1)Θ(1)
	= T(1) + nΘ(1) - Θ(1)
	= Θ(1) + nΘ(1) - Θ(1)
	= nΘ(1) (Costo Θ(1), n veces)
```

La solucion: Es igual a buscar linealmente en un array

> **T(n) = Θ(n)**

### 4) consultarRango aumentado

El algoritmo calcular menores(a) y menoresOIguales(B), es decir, dos bajadas independientes desde la raiz, en cada cual se va bajando nodo a nodo y comparando la clave con la del nodo

Obtener el tamano de los nodos deseados toma un tiempo Θ(1), entonces tenemos la misma recurrencia que el punto 1:

```
T(h) = T(h - 1) + Θ(1), T(1) = Θ(1)
```

Como se hacen dos bajadas, el tiempo total es

```
T(h) = 2Θ(h)
T(h) = Θ(h)
```

De espacio extra: como la implementacion es recursiva, habra como maximo Θ(h) niveles de recursion =  la pila de recursion llega a profundidad Θ(h)

#### consultarRangoIngenuo

Es una implementacion recursiva que visita cada nodo y va contando si el nodo en cuestion cumple con el rango, como cada nodo se visita una vez y suma un trabajo constante,

```
T(h) = Θ(h)
```

El espacio de la pila es Θ(h), porque llega hasta la mayor profundidad del arbol al visitar todos los nodos

### 5) Coste espacial

Cada nodo guarda clave, valor, dos referencias y un entero tamano, o sea una cantidad constante de memoria. Con n nodos, el espacio es Θ(n). El entero tamano agrega una constante por nodo, asi que no cambia el orden de magnitud.

### 6) Relacion con la tabla de visitas

- **a -** `h_ord` = N-1, `vis_kEsimo_ord` = N/2 : Como la insercion es ordenada, en cada nodo, el tamano del hijo izquierdo es 0, por tanto bajamos h - 1 niveles (por partir de la raiz). LLegar al punto medio requiere recorrer medio camino, es decir N/2

- **b -** `h_aleat` ≈ log2(N), al ingresar elementos de manera aleatoria, la altura esperada es logaritmica, creciendo mucho mas lento que N

- **c -** `vis_rango_aum` queda en decenas porque aprovecha el atributo tamano en cada nodo, asi no necesita recorrer todo el arbol por el concepto de ABB. `vis_rango_ing` es N porque recorre TODOS los nodos del arbol

- **d -** En la traza del arbol chico (n = 9 y h = 3), las visitas en rango = 8, y rangoIng = 9, a pesar de que la diferencia es poca, la ventaja de rango() se sigue manteniendo por sobre rangoIngenuo, y la diferencia entre ambos crecera conforme crezca N, esto no contradice los puntos expuestos anteriormente
## 7. Búsqueda con encadenamiento

En una tabla hash con encadenamiento, si las claves se distribuyen de forma uniforme entre las cubetas, el costo esperado de una búsqueda es:

> **Θ(1 + α)**

donde `α = n / m` es el factor de carga, con `n` claves y `m` cubetas.

Cuando `α` se mantiene constante, este costo se puede expresar como **Θ(1)**.

En el peor caso, si todas las claves están en una misma cubeta, la búsqueda debe recorrer toda la cadena. Por lo tanto, el peor caso es:

> **Θ(n)**

El espacio utilizado por la tabla es:

> **Θ(n + m)**

ya que se deben almacenar las `n` claves y sus referencias, además del arreglo de `m` cubetas.


---

## 8. `agregar` del índice doble

La operación `agregar` del índice doble realiza una inserción en el ABB y una inserción en la tabla hash.

En el caso promedio, la inserción en la tabla hash cuesta:

> **Θ(1 + α)**

mientras que la inserción en el ABB depende de su altura `h` y cuesta:

> **Θ(h)**

Por lo tanto, el costo promedio de `agregar` es:

> **Θ(h + 1 + α)**

Si el factor de carga `α` se mantiene constante, esto se puede expresar como:

> **Θ(h)**

En el peor caso, una inserción puede disparar un `rehash`. El `rehash` debe recorrer y reubicar las claves almacenadas en la tabla, por lo que cuesta:

> **Θ(n + m)**

Además, la inserción en el ABB cuesta `Θ(h)`. Por lo tanto, una operación `agregar` que dispara `rehash` puede costar:

> **Θ(n + m + h)**

Sin embargo, la capacidad de la tabla se duplica cada vez que se realiza un `rehash`. Al insertar `n` claves desde una tabla vacía, el trabajo total de los `rehash` es:

> **Θ(n)**

porque la suma de los elementos reubicados forma una serie geométrica:

```text
n + n/2 + n/4 + ... < 2n
```

Este costo se reparte entre todas las inserciones, por lo que el costo amortizado de la parte de tabla hash es:

> **Θ(1 + α)**

y, si `α` se mantiene constante:

> **Θ(1)**

Por lo tanto, el costo amortizado de `agregar` en el índice doble es:

> **Θ(h)**


---

## 9. Relación con las tablas del experimento

Los resultados experimentales permiten observar las complejidades analizadas anteriormente.

En el primer experimento, el índice doble utiliza una tabla hash con rehash. Al aumentar `N`, la tabla mantiene un factor de carga controlado y las búsquedas por hash requieren pocas sondas. Por eso, la cantidad de `sondas_hash_get` se mantiene relativamente estable.

En cambio, las búsquedas realizadas directamente sobre el ABB dependen de su altura `h`. Por eso, `vis_ABB_get` crece a medida que aumenta la cantidad de claves.

En el segundo experimento, la tabla tiene una cantidad fija de cubetas (`m = 97`) y no realiza rehash. Al aumentar `N`, aumenta el factor de carga:

```text
α = N / m
```

Como consecuencia, las cadenas de las cubetas se hacen más largas y aumenta la cantidad promedio de sondas por búsqueda. Por lo tanto, `sondas/N` crece junto con `α`, de acuerdo con el costo esperado:

> **Θ(1 + α)**

Estos resultados muestran la diferencia entre las dos estructuras: el ABB permite realizar las operaciones ordenadas, como `kEsimo` y `consultarRango`, mientras que la tabla hash permite realizar búsquedas por clave recorriendo solamente la cadena correspondiente.

