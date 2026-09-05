public class TestABBAumentado {
	public static void main(String[] args) throws ClaveNulaException, ClaveInexistenteException {
		ABBAumentado<Integer, String> abba = new ABBAumentado<>();
		abba.agregar(50, "P" + 50);
		abba.agregar(30, "P" + 30);
		abba.agregar(70, "P" + 70);
		abba.agregar(20, "P" + 20);
		abba.agregar(40, "P" + 40);
		abba.agregar(60, "P" + 60);
		abba.agregar(80, "P" + 80);
		abba.agregar(35, "P" + 35);
		abba.agregar(65, "P" + 65);

		abba.reiniciarVisitas();
		System.out.println("kEsimo(6) => " + abba.kEsimo(6));
		System.out.println("Visitas totales: " + abba.visitas());
		abba.reiniciarVisitas();
		System.out.println("Rango de 35 a 65: " + abba.consultarRango(35, 65));
		System.out.println("Visitas totales: " + abba.visitas());
		System.out.println("Rango de 50: " + abba.rango(50));
		abba.reiniciarVisitas();
		System.out.println("Cnsultar rango ingenuo: " + abba.consultarRangoIngenuo(35, 65));
		System.out.println("Visitas totales: " + abba.visitas());



	}
}
