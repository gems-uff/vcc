package br.uff.pacote2;

public class Printer {
	
	public void print(String value) {
		System.out.println("O valor é:" + value);
	}
	
	public void print(Integer value, Float[] valores) {
		System.out.println("O valor é:" + value);
		System.out.println("Valores do array: ");
		for (int i = 0; i < valores.length; i++) {
			System.out.println(valores[i]);
		}
	}
}
